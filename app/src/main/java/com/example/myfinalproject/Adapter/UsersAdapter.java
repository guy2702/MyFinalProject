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

    private EditText etFirstName, etLastName, etEmail, etPhone;
    private Button btnDelete, btnUpdate;
    private String userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        etFirstName = findViewById(R.id.etUserFirstNameDetail);
        etLastName = findViewById(R.id.etUserLastNameDetail);
        etEmail = findViewById(R.id.etUserEmailDetail);
        etPhone = findViewById(R.id.etUserPhoneDetail);

        btnDelete = findViewById(R.id.btnDeleteUser);
        btnUpdate = findViewById(R.id.btnUpdateUser);

        userId = getIntent().getStringExtra("userId");
        if (userId == null) return;

        loadUserDetails();

        btnDelete.setOnClickListener(v -> deleteUser());
        btnUpdate.setOnClickListener(v -> updateUser());
    }

    private void loadUserDetails() {
        DatabaseService.getInstance().getUser(userId, new DatabaseService.DatabaseCallback<User>() {
            @Override
            public void onCompleted(User user) {
                if (user == null) return;

                etFirstName.setText(user.getFname());
                etLastName.setText(user.getLname());
                etEmail.setText(user.getEmail());
                etPhone.setText(user.getPhone());
            }

            @Override
            public void onFailed(Exception e) {
                e.printStackTrace();
                Toast.makeText(UserDetails.this, "שגיאה בטעינת המשתמש", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteUser() {
        DatabaseService.getInstance().deleteUser(userId, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void unused) {
                Toast.makeText(UserDetails.this, "המשתמש נמחק בהצלחה", Toast.LENGTH_SHORT).show();
                finish(); // סגור עמוד זה וחזור לרשימת המשתמשים
            }

            @Override
            public void onFailed(Exception e) {
                e.printStackTrace();
                Toast.makeText(UserDetails.this, "שגיאה במחיקת המשתמש", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUser() {
        String fname = etFirstName.getText().toString().trim();
        String lname = etLastName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        User updatedUser = new User(userId, fname, lname, email, phone, ""); // הסיסמה נשארת ריקה

        DatabaseService.getInstance().updateUser(updatedUser, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void unused) {
                Toast.makeText(UserDetails.this, "פרטי המשתמש עודכנו בהצלחה", Toast.LENGTH_SHORT).show();
                finish(); // חזרה לרשימת המשתמשים
            }

            @Override
            public void onFailed(Exception e) {
                e.printStackTrace();
                Toast.makeText(UserDetails.this, "שגיאה בעדכון המשתמש", Toast.LENGTH_SHORT).show();
            }
        });
    }
}