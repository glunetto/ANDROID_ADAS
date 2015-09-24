package org.idra.adas;

import org.idra.adas.DetectionBasedTracker;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
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

		mRgba = inputFrame.rgba();
		mGray = inputFrame.gray();

		if (mNativeDetector != null)
		{
			mNativeDetector.detect(mGray);
		}

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
