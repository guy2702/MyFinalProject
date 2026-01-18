package com.example.myfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myfinalproject.model.User;
import com.example.myfinalproject.services.DatabaseService;

public class UserDetails extends AppCompatActivity {

    private EditText etFirstName, etLastName, etEmail, etPhone, etPassword;
    private Button btnDelete, btnUpdate;
    private String userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        // קישור ל־XML
        etFirstName = findViewById(R.id.tvUserFirstNameDetail);
        etLastName  = findViewById(R.id.tvUserLastNameDetail);
        etEmail     = findViewById(R.id.tvUserEmailDetail);
        etPhone     = findViewById(R.id.tvUserPhoneDetail);
        etPassword  = findViewById(R.id.tvUserPasswordDetail);

        btnDelete = findViewById(R.id.btnDeleteUser);
        btnUpdate = findViewById(R.id.btnUpdateUser);

        // קבלת ID מה־Intent
        userId = getIntent().getStringExtra("userId");
        if (userId == null) return;

        loadUserDetails(userId);

        // כפתור מחיקה
        btnDelete.setOnClickListener(v -> {
            DatabaseService.getInstance().deleteUser(userId, new DatabaseService.DatabaseCallback<Void>() {
                @Override
                public void onCompleted(Void unused) {
                    Toast.makeText(UserDetailsActivity.this, "המשתמש נמחק בהצלחה", Toast.LENGTH_SHORT).show();
                    finish(); // חזרה לרשימת המשתמשים
                }

                @Override
                public void onFailed(Exception e) {
                    Toast.makeText(UserDetailsActivity.this, "שגיאה במחיקה", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            });
        });

        // כפתור עדכון
        btnUpdate.setOnClickListener(v -> {
            User updatedUser = new User();
            updatedUser.setId(userId);
            updatedUser.setFname(etFirstName.getText().toString());
            updatedUser.setLname(etLastName.getText().toString());
            updatedUser.setEmail(etEmail.getText().toString());
            updatedUser.setPhone(etPhone.getText().toString());
            updatedUser.setPassword(etPassword.getText().toString());

            DatabaseService.getInstance().updateUser(updatedUser, new DatabaseService.DatabaseCallback<Void>() {
                @Override
                public void onCompleted(Void unused) {
                    Toast.makeText(UserDetailsActivity.this, "המשתמש עודכן בהצלחה", Toast.LENGTH_SHORT).show();
                    finish(); // חזרה לרשימת המשתמשים
                }

                @Override
                public void onFailed(Exception e) {
                    Toast.makeText(UserDetailsActivity.this, "שגיאה בעדכון", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            });
        });
    }

    private void loadUserDetails(String uid) {
        DatabaseService.getInstance().getUser(uid, new DatabaseService.DatabaseCallback<User>() {
            @Override
            public void onCompleted(User user) {
                if (user != null) {
                    etFirstName.setText(user.getFname());
                    etLastName.setText(user.getLname());
                    etEmail.setText(user.getEmail());
                    etPhone.setText(user.getPhone());
                    etPassword.setText(user.getPassword());
                }
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(UserDetailsActivity.this, "שגיאה בטעינת פרטי המשתמש", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
    }
}