package helloandroid.ut3.minijeu;

import static java.lang.Thread.sleep;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends Activity implements SensorEventListener {

    private GameView gameView;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor lightSensor;
    private Timer gameTimer;
    private TimerTask gameTimerTask;
    private double remainingTimeGame = 20;

    private float acceleration = 0.0f;
    private float currentAcceleration = 0.0f;
    private float lastAcceleration = 0.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPref = this.getSharedPreferences("sharedFile", Context.MODE_PRIVATE);
        setPreferenceWidthAndHeight(sharedPref);

        gameTimer = new Timer();
        gameTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (remainingTimeGame > 0.25) {
                    // Update the timer TextView
                    TextView timerTextView = findViewById(R.id.text_view_timer_placeholder);
                    timerTextView.setText(String.valueOf((int) (remainingTimeGame + 0.99)));

                    gameView.checkStatus();
                    Log.d("TAG", "" + remainingTimeGame);
                    if (remainingTimeGame < 10.1 && remainingTimeGame > 9.9) {
                        gameView.spawnMaya();
                    }
                    gameView.speedUpFlies();

                    remainingTimeGame -= 0.2;
                } else {
                    // Stop the game and go to the score activity
                    try {
                        stopGameAndGoToScoreActivity();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };
        setContentView(R.layout.activity_game);

        gameView = new GameView(this, findViewById(R.id.text_view_score_placeholder));
        ConstraintLayout constraintLayout = findViewById(R.id.layout_game_view);
        constraintLayout.addView(gameView);

        activateSensors();

        // Schedule the timer to execute the TimerTask after 60 seconds
        gameTimer.schedule(gameTimerTask, 0,200);

    }

    private void activateSensors() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
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
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            lastAcceleration = currentAcceleration;
            currentAcceleration = (float) Math.sqrt((double) (x * x + y * y + z * z));
            float deltaAcceleration = currentAcceleration - lastAcceleration;
            acceleration = acceleration * 0.9f + deltaAcceleration;

            if (acceleration > 12) {
                wakeUpFlies();
            }
        }

        else if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            float lightValue = event.values[0];
            if (lightValue < 10) {
                stopFlies();
            }
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
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);

        if (gameTimer != null) {
            gameTimer.cancel();
            gameTimer = null;
        }
    }


    private void wakeUpFlies() {
        gameView.wakeUpFlies();
    }
    private void stopFlies() { gameView.stopFlies(); }

    private void stopGameAndGoToScoreActivity() throws InterruptedException {
        // Stop the game and save the score if necessary
        gameTimer.cancel();
        gameView.stopThread();
        sleep(100);

        // Start the score activity
        Intent intent = new Intent(this, ScoreActivity.class);
        intent.putExtra("Score", gameView.getScore());
        startActivity(intent);
    }


}
