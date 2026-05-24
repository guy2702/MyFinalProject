package com.example.myfinalproject;

/**
 * מטרת העמוד: הצגת דף "אודות" (About) למשתמש.
 * דף זה מספק מידע תמציתי על מטרת האפליקציה, היכולות המרכזיות שלה (חישוב ערכים תזונתיים לפי מטרה),
 * והערך המוסף שהיא מעניקה למשתמש בתהליך הכנת השייקים.
 */

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity2 extends AppCompatActivity {

    // רכיבי ממשק משתמש (UI) להצגת הטקסט וכפתור החזרה
    private TextView textViewAbout;
    private Button btnBackAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // הפעלת מצב Edge-to-Edge לעיצוב מודרני מקצה לקצה

        try {
            setContentView(R.layout.activity_main2);

            // הגדרת מאזין ל-WindowInsets כדי לוודא שהטקסט לא מוסתר ע"י סרגלי המערכת (System Bars)
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });

            // קישור הרכיבים מה-XML לקוד ה-Java
            textViewAbout = findViewById(R.id.textViewAbout);
            btnBackAbout = findViewById(R.id.btnBackAbout);

            // בניית תוכן האודות: שימוש ב-String דינמי כדי להציג את הערך המוסף של האפליקציה למשתמשים
            String aboutText = "ברוכים הבאים לאפליקציית 'שייק בריאותי'! 🍹\n\n" +
                    "האפליקציה מאפשרת לכם להכין שייקים מותאמים אישית לפי המטרות התזונתיות שלכם:\n\n" +
                    "💪 בניית מסה\n" +
                    "🔥 חיטוב והרזיה\n\n" +
                    "בחרו את המרכיבים המועדפים עליכם, והאפליקציה תחשב עבורכם במדויק את כמות החלבונים, השומנים, הפחמימות והקלוריות בכל כוס.\n\n" +
                    "כך תוכלו ליהנות משייק טעים ובריא, ללא ניחושים, תוך שמירה קפדנית על המטרות שלכם.\n\n" +
                    "האפליקציה נועדה להקל על תכנון התזונה שלכם ולהפוך את הכנת השייק למשימה פשוטה, חכמה ומהנה!";

            textViewAbout.setText(aboutText);

            // כפתור חזרה לדף הקודם (משתמש ב-finish כדי לסגור את ה-Activity הנוכחית ולחזור למסך הקודם ב-Stack)
            btnBackAbout.setOnClickListener(v -> finish());

        } catch (Exception e) {
            // טיפול בשגיאות טעינה למניעת קריסה (Crash) והצגת הודעה למשתמש
            e.printStackTrace();
            Toast.makeText(this, "שגיאה בטעינת מסך אודות", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}