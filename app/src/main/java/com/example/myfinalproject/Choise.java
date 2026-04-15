package com.example.myfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Choise extends AppCompatActivity {

    private TextView tvWelcome;
    private RadioGroup rgGoal, rgSize;
    private Button btnNext, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choise);

        tvWelcome = findViewById(R.id.tvWelcome);
        rgGoal = findViewById(R.id.rgGoal);
        rgSize = findViewById(R.id.rgSize);
        btnNext = findViewById(R.id.btnNext);
        btnBack = findViewById(R.id.btnBack);

        String userName = getIntent().getStringExtra("USER_NAME");

        if (userName != null && !userName.isEmpty()) {
            tvWelcome.setText("שלום, " + userName + "!");
        } else {
            tvWelcome.setText("שלום!");
        }

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(Choise.this, UserHome.class);
            startActivity(intent);
            finish();
        });

        btnNext.setOnClickListener(v -> {
            int selectedGoalId = rgGoal.getCheckedRadioButtonId();
            int selectedSizeId = rgSize.getCheckedRadioButtonId();

            if (selectedGoalId == -1) {
                Toast.makeText(Choise.this, "אנא בחר מסה או חיטוב", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedSizeId == -1) {
                Toast.makeText(Choise.this, "אנא בחר גודל כוס", Toast.LENGTH_SHORT).show();
                return;
            }

            String goal;
            if (selectedGoalId == R.id.rbMuscle) {
                goal = "MUSCLE";
            } else {
                goal = "CUT";
            }

            int cupSize;
            if (selectedSizeId == R.id.rb200) {
                cupSize = 200;
            } else if (selectedSizeId == R.id.rb400) {
                cupSize = 400;
            } else {
                cupSize = 600;
            }

            Intent intent = new Intent(Choise.this, FruitsandVegtables.class);
            intent.putExtra("USER_NAME", userName);
            intent.putExtra("GOAL", goal);
            intent.putExtra("CUP_SIZE", cupSize);
            startActivity(intent);
        });
    }
}