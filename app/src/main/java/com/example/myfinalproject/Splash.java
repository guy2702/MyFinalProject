package com.example.myfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

/**
 * מחלקת Splash מנהלת את מסך הפתיחה (Splash Screen) של האפליקציה.
 * מטרת המסך היא להציג אנימציה ויזואלית של מילוי כוס שייק לפני המעבר למסך הראשי.
 */
public class Splash extends AppCompatActivity {

    private ImageView smoothieImage;
    private int stageIndex = 0; // משתנה למעקב אחר השלב הנוכחי באנימציה

    // מערך המכיל את מזהי התמונות (Drawables) המרכיבות את שלבי מילוי השייק
    private int[] smoothieStages = {
            R.drawable.smoothie1,
            R.drawable.smoothie2,
            R.drawable.smoothie3,
            R.drawable.smoothie4,
            R.drawable.smoothie5,
            R.drawable.smoothie6
    };

    // משך הזמן (במילי-שניות) להצגת כל שלב באנימציה
    private final int delayMillis = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        smoothieImage = findViewById(R.id.smoothieImage);

        // הפעלת לוגיקת האנימציה מיד עם פתיחת המסך
        startSmoothieAnimation();
    }

    /**
     * פונקציה המנהלת את האנימציה באמצעות Handler.
     * הפונקציה מחליפה תמונות במרווחי זמן קבועים עד להשלמת רצף התמונות.
     */
    private void startSmoothieAnimation() {
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // בדיקה האם ישנם עוד שלבים להצגה
                if(stageIndex < smoothieStages.length){
                    smoothieImage.setImageResource(smoothieStages[stageIndex]);
                    stageIndex++;
                    // תזמון הצגת השלב הבא
                    handler.postDelayed(this, delayMillis);
                } else {
                    // לאחר סיום האנימציה – ביצוע ניווט אוטומטי למסך הראשי (MainActivity)
                    Intent intent = new Intent(Splash.this, MainActivity.class);
                    startActivity(intent);
                    finish(); // סגירת מסך ה-Splash כדי שלא ניתן יהיה לחזור אליו בלחיצה על כפתור "חזור"
                }
            }
        };
        // הפעלה ראשונית של הלולאה
        handler.post(runnable);
    }
}