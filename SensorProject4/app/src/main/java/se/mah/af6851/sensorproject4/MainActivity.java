package se.mah.af6851.sensorproject4;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private String loginName, loginPassword;
    private SensorManager mSensorManager;
    private Sensor mAccelerometerSensor;
    private Sensor mMagnetometerSensor;
    private Sensor mOrientationSensor;

    private int stepCounts = 0;
    //booleans for sensors
    private boolean isOrientationSensorPresent = false;
    private boolean isAccelerometerSensorPresent = false;
    private boolean isMagnetometerSensorPresent = false;
//variables
    private long lastUpdateTime = 0;
    private float mCurrentDegree = 0;
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;

    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private float[] mRotationMatrix = new float[9];
    private float[] mOrientation = new float[9];
    private boolean isFirstValue = false;

    private float x = 0, y = 0, z = 0;
    private float last_x = 0, last_y = 0, last_z = 0;
    private int shakeThreshold = 5;

    private ImageView mCompass;
    private TextView tvSteps;
    private TextView tvLoggedIn;
    private TextView tvStepsPerS;
    private boolean useOrientationAPI = true;
    private MyDBHandler dbHandler;
    private Button btnResetSteps;
    private UserInfo userInfo;

    private MyServiceConnection mConnection;
    private Intent stepsIntent;
    public boolean mBound = false;
    public StepsService mService;

    private boolean isFirstTime = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHandler = new MyDBHandler(this, null, null, 1);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        tvSteps = (TextView)findViewById(R.id.tvSteps);
        tvStepsPerS = (TextView)findViewById(R.id.tvStepsPerS);
        Bundle loginData = getIntent().getExtras();
        loginName =  loginData.getString("LoginName");
        loginPassword =  loginData.getString("LoginPassword");
        tvLoggedIn = (TextView)findViewById(R.id.tvLoggedIn);
        tvLoggedIn.setText("Logged in as: "+loginName);


        mConnection = new MyServiceConnection(this);
        stepsIntent = new Intent(this, StepsService.class);
        bindService(stepsIntent, mConnection, Context.BIND_AUTO_CREATE);
        Log.v("Pedometer", "service bound");
        Toast.makeText(getApplicationContext(), "Service bound" , Toast.LENGTH_SHORT).show();

        if(mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!= null) {
            mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            isAccelerometerSensorPresent = true;
        }else{
            Toast.makeText(this, "Accelerometer Sensor missing" , Toast.LENGTH_SHORT).show();
        }
        if(mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)!= null) {
            mMagnetometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            isMagnetometerSensorPresent = true;
        }else{
            Toast.makeText(this, "Magnetometer Sensor missing" , Toast.LENGTH_SHORT).show();
        }
        if(mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION) != null){
            mOrientationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
            isOrientationSensorPresent = true;
        }else{
            Toast.makeText(this, "Orientation Sensor missing" , Toast.LENGTH_SHORT).show();
        }
        mCompass = (ImageView) findViewById(R.id.compass_image);
        lastUpdateTime = System.currentTimeMillis();

        tvSteps.setText("Steps: " +stepCounts);
        tvStepsPerS.setText("Steps per second: 0.00");
        btnResetSteps = (Button)findViewById(R.id.btnResetSteps);
        btnResetSteps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbHandler.resetSteps(loginName);
                dbHandler.addFirstTime(loginName, 0);
                tvSteps.setText("Steps taken: 0" );
                tvStepsPerS.setText("Steps per second: 0.00");
            }
        });

    }

    public String getName(){
        return loginName;
    }

    public void updateSteps(long timeStamp){
        int steps = dbHandler.databaseGetSteps(loginName);
        tvSteps.setText("Steps taken: "+steps);
        long timeSec = timeStamp - dbHandler.databaseStartTime(loginName);
        double stepsPerS = 0;
        if(timeSec != 0){
            double timer = timeSec / 1000000000;
            if(timer > 0) {
                stepsPerS = (steps) / (timer);
                System.out.println("Start time: " + timeSec + "Timestamp " + mService.getTimeStamp());
            }else{
                stepsPerS = steps;
            }
        } else {
            stepsPerS = steps;
        }
        if(dbHandler.databaseGetSteps(loginName)==1){
           tvStepsPerS.setText("Steps per second: 1.00");
        }else{
            tvStepsPerS.setText("Steps per second: " + String.format("%.2f", stepsPerS) + " S/S");
        }
    }
    @Override
    public void onBackPressed() {
    }

    public void rotateUsingOrientationAPI(SensorEvent event) {

        if (event.sensor == mAccelerometerSensor) {
            System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
            mLastAccelerometerSet = true;
        } else if (event.sensor == mMagnetometerSensor) {
            System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
            mLastMagnetometerSet = true;
        }
        if (mLastAccelerometerSet && mLastMagnetometerSet && System.currentTimeMillis() - lastUpdateTime > 250) {
            SensorManager.getRotationMatrix(mRotationMatrix, null,
                    mLastAccelerometer, mLastMagnetometer);
            SensorManager.getOrientation(mRotationMatrix, mOrientation);
            float azimuthInRadians = mOrientation[0];
            float azimuthInDegress = (float) (Math.toDegrees(azimuthInRadians) + 360) % 360;
            RotateAnimation mRotateAnimation = new RotateAnimation(
                    mCurrentDegree, -azimuthInDegress,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            mRotateAnimation.setDuration(250);
            mRotateAnimation.setFillAfter(true);
            mCompass.startAnimation(mRotateAnimation);
            mCurrentDegree = -azimuthInDegress;
            lastUpdateTime = System.currentTimeMillis();

//        }
        }
    }
    public void rotateUsingOrientationSensor(SensorEvent event) {
//only 4 times in 1 second
        if (System.currentTimeMillis() - lastUpdateTime > 250) {
            float angleInDegress = event.values[0];
            RotateAnimation mRotateAnimation = new RotateAnimation(
                    mCurrentDegree, -angleInDegress,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
//250 milliseconds
            mRotateAnimation.setDuration(250);
            mRotateAnimation.setFillAfter(true);
            mCompass.startAnimation(mRotateAnimation);
            mCurrentDegree = -angleInDegress;
            lastUpdateTime = System.currentTimeMillis();
        }
    }


    protected void onResume(){
        super.onResume();
        if (useOrientationAPI) {
            if(isAccelerometerSensorPresent && isMagnetometerSensorPresent) {
                mSensorManager.registerListener(this, mAccelerometerSensor, SensorManager.SENSOR_DELAY_UI);
                mSensorManager.registerListener(this, mMagnetometerSensor, SensorManager.SENSOR_DELAY_UI);
            }
        } else {
            if(isAccelerometerSensorPresent && isOrientationSensorPresent) {
                mSensorManager.registerListener(this, mAccelerometerSensor, SensorManager.SENSOR_DELAY_UI);
                mSensorManager.registerListener(this, mOrientationSensor, SensorManager.SENSOR_DELAY_UI);
            }
        }
        this.isFirstValue = false;

    }

    protected void onPause(){
        super.onPause();
        if (useOrientationAPI) {
            if(isAccelerometerSensorPresent && isMagnetometerSensorPresent) {
                mSensorManager.unregisterListener(this, mAccelerometerSensor);
                mSensorManager.unregisterListener(this, mMagnetometerSensor);
            }
        } else {
            if(isAccelerometerSensorPresent && isOrientationSensorPresent) {
                mSensorManager.unregisterListener(this, mAccelerometerSensor);
                mSensorManager.unregisterListener(this, mOrientationSensor);
            }
        }
        Toast.makeText(this, "Sensor(s) unregistered." , Toast.LENGTH_SHORT).show();
    }

    protected void onDestroy(){
        super.onDestroy();

        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
        Log.v("Pedometer", "service unbound");
        Toast.makeText(getApplicationContext(), "Service unbound" , Toast.LENGTH_SHORT).show();
        mSensorManager.unregisterListener(this);
        mSensorManager = null;
        mAccelerometerSensor = null;
        mMagnetometerSensor = null;
        mOrientationSensor = null;
        Toast.makeText(this, "Sensor(s) unregistered." , Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
         if(event.sensor.getType() == (event.sensor.TYPE_MAGNETIC_FIELD)) {
             rotateUsingOrientationAPI(event);
         } else if(event.sensor.getType() == (event.sensor.TYPE_ORIENTATION)){
             rotateUsingOrientationSensor(event);
         } else if(event.sensor.getType() == (event.sensor.TYPE_ACCELEROMETER)) {

                 x = event.values[0];
                 y = event.values[1];
                 z = event.values[2];
                 if (isFirstValue) {
                     float deltaX = Math.abs(last_x - x);
                     float deltaY = Math.abs(last_y - y);
                     float deltaZ = Math.abs(last_z - z);
                     if ((deltaX > shakeThreshold && deltaY > shakeThreshold)
                             || (deltaX > shakeThreshold && deltaZ > shakeThreshold)
                             || (deltaY > shakeThreshold && deltaZ > shakeThreshold)) {
                         //What the thing should do
                         Random rand = new Random();
                         int randomDegrees = rand.nextInt(720) - 360;
                         RotateAnimation mRotateAnimation = new RotateAnimation(
                                 mCurrentDegree, randomDegrees,
                                 Animation.RELATIVE_TO_SELF, 0.5f,
                                 Animation.RELATIVE_TO_SELF, 0.5f);
                         mRotateAnimation.setDuration(250);
                         mRotateAnimation.setFillAfter(true);
                         mCompass.startAnimation(mRotateAnimation);
                     }
                 }
                 last_x = x;
                 last_y = y;
                 last_z = z;
                 isFirstValue = true;
             if(useOrientationAPI){
                 rotateUsingOrientationAPI(event);
             }
         }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
