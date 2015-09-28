package org.idra.adas;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.ActivityRecognition;

public class Sensor_Socket implements ConnectionCallbacks, OnConnectionFailedListener, SensorEventListener
{
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    // LOCATION RELATED OBJECTS

    // -Google client to interact with Google API
	private GoogleApiClient _google_api_client;

	// SENSORS RELATED OBJECTS

	private SensorManager _accelerometer_manager;
    private Sensor _accelerometer;
	private SensorManager _gyroscope_manager;
    private Sensor _gyroscope;
    // Accelerator
    private float _acc_x, _acc_y, _acc_z;
    
    /* Gyroscope
    private static final float NS2S = 1.0f / 1000000000.0f;
	private static final float EPSILON = 0;
    private final float[] deltaRotationVector = new float[4];
    private float timestamp; */
    
    private Context _context;
    
    public float[] get_accelerometer()
    {
    	float acc[] = { _acc_x, _acc_y, _acc_z };
    	return acc;
    }
   
    public Sensor_Socket(Context application__context)
    {
    	_context = application__context;
		if (checkPlayServices()) 
		{
            // Building the GoogleApi client
            _build_google_api_client();
        }

		_acc_x = 0;
		_acc_y = 0;
		_acc_z = 0;

		_accelerometer_manager = (SensorManager) _context.getSystemService(Context.SENSOR_SERVICE);
		_gyroscope_manager = (SensorManager) _context.getSystemService(Context.SENSOR_SERVICE);
	    
		_accelerometer = _accelerometer_manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	    _gyroscope = _gyroscope_manager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
	    
        _accelerometer_manager.registerListener(this, _accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        _gyroscope_manager.registerListener(this, _gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
    }
    
    public void pause_socket()
    {
        _accelerometer_manager.unregisterListener(this);
        _gyroscope_manager.unregisterListener(this);
    }
    
    public void start_socket()
    {
		_google_api_client.connect();
    }
    
    public void stop_socket()
    {
		_google_api_client.disconnect();
    }
    
    public void resume_socket()
    {
        _accelerometer_manager.registerListener(this, _accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        _gyroscope_manager.registerListener(this, _gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
    }
   	
	protected synchronized void _build_google_api_client() 
	{
        _google_api_client = new GoogleApiClient.Builder(_context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(ActivityRecognition.API)
                .build();
    }
	
	public Location get_last_location()
	{
		return LocationServices.FusedLocationApi.getLastLocation(_google_api_client);
	}
	
	// Method to verify google play services on the device
    private boolean checkPlayServices()
    {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(_context);
        if (resultCode != ConnectionResult.SUCCESS) 
        {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) 
            {
                GooglePlayServicesUtil.getErrorDialog(resultCode, (Activity) _context,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } 
            else 
            {
                Toast.makeText(_context.getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                ((Activity) _context).finish();
            }
            return false;
        }
        return true;
    }

	//  SENSORS ACTIVITIES

    public void AccelerometerActivity()
    {
        _accelerometer_manager = (SensorManager)_context.getSystemService(Context.SENSOR_SERVICE);
        
        if(_accelerometer_manager.getSensorList(Sensor.TYPE_ACCELEROMETER).size() != 0)
        	_accelerometer = _accelerometer_manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }
    
    public void GyroscopeActivity()
    {
        _gyroscope_manager = (SensorManager)_context.getSystemService(Context.SENSOR_SERVICE);
        
        if(_gyroscope_manager.getSensorList(Sensor.TYPE_GYROSCOPE).size() != 0)
        	_gyroscope = _gyroscope_manager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    }

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {}

	@Override
	public void onSensorChanged(SensorEvent event) 
	{
		Sensor mySensor = event.sensor;
		 
	    switch ( mySensor.getType() )
	    {
	    case Sensor.TYPE_ACCELEROMETER:
	    {
	    	_acc_x = event.values[0];
	    	_acc_y = event.values[1];
	    	_acc_z = event.values[2];
			break;
	    }
	    case Sensor.TYPE_GYROSCOPE:
	    {/*
	    	// This timestep's delta rotation to be multiplied by the current rotation
	    	// after computing it from the gyro sample data.
	    	if (timestamp != 0)
	    	{
		    	final float dT = (event.timestamp - timestamp) * NS2S;
		    	// Axis of the rotation sample, not normalized yet.
		    	float axisX = event.values[0];
		    	float axisY = event.values[1];
		    	float axisZ = event.values[2];
	    	    // Calculate the angular speed of the sample
		    	float omegaMagnitude = (float) Math.sqrt(axisX*axisX + axisY*axisY + axisZ*axisZ);
	
		    	// Normalize the rotation vector if it's big enough to get the axis
		    	// (that is, EPSILON should represent your maximum allowable margin of error)
		    	if (omegaMagnitude > EPSILON)
		    	{
			    	axisX /= omegaMagnitude;
			    	axisY /= omegaMagnitude;
			    	axisZ /= omegaMagnitude;
		    	}

		    	// Integrate around this axis with the angular speed by the timestep
		    	// in order to get a delta rotation from this sample over the timestep
		    	// We will convert this axis-angle representation of the delta rotation
		    	// into a quaternion before turning it into the rotation matrix.
		    	float thetaOverTwo = omegaMagnitude * dT / 2.0f;
	    	    float sinThetaOverTwo = (float) Math.sin(thetaOverTwo);
	    	    float cosThetaOverTwo = (float) Math.cos(thetaOverTwo);
	    	    deltaRotationVector[0] = sinThetaOverTwo * axisX;
	    	    deltaRotationVector[1] = sinThetaOverTwo * axisY;
	    		deltaRotationVector[2] = sinThetaOverTwo * axisZ;
	    		deltaRotationVector[3] = cosThetaOverTwo;
	    	}
	    	  timestamp = event.timestamp;
	    	  float[] deltaRotationMatrix = new float[9];
	    	  SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, deltaRotationVector);
	    	    // User code should concatenate the delta rotation we computed with the current rotation
	    	    // in order to get the updated rotation.
	    	    
	    	   rotationCurrent = rotationCurrent * deltaRotationMatrix;
	    	  */
			break;
	    }
		default:
			Log.d("SensorValueChanged","Unknown sensor changed his value");
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult result)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void onConnected(Bundle connectionHint)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void onConnectionSuspended(int cause)
	{
		// TODO Auto-generated method stub
	}

}
