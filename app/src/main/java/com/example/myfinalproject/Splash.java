package com.example.myfinalproject;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Thread mSplashThread = new Thread() {
            @Override
            public void run() {
                try {
                    synchronized(this) {
                        wait(3000);
                    }
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }

                finish();

                Intent intent = new Intent(Splash.this, MainActivity.class);
                startActivity(intent);
            }
        };

        mSplashThread.start();
    }
}