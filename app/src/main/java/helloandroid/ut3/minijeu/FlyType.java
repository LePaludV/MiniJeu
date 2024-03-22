package helloandroid.ut3.minijeu;

import java.util.Random;

public class FlyType {

    int imageName;
    int speed, score, radius, soundId;

    public FlyType(int imageName, int speed, int score, int radius, int soundId) {
        this.imageName = imageName;
        this.speed = speed * ((new Random()).nextInt(301)/100);
        this.score = score;
        this.radius = radius;
        this.soundId = soundId;
    }
}