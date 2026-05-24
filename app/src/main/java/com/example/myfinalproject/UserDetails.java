// =================================================================================================
// מטרת העמוד: ניהול פרטי משתמש.
// העמוד מאפשר למנהל המערכת (Admin) לצפות בפרטים האישיים של משתמש ספציפי שנבחר,
// לעדכן את הפרטים הללו (שם, טלפון, סיסמה) במסד הנתונים, או למחוק את המשתמש מהמערכת כליל.
// =================================================================================================

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserDetails extends AppCompatActivity {

    private EditText etFname, etLname, etPhone, etPassword;
    private Button btnDelete, btnUpdate;

    private DatabaseReference usersRef; // הפניה למסד הנתונים של המשתמשים ב-Firebase
    private String userId;
    private boolean isAdmin;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_details);

        // הגנה על התוכן כדי שכפתור המחיקה התחתון לא יוסתר מאחורי מערכת ההפעלה
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // אתחול רכיבי הטקסט והכפתורים מהממשק הגרפי
        etFname = findViewById(R.id.tvUserFirstNameDetail);
        etLname = findViewById(R.id.tvUserLastNameDetail);
        etPhone = findViewById(R.id.tvUserPhoneDetail);
        etPassword = findViewById(R.id.tvUserPasswordDetail);

        btnDelete = findViewById(R.id.btnDeleteUser);
        btnUpdate = findViewById(R.id.btnUpdateUser);

        // יצירת הפניה לתיקיית המשתמשים ב-Firebase Realtime Database
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        // קבלת אובייקט המשתמש שנשלח מהמסך הקודם דרך ה-Intent
        User user = (User) getIntent().getSerializableExtra("user");
        if (user != null) {
            userId = user.getId();
            isAdmin = user.isAdmin();
            userEmail = user.getEmail();

            // מילוי שדות הטקסט בפרטי המשתמש לצורך הצגה/עריכה
            etFname.setText(user.getFname());
            etLname.setText(user.getLname());
            etPhone.setText(user.getPhone());
            etPassword.setText(user.getPassword());
        } else {
            // טיפול במקרה של שגיאה בהעברת הנתונים
            Toast.makeText(this, "שגיאה: המשתמש לא קיים", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // הגדרת פעולת מחיקת משתמש ממסד הנתונים
        btnDelete.setOnClickListener(v -> {
            if (userId != null && !userId.isEmpty()) {
                usersRef.child(userId).removeValue()
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(this, "המשתמש נמחק בהצלחה", Toast.LENGTH_SHORT).show();
                            finish(); // סגירת המסך וחזרה לרשימת המשתמשים
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "שגיאה במחיקה", Toast.LENGTH_SHORT).show());
            }
        });

        // הגדרת פעולת עדכון פרטי משתמש
        btnUpdate.setOnClickListener(v -> {
            if (userId != null && !userId.isEmpty()) {
                // קליטת הערכים החדשים מהמשתמש
                String fname = etFname.getText().toString().trim();
                String lname = etLname.getText().toString().trim();
                String phone = etPhone.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                // וולידציה: בדיקה שאין שדות ריקים
                if (fname.isEmpty() || lname.isEmpty() || phone.isEmpty() || password.isEmpty()) {
                    Toast.makeText(this, "יש למלא את כל השדות", Toast.LENGTH_SHORT).show();
                    return;
                }

                // יצירת אובייקט משתמש מעודכן
                User updatedUser = new User(
                        userId,
                        fname,
                        lname,
                        phone,
                        userEmail,
                        password,
                        isAdmin
                );

                // עדכון הערך ב-Firebase לפי ה-ID של המשתמש
                usersRef.child(userId).setValue(updatedUser)
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(this, "עודכן בהצלחה", Toast.LENGTH_SHORT).show();
                            finish(); // סגירת המסך לאחר עדכון
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "שגיאה בעדכון", Toast.LENGTH_SHORT).show());
            }
        });
    }
}