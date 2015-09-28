/*TODO:
 * 		-Modificare/Inserire/Togliere i commenti							( priorità: 0 )
 * 		-Implementare un controllo del movimento 							( priorità: 2 )
 * 		-Implementare un sistema per capire se esiste un pericolo o meno 	( priorità: 1 )
 * 		-Inserire un menù di "impostazioni"									( priorità: 0 )
 * 		-Utilizzare l'audio di John Cena									( priorità: MAX )
 * 		-Inserire gli avvertimenti audio nei posti giusti					( priorità: 1 )
 * 		-Migliorare la velocità di esecuzione								( priorità: 1 )
 * 
 ********/
package org.idra.adas;

import org.idra.adas.DetectionBasedTracker;
import org.idra.adas.audio.AudioManager;
import org.idra.adas.utm.Utm;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

public class FdActivity extends Activity implements CvCameraViewListener2
 {
	// Tag for log info
	private static final String TAG = "OCVSample::Activity";
	// ratio for image resizing
	private static final int sample_ratio = 3;
	// Color for rect drawing
	private static final Scalar RECT_COLOR = new Scalar(0, 255, 0, 255);
	// Matrices used in image processing
	private Mat mRgba;
	private Mat mGray;
	// Audio manager object
	private AudioManager ADAS_Audio;
	// Sensor socket object
	private Sensor_Socket ADAS_Sensors;
	// detector object
	private DetectionBasedTracker mNativeDetector;
	// OCV android camera object
	private CameraBridgeViewBase mOpenCvCameraView;
	// VI PREGO SCRIVETE QUALCOSA DI DECENTE AL POSTO DI QUESTI DUE COMMENTI
	// qualcosa per decidere se attivare l'audio o meno
	private boolean _audio = false;
	// qualcosa per capire se ci si sta muovendo o meno
	private boolean _moving = false;

	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) 
	{
		@Override
		public void onManagerConnected(int status) 
		{
			switch (status) 
			{
				case LoaderCallbackInterface.SUCCESS: 
				{
					Log.i(TAG, "OpenCV loaded successfully");
	
					// Load native library after(!) OpenCV initialization
					System.loadLibrary("detection_based_tracker");
					mNativeDetector = new DetectionBasedTracker();
					mOpenCvCameraView.enableView();
				}
				break;
				
				default:
				{
					super.onManagerConnected(status);
				}
				break;
			
			}
		}
	};

	public FdActivity() 
	{
		Log.i(TAG, "Instantiated new " + this.getClass());
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		Log.i(TAG, "called onCreate");
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		setContentView(R.layout.face_detect_surface_view);

		mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.fd_activity_surface_view);
		mOpenCvCameraView.setCvCameraViewListener(this);
		
		if(_audio)
		{
			// New ADAS_Audiio object with associated thread
			ADAS_Audio = new AudioManager(this.getApplicationContext());
			Thread audio_thread = new Thread (ADAS_Audio);
			audio_thread.start();
		}
		
		// new ADAS_Sensors object
		ADAS_Sensors = new Sensor_Socket(this.getApplicationContext());
	}

	@Override
	public void onPause() 
	{
		super.onPause();
		if (mOpenCvCameraView != null)
		{
			mOpenCvCameraView.disableView();
		}
		
		ADAS_Sensors.pause_socket();
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		ADAS_Sensors.start_socket();
	}
	
	@Override
	public void onStop()
	{
		super.onStop();
		ADAS_Sensors.stop_socket();
	}

	@Override
	public void onResume() 
	{
		super.onResume();
		
		if (!OpenCVLoader.initDebug()) 
		{
			Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
			OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
		} 
		else 
		{
			Log.d(TAG, "OpenCV library found inside package. Using it!");
			mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
		}
		ADAS_Sensors.resume_socket();
	}

	public void onDestroy() 
	{
		super.onDestroy();
		mOpenCvCameraView.disableView();
	}

	public void onCameraViewStarted(int width, int height) 
	{
		mGray = new Mat();
		mRgba = new Mat();
	}

	public void onCameraViewStopped() 
	{
		mGray.release();
		mRgba.release();
	}

	// praticamente iol main di questa app (CAMBIATE ANCHE QUESTO COMMENTO PER FAVORE)
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) 
	{
		mRgba = inputFrame.rgba();
		mGray = inputFrame.gray();

		MatOfRect items = new MatOfRect();

		// qua facciamo un po di macello coi sensori per capire se siamo in movimento o meno
		
		// Dopodichè trasformiamo i valori di posizione del gps con Utm 
		
		if(_moving)
		{
			if (mNativeDetector != null)
			{	
				//resizing the image to gain FPS
				Imgproc.resize(mGray, mGray, new Size(mGray.cols()/sample_ratio, mGray.rows()/sample_ratio));
				// Detecting people
				mNativeDetector.detect(mGray, items);
			}

			// Drawing rects where there is a positive match
			// Questa parte se riusciamo a implementare qualcosa di meglio magari la leviamo (es.:Kalman filter)
			Rect[] itemsArray = items.toArray();
			for (int i = 0; i < itemsArray.length; i++)
			{
				Log.d("TEST", "Found pedestrians: "+itemsArray.length);
				Imgproc.rectangle(mRgba, new Point(itemsArray[i].tl().x*sample_ratio, itemsArray[i].tl().y*sample_ratio),
						new Point(itemsArray[i].br().x*sample_ratio, itemsArray[i].br().y*sample_ratio), RECT_COLOR, 3);
			}
		}
        items.release();
        
		return mRgba;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		Log.i(TAG, "called onCreateOptionsMenu");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		return false;
	}


}
