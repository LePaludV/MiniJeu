package helloandroid.ut3.minijeu;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameActivity extends Activity implements SensorEventListener {
    private SharedPreferences sharedPref;

    private GameView gameView;
    private SensorManager sensorManager;
    private Sensor accelerometer;

    private float acceleration = 0.0f;
    private float currentAcceleration = 0.0f;
    private float lastAcceleration = 0.0f;
    private Random random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = getSharedPreferences("my_preferences", Context.MODE_PRIVATE);

        gameView = new GameView(this, sharedPref);
        setContentView(gameView);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        random = new Random();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        lastAcceleration = currentAcceleration;
        currentAcceleration = (float) Math.sqrt((double) (x * x + y * y + z * z));
        float deltaAcceleration = currentAcceleration - lastAcceleration;
        acceleration = acceleration * 0.9f + deltaAcceleration;

        if (acceleration > 12) {
            // Le téléphone a été secoué, réveillez les points noirs ici
            wakeUpFlies();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Pas utilisé dans cet exemple
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    private void wakeUpFlies() {
        gameView.wakeUpFlies();
    }
}
