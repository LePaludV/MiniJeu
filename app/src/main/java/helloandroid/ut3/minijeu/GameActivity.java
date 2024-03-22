package helloandroid.ut3.minijeu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameActivity extends Activity implements SensorEventListener {
    private GameView gameView;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private float acceleration = 0.0f;
    private float currentAcceleration = 0.0f;
    private float lastAcceleration = 0.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPref = this.getSharedPreferences("sharedFile", Context.MODE_PRIVATE);
        setPreferenceWidthAndHeight(sharedPref);

        setContentView(R.layout.activity_game);

        gameView = new GameView(this, sharedPref, findViewById(R.id.text_view_score_placeholder));
        ConstraintLayout constraintLayout = findViewById(R.id.layout_game_view);
        constraintLayout.addView(gameView);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void setPreferenceWidthAndHeight(SharedPreferences sharedPref) {
        SharedPreferences.Editor editor = sharedPref.edit();
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        editor.putInt("screenWidth",  displayMetrics.widthPixels);
        editor.putInt("screenHeight",displayMetrics.heightPixels);
        editor.apply();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        lastAcceleration = currentAcceleration;
        currentAcceleration = (float) Math.sqrt((x * x + y * y + z * z));
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
