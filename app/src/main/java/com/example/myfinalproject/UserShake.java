package com.example.myfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfinalproject.Adapter.UserShakeAdapter;
import com.example.myfinalproject.model.Shake;
import com.example.myfinalproject.model.ShakeSelectionManager;
import com.example.myfinalproject.services.DatabaseService;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

/**
 * מחלקה זו (Activity) מציגה את רשימת השייקים האישיים שנוצרו על ידי המשתמש המחובר.
 * מטרת המסך היא לאפשר למשתמש לצפות בהיסטוריית השייקים שלו ולעבור לפרטי שייק ספציפי לצורך עיון נוסף.
 */
public class UserShake extends AppCompatActivity {

    // הגדרת משתני המסך: RecyclerView לתצוגת הרשימה, Adapter לניהול השורות, ורשימת השייקים
    private RecyclerView rvUserShakes;
    private UserShakeAdapter adapter;
    private ArrayList<Shake> shakeList;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_user_shake); // טעינת ממשק המשתמש (XML)

            // הגדרת התאמה למסכים עם "שוליים" (System Bars) כדי שהתוכן לא יחתוך
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });

            // אתחול כפתורים ורשימת השייקים
            btnBack = findViewById(R.id.btnBack);
            rvUserShakes = findViewById(R.id.rvUserShakes);
            shakeList = new ArrayList<>();

            // פעולת כפתור חזור: ניווט למסך הבית וסגירת המסך הנוכחי
            btnBack.setOnClickListener(v -> {
                Intent intent = new Intent(UserShake.this, UserHome.class);
                startActivity(intent);
                finish();
            });

            // יצירת ה-Adapter: מגדיר מה קורה בלחיצה על פריט ברשימה (מעבר למסך פרטי השייק)
            adapter = new UserShakeAdapter(shakeList, shake -> {
                try {
                    ShakeSelectionManager.setCurrentViewedShake(shake); // שמירת השייק שנבחר במנהל הבחירות
                    Intent intent = new Intent(UserShake.this, ShakeDetails.class);
                    intent.putExtra("isAdminView", false); // ציון שזו תצוגת משתמש רגיל
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e("SHAKE_CLICK", "Error opening shake details", e);
                    Toast.makeText(UserShake.this, "שגיאה בפתיחת פרטי השייק", Toast.LENGTH_SHORT).show();
                }
            });

            // הגדרת ה-RecyclerView להצגת השורות בצורה ליניארית (אחת אחרי השנייה)
            rvUserShakes.setLayoutManager(new LinearLayoutManager(this));
            rvUserShakes.setAdapter(adapter);

            // משיכת מזהה המשתמש המחובר מ-Firebase Auth כדי לשלוף רק את השייקים שלו
            String uid = FirebaseAuth.getInstance().getUid();
            if (uid == null) {
                Toast.makeText(this, "המשתמש לא מחובר", Toast.LENGTH_SHORT).show();
                return;
            }

            // האזנה לנתונים בזמן אמת (Realtime): שליפת השייקים הספציפיים של המשתמש מהשרת
            DatabaseService.getInstance().listenToUserShakesRealtime(uid,
                    new DatabaseService.DatabaseCallback<List<Shake>>() {
                        @Override
                        public void onCompleted(List<Shake> shakes) {
                            shakeList.clear(); // מחיקת הרשימה הישנה לפני העדכון
                            if (shakes != null) {
                                shakeList.addAll(shakes); // הוספת השייקים העדכניים שהגיעו מהשרת
                            }
                            adapter.notifyDataSetChanged(); // עדכון ה-Adapter להצגת הנתונים החדשים שהתקבלו
                        }

                        @Override
                        public void onFailed(Exception e) {
                            Toast.makeText(UserShake.this, "שגיאה בטעינת השייקים מהמסד", Toast.LENGTH_SHORT).show();
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace(); // לוג למקרה של תקלה כללית בטעינת המסך כדי לעזור בדיבאג
            Toast.makeText(this, "שגיאה בטעינת המסך. נסה שוב.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}