package com.example.myfinalproject;

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

    private TextView textViewAbout;
    private Button btnBackAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        try {
            setContentView(R.layout.activity_main2);

            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });

            textViewAbout = findViewById(R.id.textViewAbout);
            btnBackAbout = findViewById(R.id.btnBackAbout);

            // טקסט האודות מעוצב עם אמוג'י ומרווחים שמתאימים לעיצוב החדש
            String aboutText = "ברוכים הבאים לאפליקציית 'שייק בריאותי'! 🍹\n\n" +
                    "האפליקציה מאפשרת לכם להכין שייקים מותאמים אישית לפי המטרות התזונתיות שלכם:\n\n" +
                    "💪 בניית מסה\n" +
                    "🔥 חיטוב והרזיה\n\n" +
                    "בחרו את המרכיבים המועדפים עליכם, והאפליקציה תחשב עבורכם במדויק את כמות החלבונים, השומנים, הפחמימות והקלוריות בכל כוס.\n\n" +
                    "כך תוכלו ליהנות משייק טעים ובריא, ללא ניחושים, תוך שמירה קפדנית על המטרות שלכם.\n\n" +
                    "האפליקציה נועדה להקל על תכנון התזונה שלכם ולהפוך את הכנת השייק למשימה פשוטה, חכמה ומהנה!";

            textViewAbout.setText(aboutText);

            // האזנה לכפתור חזרה
            btnBackAbout.setOnClickListener(v -> finish());

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "שגיאה בטעינת מסך אודות", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}