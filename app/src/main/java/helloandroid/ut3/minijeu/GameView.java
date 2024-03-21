package helloandroid.ut3.minijeu;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

public class GameView extends SurfaceView implements
        SurfaceHolder.Callback {
    private GameThread thread;
    private int x=0;
    private int y =0;
    public GameView(Context context) {
        super(context);
        getHolder().addCallback(this);
        thread = new GameThread(getHolder(), this);
        setFocusable(true);
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

            canvas.drawRect(x, getPreferences(getContext(),"valeur_y"), x+100, 200, paint);
        }
    }

    public void update() {
        x = (x + 1) % 300;
    }
    public static int getPreferences(Context ctx,String name){

        SharedPreferences a = ctx.getSharedPreferences("valeur_y",0);
        int res = a.getInt("valeur_y",0);
        int new_valeur_y = (res + 100) % 400;
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("valeur_y", new_valeur_y);
        editor.apply();

        return res;
    }
}
