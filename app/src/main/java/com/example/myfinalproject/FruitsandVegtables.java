package com.example.myfinalproject;

/**
 * --- סיכום מחלקת FruitsandVegtables (מסך בחירת פירות וירקות) ---
 * * תפקיד המסך:
 * זהו השלב הראשון בתהליך הרכבת השייק. המסך מקבל מהמסך הקודם את מטרת המתאמן
 * וגודל הכוס, מחשב בעזרת מחלקת לוגיקה כמה גרם פירות/ירקות עליו לצרוך, ומציג לו רשימה מסוננת.
 * * * זרימת הנתונים (Data Flow):
 * 1. קבלה: מקבל Goal ו-CupSize דרך Intent.
 * 2. חישוב: פונה ל-SmoothieCalculator כדי לדעת כמה גרם מותר למשתמש לבחור בקטגוריה זו.
 * 3. שליפה: מוריד את כל הפריטים מ-Firebase בזמן אמת (DatabaseService).
 * 4. סינון (Filter): מנגנון הסינון מתחיל כאשר ה-Activity מפעיל את הפעולה DatabaseService.getInstance().listenToItemsRealtime()
 * כדי למשוך את כל חומרי הגלם מ-Firebase, וברגע שהרשימה הגולמית מתקבלת בתוך פעולת ה-Callback שנקראת onCompleted(),
 * ה-Activity מפעיל מיד את הפעולה itemList.clear() כדי לרוקן את הקופסה המקומית ולמנוע כפילויות בתצוגה.
 * משם, הקוד מריץ לולאת for שעוברת פריט-פריט ומעבירה אותו סינון קפדני באמצעות שתי פעולות תנאי:
 * הפעולה isFruitVegetable(item) שבודקת אם המוצר שייך לקטגוריית פירות וירקות, והפעולה matchesGoal(item, goal)
 * שמוודאת שהפריט מתאים למטרת המתאמן (חיטוב או מסה). רק מוצר שעומד בהצלחה בשני התנאים הללו מתווסף לרשימה
 * המקורית בעזרת הפעולה itemList.add(item), ולבסוף, כדי להציג את התוצאה המסוננת למשתמש, ה-Activity מפעיל את
 * הפעולה adapter.notifyDataSetChanged() שמקפיצה את האדפטר ומחדשת את המראה של הרשימה על המסך.
 * 5. אימות (Validation): בלחיצה על "הבא", מוודא שהמשתמש בחר פריטים, הזין כמויות תקינות,
 * ושהסכום הכולל שווה בדיוק ליעד הגרמים שחושב.
 * 6. שמירה ומעבר: שומר את הבחירות ב-ShakeSelectionManager ועובר למסך הבא (Liquids).
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
            // --- אתחול והגדרת ה-RecyclerView ---
            // יצירת "מיכל" (Data Model) לשמירת הנתונים בזיכרון המכשיר
            itemList = new ArrayList<>();

            // יצירת ה-Adapter (הגשר בין ה-Data ל-UI). הפונקציה בתוך הסוגריים היא Callback ללחיצה.
            adapter = new ItemAdapter(itemList, item -> {});

            // הגדרה ב-Adapter המאפשרת למשתמש לבחור יותר מפריט אחד ברשימה (Multiple Selection)
            adapter.setSelectionMode(true);

            // אדריכל התצוגה - ה-LayoutManager קובע שהפריטים יסודרו בצורה אנכית (LinearLayout)
            rvItems.setLayoutManager(new LinearLayoutManager(this));

            // החיבור הסופי בין התצוגה (RecyclerView) לבין המתאם (Adapter)
            rvItems.setAdapter(adapter);

            // --- האזנה לבסיס הנתונים בזמן אמת (Realtime Listener) ---
            // שימוש בתכנות אסינכרוני כדי להאזין לעדכונים מבלי לתקוע את ממשק המשתמש (UI Thread)
            DatabaseService.getInstance().listenToItemsRealtime(new DatabaseService.DatabaseCallback<List<Item>>() {

                // פונקציה זו תופעל אוטומטית ברגע שהנתונים יגיעו מהשרת
                @Override
                public void onCompleted(List<Item> items) {
                    // ניקוי הרשימה הישנה לפני מילוי בנתונים חדשים - מונע כפילויות בתצוגה
                    itemList.clear();

                    // לוגיקה עסקית (Business Logic): סינון הנתונים שהגיעו מה-Database
                    for (Item item : items) {
                        // הגנה (Defensive Programming) - דילוג על אובייקטים ריקים
                        if (item == null) continue;

                        // סינון לפי תנאים: האם זה פרי/ירק? והאם זה מתאים למטרה שהוגדרה?
                        if (isFruitVegetable(item) && matchesGoal(item, goal)) {
                            itemList.add(item); // הוספת הפריט לרשימה שבזיכרון
                        }
                    }

                    // עדכון ה-Adapter שהנתונים השתנו כדי שירענן את התצוגה (UI Binding)
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onFailed(Exception e) {
                    // טיפול בשגיאות תקשורת מול השרת
                    Log.e("DatabaseError", "Failed to load items: " + e.getMessage());
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