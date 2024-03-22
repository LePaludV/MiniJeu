package helloandroid.ut3.minijeu;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

public class GameActivity extends Activity {

    private GameView gameView;
    private SensorManager sensorManager;
    private Sensor sensorAccelerometer;
    private ShakeDetector shakeDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        shakeDetector = new ShakeDetector(this);
        shakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {
            @Override
            public void onShake() {
                Log.d("GROS TAG", "JE SECOUE");
                gameView.wakeUpFlies();
            }
        });

        if (sensorAccelerometer != null) {
            sensorManager.registerListener(shakeDetector, sensorAccelerometer, SensorManager.SENSOR_DELAY_UI);
        } else {
            Log.e("GameActivity", "Capteur d'accélération non disponible");
        }


        SharedPreferences sharedPref = getSharedPreferences("my_preferences", MODE_PRIVATE);
        gameView = new GameView(this, sharedPref);
        setContentView(gameView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(shakeDetector, sensorAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(shakeDetector);
    }

}