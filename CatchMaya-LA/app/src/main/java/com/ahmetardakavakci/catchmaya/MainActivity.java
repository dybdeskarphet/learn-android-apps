package com.ahmetardakavakci.catchmaya;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    ImageView[] maya = new ImageView[9];
    TextView scoreText;
    TextView timeText;
    Runnable runnable;
    Handler handler;
    int score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        maya[0] = findViewById(R.id.maya1);
        maya[1] = findViewById(R.id.maya2);
        maya[2] = findViewById(R.id.maya3);
        maya[3] = findViewById(R.id.maya4);
        maya[4] = findViewById(R.id.maya5);
        maya[5] = findViewById(R.id.maya6);
        maya[6] = findViewById(R.id.maya7);
        maya[7] = findViewById(R.id.maya8);
        maya[8] = findViewById(R.id.maya9);

        scoreText = findViewById(R.id.scoreText);
        timeText = findViewById(R.id.timeText);

        score = 0;
        scoreText.setText("Score: " + score);

        randomizeMaya();
        timeLeft();

    }

    public void addScore(View view){
        score++;
        scoreText.setText("Score: " + score);
    }

    public void randomizeMaya(){

        Random rand = new Random();

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {

                int r = rand.nextInt(9);

                for (ImageView image : maya) {
                   image.setVisibility(View.INVISIBLE);
                }

                maya[r].setVisibility(View.VISIBLE);
                handler.postDelayed(runnable, 500);
            }
        };

        handler.post(runnable);

    }

    public void timeLeft(){

        AlertDialog.Builder finishAlert = new AlertDialog.Builder(this);
        finishAlert.setTitle("Congratulations!");
        finishAlert.setMessage("You finished the game. Do you want to play again?");
        finishAlert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });


        finishAlert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(MainActivity.this, "Game will not be restarted", Toast.LENGTH_SHORT).show();
            }
        });

        new CountDownTimer(10000,1000) {

            @Override
            public void onTick(long l) {
                timeText.setText("Time left: " + (l + 1000) / 1000);
            }

            @Override
            public void onFinish() {
                timeText.setText("Finished!");
                handler.removeCallbacks(runnable);
                finishAlert.show();
            }
        }.start();
    }

}