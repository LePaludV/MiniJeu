package helloandroid.ut3.minijeu;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public class ShakeDetector implements SensorEventListener {
    private static final int SHAKE_THRESHOLD = 600;
    private static final int SHAKE_SLOP_TIME = 500;
    private static final int SHAKE_COUNT_RESET_TIME = 3000;

    private OnShakeListener onShakeListener;
    private long lastShakeTime;
    private int shakeCount;
    private GameActivity gameActivity;

    public ShakeDetector(GameActivity gameActivity) {
        lastShakeTime = System.currentTimeMillis();
        shakeCount = 0;
        this.gameActivity = gameActivity;
    }

    public void setOnShakeListener(OnShakeListener onShakeListener) {
        this.onShakeListener = onShakeListener;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long now = System.currentTimeMillis();
            if ((now - lastShakeTime) > SHAKE_COUNT_RESET_TIME) {
                shakeCount = 0;
            }

            float gX = event.values[0];
            float gY = event.values[1];
            float gZ = event.values[2];

            double gForce = Math.sqrt(gX * gX + gY * gY + gZ * gZ);

            if (gForce > SHAKE_THRESHOLD) {
                lastShakeTime = now;
                gameActivity.onResume();
                shakeCount++;

                if (shakeCount >= 3) {
                    shakeCount = 0;
                    if (onShakeListener != null) {
                        onShakeListener.onShake();
                    }
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public interface OnShakeListener {
        void onShake();
    }
}


