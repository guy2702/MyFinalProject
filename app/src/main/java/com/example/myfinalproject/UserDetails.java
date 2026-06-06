package com.example.myfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myfinalproject.model.User;
import com.example.myfinalproject.services.DatabaseService;

public class UserDetails extends AppCompatActivity {

    private EditText etFname, etLname, etPhone, etPassword;
    private Button btnDelete, btnUpdate;

    private String userId;
    private boolean isAdmin;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_details);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // אתחול רכיבי הממשק
        etFname = findViewById(R.id.tvUserFirstNameDetail);
        etLname = findViewById(R.id.tvUserLastNameDetail);
        etPhone = findViewById(R.id.tvUserPhoneDetail);
        etPassword = findViewById(R.id.tvUserPasswordDetail);

        btnDelete = findViewById(R.id.btnDeleteUser);
        btnUpdate = findViewById(R.id.btnUpdateUser);

        // קבלת המשתמש מה-Intent
        User user = (User) getIntent().getSerializableExtra("user");
        if (user != null) {
            userId = user.getId();
            isAdmin = user.isAdmin();
            userEmail = user.getEmail();

            etFname.setText(user.getFname());
            etLname.setText(user.getLname());
            etPhone.setText(user.getPhone());
            etPassword.setText(user.getPassword());
        } else {
            Toast.makeText(this, "שגיאה: המשתמש לא קיים", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // הגדרת פעולת מחיקה דרך DatabaseService
        btnDelete.setOnClickListener(v -> {
            DatabaseService.getInstance().deleteUser(userId, new DatabaseService.DatabaseCallback<Void>() {
                @Override
                public void onCompleted(Void object) {
                    Toast.makeText(UserDetails.this, "המשתמש נמחק בהצלחה", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onFailed(Exception e) {
                    Toast.makeText(UserDetails.this, "שגיאה במחיקה: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        // הגדרת פעולת עדכון דרך DatabaseService
        btnUpdate.setOnClickListener(v -> {
            String fname = etFname.getText().toString().trim();
            String lname = etLname.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (fname.isEmpty() || lname.isEmpty() || phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "יש למלא את כל השדות", Toast.LENGTH_SHORT).show();
                return;
            }

            User updatedUser = new User(userId, fname, lname, phone, userEmail, password, isAdmin);

            DatabaseService.getInstance().updateUser(updatedUser, new DatabaseService.DatabaseCallback<Void>() {
                @Override
                public void onCompleted(Void object) {
                    Toast.makeText(UserDetails.this, "עודכן בהצלחה", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onFailed(Exception e) {
                    Toast.makeText(UserDetails.this, "שגיאה בעדכון: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}