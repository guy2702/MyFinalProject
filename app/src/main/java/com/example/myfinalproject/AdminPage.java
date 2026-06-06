package com.example.myfinalproject;

/**
 * מחלקה זו מנהלת את דף הבית של מנהל המערכת (AdminPage).
 * הדף מרכז את כל פעולות הניהול הזמינות למנהל (Admin), כגון:
 * הוספת מוצרים, צפייה בפריטים, ניהול משתמשים וצפייה בשייקים שהורכבו.
 */

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
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

    private static final String TAG = "AdminPage";

    private Button btnAddItem, btnItems, btnUsers, btnAllShakes, btnLogout;
    private TextView tvGreeting;
    private TextView btnHamburgerMenu; // הוספנו את משתנה התפריט

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_page);

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
        btnHamburgerMenu = findViewById(R.id.btnHamburgerMenu); // אתחול כפתור התפריט

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            tvGreeting.setText("טוען נתונים...");

            DatabaseService.getInstance().getUser(currentUser.getUid(), new DatabaseService.DatabaseCallback<User>() {
                @Override
                public void onCompleted(User user) {
                    if (user != null && user.getFname() != null && !user.getFname().isEmpty()) {
                        tvGreeting.setText("שלום " + user.getFname() + " (מנהל)!");
                    } else {
                        String nameFallback = currentUser.getEmail() != null ? currentUser.getEmail().split("@")[0] : "מנהל";
                        tvGreeting.setText("שלום " + nameFallback + "!");
                    }
                }

                @Override
                public void onFailed(Exception e) {
                    Log.e(TAG, "Error fetching admin data", e);
                    String nameFallback = currentUser.getEmail() != null ? currentUser.getEmail().split("@")[0] : "מנהל";
                    tvGreeting.setText("שלום " + nameFallback + "!");
                }
            });
        } else {
            tvGreeting.setText("שלום מנהל!");
        }

        // הגדרת מאזיני לחיצה
        btnAddItem.setOnClickListener(this);
        btnItems.setOnClickListener(this);
        btnUsers.setOnClickListener(this);
        btnAllShakes.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
        btnHamburgerMenu.setOnClickListener(this); // מאזין לתפריט
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() == null) {
            Intent intent = new Intent(AdminPage.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

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
        } else if (id == R.id.btnHamburgerMenu) {
            showPopupMenu(v); // פתיחת התפריט בלחיצה
        }
    }

    /**
     * פונקציה: מציגה ומנהלת את תפריט הפופ-אפ של המנהל
     */
    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.admin_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_add_item) {
                    startActivity(new Intent(AdminPage.this, AddItem.class));
                    return true;
                } else if (id == R.id.nav_items) {
                    startActivity(new Intent(AdminPage.this, Items.class));
                    return true;
                } else if (id == R.id.nav_users) {
                    startActivity(new Intent(AdminPage.this, users.class));
                    return true;
                } else if (id == R.id.nav_all_shakes) {
                    startActivity(new Intent(AdminPage.this, AdminAllShakes.class));
                    return true;
                } else if (id == R.id.nav_logout) {
                    handleLogout();
                    return true;
                }
                return false;
            }
        });

        popupMenu.show();
    }

    private void handleLogout() {
        mAuth.signOut();
        Log.d(TAG, "Admin logged out.");

        Intent intent = new Intent(AdminPage.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}