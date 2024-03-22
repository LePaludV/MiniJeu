package helloandroid.ut3.minijeu;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Fly {


    public int getPositionX() {
        return positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    public int getRadius() {
        return radius;
    }

    private int positionX;

    private int positionY;
    private int radius;

    private Context context;

    private int ballSpeed;

    private int ballDirectionX = 1;

    private int ballDirectionY = 1;

    private Vector2D direction;

    private int defautSpeed;

    private int score;

    private int timer;

    public Fly(FlyType type, Context context) {
        this.context = context;
        this.defautSpeed = type.speed | 3;
        this.ballSpeed = defautSpeed;
        getRandPosition();
        this.radius = type.radius | 100;
        this.score = type.score;
        this.direction = getRandomDirection();
        this.timer = 5;
    }

    public void resetSpeed() {
        this.ballSpeed = this.defautSpeed;
    }

    public int getScore() {
        //TODO LE CALCUL
        return this.score;
    }

    public void getRandPosition() {
        SharedPreferences sharedPref = this.context.getSharedPreferences("sharedFile", Context.MODE_PRIVATE);
        int screenWidth = sharedPref.getInt("screenWidth", 0);
        int screenHeight = sharedPref.getInt("screenHeight", 0);
        this.positionX = (int) (Math.random() * (screenWidth - 0));
        this.positionY = (int) (Math.random() * (screenHeight - 0));
    }

    public boolean isOutsideScreen() {
        SharedPreferences sharedPref = this.context.getSharedPreferences("sharedFile", Context.MODE_PRIVATE);
        int screenWidth = sharedPref.getInt("screenWidth", 0);
        int screenHeight = sharedPref.getInt("screenHeight", 0);
        if (this.positionX < 0 || this.positionY < 0) {
            return true;
        }
        if (this.positionX > screenWidth || this.positionY > screenHeight) {
            return true;
        }
        return false;
    }

    public void updatePosition() {
        SharedPreferences sharedPref = this.context.getSharedPreferences("sharedFile", Context.MODE_PRIVATE);
        int screenWidth = sharedPref.getInt("screenWidth", 0);
        int screenHeight = sharedPref.getInt("screenHeight", 0);
        int top = this.getPositionY();
        int left = this.getPositionX();
        float dx = this.direction.x;
        float dy = this.direction.y;

        // Mise à jour de la position du carré
        left += dx * ballSpeed;
        top += dy * ballSpeed;
        int squareSize = this.getRadius();

        // Vérification des limites de l'écran et rebond du carré si nécessaire
        if (left < 0) {
            left = 0;
            this.direction.x = -dx;
        } else if (left + squareSize > screenWidth) {
            left = screenWidth - squareSize;
            this.direction.x = -dx;
        }

        if (top < 0) {
            top = 0;
            this.direction.y = -dy;
        } else if (top + squareSize > screenHeight) {
            top = screenHeight - squareSize;
            this.direction.y = -dy;
        }

        // Mise à jour du tableau de coordonnées du carré
        this.positionY = top;
        this.positionX = left;
    }

    private Vector2D getRandomDirection() {
        double angle = Math.random() * 2 * Math.PI;
        float x = (float) Math.cos(angle);
        float y = (float) Math.sin(angle);
        return new Vector2D(x, y);
    }

    public float getAngleInDegrees() {

        double angleInRadians = Math.atan2(this.direction.y, this.direction.x);
        double angleInDegrees = Math.toDegrees(angleInRadians) % 360;
        return (float)angleInDegrees+90;
    }

    public boolean isPointInsideSquare(int x, int y) {
        int left = this.positionX;
        int top = this.positionY;
        int bottom = top + this.radius;
        int right = left + this.radius;

        if (x >= left && x <= right && y >= top && y <= bottom) {
            return true;
        } else {
            return false;
        }
    }

    public void speedUp() {
        ballSpeed += 2.5;
    }


    public int updateLocalTimer() {
        this.timer -= 1;
        Log.d("TAG", "updateLocalTimer: "+this.timer);
        return timer;
    }
}
