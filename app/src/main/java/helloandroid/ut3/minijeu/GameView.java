package helloandroid.ut3.minijeu;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class GameView extends SurfaceView implements
        SurfaceHolder.Callback {
    private final GameThread thread;
    private SharedPreferences sharedPref;
    private int ballY=0;
    private int ballX=0;
    private float ballSpeed=1;
    private int ballDirectionX=1;
    private int ballDirectionY=1;
    private final int ballRadius=50;

    private int screenWidth;
    private int screenHeight;
    ArrayList<Fly> Flys;

    public GameView(Context context, SharedPreferences sharedPref) {
        super(context);
        this.sharedPref = sharedPref;
        getHolder().addCallback(this);
        thread = new GameThread(getHolder(), this);
        setFocusable(true);

        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        this.screenWidth = displayMetrics.widthPixels;
        this.screenHeight = displayMetrics.heightPixels;
        this.ballX=(int)screenWidth/2 - ballRadius/2;
        this.ballY=(int)screenHeight/2 - ballRadius/2;

        setOnTouchListener(touchListener);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        thread.setRunning(true);
        thread.start();
        Flys= new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Flys.add(new Fly(100,getContext()));
        }
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
            Bitmap flyImg= (BitmapFactory.decodeResource(getResources(), R.drawable.fly));
            paint.setColor(Color.rgb(250, 0, 0));
            for (int i = 0; i <Flys.size() ; i++) {
                Fly myFly = Flys.get(i);
                Matrix matrix = new Matrix();
                matrix.postRotate(i*10);
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(flyImg, 200, 200, true);
                Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
                Log.d("TAG", "draw: "+myFly);
                canvas.drawBitmap(rotatedBitmap,myFly.getPositionX(), myFly.getPositionY(), paint);
            }
        }
    }

    View.OnTouchListener touchListener = (v, event) -> {
        // Handle touch events here
        float x = event.getX();
        float y = event.getY();

        performClick();
        // Perform actions based on touch coordinates
        return true; // Return true to consume the event
    };

    public void update() {
        for (int i = 0; i <Flys.size() ; i++) {
            Fly myFly = Flys.get(i);
            myFly.updatePosition();
        }
    }
}
