package com.example.myfinalproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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

    private EditText etEmail, etPassword, etFName, etLName, etPhone;
    private Button btnRegister;
    private TextView tvLogin;

    private DatabaseService databaseService;

    public static final String MyPREFERENCES = "MyPrefs";
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        EdgeToEdge.enable(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        databaseService = DatabaseService.getInstance();

        etFName = findViewById(R.id.firstname);
        etLName = findViewById(R.id.lastname);
        etEmail = findViewById(R.id.email);
        etPhone = findViewById(R.id.phone);
        etPassword = findViewById(R.id.password);

        btnRegister = findViewById(R.id.btn_register_register);
        tvLogin = findViewById(R.id.tv_register_login);

        btnRegister.setOnClickListener(this);
        tvLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == btnRegister.getId()) {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String fName = etFName.getText().toString().trim();
            String lName = etLName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();

            if (!checkInput(email, password, fName, lName, phone)) return;

            registerUser(fName, lName, phone, email, password);

        } else if (id == tvLogin.getId()) {
            // כאשר המשתמש לוחץ "יש לי משתמש" – נפתח מסך login
            Intent loginIntent = new Intent(register.this, login.class);
            startActivity(loginIntent);
            finish();
        }
    }

    private boolean checkInput(String email, String password, String fName, String lName, String phone) {
        if (email.isEmpty() || !email.contains("@")) {
            Toast.makeText(this, "אימייל לא תקין", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "הסיסמה חייבת להכיל לפחות 6 תווים", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (fName.isEmpty() || lName.isEmpty()) {
            Toast.makeText(this, "יש להזין שם פרטי ומשפחה", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (phone.isEmpty()) {
            Toast.makeText(this, "טלפון לא תקין", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void registerUser(String fname, String lname, String phone, String email, String password) {
        // ברירת מחדל: משתמש רגיל (לא מנהל)
        User user = new User(null, fname, lname, phone, email, password, false);
        createUserInDatabase(user);
    }

    private void createUserInDatabase(User user) {
        databaseService.createNewUser(user, new DatabaseService.DatabaseCallback<String>() {
            @Override
            public void onCompleted(String uid) {
                Log.d(TAG, "User created successfully with UID: " + uid);

                // שמירה ב-SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("email", user.getEmail());
                editor.putString("password", user.getPassword());
                editor.apply();

                // פתיחת מסך הראשי
                Intent intent = new Intent(register.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "Failed to register user", e);
                Toast.makeText(register.this, "נכשל ביצירת משתמש", Toast.LENGTH_SHORT).show();
            }
        });
    }
}