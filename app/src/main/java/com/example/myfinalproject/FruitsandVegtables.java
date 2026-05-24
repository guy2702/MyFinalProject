package com.example.myfinalproject;

/**
 * מחלקה זו אחראית על שלב בחירת הפירות והירקות בשייק.
 * מטרתה לאפשר למשתמש לבחור פריטים מתוך רשימה, להגדיר להם כמויות,
 * ולוודא שהסך הכולל של הפריטים שנבחרו תואם להגדרות התזונתיות של המשתמש.
 */

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import com.example.myfinalproject.Adapter.ItemAdapter;
import com.example.myfinalproject.model.Item;
import com.example.myfinalproject.model.ShakeSelectionManager;
import com.example.myfinalproject.services.DatabaseService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FruitsandVegtables extends AppCompatActivity {

    // רכיבי ה-UI: תצוגת טקסט, רשימה וניווט
    private TextView textView;
    private RecyclerView rvItems;
    private Button btnNext;
    private Button btnPrev;
    private ItemAdapter adapter;
    private ArrayList<Item> itemList;

    // משתנים לשמירת נתוני המשתמש מהשלבים הקודמים
    private String goal;
    private int cupSize;
    private int fruitsVegAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_fruitsand_vegtables);

            // התאמת חלון המסך למערכת (System Bars)
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });

            // אתחול הרכיבים מה-Layout
            textView = findViewById(R.id.textView);
            rvItems = findViewById(R.id.rvFruitsandVegetables);
            btnNext = findViewById(R.id.btnNext);
            btnPrev = findViewById(R.id.btnPrev);

            // קבלת המטרה וגודל הכוס מה-Intent שנשלח מהמסך הקודם
            goal = getIntent().getStringExtra("GOAL");
            cupSize = getIntent().getIntExtra("CUP_SIZE", 0);

            // חישוב המכסה המותרת של פירות וירקות לפי המטרה וגודל הכוס
            fruitsVegAmount = SmoothieCalculator.getCategoryAmount(
                    goal,
                    cupSize,
                    SmoothieCalculator.TYPE_FRUITS_VEGETABLES
            );

            // תרגום קוד המטרה (אנגלית) לטקסט מוצג (עברית)
            String goalText = "";
            if ("MUSCLE".equalsIgnoreCase(goal)) {
                goalText = "בניית מסה";
            } else if ("CUT".equalsIgnoreCase(goal)) {
                goalText = "חיטוב";
            }

            // עדכון הכותרת למשתמש עם הערכים המחושבים
            textView.setText("בחר פירות וירקות\nכמות נדרשת למטרה שלך (" + goalText + "): " + fruitsVegAmount + " גרם");

            // אתחול רשימת הפריטים וה-Adapter
            itemList = new ArrayList<>();
            adapter = new ItemAdapter(itemList, item -> {});
            adapter.setSelectionMode(true); // הפעלת מצב בחירה מרובה

            rvItems.setLayoutManager(new LinearLayoutManager(this));
            rvItems.setAdapter(adapter);

            // האזנה לעדכונים מבסיס הנתונים בזמן אמת (Realtime)
            DatabaseService.getInstance().listenToItemsRealtime(new DatabaseService.DatabaseCallback<List<Item>>() {
                @Override
                public void onCompleted(List<Item> items) {
                    itemList.clear();

                    // סינון הפריטים שמתאימים לקטגוריית פירות/ירקות ולאופי המטרה
                    for (Item item : items) {
                        if (item == null) continue;

                        if (isFruitVegetable(item) && matchesGoal(item, goal)) {
                            itemList.add(item);
                        }
                    }

                    // עדכון הרשימה בתצוגה לאחר טעינת הנתונים
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onFailed(Exception e) {
                    Toast.makeText(FruitsandVegtables.this,
                            "שגיאה בטעינת הנתונים מהמסד",
                            Toast.LENGTH_SHORT).show();
                }
            });

            // כפתור חזור: חוזר למסך הקודם
            btnPrev.setOnClickListener(v -> finish());

            // כפתור הבא: אימות בחירת המשתמש ומעבר למסך הבא
            btnNext.setOnClickListener(v -> {
                int totalAmount = 0;
                int selectedCount = 0;

                // איסוף כמות הפריטים שנבחרו וסכימת המשקל שלהם
                for (Item item : adapter.getItems()) {
                    if (item.isSelected()) {
                        selectedCount++;

                        // מוודא שהמשתמש הזין כמות (גרמים) עבור כל פריט
                        if (item.getAmount() <= 0) {
                            Toast.makeText(FruitsandVegtables.this,
                                    "יש להזין כמות לכל פריט שנבחר",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        totalAmount += item.getAmount();
                    }
                }

                // מוודא שנבחר לפחות פריט אחד
                if (selectedCount == 0) {
                    Toast.makeText(FruitsandVegtables.this,
                            "יש לבחור לפחות פריט אחד",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.d("FRUITS_AMOUNT", "total=" + totalAmount + " allowed=" + fruitsVegAmount);

                // מוודא שהמשתמש הגיע בדיוק לכמות המומלצת למטרה שלו
                if (totalAmount != fruitsVegAmount) {
                    Toast.makeText(FruitsandVegtables.this,
                            "הכמות שבחרת: " + totalAmount + " גרם\nיש לבחור בדיוק: " + fruitsVegAmount + " גרם",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    // שמירת הפריטים הנבחרים בניהול הבחירות ומעבר למסך הנוזלים (liquids)
                    ShakeSelectionManager.setCategoryItems("fruits_vegetables", adapter.getItems());

                    Intent intent = new Intent(FruitsandVegtables.this, liquids.class);
                    intent.putExtra("GOAL", goal);
                    intent.putExtra("CUP_SIZE", cupSize);
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e("LIQUIDS_ERROR", "Crash opening liquids", e);
                    Toast.makeText(FruitsandVegtables.this,
                            "שגיאה במעבר למסך הנוזלים",
                            Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "שגיאה בטעינת המסך, נסה שוב", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * פונקציית סינון פנימית: בודקת האם הפריט שייך לקטגוריית פירות או ירקות.
     * תומכת בחיפוש מחרוזות בעברית ובאנגלית.
     */
    private boolean isFruitVegetable(Item item) {
        String type = item.getType();
        if (type == null) return false;

        type = type.trim().toLowerCase(Locale.ROOT);

        return type.contains("fruit")
                || type.contains("vegetable")
                || type.contains("fruits")
                || type.contains("veg")
                || type.contains("פירות")
                || type.contains("ירקות");
    }

    /**
     * פונקציית סינון פנימית: בודקת האם הפריט מתאים למטרה שנבחרה.
     * משווה בין מטרה בסיסית (מסה/חיטוב) לבין מה שמוגדר בפריט.
     */
    private boolean matchesGoal(Item item, String selectedGoal) {
        String itemGoal = item.getGoal();
        if (itemGoal == null || selectedGoal == null) return false;

        itemGoal = itemGoal.trim().toLowerCase(Locale.ROOT);
        selectedGoal = selectedGoal.trim().toLowerCase(Locale.ROOT);

        if (selectedGoal.equals("muscle")) {
            return itemGoal.equals("muscle") || itemGoal.equals("מסה");
        }

        if (selectedGoal.equals("cut")) {
            return itemGoal.equals("cut") || itemGoal.equals("חיטוב");
        }

        return false;
    }
}