package com.example.myfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View; // חשוב: הוספת ה-Import הזה
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast; // חשוב: לבדיקת אבטחה

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myfinalproject.Items;
import com.google.firebase.auth.FirebaseAuth; // חשוב: לבדיקת אבטחה

// --- שינוי: הוספת Implements לניהול קליקים מרוכז ---
public class AdminPage extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "AdminPage"; // להוספת לוגים

    private Button btnAddItem, btnItems, btnUsers, btnAllShakes, btnLogout;
    private TextView tvGreeting;

    private FirebaseAuth mAuth; // משתנה לבדיקת אבטחה

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_page);

        // שינוי קטן בקבלת ה-root כדי למנוע קריסה אם ה-ID ב-XML שונה
        View rootLayout = findViewById(R.id.main);
        if (rootLayout != null) {
            ViewCompat.setOnApplyWindowInsetsListener(rootLayout, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        mAuth = FirebaseAuth.getInstance(); // אתחול Firebase Auth

        btnAddItem = findViewById(R.id.btnAddItem);
        btnItems = findViewById(R.id.btnItems);
        btnUsers = findViewById(R.id.btnUserTable);
        btnAllShakes = findViewById(R.id.btnAllShakes);
        btnLogout = findViewById(R.id.btnLogout);

        // --- תיקון הפתרון לקריסה: אתחול tvGreeting כאן! ---
        tvGreeting = findViewById(R.id.tvGreeting); // וודא ששורה זו קיימת ואינה בהערה

        String userName = getIntent().getStringExtra("USER_NAME");
        // עכשיו tvGreeting מאותחל, שורות אלו יעבדו ללא קריסה
        if (userName != null) {
            tvGreeting.setText("שלום " + userName + "!");
        } else {
            tvGreeting.setText("שלום מנהל!"); // ברירת מחדל אם אין שם
        }

        // --- שינוי: הגדרת ClickListeners באמצעות מתודה אחת ---
        btnAddItem.setOnClickListener(this);
        btnItems.setOnClickListener(this);
        btnUsers.setOnClickListener(this);
        btnAllShakes.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // --- שיפור UX/אבטחה: בדיקה אם המשתמש עדיין מחובר ---
        if (mAuth.getCurrentUser() == null) {
            // אם המשתמש לא מחובר ב-Firebase Auth, ננתב אותו בחזרה למסך הכניסה
            Intent intent = new Intent(AdminPage.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish(); // סגירת דף המנהל
        }
    }

    // --- שינוי: ניהול קליקים מרוכז ונקי ---
    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btnAddItem) {
            startActivity(new Intent(AdminPage.this, AddItem.class));
        } else if (id == R.id.btnItems) {
            startActivity(new Intent(AdminPage.this, Items.class));
        } else if (id == R.id.btnUserTable) {
            startActivity(new Intent(AdminPage.this, users.class));
        } else if (id == R.id.btnAllShakes) {
            startActivity(new Intent(AdminPage.this, AdminAllShakes.class));
        } else if (id == R.id.btnLogout) {
            handleLogout();
        }
    }

    private void handleLogout() {
        // --- שיפור אבטחה: ניתוק מ-Firebase Auth ---
        mAuth.signOut();
        Log.d(TAG, "Admin logged out.");

        Intent intent = new Intent(AdminPage.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}