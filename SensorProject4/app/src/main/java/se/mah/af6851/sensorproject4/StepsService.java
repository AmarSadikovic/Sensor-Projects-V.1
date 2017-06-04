package se.mah.af6851.sensorproject4;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Amar on 2017-02-24.
 */

public class StepsService extends Service implements SensorEventListener {
    private MainActivity mainActivity;
    private SensorManager mSensorManager;
    private Sensor mStepDetectorSensor;
    private LocalBinder mBinder;
    private MyDBHandler dbHandler;
    private long timeStampSecond;
    private boolean isFirstStep = true;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        mSensorManager.registerListener(this, mStepDetectorSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
        return mBinder;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        dbHandler = new MyDBHandler(this, null, null, 1);
        mBinder = new LocalBinder();
        mSensorManager = (SensorManager)
                this.getSystemService(Context.SENSOR_SERVICE);
        if(mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null) {
            mStepDetectorSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        dbHandler.addSteps(mainActivity.getName());
        timeStampSecond = sensorEvent.timestamp;
        if(dbHandler.databaseStartTime(mainActivity.getName()) == 0){
                dbHandler.addFirstTime(mainActivity.getName(), timeStampSecond);
        }
        mainActivity.updateSteps(timeStampSecond);

    }
    public double getTimeStamp(){
        return timeStampSecond;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
    public class LocalBinder extends Binder {
        StepsService getService(){
            return StepsService.this;
        }
    }
    public void setListenerActivity(MainActivity activity){
        this.mainActivity = activity;
    }
}
