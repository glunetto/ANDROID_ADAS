package org.idra.adas;

import org.idra.adas.DetectionBasedTracker;
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

	private static final String TAG = "OCVSample::Activity";

	private static final Scalar FACE_RECT_COLOR = new Scalar(0, 255, 0, 255);

	private Mat mRgba;
	private Mat mGray;
	
	private DetectionBasedTracker mNativeDetector;

	private CameraBridgeViewBase mOpenCvCameraView;

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
	}

	@Override
	public void onPause() 
	{
		super.onPause();
		if (mOpenCvCameraView != null)
		{
			mOpenCvCameraView.disableView();
		}
	}

	@Override
	public void onResume() 
	{
		super.onResume();
		if (!OpenCVLoader.initDebug()) 
		{
			Log.d(TAG,
					"Internal OpenCV library not found. Using OpenCV Manager for initialization");
			OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this,
					mLoaderCallback);
		} 
		else 
		{
			Log.d(TAG, "OpenCV library found inside package. Using it!");
			mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
		}
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

	public Mat onCameraFrame(CvCameraViewFrame inputFrame) 
	{
		int sample_ratio = 3;

		mRgba = inputFrame.rgba();
		mGray = inputFrame.gray();
		
        MatOfRect items = new MatOfRect();

		if (mNativeDetector != null)
		{	
			Imgproc.resize(mGray, mGray, new Size(mGray.cols()/sample_ratio, mGray.rows()/sample_ratio));
			mNativeDetector.detect(mGray, items);
		}
		
		Rect[] itemsArray = items.toArray();
        for (int i = 0; i < itemsArray.length; i++)
        {
			Log.d("TEST", "Found pedestrians: "+itemsArray.length);
            Imgproc.rectangle(mRgba, new Point(itemsArray[i].tl().x*sample_ratio,itemsArray[i].tl().y*sample_ratio),
            		new Point(itemsArray[i].br().x*sample_ratio,itemsArray[i].br().y*sample_ratio), FACE_RECT_COLOR, 3);
        }

        items.release();
        //mGray.release();
        
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
