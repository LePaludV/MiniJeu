package helloandroid.ut3.minijeu;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {
    // AppCompactActivity : permet l'utilisation de fonctionnalité récente même avec les anciennes version d'android

    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPref = this.getSharedPreferences("sharedFile", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        editor.putInt("screenWidth",  displayMetrics.widthPixels);
        editor.putInt("screenHeight",displayMetrics.heightPixels);
        editor.apply();


        //supprime la bar et le titre
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //setContentView(R.layout.activity_main);
        setContentView(new GameView(this, sharedPref));
    }
}