package helloandroid.ut3.minijeu;

import android.content.Context;
import android.content.SharedPreferences;

public class Fly {


    public int getPositionX() {
        return positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    public float getRadius() {
        return radius;
    }
    private int positionX;

    private int positionY;
    private float radius;

    private Context context;

    private int ballSpeed;

    private int ballDirectionX=1;

    private int ballDirectionY=1;

    private Vector2D direction;

    public Fly(float radius, Context context) {
        this.context=context;
        getRandPosition();
        this.radius = radius;
        this.ballSpeed=3;
        this.direction = getRandomDirection();
    }

public void getRandPosition(){
    SharedPreferences sharedPref = this.context.getSharedPreferences("sharedFile",Context.MODE_PRIVATE);
    int screenWidth = sharedPref.getInt("screenWidth",0);
    int screenHeight = sharedPref.getInt("screenHeight",0);
    this.positionX  =  (int) (Math.random() * (screenWidth - 0));
    this.positionY = (int) (Math.random() * (screenHeight - 0));
}
public boolean isOutsideScreen(){
    SharedPreferences sharedPref = this.context.getSharedPreferences("sharedFile",Context.MODE_PRIVATE);
    int screenWidth = sharedPref.getInt("screenWidth",0);
    int screenHeight = sharedPref.getInt("screenHeight",0);
        if(this.positionX<0 || this.positionY<0){
            return true;
        }
        if(this.positionX>screenWidth || this.positionY>screenHeight){
            return true;
        }
        return false;
}
public void updatePosition() {
    SharedPreferences sharedPref = this.context.getSharedPreferences("sharedFile", Context.MODE_PRIVATE);
    int screenWidth = sharedPref.getInt("screenWidth", 0);
    int screenHeight = sharedPref.getInt("screenHeight", 0);

    if (this.positionX > screenWidth - this.radius || this.positionX < this.radius) {
        this.direction.x *= -1;
    }

    if (this.positionY > screenHeight - this.radius || this.positionY < this.radius) {
        this.direction.y *= -1;
    }
    this.ballSpeed += 0.1;

    this.positionX = (int) (this.positionX + ballSpeed * direction.x);
    this.positionY = (int) (this.positionY + ballSpeed * direction.y);
}
    private Vector2D getRandomDirection() {
        double angle = Math.random() * 2 * Math.PI;
        float x = (float) Math.cos(angle);
        float y = (float) Math.sin(angle);
        return new Vector2D(x, y);
    }


}
