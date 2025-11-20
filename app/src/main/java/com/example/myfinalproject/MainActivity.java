package com.example.myfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    View btnSignUp;
    View btnLogin;
    View btnAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        btnSignUp = findViewById(R.id.btnSignUp);
        btnLogin = findViewById(R.id.btnLogin);
        btnAbout = findViewById(R.id.btnAbout);

        btnSignUp.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        btnAbout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btnSignUp) {

            Intent intent = new Intent(MainActivity.this, register.class);
            startActivity(intent);
        } else if (id == R.id.btnLogin) {

        } else if (id == R.id.btnAbout) {

            Intent intent = new Intent(MainActivity.this, MainActivity2.class);
            startActivity(intent);
        }
    }
}
