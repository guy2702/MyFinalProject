package com.example.myfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Choise extends AppCompatActivity {

    private TextView tvWelcome;
    private Button btnMuscle, btnCut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choise);

        tvWelcome = findViewById(R.id.tvWelcome);
        btnMuscle = findViewById(R.id.btnMuscle);
        btnCut = findViewById(R.id.btnCut);

        // קבלת שם המשתמש מה-Intent
        String userName = getIntent().getStringExtra("USER_NAME");
        if(userName != null && !userName.isEmpty()){
            tvWelcome.setText("שלום, " + userName + "!");
        } else {
            tvWelcome.setText("שלום!");
        }

        btnMuscle.setOnClickListener(v -> {
            Intent intent = new Intent(Choise.this, Size.class);
            intent.putExtra("USER_NAME", userName); // העברת שם המשתמש לעמוד הבא
            intent.putExtra("CHOICE", "מסה");       // אפשר להעביר גם את הבחירה
            startActivity(intent);
        });

        btnCut.setOnClickListener(v -> {
            Intent intent = new Intent(Choise.this, Size.class);
            intent.putExtra("USER_NAME", userName);
            intent.putExtra("CHOICE", "חיטוב");
            startActivity(intent);
        });
    }
}