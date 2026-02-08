package com.example.myfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Choise extends AppCompatActivity {

    private TextView tvWelcome;
    private RadioGroup rgGoal, rgSize; // rgGoal = מסה/חיטוב, rgSize = גודל כוס
    private Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choise);

        // התחברות ל־Views
        tvWelcome = findViewById(R.id.tvWelcome);
        rgGoal = findViewById(R.id.rgGoal);
        rgSize = findViewById(R.id.rgSize);
        btnNext = findViewById(R.id.btnNext);

        // קבלת שם המשתמש
        String userName = getIntent().getStringExtra("USER_NAME");
        if (userName != null && !userName.isEmpty()) {
            tvWelcome.setText("שלום, " + userName + "!");
        } else {
            tvWelcome.setText("שלום!");
        }

        btnNext.setOnClickListener(v -> {
            // בדיקה אם נבחרה מטרה
            int selectedGoalId = rgGoal.getCheckedRadioButtonId();
            if (selectedGoalId == -1) {
                Toast.makeText(this, "אנא בחר מסה או חיטוב", Toast.LENGTH_SHORT).show();
                return;
            }

            // בדיקה אם נבחר גודל כוס
            int selectedSizeId = rgSize.getCheckedRadioButtonId();
            if (selectedSizeId == -1) {
                Toast.makeText(this, "אנא בחר גודל כוס", Toast.LENGTH_SHORT).show();
                return;
            }

            // קבלת הערכים שנבחרו
            RadioButton selectedGoalRadio = findViewById(selectedGoalId);
            RadioButton selectedSizeRadio = findViewById(selectedSizeId);

            String choice = selectedGoalRadio.getText().toString(); // מסה/חיטוב
            String cupSize = selectedSizeRadio.getText().toString();  // 200/400/600 גרם

            // שליחת הנתונים ל-FruitsandVegtables
            Intent intent = new Intent(Choise.this, FruitsandVegtables.class);
            intent.putExtra("USER_NAME", userName);
            intent.putExtra("CHOICE", choice);
            intent.putExtra("CUP_SIZE", cupSize);
            startActivity(intent);
        });
    }
}