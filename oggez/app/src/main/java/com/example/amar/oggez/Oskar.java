package com.example.amar.oggez;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * SensorProject3 changing brightness on the phone and turning on off the flash light
 * @author oskar
 */
public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mSensorProximity;
    private Sensor mSensorLight;
    private CameraManager mCameraManager;
    private String mCameraID;
    private CameraCharacteristics mParameters;
    private boolean isSensorProximityPresent = false, isSensorLightPresent = false;
    private boolean isFlashLightOn = false;

    private ContentResolver mContentResolver;
    private Window mWindow;

    private TextView tvMode;
    private TextView tvPreset;
    private TextView tvBrightness;

    private boolean isSystemMode = false;
    private int preset = 3;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);

        initComponents();
        initiateSensors();
        initCameraFlashLight();
        initScreenBrightness();
    }

    public void initiateSensors(){
        if(mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null){
            mSensorProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            isSensorProximityPresent = true;
        } else {
            isSensorProximityPresent = false;
        }

        if (mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) != null) {
            mSensorLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
            isSensorLightPresent = true;
        } else {
            isSensorLightPresent = false;
        }
    }

    public void initScreenBrightness(){
        mContentResolver = getContentResolver();
        mWindow = getWindow();
    }

    public void initCameraFlashLight(){
        mCameraManager = (CameraManager)getSystemService(Context.CAMERA_SERVICE);
        try {
            mCameraID = mCameraManager.getCameraIdList()[0];
            mParameters = mCameraManager.getCameraCharacteristics(mCameraID);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void initComponents(){

        tvMode = (TextView)findViewById(R.id.tvMode);
        tvBrightness = (TextView)findViewById(R.id.tvBright);
        tvPreset = (TextView)findViewById(R.id.tvPresets);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isSensorProximityPresent) {
            mSensorManager.registerListener(this, mSensorProximity, SensorManager.SENSOR_DELAY_NORMAL);
        }

        if(isSensorLightPresent){
            mSensorManager.registerListener(this, mSensorLight,SensorManager.SENSOR_DELAY_NORMAL);
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
        if(isFlashLightOn){
            turnTorchLightOff();
        }
        Toast.makeText(this, "Sensor unregister", Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSensorManager.unregisterListener(this);
        mSensorManager = null;
        mSensorProximity = null;
        mSensorLight = null;
        if(isFlashLightOn){
            turnTorchLightOff();
        }
        Toast.makeText(this, "Sensor unregister", Toast.LENGTH_SHORT).show();
    }

    public void turnTorchLightOff(){
        try {
            mCameraManager.setTorchMode(mCameraID, false);
            isFlashLightOn = false;
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

    public void turnTorchLightOn(){
        if (mParameters.get(CameraCharacteristics.FLASH_INFO_AVAILABLE)){
            try {
                mCameraManager.setTorchMode(mCameraID, true);
                isFlashLightOn = true;
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public void changeScreenBrightness(float value){
        if (!Settings.System.canWrite(this)){
            Intent i = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            startActivity(i);
        } else if(isSystemMode) {
            Settings.System.putInt(mContentResolver, Settings.System.SCREEN_BRIGHTNESS, (int)(value*255) * preset);
        }
        WindowManager.LayoutParams mLayoutParams = mWindow.getAttributes();
        mLayoutParams.screenBrightness = value * preset;
        mWindow.setAttributes(mLayoutParams);
        tvBrightness.setText("Current brightness: " + value*preset);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            float distanceFromPhone = sensorEvent.values[0];
            if (distanceFromPhone < mSensorProximity.getMaximumRange()) {
                if (!isFlashLightOn) {
                    turnTorchLightOn();
                }

            } else {
                if (isFlashLightOn) {
                    turnTorchLightOff();
                }
            }
        } else if(sensorEvent.sensor.getType() == Sensor.TYPE_LIGHT){
            float light = sensorEvent.values[0];
            if(light > 0 && light < 100){
                changeScreenBrightness(1/light);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    public void onModeClick(View view){
        boolean checked = ((RadioButton) view).isChecked();
        switch(view.getId()) {
            case R.id.rbSystem:
                if (checked)
                    tvMode.setText("Mode: System brightness");
                    isSystemMode = true;
                    Toast.makeText(this, "System brightness on", Toast.LENGTH_SHORT).show();
                    break;
            case R.id.rbScreen:
                if (checked)
                    tvMode.setText("Mode: Screen brightness");
                    isSystemMode = false;
                     Toast.makeText(this, "Screen brightness on", Toast.LENGTH_SHORT).show();
                    break;
        }
    }

    public void onPresetClick(View view){
        boolean checked = ((RadioButton) view).isChecked();
        switch(view.getId()) {
            case R.id.rbLowLight:
                if (checked) {
                    preset = 1;
                    tvPreset.setText("Preset: Low Light");
                }
                break;
            case R.id.rbMedLow:
                if (checked) {
                    preset = 2;
                    tvPreset.setText("Preset: Med-low Light");
                }
                break;
            case R.id.rbMedium:
                if (checked) {
                    preset = 3;
                    tvPreset.setText("Preset: Medium Light");
                }
                    break;
            case R.id.rbSunMed:
                if (checked) {
                    preset = 4;
                    tvPreset.setText("Preset: Sun-med Light");
                }
                    break;
            case R.id.rbDirectSun:
                if (checked) {
                    preset = 5;
                    tvPreset.setText("Preset: Direct Sunlight");
                }
                    break;
        }
    }
}
