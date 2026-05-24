package com.example.myfinalproject;

/**
 * מחלקה זו מנהלת את דף הבית של מנהל המערכת (AdminPage).
 * הדף מרכז את כל פעולות הניהול הזמינות למנהל (Admin), כגון:
 * הוספת מוצרים, צפייה בפריטים, ניהול משתמשים וצפייה בשייקים שהורכבו.
 */

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AdminPage extends AppCompatActivity implements View.OnClickListener {

    // תגית לוג לניפוי שגיאות
    private static final String TAG = "AdminPage";

    // כפתורי הניווט של המנהל ותגית הברכה
    private Button btnAddItem, btnItems, btnUsers, btnAllShakes, btnLogout;
    private TextView tvGreeting;

    // מופע של Firebase Authentication לניהול התחברות/יציאה
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_page);

        // התאמת ה-Layout לתצוגה במכשירי אנדרואיד עם סרגלי מערכת (System Bars)
        View rootLayout = findViewById(R.id.main);
        if (rootLayout != null) {
            ViewCompat.setOnApplyWindowInsetsListener(rootLayout, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        mAuth = FirebaseAuth.getInstance();

        // אתחול הכפתורים ותגית הטקסט מה-XML
        btnAddItem = findViewById(R.id.btnAddItem);
        btnItems = findViewById(R.id.btnItems);
        btnUsers = findViewById(R.id.btnUserTable);
        btnAllShakes = findViewById(R.id.btnAllShakes);
        btnLogout = findViewById(R.id.btnLogout);
        tvGreeting = findViewById(R.id.tvGreeting);

        FirebaseUser currentUser = mAuth.getCurrentUser();

        // בדיקה האם יש משתמש מחובר ושליפת פרטיו מה-Database
        if (currentUser != null) {
            tvGreeting.setText("טוען נתונים...");

            // שליפת פרטי המנהל מהמסד כדי להציג את שמו הפרטי
            DatabaseService.getInstance().getUser(currentUser.getUid(), new DatabaseService.DatabaseCallback<User>() {
                @Override
                public void onCompleted(User user) {
                    if (user != null && user.getFname() != null && !user.getFname().isEmpty()) {
                        // הצגת שם המנהל בברכה
                        tvGreeting.setText("שלום " + user.getFname() + " (מנהל)!");
                    } else {
                        // גיבוי למקרה שאין שם - מציג את חלק האימייל לפני ה-@
                        String nameFallback = currentUser.getEmail() != null ? currentUser.getEmail().split("@")[0] : "מנהל";
                        tvGreeting.setText("שלום " + nameFallback + "!");
                    }
                }

                @Override
                public void onFailed(Exception e) {
                    // טיפול בשגיאה במידה והשליפה נכשלה
                    Log.e(TAG, "Error fetching admin data", e);
                    String nameFallback = currentUser.getEmail() != null ? currentUser.getEmail().split("@")[0] : "מנהל";
                    tvGreeting.setText("שלום " + nameFallback + "!");
                }
            });
        } else {
            // ברירת מחדל אם המשתמש אינו מאומת
            tvGreeting.setText("שלום מנהל!");
        }

        // הגדרת מאזיני לחיצה לכפתורים
        btnAddItem.setOnClickListener(this);
        btnItems.setOnClickListener(this);
        btnUsers.setOnClickListener(this);
        btnAllShakes.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // אבטחה: מוודא שהמשתמש מחובר; אם לא, מחזיר אותו למסך הראשי
        if (mAuth.getCurrentUser() == null) {
            Intent intent = new Intent(AdminPage.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    /**
     * ניהול ניווט בין המסכים השונים בהתאם לכפתור שנלחץ.
     */
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
            handleLogout(); // ביצוע התנתקות
        }
    }

    /**
     * פונקציה לביצוע התנתקות מהמערכת (Sign Out) וניקוי המחסנית.
     */
    private void handleLogout() {
        mAuth.signOut();
        Log.d(TAG, "Admin logged out.");

        // חזרה למסך הראשי וניקוי פעילויות פתוחות
        Intent intent = new Intent(AdminPage.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
