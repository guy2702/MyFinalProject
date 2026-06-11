package com.example.myfinalproject;

/**
 * מטרת העמוד: ניהול שלב בחירת תוספי החלבון עבור השייק.
 * העמוד טוען בזמן אמת (Real-time) את רשימת תוספי החלבון הרלוונטיים למטרת המשתמש (מסה או חיטוב),
 * מוודא שהמשתמש בחר כמות גרמים התואמת להנחיות המחושבות, ומאפשר ניווט לשלב הבא.
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

public class ProtienSupplements extends AppCompatActivity {

    private RecyclerView rvSupplements;
    private ItemAdapter adapter;
    private ArrayList<Item> supplementsList;
    private Button btnFinish;
    private Button btnPrev;
    private TextView tvTitleSupplements;

    private String selectedGoal;
    private int cupSize;
    private int allowedGrams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_protien_supplements);

            // קבלת הפרמטרים שנבחרו במסכים הקודמים (מטרה וגודל כוס)
            selectedGoal = getIntent().getStringExtra("GOAL");
            cupSize = getIntent().getIntExtra("CUP_SIZE", 400);

            if (selectedGoal == null) {
                Toast.makeText(this, "שגיאה בקבלת המטרה", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            // הגדרת Padding למניעת חפיפה עם רכיבי המערכת
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });

            rvSupplements = findViewById(R.id.rvSupplements);
            btnFinish = findViewById(R.id.btnFinishSupplements);
            btnPrev = findViewById(R.id.btnPrevSupplements);
            tvTitleSupplements = findViewById(R.id.tvTitleSupplements);

            supplementsList = new ArrayList<>();

            // חישוב הכמות המותרת לתוספי חלבון לפי המטרה שנבחרה
            allowedGrams = SmoothieCalculator.getCategoryAmount(
                    selectedGoal,
                    cupSize,
                    SmoothieCalculator.TYPE_PROTEIN
            );

            // הגדרת טקסט המטרה לתצוגה בכותרת
            final String goalText;
            if ("MUSCLE".equalsIgnoreCase(selectedGoal)) {
                goalText = "בניית מסה";
            } else if ("CUT".equalsIgnoreCase(selectedGoal)) {
                goalText = "חיטוב";
            } else {
                goalText = "";
            }

            // פונקציית עזר לעדכון הכותרת עם הנתונים המחושבים
            Runnable updateTitle = () -> {
                String title = "בחר תוספי חלבון\nכמות נדרשת למטרה שלך (" + goalText + "): " + allowedGrams + " גרם";
                tvTitleSupplements.setText(title);
            };

            // אתחול ה-Adapter המציג את רשימת התוספים
            adapter = new ItemAdapter(supplementsList, item -> {});
            adapter.setSelectionMode(true);

            rvSupplements.setLayoutManager(new LinearLayoutManager(this));
            rvSupplements.setAdapter(adapter);

            updateTitle.run();

            // כפתור חזור למסך הקודם
            btnPrev.setOnClickListener(v -> {
                Intent intent = new Intent(ProtienSupplements.this, liquids.class);
                intent.putExtra("GOAL", selectedGoal);
                intent.putExtra("CUP_SIZE", cupSize);
                startActivity(intent);
                finish();
            });

            // כפתור סיום - בדיקת תקינות הבחירה לפני מעבר למסך הבא
            btnFinish.setOnClickListener(v -> {
                int selectedCount = 0;
                int totalAmount = 0;

                // חישוב הכמות הכוללת שבחר המשתמש ב-RecyclerView
                for (Item item : adapter.getItems()) {
                    if (item.isSelected()) {
                        selectedCount++;

                        if (item.getAmount() <= 0) {
                            Toast.makeText(this, "יש להזין כמות לכל תוסף שנבחר", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        totalAmount += item.getAmount();
                    }
                }

                // וולידציה: חובה לבחור לפחות פריט אחד
                if (selectedCount == 0) {
                    Toast.makeText(this, "חובה לבחור לפחות תוסף אחד", Toast.LENGTH_SHORT).show();
                    return;
                }

                // וולידציה: הכמות הכוללת חייבת להתאים למכסה המחושבת
                if (totalAmount != allowedGrams) {
                    Toast.makeText(this, "הכמות שבחרת: " + totalAmount + " גרם\nיש לבחור בדיוק: " + allowedGrams + " גרם", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    // שמירת הבחירות ב-Manager ומעבר למסך הבא (Sweeteners)
                    ShakeSelectionManager.setCategoryItems("protein", adapter.getItems());

                    Intent intent = new Intent(ProtienSupplements.this, Sweeteners.class);
                    intent.putExtra("GOAL", selectedGoal);
                    intent.putExtra("CUP_SIZE", cupSize);
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e("SWEETENERS_ERROR", "Crash opening sweeteners", e);
                    Toast.makeText(ProtienSupplements.this, "שגיאה במעבר למסך הבא", Toast.LENGTH_SHORT).show();
                }
            });

            // משיכת רשימת התוספים ממסד הנתונים בזמן אמת
            DatabaseService.getInstance().listenToItemsRealtime(new DatabaseService.DatabaseCallback<List<Item>>() {
                @Override
                public void onCompleted(List<Item> items) {
                    supplementsList.clear();

                    // סינון הרכיבים לפי סוג (חלבון) והתאמה למטרה (מסה/חיטוב)
                    for (Item item : items) {
                        if (item == null) continue;

                        if (isProteinSupplement(item) && matchesGoal(item, selectedGoal)) {
                            item.setSelected(false);
                            item.setAmount(0);
                            supplementsList.add(item);
                        }
                    }

                    adapter.notifyDataSetChanged();
                    updateTitle.run();
                }

                @Override
                public void onFailed(Exception e) {
                    Toast.makeText(ProtienSupplements.this, "שגיאה בטעינת תוספי החלבון", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "שגיאה בטעינת המסך. נסה שוב.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    // פונקציית עזר לסינון פריטים לפי סוג (חלבון/תוסף)
    private boolean isProteinSupplement(Item item) {
        String type = item.getType();
        if (type == null) return false;

        type = type.trim().toLowerCase(Locale.ROOT);

        return type.contains("protein")
                || type.contains("supplement")
                || type.contains("חלבון")
                || type.contains("תוסף");
    }

    // פונקציית עזר לסינון פריטים לפי התאמה למטרת המשתמש
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