package com.example.myfinalproject;

/**
 * מחלקה זו מנהלת את תהליך ההתחברות של המשתמש לאפליקציה.
 * התהליך כולל:
 * 1. איסוף נתונים משדות הקלט (Email, Password).
 * 2. וולידציה של הנתונים כדי למנוע קלט שגוי.
 * 3. תקשורת עם Firebase Authentication לאימות המשתמש.
 * 4. שליפת פרטי המשתמש מ-DatabaseService לקבלת הרשאות.
 * 5. ניתוב המשתמש למסך הנכון (Admin/User) על בסיס הרשאות אלו.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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

    // תגית לוג עבור ניפוי שגיאות (Debugging)
    private static final String TAG = "LoginActivity";

    // רכיבי ממשק המשתמש של דף ההתחברות
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private ProgressBar progressBar;

    // שירותי תשתית: בסיס נתונים ושירות אימות משתמשים
    private DatabaseService databaseService;
    private FirebaseAuth mAuth;

    // הגדרות עבור SharedPreferences לשמירת נתונים מקומית על המכשיר
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String KEY_LAST_EMAIL = "last_logged_email";
    public static final String KEY_LAST_PASSWORD = "last_logged_password";
    SharedPreferences sharedPreferences;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // הגדרת מצב מסך מלא המשתרע מעבר ל-System Bars
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // הגדרת מאזין לשינויים בגודל החלון כדי לטפל נכונה ב-System Bars
        View rootLayout = findViewById(R.id.main);
        if (rootLayout != null) {
            ViewCompat.setOnApplyWindowInsetsListener(rootLayout, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // אתחול מופעי השירותים השונים
        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        databaseService = DatabaseService.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // קישור רכיבי הממשק מה-XML למשתני המחלקה ב-Java
        etEmail = findViewById(R.id.emailInput);
        etPassword = findViewById(R.id.passwordInput);
        btnLogin = findViewById(R.id.loginBtn);
        tvRegister = findViewById(R.id.registerText);
        progressBar = findViewById(R.id.loginProgressBar);

        // טעינת אימייל וסיסמה שנשמרו מההתחברות הקודמת במידה וקיימים
        String savedEmail = sharedPreferences.getString(KEY_LAST_EMAIL, "");
        etEmail.setText(savedEmail);
        String savedPassword = sharedPreferences.getString(KEY_LAST_PASSWORD, "");
        etPassword.setText(savedPassword);

        // רישום מאזיני לחיצה לכפתורים
        btnLogin.setOnClickListener(this);
        tvRegister.setOnClickListener(this);
    }

    /**
     * פונקציה המגיבה ללחיצות על כפתורי הממשק.
     * מפרידה בין לחיצה על כפתור ההתחברות לבין ניווט למסך ההרשמה.
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();

        // בדיקה האם המשתמש לחץ על כפתור ההתחברות (Login)
        if (id == R.id.loginBtn) {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            // אימות קלט מוקדם (Validation)
            if (!checkInput(email, password)) return;

            // שמירת נתוני המשתמש ב-SharedPreferences לשימוש עתידי (חווית משתמש)
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_LAST_EMAIL, email);
            editor.putString(KEY_LAST_PASSWORD, password);
            editor.apply();

            // ביצוע תהליך ההתחברות השרתי
            loginUser(email, password);

        } else if (id == R.id.registerText) {
            // ניווט למסך הרשמה מבלי לסיים את המסך הנוכחי (כדי לאפשר חזרה)
            Intent registerIntent = new Intent(login.this, register.class);
            startActivity(registerIntent);
        }
    }

    /**
     * פונקציה לבדיקת תקינות הקלט.
     * מוודאת שהאימייל תואם לפורמט סטנדרטי ושסיסמה באורך של לפחות 6 תווים.
     * מחזירה true אם תקין, false אחרת.
     */
    private boolean checkInput(String email, String password) {
        // בדיקת תקינות אימייל באמצעות תבניות מובנות של אנדרואיד
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("נא להכניס אימייל תקין");
            etEmail.requestFocus();
            return false;
        }
        // בדיקת אורך סיסמה למניעת סיסמאות חלשות מדי
        if (password.isEmpty() || password.length() < 6) {
            etPassword.setError("הסיסמה חייבת להיות לפחות 6 תווים");
            etPassword.requestFocus();
            return false;
        }
        return true;
    }

    /**
     * פונקציה המבצעת את ההתחברות בפועל מול Firebase Authentication.
     * מנהלת חיווי טעינה למשתמש ומונעת פעולות כפולות בזמן המתנה לתגובה.
     */
    private void loginUser(String email, String password) {
        // הצגת ProgressBar למשתמש כדי שיידע שהאפליקציה בטעינה
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false); // ביטול לחיצה על כפתור בזמן תהליך אסינכרוני

        // קריאה לשירות האימות של Firebase
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    // סיום הצגת ה-ProgressBar לאחר קבלת תשובה מהשרת
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    btnLogin.setEnabled(true);

                    // טיפול בתוצאת ההתחברות
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            // לאחר אימות הזהות, נדרשת משיכת נתוני המשתמש מהמסד
                            fetchUserDataAndNavigate(firebaseUser.getUid());
                        }
                    } else {
                        // הודעת שגיאה כללית למשתמש במידה וההתחברות נכשלה
                        etPassword.setError("אימייל או סיסמה שגויים");
                        etPassword.requestFocus();
                        Log.e(TAG, "Login failed", task.getException());
                    }
                });
    }

    /**
     * פונקציה השולפת את פרטי המשתמש מבסיס הנתונים (Realtime Database/Firestore).
     * מנתחת את הנתונים ומחליטה לאיזה מסך לנווט (Admin או User).
     */
    private void fetchUserDataAndNavigate(String uid) {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        // משיכת נתוני משתמש לפי ה-UID הייחודי
        databaseService.getUser(uid, new DatabaseService.DatabaseCallback<User>() {
            @Override
            public void onCompleted(User user) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);

                // טיפול במקרה חריג בו המשתמש קיים ב-Auth אך לא בבסיס הנתונים
                if (user == null) {
                    mAuth.signOut();
                    Toast.makeText(login.this, "שגיאה בנתוני משתמש.", Toast.LENGTH_LONG).show();
                    return;
                }

                // ניתוב מבוסס תפקיד (Role-based navigation)
                // כאן אנחנו מחליטים לפי דגל ה-Admin האם המשתמש מנהל או משתמש רגיל
                Intent intent;
                if (user.isAdmin()) {
                    intent = new Intent(login.this, AdminPage.class);
                } else {
                    intent = new Intent(login.this, UserHome.class);
                }

                // העברת שם המשתמש למסך היעד לצורך התאמה אישית
                intent.putExtra("USER_NAME", user.getFname());

                // ניקוי המחסנית (Stack) כדי שלא יהיה ניתן לחזור אחורה למסך הלוגין
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish(); // סגירת הפעילות הנוכחית
            }

            @Override
            public void onFailed(Exception e) {
                // טיפול בשגיאות תקשורת או שגיאות בסיס נתונים
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                mAuth.signOut();
                Toast.makeText(login.this, "שגיאה בתקשורת: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
