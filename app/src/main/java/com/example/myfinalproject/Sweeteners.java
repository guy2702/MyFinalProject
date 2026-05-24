package com.example.myfinalproject;

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

// מחלקה זו מנהלת את מסך בחירת הממתיקים בשייק
public class Sweeteners extends AppCompatActivity {

    private RecyclerView rvSweeteners;
    private ItemAdapter adapter;
    private ArrayList<Item> sweetenersList;
    private Button btnFinish;
    private Button btnPrev;
    private TextView tvTitleSweeteners;

    private String selectedGoal; // מטרה נבחרת: מסה או חיטוב
    private int cupSize;         // גודל הכוס
    private int allowedGrams;    // כמות הגרמים המותרת למטרה זו

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_sweeteners);

            // התאמת ממשק המשתמש למסגרת המכשיר
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });

            // קבלת נתונים מהמסך הקודם
            selectedGoal = getIntent().getStringExtra("GOAL");
            cupSize = getIntent().getIntExtra("CUP_SIZE", 400);

            if (selectedGoal == null) {
                Toast.makeText(this, "שגיאה בקבלת המטרה", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            rvSweeteners = findViewById(R.id.rvSweeteners);
            btnFinish = findViewById(R.id.btnFinishSweeteners);
            btnPrev = findViewById(R.id.btnPrevSweeteners);
            tvTitleSweeteners = findViewById(R.id.tvTitleSweeteners);

            sweetenersList = new ArrayList<>();

            // חישוב כמות הממתיקים המותרת לפי המטרה וגודל הכוס
            allowedGrams = SmoothieCalculator.getCategoryAmount(
                    selectedGoal,
                    cupSize,
                    SmoothieCalculator.TYPE_SWEETENERS
            );

            final String goalText;
            if ("MUSCLE".equalsIgnoreCase(selectedGoal)) {
                goalText = "בניית מסה";
            } else if ("CUT".equalsIgnoreCase(selectedGoal)) {
                goalText = "חיטוב";
            } else {
                goalText = "";
            }

            // פונקציה לעדכון הדינמי של כותרת המסך עם כמות הגרמים הנדרשת
            Runnable updateTitle = () -> {
                String title = "בחר ממתיקים\nכמות נדרשת למטרה שלך (" + goalText + "): " + allowedGrams + " גרם";
                tvTitleSweeteners.setText(title);
            };

            adapter = new ItemAdapter(sweetenersList, item -> {});
            adapter.setSelectionMode(true);

            rvSweeteners.setLayoutManager(new LinearLayoutManager(this));
            rvSweeteners.setAdapter(adapter);

            updateTitle.run();

            // ניווט חזרה למסך הקודם
            btnPrev.setOnClickListener(v -> {
                Intent intent = new Intent(Sweeteners.this, ProtienSupplements.class);
                intent.putExtra("GOAL", selectedGoal);
                intent.putExtra("CUP_SIZE", cupSize);
                startActivity(intent);
                finish();
            });

            // לוגיקת סיום ובדיקת תקינות בחירת המשתמש
            btnFinish.setOnClickListener(v -> {
                int selectedCount = 0;
                int totalAmount = 0;

                for (Item item : adapter.getItems()) {
                    if (item.isSelected()) {
                        selectedCount++;
                        // וולידציה: חובה להזין כמות לכל פריט שנבחר
                        if (item.getAmount() <= 0) {
                            Toast.makeText(this, "יש להזין כמות לכל ממתיק שנבחר", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        totalAmount += item.getAmount();
                    }
                }

                // בדיקה שבוחר לפחות פריט אחד
                if (selectedCount == 0) {
                    Toast.makeText(this, "חובה לבחור לפחות ממתיק אחד", Toast.LENGTH_SHORT).show();
                    return;
                }

                // בדיקה שהכמות שנבחרה תואמת בדיוק למכסה המותרת
                if (totalAmount != allowedGrams) {
                    Toast.makeText(this, "הכמות שבחרת: " + totalAmount + " גרם\nיש לבחור בדיוק: " + allowedGrams + " גרם", Toast.LENGTH_SHORT).show();
                    return;
                }

                // מעבר למסך הבא (אגוזים) ושמירת הנתונים
                try {
                    ShakeSelectionManager.setCategoryItems("sweeteners", adapter.getItems());
                    Intent intent = new Intent(Sweeteners.this, Nuts.class);
                    intent.putExtra("GOAL", selectedGoal);
                    intent.putExtra("CUP_SIZE", cupSize);
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e("NUTS_ERROR", "Crash opening nuts", e);
                    Toast.makeText(Sweeteners.this, "שגיאה במעבר למסך הבא", Toast.LENGTH_SHORT).show();
                }
            });

            // טעינת רשימת הממתיקים ממסד הנתונים
            DatabaseService.getInstance().listenToItemsRealtime(new DatabaseService.DatabaseCallback<List<Item>>() {
                @Override
                public void onCompleted(List<Item> items) {
                    sweetenersList.clear();
                    for (Item item : items) {
                        if (item == null) continue;
                        // סינון פריטים לפי סוג (ממתיק) והתאמה למטרה
                        if (isSweetener(item) && matchesGoal(item, selectedGoal)) {
                            item.setSelected(false);
                            item.setAmount(0);
                            sweetenersList.add(item);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    updateTitle.run();
                }

                @Override
                public void onFailed(Exception e) {
                    Toast.makeText(Sweeteners.this, "שגיאה בטעינת הממתיקים", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            // טיפול כללי בשגיאות טעינת דף
            e.printStackTrace();
            Toast.makeText(this, "שגיאה בטעינת המסך. נסה שוב.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    // פונקציית עזר לזיהוי פריטי ממתיקים
    private boolean isSweetener(Item item) {
        String type = item.getType();
        if (type == null) return false;
        type = type.trim().toLowerCase(Locale.ROOT);
        return type.contains("sweet") || type.contains("sweetener") || type.contains("ממתיק") || type.contains("ממתיקים");
    }

    // פונקציית עזר לבדיקת התאמה בין הפריט למטרה (מסה/חיטוב)
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