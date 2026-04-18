package com.example.myfinalproject;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myfinalproject.model.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserDetails extends AppCompatActivity {

    private EditText etFname, etLname, etPhone, etPassword;
    private Button btnDelete, btnUpdate;

    private DatabaseReference usersRef;
    private String userId;
    private boolean isAdmin;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        etFname = findViewById(R.id.tvUserFirstNameDetail);
        etLname = findViewById(R.id.tvUserLastNameDetail);
        etPhone = findViewById(R.id.tvUserPhoneDetail);
        etPassword = findViewById(R.id.tvUserPasswordDetail);

        btnDelete = findViewById(R.id.btnDeleteUser);
        btnUpdate = findViewById(R.id.btnUpdateUser);

        usersRef = FirebaseDatabase.getInstance().getReference("users");

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

        btnDelete.setOnClickListener(v -> {
            if (userId != null && !userId.isEmpty()) {
                usersRef.child(userId).removeValue()
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(this, "המשתמש נמחק בהצלחה", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "שגיאה במחיקה", Toast.LENGTH_SHORT).show());
            }
        });

        btnUpdate.setOnClickListener(v -> {
            if (userId != null && !userId.isEmpty()) {

                String fname = etFname.getText().toString().trim();
                String lname = etLname.getText().toString().trim();
                String phone = etPhone.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (fname.isEmpty() || lname.isEmpty() || phone.isEmpty() || password.isEmpty()) {
                    Toast.makeText(this, "יש למלא את כל השדות", Toast.LENGTH_SHORT).show();
                    return;
                }

                User updatedUser = new User(
                        userId,
                        fname,
                        lname,
                        phone,
                        userEmail,
                        password,
                        isAdmin
                );

                usersRef.child(userId).setValue(updatedUser)
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(this, "עודכן בהצלחה", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "שגיאה בעדכון", Toast.LENGTH_SHORT).show());
            }
        });
    }
}