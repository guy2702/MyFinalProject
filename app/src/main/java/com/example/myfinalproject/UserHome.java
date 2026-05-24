// =================================================================================================
// מטרת העמוד: מסך הבית של המשתמש הרשום (UserHome).
// המסך מציג הודעת ברוך הבא אישית (לפי שם המשתמש שנשלף ממסד הנתונים) ומספק ניווט לביצוע פעולות מרכזיות:
// התחלת הכנת שייק חדש, צפייה בהיסטוריית השייקים האישית, והתנתקות מהמערכת.
// =================================================================================================

package com.example.myfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // הפעלת תצוגה מקצה לקצה
        setContentView(R.layout.activity_user_home);

        // הגנה על התוכן משולי המערכת (System Bars) - מניעת חפיפה בין רכיבי ממשק לבין שורת המצב
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

        // קבלת פרטי המשתמש הנוכחי המחובר דרך Firebase Authentication
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // בדיקה האם משתמש מחובר ושליפת פרטיו מה-DatabaseService
        if (firebaseUser != null) {
            tvWelcome.setText("טוען נתונים...");

            // שליפת פרטי המשתמש מה-Realtime Database לפי ה-UID הייחודי שלו
            DatabaseService.getInstance().getUser(firebaseUser.getUid(), new DatabaseService.DatabaseCallback<User>() {
                @Override
                public void onCompleted(User user) {
                    // אם הנתונים התקבלו בהצלחה והמשתמש קיים, הצגת שם פרטי
                    if (user != null && user.getFname() != null && !user.getFname().isEmpty()) {
                        tvWelcome.setText("ברוך הבא, " + user.getFname());
                    } else {
                        // גיבוי: במידה ואין שם, הצגת שם מהאימייל (חלק השם לפני ה-@)
                        String nameFallback = firebaseUser.getEmail() != null ? firebaseUser.getEmail().split("@")[0] : "";
                        tvWelcome.setText("ברוך הבא, " + nameFallback);
                    }
                }

                @Override
                public void onFailed(Exception e) {
                    // תיעוד שגיאה ב-Logcat והצגת שם חלופי במקרה של כשל בטעינה
                    Log.e("UserHome", "Error fetching user data", e);
                    String nameFallback = firebaseUser.getEmail() != null ? firebaseUser.getEmail().split("@")[0] : "";
                    tvWelcome.setText("ברוך הבא, " + nameFallback);
                }
            });
        } else {
            // במקרה שאין משתמש מחובר
            tvWelcome.setText("ברוך הבא");
        }

        // הגדרת מאזין לחיצה למעבר למסך בחירת מרכיבי השייק
        btnStartShake.setOnClickListener(v -> {
            Intent intent = new Intent(UserHome.this, Choise.class);
            startActivity(intent);
        });

        // הגדרת מאזין לחיצה למעבר למסך השייקים האישיים של המשתמש
        btnMyShakes.setOnClickListener(v -> {
            Intent intent = new Intent(UserHome.this, UserShake.class);
            startActivity(intent);
        });

        // הגדרת מאזין לחיצה לביצוע ניתוק (Logout) מהמערכת וחזרה למסך הכניסה
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut(); // ניתוק מה-Firebase

            // יצירת Intent חדש וניקוי כל ה-Activity הקודמות כדי למנוע חזרה למסך הבית ללא חיבור
            Intent intent = new Intent(UserHome.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}