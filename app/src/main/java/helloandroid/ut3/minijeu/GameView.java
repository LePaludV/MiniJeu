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
import java.util.Random;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private boolean areFliesActive = true;

    private final GameThread thread;
    private int score = 0;
    private SharedPreferences sharedPref;

    private final ArrayList<FlyType> flyTypes = new ArrayList(Arrays.asList(
            new FlyType(R.drawable.fly,3,1,150),
            new FlyType(R.drawable.fly2,5,3,200),
            new FlyType(R.drawable.fly, 10, 5, 50),
            new FlyType(R.drawable.guepe, 20, -10, 100)
    ));
    private int screenWidth;
    private int screenHeight;
    ArrayList<Fly> Flys;
    TextView viewScore;
    private int MAX_FLY =10;
    public GameView(Context context, SharedPreferences sharedPref, TextView viewScore) {
        super(context);

        this.sharedPref = sharedPref;
        this.viewScore = viewScore;
        getHolder().addCallback(this);
        thread = new GameThread(getHolder(), this);
        setFocusable(true);

        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        this.screenWidth = displayMetrics.widthPixels;
        this.screenHeight = displayMetrics.heightPixels;

        Flys= new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            FlyType myFlyType = getRandomFlyType();
            Flys.add(new Fly(myFlyType,getContext()));
        }

        setOnTouchListener(touchListener);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("TAG1", "onTouch: ");
                performClick();

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.d("TAG", "onTouch: ");

                    int x = (int) event.getX();
                    int y = (int) event.getY();
                    for (int i = 0; i < Flys.size(); i++) {
                        Fly myFly = Flys.get(i);
                        if (myFly.isPointInsideSquare(x,y)) {
                            // do something when the fly is touched
                            Log.d("TAG", "onTouch: ");
                            Toast.makeText(getContext(), "Mouche touchée !", Toast.LENGTH_SHORT).show();
                            Flys.remove(i);
                            score += myFly.getScore();
                            break;
                        }
                    }
                }
                return true;
            }
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
                int flyRadius = myFly.getRadius();
                Matrix matrix = new Matrix();
                matrix.postRotate(myFly.getAngleInDegrees());
                Resources resources = getContext().getResources();
                Bitmap flyImg= (BitmapFactory.decodeResource(getResources(), myFly.getImage()));
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(flyImg, flyRadius, flyRadius, true);
                Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
               // Log.d("TAG", "draw: " + myFly);
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
            if(f.updateLocalTimer()==0){
                Flys.remove(i);
            }
        }

    }

    public FlyType getRandomFlyType() {
        Random rand = new Random();
        int index = rand.nextInt(flyTypes.size());
        return flyTypes.get(index);
    }

}
