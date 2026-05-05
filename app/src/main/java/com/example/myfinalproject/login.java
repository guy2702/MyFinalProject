package com.example.myfinalproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns; // נוסף לבדיקת אימייל תקנית
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar; // מומלץ להוסיף ב-XML
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myfinalproject.model.User;
import com.example.myfinalproject.services.DatabaseService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class login extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private ProgressBar progressBar; // רכיב חיווי טעינה

    private DatabaseService databaseService;
    private FirebaseAuth mAuth;

    // עדיף להשתמש במפתח ייחודי לשמירת אימייל וסיסמה לצורך ה-UI
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String KEY_LAST_EMAIL = "last_logged_email";
    public static final String KEY_LAST_PASSWORD = "last_logged_password"; // נוסף מפתח לשמירת הסיסמה
    SharedPreferences sharedPreferences;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // שינוי קטן בקבלת ה-root כדי למנוע קריסה אם ה-ID ב-XML שונה
        View rootLayout = findViewById(R.id.main);
        if (rootLayout != null) {
            ViewCompat.setOnApplyWindowInsetsListener(rootLayout, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        databaseService = DatabaseService.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // --- תיקון הפתרון לקריסה: עדכון ה-IDs לחדשים שהגדרנו ב-XML ---

        // ה-ID הישן (R.id.email_login_login) הוסר מה-XML שלך.
        // ה-ID החדש הוא emailInput
        etEmail = findViewById(R.id.emailInput);

        // ה-ID הישן (R.id.password_login_login) הוסר מה-XML שלך.
        // ה-ID החדש הוא passwordInput
        etPassword = findViewById(R.id.passwordInput);

        // IDs אלו קיימים ב-XML ששלחת
        btnLogin = findViewById(R.id.loginBtn);
        tvRegister = findViewById(R.id.registerText);

        // וודא שקיים ProgressBar ב-XML שלך עם ה-ID הזה
        progressBar = findViewById(R.id.loginProgressBar);

        // --- שינוי 1: שאיבת אימייל וסיסמה שמורים ---
        String savedEmail = sharedPreferences.getString(KEY_LAST_EMAIL, "");
        etEmail.setText(savedEmail);

        // החזרנו את הפעולה לקריאת הסיסמה השמורה
        String savedPassword = sharedPreferences.getString(KEY_LAST_PASSWORD, "");
        etPassword.setText(savedPassword); // הפעלנו שורה זו מחדש

        btnLogin.setOnClickListener(this);
        tvRegister.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // --- שינוי 2: ביטול Auto-Login ---

        /* מחקנו/ביטלנו את הלוגיקה הבאה כדי שהמשתמש יישאר במסך הלוגין
            גם אם הוא מחובר ב-Firebase, ויתקדם רק בלחיצה על כפתור ההתחברות.

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Log.d(TAG, "User already logged in, fetching data...");
            // אם המשתמש מחובר ב-Auth, ניקח את הנתונים שלו וננתב אותו
            fetchUserDataAndNavigate(currentUser.getUid());
        }
        */
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.loginBtn) { // השוואה ישירה ל-ID קריאה יותר
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (!checkInput(email, password)) return;

            // --- שינוי 3: שמירת אימייל וסיסמה ---
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_LAST_EMAIL, email);
            // הפעלנו מחדש את השורה ששומרת את הסיסמה (עם המפתח החדש)
            editor.putString(KEY_LAST_PASSWORD, password);
            editor.apply();

            loginUser(email, password);

        } else if (id == R.id.registerText) {
            Intent registerIntent = new Intent(login.this, register.class);
            startActivity(registerIntent);
            // לא עושים finish() כדי שהמשתמש יוכל לחזור למסך הלוגין מההרשמה
        }
    }

    private boolean checkInput(String email, String password) {
        // שימוש ב-Patterns לבדיקת פורמט אימייל תקני
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("נא להכניס אימייל תקין");
            etEmail.requestFocus();
            return false;
        }
        if (password.isEmpty() || password.length() < 6) {
            etPassword.setError("הסיסמה חייבת להיות לפחות 6 תווים");
            etPassword.requestFocus();
            return false;
        }
        return true;
    }

    private void loginUser(String email, String password) {
        // הצגת גלגל טעינה וביטול יכולת לחיצה על הכפתור
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    // בכל מקרה (הצלחה או כישלון), נבטל את מצב הטעינה
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    btnLogin.setEnabled(true);

                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser == null) {
                            // מקרה קצה חריג
                            Toast.makeText(login.this, "שגיאה לא צפויה, נסה שוב", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String uid = firebaseUser.getUid();
                        fetchUserDataAndNavigate(uid);

                    } else {
                        // שגיאה כללית כדי לא לתת רמז מה לא נכון (אימייל או סיסמה)
                        etPassword.setError("אימייל או סיסמה שגויים");
                        etPassword.requestFocus();
                        Log.e(TAG, "Login failed", task.getException());
                    }
                });
    }

    /**
     * פונקציית עזר לשליפת נתוני משתמש מה-Database וניתוח למסך המתאים.
     * משמשת בלחיצה על כפתור ההתחברות.
     */
    private void fetchUserDataAndNavigate(String uid) {
        // חיווי טעינה
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        databaseService.getUser(uid, new DatabaseService.DatabaseCallback<User>() {
            @Override
            public void onCompleted(User user) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);

                if (user == null) {
                    Log.e(TAG, "User not found in DB for UID: " + uid);
                    // אם המשתמש מחובר ב-Auth אבל לא קיים ב-DB, זו שגיאת מערכת חמורה.
                    // מומלץ לנתק אותו ולהחזיר למסך לוגין.
                    mAuth.signOut();
                    Toast.makeText(login.this, "שגיאה בנתוני משתמש. נסה להירשם שוב.", Toast.LENGTH_LONG).show();
                    return;
                }

                Log.d(TAG, "Login success, user: " + user.getId() + ", isAdmin: " + user.isAdmin());

                // --- שינוי 4: לוגיקת ניתוב מבוסס תפקיד (Role-based navigation) ---
                Intent intent;
                if (user.isAdmin()) {
                    // 1. אם הוא מנהל (isAdmin == true) -> ניקח אותו ל-AdminPage
                    intent = new Intent(login.this, AdminPage.class);
                } else {
                    // 2. אם הוא משתמש רגיל (else) -> ניקח אותו ל-UserHome
                    // שינינו בחזרה מ-UserPage.class ל-UserHome.class
                    intent = new Intent(login.this, UserHome.class);
                }

                // העברת שם המשתמש (חווית משתמש - "שלום פלוני")
                intent.putExtra("USER_NAME", user.getFname());

                // ניקוי המחסנית כדי שלחיצה על 'אחורה' לא תחזיר למסך הלוגין
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish(); // סגירת מסך הלוגין
            }

            @Override
            public void onFailed(Exception e) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Failed to get user data from DB", e);
                mAuth.signOut(); // ננתק כדי שלא יתקע במצב לא תקין
                Toast.makeText(login.this, "שגיאה בתקשורת: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}