package helloandroid.ut3.minijeu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MenuActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
    }

    public void startMainActivity(View view) {
        startActivity(new Intent(this, GameActivity.class));
    }

    public void startRulesActivity(View view) {
        startActivity(new Intent(this, RulesActivity.class));
    }

    public void startCreditsActivity(View view) {
        startActivity(new Intent(this, CreditsActivity.class));
    }

    public void exitGame(View view) {
        finish();
    }
}
