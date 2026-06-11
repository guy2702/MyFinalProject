package com.example.myfinalproject;

/**
 * מטרת העמוד: ניהול בחירת אגוזים וזרעים עבור השייק.
 * העמוד מאפשר למשתמש לבחור תוספת אגוזים (כאופציה) מתוך רשימה שנטענת מהשרת.
 * העמוד מוודא שהמשתמש לא חורג מכמות הגרמים המקסימלית המותרת לפי מטרה (מסה/חיטוב) וגודל הכוס.
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
import com.example.myfinalproject.model.SmoothieCalculator;
import com.example.myfinalproject.services.DatabaseService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Nuts extends AppCompatActivity {

    private RecyclerView rvNuts;
    private ItemAdapter adapter;
    private ArrayList<Item> nutsList;
    private Button btnFinish;
    private Button btnPrev;
    private TextView tvTitleNuts;

    private String selectedGoal;
    private int cupSize;
    private int allowedGrams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_nuts);

            // התאמת ממשק המשתמש למסגרת המכשיר
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });

            // שליפת פרטי המטרה וגודל הכוס מהמסך הקודם דרך ה-Intent
            selectedGoal = getIntent().getStringExtra("GOAL");
            cupSize = getIntent().getIntExtra("CUP_SIZE", 400);

            if (selectedGoal == null) {
                Toast.makeText(this, "שגיאה בקבלת המטרה", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            rvNuts = findViewById(R.id.rvNuts);
            btnFinish = findViewById(R.id.btnFinishNuts);
            btnPrev = findViewById(R.id.btnPrevNuts);
            tvTitleNuts = findViewById(R.id.tvTitleNuts);

            nutsList = new ArrayList<>();

            // חישוב הכמות המקסימלית של אגוזים המותרת לפי חישוב ה-SmoothieCalculator
            allowedGrams = SmoothieCalculator.getCategoryAmount(
                    selectedGoal,
                    cupSize,
                    SmoothieCalculator.TYPE_NUTS
            );

            // הגדרת תווית המטרה למשתמש
            final String goalText;
            if ("MUSCLE".equalsIgnoreCase(selectedGoal)) {
                goalText = "בניית מסה";
            } else if ("CUT".equalsIgnoreCase(selectedGoal)) {
                goalText = "חיטוב";
            } else {
                goalText = "";
            }

            // עדכון דינמי של הכותרת עם הערך המחושב
            Runnable updateTitle = () -> {
                String title = "בחר אגוזים\nכמות מקסימלית למטרה שלך (" + goalText + "): " + allowedGrams + " גרם";
                tvTitleNuts.setText(title);
            };
            // ניהול רשימת האגוזים באמצעות RecyclerView ו-Adapter מותאם
            adapter = new ItemAdapter(nutsList, item -> {});
            adapter.setSelectionMode(true);

            rvNuts.setLayoutManager(new LinearLayoutManager(this));
            rvNuts.setAdapter(adapter);

            updateTitle.run();

            // חזרה למסך הקודם (Sweeteners/Liquids)
            btnPrev.setOnClickListener(v -> {
                Intent intent = new Intent(Nuts.this, Sweeteners.class);
                intent.putExtra("GOAL", selectedGoal);
                intent.putExtra("CUP_SIZE", cupSize);
                startActivity(intent);
                finish();
            });

            // כפתור סיום - בדיקת תקינות בחירת המשתמש
            btnFinish.setOnClickListener(v -> {
                int totalAmount = 0;

                // חישוב סיכום הכמויות מתוך הפריטים שנבחרו
                for (Item item : adapter.getItems()) {
                    if (item.isSelected()) {
                        if (item.getAmount() <= 0) {
                            Toast.makeText(this, "יש להזין כמות לכל אגוז שנבחר", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        totalAmount += item.getAmount();
                    }
                }

                // וולידציה: לוודא שלא עוברים את המכסה המקסימלית המותרת
                if (totalAmount > allowedGrams) {
                    Toast.makeText(this, "הכמות שבחרת חורגת!\nבחרת " + totalAmount + " גרם מתוך מקסימום " + allowedGrams + " גרם מותרים.", Toast.LENGTH_LONG).show();
                    return;
                }

                try {
                    // שמירת הבחירות ב-Manager ומעבר למסך התוצאות הסופי
                    ShakeSelectionManager.setCategoryItems("nuts", adapter.getItems());

                    Intent intent = new Intent(Nuts.this, ShakeResults.class);
                    intent.putExtra("GOAL", selectedGoal);
                    intent.putExtra("CUP_SIZE", cupSize);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    Log.e("RESULTS_ERROR", "Crash opening shake results", e);
                    Toast.makeText(Nuts.this, "שגיאה בחישוב התוצאות", Toast.LENGTH_SHORT).show();
                }
            });

            // משיכת נתונים מה-Firebase בזמן אמת
            DatabaseService.getInstance().listenToItemsRealtime(new DatabaseService.DatabaseCallback<List<Item>>() {
                @Override
                public void onCompleted(List<Item> items) {
                    nutsList.clear();
                    // סינון הפריטים כך שיוצגו רק אגוזים המתאימים למטרת המשתמש
                    for (Item item : items) {
                        if (item == null) continue;

                        if (isNut(item) && matchesGoal(item, selectedGoal)) {
                            item.setSelected(false);
                            item.setAmount(0);
                            nutsList.add(item);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    updateTitle.run();
                }

                @Override
                public void onFailed(Exception e) {
                    Toast.makeText(Nuts.this, "שגיאה בטעינת האגוזים", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "שגיאה בטעינת המסך. נסה שוב.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    // פונקציית עזר לזיהוי פריטי אגוזים לפי סיווג (Type)
    private boolean isNut(Item item) {
        String type = item.getType();
        if (type == null) return false;
        type = type.trim().toLowerCase(Locale.ROOT);
        return type.contains("nut") || type.contains("nuts") || type.contains("אגוז") || type.contains("אגוזים");
    }

    // פונקציית עזר לסינון פריטים לפי התאמה למטרת המשתמש (מסה או חיטוב)
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