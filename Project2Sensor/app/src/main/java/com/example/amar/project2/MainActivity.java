package com.example.amar.project2;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Activity that register/ungerigster all the sensors. Sends the needed information to front fragment
 * for displaying it.
 */
public class MainActivity extends Activity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mTemperatureSensor;
    private Sensor mHumiditySensor;
    private Sensor mPressureSensor;
    private FragmentManager fm;
    private FrontFragment frontFragment;
    private boolean sensorOn = false;
    private float pressure = 0, temp = 0, humidity = 0;
    private boolean isTemperaturePresent = false, isPressurePresent = false, isHumiditySensorPresent = false;
    private boolean async = false, volley = false;
    private float altitude = 0;

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fm = getFragmentManager();
        frontFragment = new FrontFragment();
        mSensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);


        initiateSensors();
        setFragment(frontFragment, false);

    }

    public void onBackPressed() {
        if (sensorOn) {
            unregisterListener();
        }
        setFragment(frontFragment, false);
    }

    /**
     * @param volleyOrAsync if volley = 2 if async = 1
     */
    public void registerListener(int volleyOrAsync) {
        //async = 1 volley = 2;
        if (volleyOrAsync == 1) {
            volley = false;
            async = true;
        } else if (volleyOrAsync == 2) {
            async = false;
            volley = true;

        }
        mSensorManager.registerListener(this, mPressureSensor, mSensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mHumiditySensor, mSensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mTemperatureSensor, mSensorManager.SENSOR_DELAY_NORMAL);
        Toast.makeText(this, "Sensor Registered", Toast.LENGTH_SHORT).show();
        sensorOn = true;

    }

    public void unregisterListener() {

        mSensorManager.unregisterListener(this);
        Toast.makeText(this, "Sensor unRegistered", Toast.LENGTH_SHORT).show();
        sensorOn = false;

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
        Toast.makeText(this, "Sensor unRegistered", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSensorManager.unregisterListener(this);
        Toast.makeText(this, "Sensor unRegistered", Toast.LENGTH_SHORT).show();
    }

    /**
     * @param event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;

        if (sensor.getType() == Sensor.TYPE_PRESSURE) {
            pressure = event.values[0];
        } else if (sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            temp = event.values[0];
        } else if (sensor.getType() == Sensor.TYPE_RELATIVE_HUMIDITY) {
            humidity = event.values[0];
        }
//        double value = Double.parseDouble(String.valueOf(event.timestamp));

//        String timestamp = ""+value/1000000000.0;


        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");
        dateFormat.format(new Date(event.timestamp / 1000000));
        String timestamp = "" + dateFormat.format(new Date(event.timestamp / 1000000));
        if (async) {
            frontFragment.sensorDataAsync(temp, pressure, humidity, altitude, timestamp);
        } else if (volley) {
            frontFragment.sensorDataVolley(temp, pressure, humidity, altitude, timestamp);
        }
//        float altitude = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, pressure);

    }

    /**
     * @param apiPressure retrieves the api pressure to calculate the altitude
     */
    public void setAltitude(String apiPressure) {
        float p0 = SensorManager.PRESSURE_STANDARD_ATMOSPHERE;
        float p1 = Float.parseFloat(apiPressure);

        altitude = SensorManager.getAltitude(p0, p1);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * @param frag      the frag to change to
     * @param backstack aviability to go back yes or no?
     */
    public void setFragment(Fragment frag, boolean backstack) {
        FragmentTransaction fs = fm.beginTransaction();
        fs.replace(R.id.fl, frag);
        if (backstack) {
            fs.addToBackStack(null);
        }
        fs.commit();
        fm.executePendingTransactions();
    }

    public void initiateSensors() {
        //        Temperature SENSOR
        if (mSensorManager.getDefaultSensor
                (Sensor.TYPE_AMBIENT_TEMPERATURE) != null) {
            mTemperatureSensor = mSensorManager.getDefaultSensor
                    (Sensor.TYPE_AMBIENT_TEMPERATURE);
            isTemperaturePresent = true;
        } else {
            Toast.makeText(this, "Ambient Temperature Sensor is not available!", Toast.LENGTH_SHORT).show();
            isTemperaturePresent = false;
        }
//        Pressure SENSOR
        if (mSensorManager.getDefaultSensor(
                Sensor.TYPE_PRESSURE) != null) {
            mPressureSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
            isPressurePresent = true;
        } else {
            Toast.makeText(this, "Pressure Sensor is not available!", Toast.LENGTH_SHORT).show();
            isPressurePresent = false;
        }
//        Humidity SENSOR
        if (mSensorManager.getDefaultSensor
                (Sensor.TYPE_RELATIVE_HUMIDITY) != null) {
            mHumiditySensor = mSensorManager.getDefaultSensor
                    (Sensor.TYPE_RELATIVE_HUMIDITY);
            isHumiditySensorPresent = true;
        } else {
            Toast.makeText(this, "Relative Humidity Sensor is not available !", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "Cannot calculate Absolute Humidity, as relative humidity sensor is not available !", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "Cannot calculate Dew Point, as relative humidity sensor is not available!", Toast.LENGTH_SHORT).show();
            isHumiditySensorPresent = false;
        }
    }
}
