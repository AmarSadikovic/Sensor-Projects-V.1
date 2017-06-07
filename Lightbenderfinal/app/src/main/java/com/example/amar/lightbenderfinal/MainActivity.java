package com.example.amar.lightbenderfinal;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private TextView tvScreenBrightness;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private Sensor mLightSensor; //Light sensor
    private boolean isSensorPresent = false;
    private boolean isLightSensorPresent = false;
    private CameraManager mCameraManager;

    private boolean isFlashLightOn = false;

    private Switch mySwitch;
    private boolean systemBrightness = false;
    //Camera
    private String mCameraID;
    private CameraCharacteristics mParameters;
    //Screen brightness
    private ContentResolver mContentResolver;
    private Window mWindow;
    private float mBrightness;

    private double treshHold;
    private float brightnessLimit;
    private float lastProximity;
    private float lastBrightness;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        treshHold = 0.2;
        lastBrightness = 0;
        lastProximity = 8;

        mySwitch = (Switch) findViewById(R.id.switch1);
        tvScreenBrightness = (TextView) findViewById(R.id.tvScreenBrightness);
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!systemBrightness) {
                    systemBrightness = true;
                } else {
                    invalidateBrightness();
                    systemBrightness = false;
                }
                switches();
            }
        });

        mSensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);

        if (mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null) {
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            isSensorPresent = true;
        } else {
            isSensorPresent = false;
        }
        initCameraFlashLight();

        if (mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) != null) {
            mLightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
            isLightSensorPresent = true;
        } else {
            isLightSensorPresent = false;
        }
        initScreenBrightness();
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        Toast.makeText(MainActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
//        switch (requestCode) {
//            case 1: {
//
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                    // permission was granted, yay! Do the
//                    // contacts-related task you need to do.
//                } else {
//
//                    // permission denied, boo! Disable the
//                    // functionality that depends on this permission.
//                    Toast.makeText(MainActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
//                }
//                return;
//            }
//
//            // other 'case' lines to check for other
//            // permissions this app might request
//        }
//    }

    public void switches() {
        if (systemBrightness) {
//            ActivityCompat.requestPermissions(MainActivity.this,
//                    new String[]{Manifest.permission.WRITE_SETTINGS},
//                    1);
            Toast.makeText(this, "Changed to system brightness: changing the “system brightness” is permanent", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Changed to screen brightness: ", Toast.LENGTH_SHORT).show();
        }
    }

    //Camera light
    private void initCameraFlashLight() {
        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            mCameraID = mCameraManager.getCameraIdList()[0];
            mParameters = mCameraManager.getCameraCharacteristics(mCameraID);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void turnTorchLightOn() {
        if (mParameters.get(CameraCharacteristics.FLASH_INFO_AVAILABLE)) {
            try {
                mCameraManager.setTorchMode(mCameraID, true);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
            isFlashLightOn = true;
        }
    }

    public void turnTorchLightOff() {
        try {
            mCameraManager.setTorchMode(mCameraID, false);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        isFlashLightOn = false;
    }

    //---------------------------------
    //Screen birghtness
    public void initScreenBrightness() {
        mContentResolver = getContentResolver();
        mWindow = getWindow();
    }

    public void changeScreenBrightness(float brightness) {
//        mBrightness = brightness;
//        if (systemBrightness) {
//            if (!Settings.System.canWrite(this)) {
//                Intent i = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
//                startActivity(i);
//            } else {
//                Settings.System.putInt(mContentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
//                Settings.System.putInt(mContentResolver, Settings.System.SCREEN_BRIGHTNESS, (int) (mBrightness * 255));
//
//            }
//        }
//        WindowManager.LayoutParams mLayoutParams = mWindow.getAttributes();
//        mLayoutParams.screenBrightness = mBrightness;
//        mWindow.setAttributes(mLayoutParams);


        float mBrightness = brightness;
        if (systemBrightness) {
            if (!Settings.System.canWrite(this)) {
                Intent i = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                startActivity(i);
            } else {
                Settings.System.putInt(mContentResolver, Settings.System.SCREEN_BRIGHTNESS, (int) (mBrightness * 255));
            }
        } else {
            WindowManager.LayoutParams mLayoutParams = mWindow.getAttributes();
            mLayoutParams.screenBrightness = mBrightness;
            mWindow.setAttributes(mLayoutParams);
        }

    }

    public void selectBrightness(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.rbVeryLow:
                treshHold = 0.2;
                break;
            case R.id.rbLow:
                treshHold = 0.4;
                break;
            case R.id.rbMedium:
                treshHold = 0.6;
                break;
            case R.id.rbHigh:
                treshHold = 0.8;
                break;
            case R.id.rbVeryHigh:
                treshHold = 1;
                break;
            default:
                break;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isSensorPresent) {
            mSensorManager.registerListener(this, mSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (isLightSensorPresent) {
            mSensorManager.registerListener(this, mLightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isSensorPresent) {
            mSensorManager.unregisterListener(this);
        }
        if (isLightSensorPresent) {
            mSensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

//        if (event.sensor.getType() == (event.sensor.TYPE_PROXIMITY)) {
//            lastProximity = event.values[0];
//        } else if (event.sensor.getType() == (event.sensor.TYPE_LIGHT)) {
//            lastBrightness = event.values[0];
//        }
//        brightnessLimit = (float) treshHold;
//        if (lastBrightness > 0 && lastBrightness < 200) {
//            tvScreenBrightness.setText("Light sensor value readings: " + lastBrightness);
//            changeScreenBrightness(brightnessLimit / lastBrightness);
//            if (lastBrightness < 20 ) {
//                if (lastProximity < mSensor.getMaximumRange()) {
//                    if (!isFlashLightOn) {
//                        turnTorchLightOn();
//                    }
//                } else {
//                    if (isFlashLightOn) {
//                        turnTorchLightOff();
//                    }
//                }
//            } else if(lastBrightness > 21 && isFlashLightOn){
//                turnTorchLightOff();
//            }
//        }

        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            lastProximity = event.values[0];
        } else if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            lastBrightness = event.values[0];
        }
        brightnessLimit = (float) treshHold;
//        System.out.println("light: " + lastBrightness);
        tvScreenBrightness.setText("Light sensor value readings: " + lastBrightness);

        if (lastBrightness > 0 && lastBrightness < 200) {
            changeScreenBrightness(brightnessLimit / lastBrightness);
            if (lastBrightness < 20) {
                if (lastProximity < mSensor.getMaximumRange()) {
                    if (!isFlashLightOn) {
                        turnTorchLightOn();
                    }
                } else {
                    if (isFlashLightOn) {
                        turnTorchLightOff();
                    }
                }
            } else {
                if(isFlashLightOn) {
                    turnTorchLightOff();
                }
            }
        }

    }

    private void invalidateBrightness() {
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
        getWindow().setAttributes(params);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }
}
