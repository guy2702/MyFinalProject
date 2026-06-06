// =================================================================================================
// מטרת העמוד: מסך הבית של המשתמש הרשום (UserHome).
// המסך מציג הודעת ברוך הבא אישית ומספק ניווט לביצוע פעולות מרכזיות, כולל תפריט צד (המבורגר).
// =================================================================================================

package com.example.myfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myfinalproject.model.User;
import com.example.myfinalproject.services.DatabaseService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserHome extends AppCompatActivity {

    private TextView tvWelcome;
    private Button btnStartShake, btnMyShakes, btnLogout;
    private TextView btnHamburgerMenu; // עדכנו כאן ל-TextView בגלל הסימן הויזואלי ☰

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_home);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // אתחול רכיבי הממשק (UI)
        tvWelcome = findViewById(R.id.tvWelcome);
        btnStartShake = findViewById(R.id.btnStartShake);
        btnMyShakes = findViewById(R.id.btnMyShakes);
        btnLogout = findViewById(R.id.btnLogout);
        btnHamburgerMenu = findViewById(R.id.btnHamburgerMenu); // אתחול כפתור התפריט

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            tvWelcome.setText("טוען נתונים...");

            DatabaseService.getInstance().getUser(firebaseUser.getUid(), new DatabaseService.DatabaseCallback<User>() {
                @Override
                public void onCompleted(User user) {
                    if (user != null && user.getFname() != null && !user.getFname().isEmpty()) {
                        tvWelcome.setText("ברוך הבא, " + user.getFname());
                    } else {
                        String nameFallback = firebaseUser.getEmail() != null ? firebaseUser.getEmail().split("@")[0] : "";
                        tvWelcome.setText("ברוך הבא, " + nameFallback);
                    }
                }

                @Override
                public void onFailed(Exception e) {
                    Log.e("UserHome", "Error fetching user data", e);
                    String nameFallback = firebaseUser.getEmail() != null ? firebaseUser.getEmail().split("@")[0] : "";
                    tvWelcome.setText("ברוך הבא, " + nameFallback);
                }
            });
        } else {
            tvWelcome.setText("ברוך הבא");
        }

        // =====================================================================
        // הפעלת תפריט ההמבורגר הקופץ (PopupMenu)
        // =====================================================================
        btnHamburgerMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });

        // =====================================================================
        // הכפתורים הרגילים במסך
        // =====================================================================
        btnStartShake.setOnClickListener(v -> {
            Intent intent = new Intent(UserHome.this, Choise.class);
            startActivity(intent);
        });

        btnMyShakes.setOnClickListener(v -> {
            Intent intent = new Intent(UserHome.this, UserShake.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(UserHome.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    // =====================================================================
    // פונקציה: מציגה ומנהלת את תפריט הפופ-אפ (Hamburger Menu)
    // =====================================================================
    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        // מנפח את העיצוב שיצרנו בקובץ user_menu.xml
        popupMenu.getMenuInflater().inflate(R.menu.user_menu, popupMenu.getMenu());

        // מאזין ללחיצות על האפשרויות בתפריט
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_new_shake) {
                    startActivity(new Intent(UserHome.this, Choise.class));
                    return true;
                }
                else if (id == R.id.nav_my_shakes) {
                    startActivity(new Intent(UserHome.this, UserShake.class));
                    return true;
                }
                else if (id == R.id.nav_logout) {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(UserHome.this, MainActivity.class);
                    // מנקה את ההיסטוריה כדי שלא יוכלו לחזור אחורה למסך הבית ללא חיבור
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    return true;
                }
                return false;
            }
        });

        // מציג את התפריט על המסך
        popupMenu.show();
    }
}