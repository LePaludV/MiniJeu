package helloandroid.ut3.minijeu;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

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
            paint.setColor(Color.rgb(250, 0, 0));

            canvas.drawCircle(ballX, ballY, ballRadius, paint);
        }
    }

    public void update() {
        ballX = (ballX + (int)ballSpeed*ballDirectionX);
        ballY = (ballY + (int)ballSpeed*ballDirectionY);

        if(ballX > screenWidth-ballRadius || ballX < ballRadius) {
            ballDirectionX *= -1;
        }

        if(ballY > screenHeight-ballRadius || ballY < ballRadius) {
            ballDirectionY *= -1;
        }

        ballSpeed += 0.1;
    }
}
