package helloandroid.ut3.minijeu;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private boolean areFliesActive = true;
    private final GameThread thread;
    private int score = 0;
    MediaPlayer mediaPlayer;
    int MAX_FLY = 5;

    private final ArrayList<FlyType> flyTypes = new ArrayList(Arrays.asList(
            new FlyType(R.drawable.fly,3,1,75, R.raw.fly_hit),
            new FlyType(R.drawable.fly2,5,3,100, R.raw.fly_hit),
            new FlyType(R.drawable.fly3, 10, 5, 50, R.raw.fly_hit),
            new FlyType(R.drawable.guepe, 20, -10, 75, R.raw.bee_hit)
    ));
    private final Map<Integer,Bitmap> TypeImg =new HashMap<Integer,Bitmap>() {
        {
            put(R.drawable.fly,BitmapFactory.decodeResource(getResources(), R.drawable.fly));
            put(R.drawable.fly2,BitmapFactory.decodeResource(getResources(), R.drawable.fly2));
            put(R.drawable.fly3,BitmapFactory.decodeResource(getResources(), R.drawable.fly3));
            put(R.drawable.guepe,BitmapFactory.decodeResource(getResources(), R.drawable.guepe));
            put(R.drawable.maya,BitmapFactory.decodeResource(getResources(), R.drawable.maya));
        }
    };

    ArrayList<Fly> Flys;
    TextView viewScore;

    public GameView(Context context, TextView viewScore) {
        super(context);
        this.viewScore = viewScore;
        getHolder().addCallback(this);
        thread = new GameThread(getHolder(), this);
        setFocusable(true);

        Flys= new ArrayList<>();
        for (int i = 0; i < MAX_FLY; i++) {
            FlyType myFlyType = getRandomFlyType();
            Flys.add(new Fly(myFlyType,getContext()));
        }

        setOnTouchListener(touchListener);
    }

    public void stopThread() {
        thread.setRunning(false);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {

        setOnTouchListener((v, event) -> {
            performClick();

            if (event.getAction() == MotionEvent.ACTION_DOWN) {

                int x = (int) event.getX();
                int y = (int) event.getY();
                for (int i = 0; i < Flys.size(); i++) {
                    Fly myFly = Flys.get(i);
                    if (myFly.isPointInsideSquare(x,y)) {
                        // do something when the fly is touched
                        Flys.remove(i);
                        score += myFly.getScore();
                        // TODO : son
                        mediaPlayer = MediaPlayer.create(this.getContext(), myFly.getSound());
                        mediaPlayer.start();
                        break;
                    }
                }
            }
            return true;
        });

        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
                thread.setRunning(false);
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            retry = false;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (canvas != null) {
            canvas.drawColor(Color.WHITE);
            Paint paint = new Paint();
            paint.setColor(Color.rgb(250, 0, 0));
            for (int i = 0; i <Flys.size() ; i++) {
                Fly myFly = Flys.get(i);
                int flyRadius = (int) (myFly.getRadius());
                Matrix matrix = new Matrix();
                matrix.postRotate(myFly.getAngleInDegrees());

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(TypeImg.get(myFly.getImage()), flyRadius, flyRadius, true);
                Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
                //Log.d("TAG", "draw: " + myFly.getTimer()/5);
                paint.setAlpha((int)(255*myFly.getTimer()/5));
                canvas.drawBitmap(rotatedBitmap, myFly.getPositionX(), myFly.getPositionY(), paint);
            }
        }
    }

    View.OnTouchListener touchListener = (v, event) -> {
        float x = event.getX();
        float y = event.getY();

        performClick();
        return true;
    };

    public void update() {
        if (areFliesActive) {
            for (int i = 0; i < Flys.size(); i++) {
                Fly myFly = Flys.get(i);
                myFly.updatePosition();
            }
        }
    }

    public void wakeUpFlies() {
        areFliesActive = true;
    }

    public void updateScore() {
        int size= Flys.size();
            for (int i = 0; i <MAX_FLY-size ; i++) {

                FlyType flyTypes = getRandomFlyType();
                Flys.add(new Fly(flyTypes,getContext()));
        }
        viewScore.setText(String.valueOf(score));
    }

    public void stopFlies() {
        areFliesActive = false;
        for (Fly f :Flys) {
            f.resetSpeed();
        }
    }

    public void speedUpFlies() {
        for(Fly fly : Flys){
            fly.speedUp();
        }
    }

    public void checkStatus(){
        for (int i = 0; i < Flys.size() ; i++) {
            Fly f = Flys.get(i);
            if(f.updateLocalTimer()<=0){
                Flys.remove(i);
            }
        }
    }

    public FlyType getRandomFlyType() {
        Random rand = new Random();
        int index = rand.nextInt(flyTypes.size());
        return flyTypes.get(index);
    }

    public void spawnMaya() {
        mediaPlayer = MediaPlayer.create(this.getContext(), R.raw.oh_my_god);
        mediaPlayer.start();
        Flys.add(new Fly(new FlyType(R.drawable.maya,10,-50,300,R.raw.bee_hit), getContext()));
    }

    public int getScore() {
        return score;
    }

    public void addFlies() {
        int size= Flys.size();
        for (int i = 0; i < MAX_FLY -size ; i++) {
            FlyType flyTypes = getRandomFlyType();
            Flys.add(new Fly(flyTypes,getContext()));
        }
    }
}
