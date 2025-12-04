package com.example.myfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class Splash extends AppCompatActivity {

    private ImageView smoothieImage;
    private int stageIndex = 0;

    // מערך התמונות של השייק בשלבים שונים
    private int[] smoothieStages = {
            R.drawable.smoothie1,
            R.drawable.smoothie2,
            R.drawable.smoothie3,
            R.drawable.smoothie4,
            R.drawable.smoothie5,
            R.drawable.smoothie6
    };

    private final int delayMillis = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        smoothieImage = findViewById(R.id.smoothieImage);

        startSmoothieAnimation();
    }

    private void startSmoothieAnimation() {
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if(stageIndex < smoothieStages.length){
                    // החלפת תמונה
                    smoothieImage.setImageResource(smoothieStages[stageIndex]);
                    stageIndex++;

                    // לחזור על זה אחרי שנייה
                    handler.postDelayed(this, delayMillis);
                } else {
                    // אחרי שהשייק התמלא – מעבר ל-MainActivity
                    Intent intent = new Intent(Splash.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        handler.post(runnable);
    }
}
