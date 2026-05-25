package com.example.myfinalproject;

/**
 * מחלקה זו אחראית על ניהול מסך הצגת כל השייקים שהורכבו במערכת (עבור מנהל).
 * העמוד מציג רשימה של שייקים ב-RecyclerView ומאפשר למנהל לצפות בפרטים מלאים
 * של כל שייק שנבחר.
 */


/*
 *
 * 1. שליפה: האפליקציה שולפת מ-Firebase את הרשימה המלאה של כלל השייקים במערכת
 * (של כל המשתמשים) דרך הפעולה getShakeList ב-DatabaseService.
 * * 2. חיבור לתצוגה: הרשימה מועברת למתאם AdminAllShakesAdapter, שאחראי לחבר
 * בין הנתונים לעיצוב השורה הבודדת (activity_item_admin_shake.xml).
 * * 3. עיבוד נתונים באדפטר: ה-Adapter מציג את שם המשתמש שיצר את השייק, ומבצע חיתוך
 * (Substring) כדי לקצר את המזהה (ID) הארוך של השייק לתצוגה אסתטית יותר.
 * * 4. לחיצה ומעבר מסך: בעת לחיצה על כרטיסייה, מופעל Listener שמחזיר את האירוע
 * למסך הראשי. שם נפתח Intent למסך פרטי השייק (ShakeDetails), ואנו מעבירים
 * אליו דגל בוליאני (isAdminView = true) שמודיע לו להציג הרשאות ניהול.
 * =======================================================================================
 */

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfinalproject.Adapter.AdminAllShakesAdapter;
import com.example.myfinalproject.model.Shake;
import com.example.myfinalproject.model.ShakeSelectionManager;
import com.example.myfinalproject.services.DatabaseService;

import java.util.ArrayList;
import java.util.List;

public class AdminAllShakes extends AppCompatActivity {

    // רכיבי ממשק המשתמש (UI) להצגת רשימת השייקים
    private RecyclerView rvAllShakes;
    private Button btnBack;
    private TextView tvEmpty; // מוצג במידה ואין שייקים להצגה
    private AdminAllShakesAdapter adapter;
    private ArrayList<Shake> shakeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_all_shakes);

        // הגדרת Padding דינמי למסך כדי להתחשב בסרגלי המערכת (System Bars)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // אתחול רכיבי הממשק מה-XML
        rvAllShakes = findViewById(R.id.rvAllShakes);
        btnBack = findViewById(R.id.btnBack);
        tvEmpty = findViewById(R.id.tvEmpty);

        shakeList = new ArrayList<>();

        // אתחול ה-Adapter והגדרת מאזין לחיצה על כל שייק ברשימה
        adapter = new AdminAllShakesAdapter(shakeList, shake -> {
            // שמירת השייק הנבחר ב-Manager לצורך מעבר למסך הפרטים
            ShakeSelectionManager.setCurrentViewedShake(shake);

            // חישוב מספר השייק (מיקום ברשימה + 1 כדי שיהיה מספר סודר שמתחיל מ-1)
            int shakeNumber = shakeList.indexOf(shake) + 1;

            // מעבר למסך פרטי השייק (ShakeDetails) עם דגל המציין שזו תצוגת מנהל
            Intent intent = new Intent(AdminAllShakes.this, ShakeDetails.class);
            intent.putExtra("isAdminView", true);
            intent.putExtra("SHAKE_NUMBER", shakeNumber);
            startActivity(intent);
        });

        // הגדרת LayoutManager לתצוגת הרשימה
        rvAllShakes.setLayoutManager(new LinearLayoutManager(this));
        rvAllShakes.setAdapter(adapter);

        // כפתור חזרה למסך הניהול הראשי
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(AdminAllShakes.this, AdminPage.class);
            startActivity(intent);
            finish();
        });

        // שליפת רשימת כל השייקים מה-Database
        DatabaseService.getInstance().getShakeList(new DatabaseService.DatabaseCallback<List<Shake>>() {
            @Override
            public void onCompleted(List<Shake> object) {
                shakeList.clear();
                shakeList.addAll(object);
                adapter.notifyDataSetChanged(); // עדכון ה-Adapter בנתונים החדשים

                // בדיקה האם הרשימה ריקה והצגת טקסט מתאים במידת הצורך
                if (shakeList.isEmpty()) {
                    tvEmpty.setVisibility(View.VISIBLE);
                } else {
                    tvEmpty.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailed(Exception e) {
                // הצגת הודעת שגיאה במקרה של תקלה בטעינה
                Toast.makeText(AdminAllShakes.this, "שגיאה בטעינת השייקים", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
