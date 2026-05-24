package com.example.myfinalproject;

/**
 * מחלקה זו מנהלת את שלב בחירת נוזלי הבסיס עבור השייק.
 * מטרת העמוד היא לאפשר למשתמש לבחור סוג נוזל וכמות, תוך ביצוע וולידציה
 * התואמת למטרה התזונתית שנבחרה (מסה או חיטוב).
 * המחלקה משתמשת ב-RecyclerView כדי להציג את רשימת הנוזלים הזמינים מהשרת.
 */

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfinalproject.Adapter.ItemAdapter;
import com.example.myfinalproject.model.Item;
import com.example.myfinalproject.model.ShakeSelectionManager;
import com.example.myfinalproject.services.DatabaseService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class liquids extends AppCompatActivity {

    // רכיבי ממשק המשתמש (UI) להצגת הנתונים
    private RecyclerView rvLiquids;
    private ItemAdapter adapter;
    private ArrayList<Item> liquidsList;
    private Button btnFinish;
    private Button btnPrev;
    private TextView tvTitleLiquids;

    // משתני מצב לשמירת המידע שהועבר מהמסכים הקודמים
    private String selectedGoal;
    private int cupSize;
    private int allowedGrams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_liquids);

            // התאמת ה-Layout למסגרת המסך (System Bars) למניעת חפיפה
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });

            // קישור אובייקטי ה-Java לרכיבי ה-XML
            tvTitleLiquids = findViewById(R.id.tvTitleLiquids);
            rvLiquids = findViewById(R.id.rvLiquids);
            btnFinish = findViewById(R.id.btnNextLiquids);
            btnPrev = findViewById(R.id.btnPrevLiquids);

            // שליפת פרמטרים שנבחרו במסכים קודמים (מטרה וגודל כוס)
            selectedGoal = getIntent().getStringExtra("GOAL");
            cupSize = getIntent().getIntExtra("CUP_SIZE", 400);

            // וולידציה למניעת מצב שבו אין מטרה מוגדרת
            if (selectedGoal == null) {
                Toast.makeText(this, "שגיאה בקבלת המטרה", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            // חישוב כמות הנוזל המומלצת לפי סוג המטרה וגודל הכוס שנבחרו
            allowedGrams = SmoothieCalculator.getCategoryAmount(
                    selectedGoal,
                    cupSize,
                    SmoothieCalculator.TYPE_LIQUIDS
            );

            // הגדרת טקסט המטרה לתצוגה בכותרת המסך
            final String goalText;
            if ("MUSCLE".equalsIgnoreCase(selectedGoal)) {
                goalText = "בניית מסה";
            } else if ("CUT".equalsIgnoreCase(selectedGoal)) {
                goalText = "חיטוב";
            } else {
                goalText = "";
            }

            liquidsList = new ArrayList<>();

            /**
             * פונקציית עזר להגדרת הכותרת בצורה דינמית.
             * הפונקציה נקראת שוב לאחר טעינת הנתונים כדי לעדכן את המידע עבור המשתמש.
             */
            Runnable updateTitle = () -> {
                String title = "בחר נוזל בסיס\nכמות נדרשת למטרה שלך (" + goalText + "): " + allowedGrams + " מ״ל";
                tvTitleLiquids.setText(title);
            };

            // הגדרת ה-Adapter שינהל את רשימת הנוזלים עם מצב בחירה פעיל
            adapter = new ItemAdapter(liquidsList, item -> {});
            adapter.setSelectionMode(true);

            rvLiquids.setLayoutManager(new LinearLayoutManager(this));
            rvLiquids.setAdapter(adapter);

            updateTitle.run();

            // כפתור חזרה למסך פירות וירקות
            btnPrev.setOnClickListener(v -> {
                Intent intent = new Intent(liquids.this, FruitsandVegtables.class);
                intent.putExtra("GOAL", selectedGoal);
                intent.putExtra("CUP_SIZE", cupSize);
                startActivity(intent);
                finish();
            });

            /**
             * לוגיקת סיום שלב:
             * מוודאת שהמשתמש בחר נוזל, הזין כמות תקינה, ושהכמות הכוללת תואמת למכסה.
             */
            btnFinish.setOnClickListener(v -> {
                int selectedCount = 0;
                int totalAmount = 0;

                // חישוב כמות כוללת מתוך הפריטים שנבחרו
                for (Item item : adapter.getItems()) {
                    if (item.isSelected()) {
                        selectedCount++;

                        // בדיקת תקינות כמות לכל פריט שנבחר
                        if (item.getAmount() <= 0) {
                            Toast.makeText(this, "יש להזין כמות לכל נוזל שנבחר", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        totalAmount += item.getAmount();
                    }
                }

                // בדיקה שחובה לבחור לפחות נוזל אחד
                if (selectedCount == 0) {
                    Toast.makeText(this, "חובה לבחור לפחות נוזל אחד", Toast.LENGTH_SHORT).show();
                    return;
                }

                // בדיקת דיוק הכמות מול המכסה המחושבת
                if (totalAmount != allowedGrams) {
                    Toast.makeText(this, "הכמות שבחרת: " + totalAmount + " מ״ל\nיש לבחור בדיוק: " + allowedGrams + " מ״ל", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    // שמירת הבחירות ב-Manager ומעבר למסך תוספי החלבון
                    ShakeSelectionManager.setCategoryItems("liquids", adapter.getItems());

                    Intent intent = new Intent(liquids.this, ProtienSupplements.class);
                    intent.putExtra("GOAL", selectedGoal);
                    intent.putExtra("CUP_SIZE", cupSize);
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e("PROTIEN_ERROR", "Crash opening protein supplements", e);
                    Toast.makeText(liquids.this, "שגיאה במעבר למסך הבא", Toast.LENGTH_SHORT).show();
                }
            });

            // תקשורת מול בסיס הנתונים לטעינת רשימת הנוזלים בזמן אמת
            DatabaseService.getInstance().listenToItemsRealtime(new DatabaseService.DatabaseCallback<List<Item>>() {
                @Override
                public void onCompleted(List<Item> items) {
                    liquidsList.clear();

                    // סינון פריטים לפי סיווג נוזלים והתאמה למטרה (מסה/חיטוב)
                    for (Item item : items) {
                        if (item == null) continue;

                        if (isLiquid(item) && matchesGoal(item, selectedGoal)) {
                            item.setSelected(false);
                            item.setAmount(0);
                            liquidsList.add(item);
                        }
                    }

                    adapter.notifyDataSetChanged();
                    updateTitle.run();
                }

                @Override
                public void onFailed(Exception e) {
                    Toast.makeText(liquids.this, "שגיאה בטעינת הנוזלים", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "שגיאה בטעינת המסך. נסה שוב.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * פונקציית עזר לזיהוי פריט כנוזל (Liquid) לפי שדה ה-Type בבסיס הנתונים.
     */
    private boolean isLiquid(Item item) {
        String type = item.getType();
        if (type == null) return false;

        type = type.trim().toLowerCase(Locale.ROOT);

        return type.contains("liquid")
                || type.contains("liquids")
                || type.contains("נוזל")
                || type.contains("נוזלים");
    }

    /**
     * פונקציית עזר לבדיקת התאמת רכיב למטרה (מסה או חיטוב).
     * מבצעת השוואה תלוית שפה (עברית/אנגלית).
     */
    private boolean matchesGoal(Item item, String goal) {
        String itemGoal = item.getGoal();
        if (itemGoal == null || goal == null) return false;

        itemGoal = itemGoal.trim().toLowerCase(Locale.ROOT);
        goal = goal.trim().toLowerCase(Locale.ROOT);

        if (goal.equals("muscle")) {
            return itemGoal.equals("muscle") || itemGoal.equals("מסה");
        }

        if (goal.equals("cut")) {
            return itemGoal.equals("cut") || itemGoal.equals("חיטוב");
        }

        return false;
    }
}
