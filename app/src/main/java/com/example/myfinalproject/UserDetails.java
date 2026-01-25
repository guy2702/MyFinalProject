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

    private EditText etFname, etLname, etEmail, etPhone, etPassword;
    private Button btnDelete, btnUpdate;

    private DatabaseReference usersRef;
    private String userId;
    private boolean isAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        etFname = findViewById(R.id.tvUserFirstNameDetail);
        etLname = findViewById(R.id.tvUserLastNameDetail);
        etEmail = findViewById(R.id.tvUserEmailDetail);
        etPhone = findViewById(R.id.tvUserPhoneDetail);
        etPassword = findViewById(R.id.tvUserPasswordDetail);

        btnDelete = findViewById(R.id.btnDeleteUser);
        btnUpdate = findViewById(R.id.btnUpdateUser);

        usersRef = FirebaseDatabase.getInstance().getReference("users");

        User user = (User) getIntent().getSerializableExtra("user");
        if(user != null){
            userId = user.getId();
            isAdmin = user.isAdmin();

            etFname.setText(user.getFname());
            etLname.setText(user.getLname());
            etEmail.setText(user.getEmail());
            etPhone.setText(user.getPhone());
            etPassword.setText(user.getPassword());
        } else {
            Toast.makeText(this,"שגיאה: המשתמש לא קיים",Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnDelete.setOnClickListener(v -> {
            if(userId != null && !userId.isEmpty()){
                usersRef.child(userId).removeValue()
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(this,"המשתמש נמחק בהצלחה",Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .addOnFailureListener(e -> Toast.makeText(this,"שגיאה במחיקה",Toast.LENGTH_SHORT).show());
            }
        });

        btnUpdate.setOnClickListener(v -> {
            if(userId != null && !userId.isEmpty()){
                User updatedUser = new User(
                        userId,
                        etFname.getText().toString().trim(),
                        etLname.getText().toString().trim(),
                        etPhone.getText().toString().trim(),
                        etEmail.getText().toString().trim(),
                        etPassword.getText().toString().trim(),
                        isAdmin
                );
                usersRef.child(userId).setValue(updatedUser)
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(this,"עודכן בהצלחה",Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .addOnFailureListener(e -> Toast.makeText(this,"שגיאה בעדכון",Toast.LENGTH_SHORT).show());
            }
        });
    }
}