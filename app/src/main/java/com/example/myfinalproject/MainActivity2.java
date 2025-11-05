package com.example.myfinalproject;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        TextView textViewAbout = findViewById(R.id.textViewAbout);
        textViewAbout.setText(
                "ברוכים הבאים לאפליקציית 'שייק בריאותי'! 🍹\n\n" +
                        "האפליקציה מאפשרת לכם להכין שייקים מותאמים אישית לפי מטרותיכם:\n" +
                        "• חיטוב\n" +
                        "• מסה\n\n" +
                        "בחרו את המרכיבים המועדפים עליכם והאפליקציה תחלק לכם את החלבונים, השומנים והקלוריות " +
                        "בצורה מדויקת.\n\n" +
                        "כך תוכלו ליהנות משייק טעים ובריא תוך שמירה על המטרות התזונתיות שלכם.\n\n" +
                        "האפליקציה נועדה להקל על תכנון התזונה שלכם ולהפוך את ההכנה של שייק בריא למשהו פשוט ומהנה!"
        );
    }


    public void finishActivity(View view) {
        finish();
    }
}