package helloandroid.ut3.minijeu;

import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private boolean areFliesActive = true;

    private final GameThread thread;
    private SharedPreferences sharedPref;

    private int screenWidth;
    private int screenHeight;
    ArrayList<Fly> Flys;

    public GameView(Context context, SharedPreferences sharedPref) {
        super(context);
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    int x = (int) event.getX();
                    int y = (int) event.getY();
                    for (int i = 0; i < Flys.size(); i++) {
                        Fly myFly = Flys.get(i);
                        if (myFly.isPointInsideSquare(x,y)) {
                            // do something when the fly is touched
                            Toast.makeText(context, "Mouche touchée !", Toast.LENGTH_SHORT).show();
                            Flys.remove(i);
                        }
                    }
                }
                return true;
            }
        });


        this.sharedPref = sharedPref;
        getHolder().addCallback(this);
        thread = new GameThread(getHolder(), this);
        setFocusable(true);

        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        this.screenWidth = displayMetrics.widthPixels;
        this.screenHeight = displayMetrics.heightPixels;

        Flys= new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Flys.add(new Fly(200,getContext()));
        }
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
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
            Bitmap flyImg= (BitmapFactory.decodeResource(getResources(), R.drawable.fly));
            paint.setColor(Color.rgb(250, 0, 0));
            for (int i = 0; i <Flys.size() ; i++) {
                Fly myFly = Flys.get(i);
                int flyRadius = myFly.getRadius();
                Matrix matrix = new Matrix();
                matrix.postRotate(myFly.getAngleInDegrees()+90);
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(flyImg, flyRadius, 200, true);
                Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
                Log.d("TAG", "draw: " + myFly);
                canvas.drawBitmap(rotatedBitmap, myFly.getPositionX(), myFly.getPositionY(), paint);
            }
        }
    }

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
}
