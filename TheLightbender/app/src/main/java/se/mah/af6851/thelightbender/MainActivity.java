package se.mah.af6851.thelightbender;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private boolean isSensorPresent = false;
    private CameraManager mCameraManager;

    private float distanceFromPhone;
    private boolean isFlashLightOn = false;

    private Switch mySwitch;
    private boolean systemBrightness = false;
    //Camera
    private String mCameraID;
    private CameraCharacteristics mParameters;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mySwitch = (Switch)findViewById(R.id.switch1);
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(!systemBrightness){
                    systemBrightness = true;
                }else{
                    systemBrightness = false;
                }
                switches();
            }
        });

        mSensorManager = (SensorManager)this.getSystemService(Context.SENSOR_SERVICE);

        if(mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)!= null){
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            isSensorPresent = true;
        }else{
            isSensorPresent = false;
        }


        initCameraFlashLight();
    }

    public void switches(){
        if(systemBrightness){
            Toast.makeText(this, "Changed to system brightness", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Changed to screen brightness", Toast.LENGTH_SHORT).show();
        }

    }

    private void initCameraFlashLight() {
        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            mCameraID = mCameraManager.getCameraIdList()[0];
            mParameters = mCameraManager.getCameraCharacteristics(mCameraID);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void turnTorchLightOn(){
//        if(mParameters.get(CameraCharacteristics.FLASH_INFO_AVAILABLE)){
//            try{
//                mCameraManager.setTorchMode(mCameraID, true);
//            }catch(CameraAccessException e){
//                e.printStackTrace();
//            }
//            isFlashLightOn = true;
//        }
    }

    public void turnTorchLightOff(){
//        try{
////            mCameraManager.setTorchMode(mCameraID, false);
//        }catch(CameraAccessException e){
//            e.printStackTrace();
//        }
//        isFlashLightOn = false;
    }

    public void selectBrightness(View view){
        boolean checked = ((RadioButton) view).isChecked();
        String temp = null;
        switch (view.getId()){
            case R.id.rbVeryLow:
                temp = "Very low";
                break;
            case R.id.rbLow:
                temp = "low";
                break;
            case R.id.rbMedium:
                temp = "medium";
                break;
            case R.id.rbHigh:
                temp = "high";
                break;
            case R.id.rbVeryHigh:
                temp = "Very high";
                break;
            default:
                break;
        }
        Toast.makeText(this, "You selected "+temp, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isSensorPresent) {
            mSensorManager.registerListener(this, mSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if(isSensorPresent) {
            mSensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        distanceFromPhone = event.values[0];
        if(distanceFromPhone < mSensor.getMaximumRange()){
            if(!isFlashLightOn){
                turnTorchLightOn();
            }else{
                turnTorchLightOff();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
