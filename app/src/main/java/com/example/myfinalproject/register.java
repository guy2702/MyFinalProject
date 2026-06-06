package com.example.myfinalproject;

/**
 * מטרת העמוד: מסך רישום משתמש חדש.
 * העמוד אוסף פרטים אישיים, מבצע וולידציה (תקינות קלט) ושומר את המשתמש במסד הנתונים.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myfinalproject.model.User;
import com.example.myfinalproject.services.DatabaseService;

public class register extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "registerActivity";

    // הגדרת משתני ה-UI של המסך
    private EditText etEmail, etPassword, etFName, etLName, etPhone;
    private Button btnRegister;
    private TextView tvLogin;

    // שירות בסיס הנתונים לטיפול בפעולות Firebase
    private DatabaseService databaseService;

    // הגדרות לשמירת נתונים מקומית על המכשיר (למשל אימייל אחרון שנרשם)
    public static final String MyPREFERENCES = "MyPrefs";
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        // טיפול בהתאמת הממשק למסכים עם Notch או סרגלי מערכת
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

        // קישור רכיבי הממשק מה-XML ל-Java
        etFName = findViewById(R.id.firstname);
        etLName = findViewById(R.id.lastname);
        etEmail = findViewById(R.id.email);
        etPhone = findViewById(R.id.phone);
        etPassword = findViewById(R.id.password);

        btnRegister = findViewById(R.id.btn_register_register);
        tvLogin = findViewById(R.id.tv_register_login);

        // הרשמת המאזינים לכפתורים
        btnRegister.setOnClickListener(this);
        tvLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        // במידה והמשתמש לחץ על הרשמה
        if (id == R.id.btn_register_register) {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String fName = etFName.getText().toString().trim();
            String lName = etLName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();

            // ביצוע בדיקת תקינות לפני שליחה לשרת
            if (!checkInput(email, password, fName, lName, phone)) return;

            // העברת הנתונים לפונקציית הרישום
            registerUser(fName, lName, phone, email, password);

        } else if (id == R.id.tv_register_login) {
            // ניווט למסך ההתחברות (login) במקרה של משתמש קיים
            Intent loginIntent = new Intent(register.this, login.class);
            startActivity(loginIntent);
            finish();
        }
    }

    /**
     * פונקציה לבדיקת תקינות הקלט (Validation).
     */
    private boolean checkInput(String email, String password, String fName, String lName, String phone) {
        boolean isVaild = true;

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("אימייל לא תקין");
            isVaild = false;
        }

        if (password.length() < 6) {
            etPassword.setError("הסיסמה חייבת להכיל לפחות 6 תווים");
            isVaild = false;
        }

        if (fName.isEmpty()) {
            etFName.setError("שדה חובה");
            isVaild = false;
        }
        if (lName.isEmpty()) {
            etLName.setError("שדה חובה");
            isVaild = false;
        }

        if (phone.isEmpty() || phone.length() < 9) {
            etPhone.setError("טלפון לא תקין (לפחות 9 ספרות)");
            isVaild = false;
        }

        return isVaild;
    }

    private void registerUser(String fname, String lname, String phone, String email, String password) {
        User user = new User(null, fname, lname, phone, email, password, false);
        createUserInDatabase(user);
    }

    private void createUserInDatabase(User user) {
        databaseService.createNewUser(user, new DatabaseService.DatabaseCallback<String>() {
            @Override
            public void onCompleted(String uid) {
                Log.d(TAG, "User created successfully with UID: " + uid);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("last_registered_email", user.getEmail());
                editor.apply();

                Intent intent = new Intent(register.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "Failed to register user", e);
                Toast.makeText(register.this, "נכשל ביצירת משתמש: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}