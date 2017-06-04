package se.mah.af6851.sensorlab1;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private TextView sensorGyroTv, typeTv, powerTv, mTime, mAccuracy, mEventValue_0, mEventValue_1, mEventValue_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorGyroTv = (TextView) findViewById(R.id.sensorGyroTv);
        typeTv = (TextView) findViewById(R.id.typeTv);
        powerTv = (TextView) findViewById(R.id.powerTv);
        mEventValue_0 = (TextView) findViewById(R.id.mEventValue_0);
        mEventValue_1 = (TextView) findViewById(R.id.mEventValue_1);
        mEventValue_2 = (TextView) findViewById(R.id.mEventValue_2);
        mAccuracy = (TextView) findViewById(R.id.mAccuracy);
        mTime = (TextView) findViewById(R.id.mTime);
        mSensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);

        if (mSensorManager.getDefaultSensor
                (Sensor.TYPE_GYROSCOPE) != null) {
            mSensor =
                    mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorGyroTv.setText("Gyro sensor exists!");
        Toast.makeText(this, "Sensor registred", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
        Toast.makeText(this, "Sensor unregistered", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSensorManager = null;
        mSensor = null;
        Toast.makeText(this, "Sensor unregistered", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        mEventValue_0.setText("Event value X: " + String.valueOf(event.values[0]));
        mEventValue_1.setText("Event value Y: " + String.valueOf(event.values[1]));
        mEventValue_2.setText("Event value Z: " + String.valueOf(event.values[2]));
        mAccuracy.setText("Accuracy: " + String.valueOf(event.accuracy));
        mTime.setText("Time: " + String.valueOf(event.timestamp));


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        typeTv.setText("Type: " + mSensor.getStringType());
        powerTv.setText("Power: " + mSensor.getPower());

    }
}
