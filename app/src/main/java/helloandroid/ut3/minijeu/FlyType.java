package helloandroid.ut3.minijeu;

import android.util.Log;

import java.util.Random;

public class FlyType {

    int imageName;
    int speed, score, radius, soundId;

    public FlyType(int imageName, int speed, int score, int radius, int soundId) {
        this.imageName = imageName;
        this.speed = speed;
        this.score = score;
        this.radius = radius;
        this.soundId = soundId;
    }
}