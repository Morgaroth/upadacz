package io.github.morgaroth.studia.semix.widzenie.upadek;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class MainActivity extends Activity implements SensorEventListener {

    final Logger log = Logger.getLogger(MainActivity.class.getSimpleName());

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private Sensor senMagnetic;
    private Sensor senGravity;

    List<Float> buff = new LinkedList<Float>();

    private float LOW_FILTER = 2f, DELTA = 0.4f, lx, ly, lz;


    private float[] magnetics = new float[3];
    private float[] gravities = new float[3];
    private TextView xCoordinateView;
    private TextView yCoordinateView;
    private TextView zCoordinateView;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        setupAccSensor();
        setupMagneticSensor();
        setupGravitySensor();

        xCoordinateView = (TextView) findViewById(R.id.XCoordinate);
        yCoordinateView = (TextView) findViewById(R.id.YCoordinate);
        zCoordinateView = (TextView) findViewById(R.id.ZCoordinate);

        Button button = (Button) findViewById(R.id.pullData);
        OnClickListener listener = new OnClickListener() {

            public void onClick(View v) {
                MainActivity.this.pullData();
            }
        };

        button.setOnClickListener(listener);
    }

    private void setupAccSensor() {
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
    }

    private void setupMagneticSensor() {
        senMagnetic = senSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        senSensorManager.registerListener(this, senMagnetic, SensorManager.SENSOR_DELAY_FASTEST);
    }

    private void setupGravitySensor() {
        senGravity = senSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        senSensorManager.registerListener(this, senGravity, SensorManager.SENSOR_DELAY_FASTEST);
    }


    public void onAccEvent(SensorEvent sensorEvent) {

        // przeksztalcanie macierzy na wspolrzedne swiata
        float[] rotation = new float[16], inv = new float[16];
        float[] relativacc = new float[4], e = new float[4];

        SensorManager.getRotationMatrix(rotation, null, gravities, magnetics);

        relativacc[0] = sensorEvent.values[0];
        relativacc[1] = sensorEvent.values[1];
        relativacc[2] = sensorEvent.values[2];
        relativacc[3] = 0;
        android.opengl.Matrix.invertM(inv, 0, rotation, 0);
        android.opengl.Matrix.multiplyMV(e, 0, inv, 0, relativacc, 0);
        xCoordinateView.setText(Double.toString(e[0]));
        yCoordinateView.setText(Double.toString(e[1]));
        zCoordinateView.setText(Double.toString(e[2]));

        if (Math.abs(e[0]) > LOW_FILTER || Math.abs(e[1]) > LOW_FILTER || Math.abs(e[2]) > LOW_FILTER) {
            log.info(String.format("%3.5f, %3.5f, %3.5f", e[0], e[1], e[2]));
            // przyspieszenie do ziemi jest w e[1] wektor jest skierowany w dół, więc spadanie jest dodatnie, podrzucanie ujemne
//            buff.add(e[2]);
        }

    }

    public void onGravChanged(SensorEvent sensorEvent) {
        gravities[0] = sensorEvent.values[0];
        gravities[0] = sensorEvent.values[1];
        gravities[0] = sensorEvent.values[2];
    }

    public void onMagnitudeEvent(SensorEvent sensorEvent) {
        magnetics[0] = sensorEvent.values[0];
        magnetics[1] = sensorEvent.values[1];
        magnetics[2] = sensorEvent.values[2];
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if (sensorEvent.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            onAccEvent(sensorEvent);
        } else if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            onMagnitudeEvent(sensorEvent);
        } else if (sensorEvent.sensor.getType() == Sensor.TYPE_GRAVITY) {
            onGravChanged(sensorEvent);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    protected void onPause() {
        super.onPause();
        senSensorManager.unregisterListener(this);
    }

    protected void onResume() {
        super.onResume();
        if (senAccelerometer != null) {
            senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        }
        if (senGravity != null) {
            senSensorManager.registerListener(this, senGravity, SensorManager.SENSOR_DELAY_FASTEST);
        }
        if (senMagnetic != null) {
            senSensorManager.registerListener(this, senMagnetic, SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    public void pullData() {
        buff.clear();
    }
}

