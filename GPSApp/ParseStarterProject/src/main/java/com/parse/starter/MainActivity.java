/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseAnalytics;
import com.parse.ParseObject;

import java.util.List;


public class MainActivity extends ActionBarActivity implements SensorEventListener,SeekBar.OnSeekBarChangeListener {
  LocationManager myLocationManager;
  String PROVIDER = LocationManager.GPS_PROVIDER;
  private ImageView mPointer;
  private SensorManager mSensorManager;
  private Sensor mAccelerometer;
  private Sensor mMagnetometer;
  private float[] mLastAccelerometer = new float[3];
  private float[] mLastMagnetometer = new float[3];
  private boolean mLastAccelerometerSet = false;
  private boolean mLastMagnetometerSet = false;
  private float[] mR = new float[9];
  private float[] mOrientation = new float[3];
  private float mCurrentDegree = 0f;
  private TextView label, latitude, longitude;
  private SeekBar distance = null;
  int progressChanged = 0;
  private double getLatitude;
  private double getLongitude;
  private Button button;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ParseAnalytics.trackAppOpenedInBackground(getIntent());
    mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    mPointer = (ImageView) findViewById(R.id.imageView);
    label = (TextView) findViewById(R.id.textView);
    latitude = (TextView) findViewById(R.id.textView5);
    longitude = (TextView) findViewById(R.id.textView6);
    button = (Button) findViewById(R.id.button);
    distance = (SeekBar) findViewById(R.id.seekBar);
    distance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      double progressChanged = 0;


      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        progressChanged = progress;
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {
      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {
        Toast.makeText(MainActivity.this, "Distance: " + progressChanged + " Meters ", Toast.LENGTH_SHORT)
                .show();
      }
    });
    {
      myLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
      Location location = getLastKnownLocation();
      ParseAnalytics.trackAppOpenedInBackground(getIntent());
      showMyLocation(location);
    }
  }

  private void showMyLocation(Location l) {
    if (l == null) {

    } else {
      getLatitude = l.getLatitude();
      getLongitude = l.getLongitude();

    }

  }

  public void sendLocation(View v) {

    ParseObject homeaddrees = new ParseObject("Location");
    double getLatitudes = progressChanged+getLatitude;
    double getLongitudes = progressChanged+getLongitude;
    homeaddrees.put("Latitude", getLatitudes);
    homeaddrees.put("Longitude", getLongitudes);
    latitude.setText(Double.toString(getLatitudes));
    longitude.setText(Double.toString(getLongitudes));
    homeaddrees.saveInBackground();

  }

  private LocationListener myLocationListener = new LocationListener() {

    @Override
    public void onLocationChanged(Location location) {
      showMyLocation(location);
    }

    @Override
    public void onProviderDisabled(String provider) {
      // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
      // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
      // TODO Auto-generated method stub

    }
  };

  private Location getLastKnownLocation() {


    myLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    List<String> providers = myLocationManager.getProviders(true);
    Location bestLocation = null;
    for (String provider : providers) {
      Location l = myLocationManager.getLastKnownLocation(provider);
      if (l == null) {
        continue;
      }
      if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
// Found best last known location: %s", l);
        bestLocation = l;
      }
    }
    return bestLocation;
  }

  @Override
  protected void onPause() {
    // TODO Auto-generated method stub
    super.onPause();
    myLocationManager.removeUpdates(myLocationListener);
    mSensorManager.unregisterListener(this, mAccelerometer);
    mSensorManager.unregisterListener(this, mMagnetometer);
  }

  @Override
  protected void onResume() {
    // TODO Auto-generated method stub
    super.onResume();
    myLocationManager.requestLocationUpdates(
            PROVIDER, //provider
            0, //minTime
            3000, //minDistance
            myLocationListener); //LocationListener
    mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
    mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_GAME);
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onSensorChanged(SensorEvent event) {
    if (event.sensor == mAccelerometer) {
      System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
      mLastAccelerometerSet = true;
    } else if (event.sensor == mMagnetometer) {
      System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
      mLastMagnetometerSet = true;
    }
    if (mLastAccelerometerSet && mLastMagnetometerSet) {
      SensorManager.getRotationMatrix(mR, null, mLastAccelerometer, mLastMagnetometer);
      SensorManager.getOrientation(mR, mOrientation);
      float azimuthInRadians = mOrientation[0];
      float azimuthInDegress = (float) (Math.toDegrees(azimuthInRadians) + 360) % 360;
      RotateAnimation ra = new RotateAnimation(mCurrentDegree, -azimuthInDegress, Animation.RELATIVE_TO_SELF, 0.5f,
              Animation.RELATIVE_TO_SELF,
              0.5f);
      ra.setDuration(250);

      ra.setFillAfter(true);
      mPointer.startAnimation(ra);
      mCurrentDegree = -azimuthInDegress;
      label.setText("" + Float.toString(mCurrentDegree));
    }
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int accuracy) {

  }

  @Override
  public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

  }

  @Override
  public void onStartTrackingTouch(SeekBar seekBar) {

  }

  @Override
  public void onStopTrackingTouch(SeekBar seekBar) {

  }
}
