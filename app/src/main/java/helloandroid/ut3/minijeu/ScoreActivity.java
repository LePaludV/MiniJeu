package helloandroid.ut3.minijeu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ScoreActivity extends AppCompatActivity {

    private Button buttonReplay;
    private Button buttonMainMenu;
    private TextView textViewFinalScore;
    private int score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        buttonReplay = findViewById(R.id.buttonReplay);
        buttonMainMenu = findViewById(R.id.buttonMainMenu);
        textViewFinalScore = findViewById(R.id.textViewFinalScore);

        Intent intent = getIntent();
        score = intent.getIntExtra("Score", 0);

        textViewFinalScore.setText(String.valueOf(score));

        buttonReplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScoreActivity.this, GameActivity.class);
                startActivity(intent);
            }
        });

        buttonMainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScoreActivity.this, MenuActivity.class);
                startActivity(intent);
            }
        });
    }
}
