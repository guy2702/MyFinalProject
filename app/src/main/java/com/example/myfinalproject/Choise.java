package com.example.myfinalproject;

/**
 * מחלקה זו מנהלת את מסך הבחירה הראשוני (Choise Screen).
 * המסך מאפשר למשתמש לבחור את המטרה התזונתית שלו (מסה או חיטוב)
 * ואת גודל הכוס המועדף עליו, נתונים שישמשו לחישוב כמויות המרכיבים בשייק.
 */

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Choise extends AppCompatActivity {

    // רכיבי ממשק המשתמש (UI): כותרת, קבוצות בחירה (RadioGroup) וכפתורי ניווט
    private TextView tvWelcome;
    private RadioGroup rgGoal, rgSize;
    private Button btnNext, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_choise);

            // קישור רכיבי הממשק מה-XML למשתני המחלקה
            tvWelcome = findViewById(R.id.tvWelcome);
            rgGoal = findViewById(R.id.rgGoal);
            rgSize = findViewById(R.id.rgSize);
            btnNext = findViewById(R.id.btnNext);
            btnBack = findViewById(R.id.btnBack);

            // שליפת שם המשתמש שהועבר מהמסך הקודם כדי להציגו בברכה אישית
            String userName = getIntent().getStringExtra("USER_NAME");

            // הצגת הודעת שלום מותאמת אישית אם שם המשתמש זמין
            if (userName != null && !userName.isEmpty()) {
                tvWelcome.setText("שלום, " + userName + "!");
            } else {
                tvWelcome.setText("שלום!");
            }

            // לוגיקת כפתור חזור: ניווט חזרה למסך הבית (UserHome)
            btnBack.setOnClickListener(v -> {
                Intent intent = new Intent(Choise.this, UserHome.class);
                startActivity(intent);
                finish(); // סגירת המסך הנוכחי
            });

            // לוגיקת כפתור הבא: אימות בחירות המשתמש וניווט למסך בחירת הפירות והירקות
            btnNext.setOnClickListener(v -> {
                int selectedGoalId = rgGoal.getCheckedRadioButtonId();
                int selectedSizeId = rgSize.getCheckedRadioButtonId();

                // וולידציה: מוודא שהמשתמש בחר מטרה
                if (selectedGoalId == -1) {
                    Toast.makeText(Choise.this, "אנא בחר מסה או חיטוב", Toast.LENGTH_SHORT).show();
                    return;
                }

                // וולידציה: מוודא שהמשתמש בחר גודל כוס
                if (selectedSizeId == -1) {
                    Toast.makeText(Choise.this, "אנא בחר גודל כוס", Toast.LENGTH_SHORT).show();
                    return;
                }

                // המרת בחירת המשתמש לפורמט פנימי (String) עבור המטרה
                String goal;
                if (selectedGoalId == R.id.rbMuscle) {
                    goal = "MUSCLE";
                } else {
                    goal = "CUT";
                }

                // המרת בחירת המשתמש לפורמט מספרי (int) עבור גודל הכוס
                int cupSize;
                if (selectedSizeId == R.id.rb200) {
                    cupSize = 200;
                } else if (selectedSizeId == R.id.rb400) {
                    cupSize = 400;
                } else {
                    cupSize = 600;
                }

                // יצירת Intent למעבר למסך הבא והעברת הנתונים שנבחרו
                Intent intent = new Intent(Choise.this, FruitsandVegtables.class);
                intent.putExtra("USER_NAME", userName);
                intent.putExtra("GOAL", goal);
                intent.putExtra("CUP_SIZE", cupSize);
                startActivity(intent);
            });
        } catch (Exception e) {
            // טיפול בשגיאות טעינה כלליות למניעת קריסת האפליקציה
            e.printStackTrace();
            Toast.makeText(this, "שגיאה בטעינת המסך, חזור ונסה שוב", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
