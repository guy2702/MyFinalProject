package com.example.myfinalproject;

/**
 * מטרת העמוד: עמוד הבית (Main Screen).
 * זהו המסך הראשון שהמשתמש פוגש באפליקציה. העמוד משמש כצומת ניווט מרכזי (Dashboard)
 * ומאפשר למשתמש לבחור בין יצירת חשבון חדש (הרשמה), כניסה לחשבון קיים (התחברות),
 * או צפייה במידע נוסף אודות האפליקציה (אודות).
 */

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // הגדרת משתני הכפתורים הראשיים המופיעים במסך הבית
    private View btnSignUp;
    private View btnLogin;
    private View btnAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // הגדרה לעיצוב מודרני של מסך מלא
        setContentView(R.layout.activity_main);

        // קישור משתני ה-Java לרכיבי ה-View המוגדרים בקובץ ה-XML
        btnSignUp = findViewById(R.id.btnSignUp);
        btnLogin = findViewById(R.id.btnLogin);
        btnAbout = findViewById(R.id.btnAbout);

        // רישום המחלקה כמאזינה (Listener) לאירועי לחיצה על הכפתורים
        btnSignUp.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        btnAbout.setOnClickListener(this);
    }

    /**
     * פונקציית ניהול הלחיצות המרכזית.
     * הפונקציה מזהה איזה כפתור נלחץ לפי ה-ID שלו ומבצעת ניווט (Intent) למסך הרלוונטי.
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btnSignUp) {
            // ניווט למסך ההרשמה
            Intent intent = new Intent(MainActivity.this, register.class);
            startActivity(intent);
        } else if (id == R.id.btnLogin) {
            // ניווט למסך ההתחברות
            Intent intent = new Intent(MainActivity.this, login.class);
            startActivity(intent);
        } else if (id == R.id.btnAbout) {
            // ניווט למסך האודות
            Intent intent = new Intent(MainActivity.this, MainActivity2.class);
            startActivity(intent);
        }
    }
}