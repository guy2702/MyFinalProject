package com.example.myfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfinalproject.Adapter.ItemAdapter;
import com.example.myfinalproject.model.Item;
import com.example.myfinalproject.services.DatabaseService;

import java.util.ArrayList;
import java.util.List;

/**
 * --- מסך ניהול הפריטים (Items Activity) ---
 * תפקיד: לשלוף את כל המוצרים מהשרת (Firebase), להציג אותם ברשימה חכמה (RecyclerView),
 * ולאפשר חיפוש דינמי בזמן אמת.
 */
public class Items extends AppCompatActivity {

    private RecyclerView rvItems;
    private ItemAdapter adapter;
    private EditText etSearch;

    // ==============================================================================
    // 1. הגדרת שתי הרשימות (הטריק החכם של המסך לחיפוש מהיר):
    // ==============================================================================

    // רשימת האב (Master List): רשימת הגיבוי.
    // שומרת את כל הנתונים כפי שהגיעו מפיירבייס. היא אף פעם לא נמחקת או מצטמצמת!
    private ArrayList<Item> masterItemList;

    // רשימת התצוגה (Display List): הרשימה שמחוברת פיזית לאדפטר ולמסך.
    // בהתחלה היא זהה לרשימת האב, אבל כשעושים חיפוש, היא מתרוקנת ומתמלאת רק בתוצאות.
    private ArrayList<Item> displayItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        // סידור תצוגה: מונע מהאפליקציה "לעלות" על שורת הסטטוס (הסוללה/שעון) בטלפון
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.items), (v, insets) -> {
            v.setPadding(
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).left,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).top,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).right,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            );
            return insets;
        });

        rvItems = findViewById(R.id.rvItems);
        etSearch = findViewById(R.id.etSearch);

        masterItemList = new ArrayList<>();
        displayItemList = new ArrayList<>();

        // ==============================================================================
        // 2. חיבור האדפטר והגדרת אירוע לחיצה על פריט
        // ==============================================================================
        // אנחנו נותנים לאדפטר רק את "רשימת התצוגה" (displayItemList).
        // ה-Callback (item -> {...}) אומר: אם המנהל לחץ על פריט, תפתח את מסך העריכה (ItemId),
        // ותעביר לו את ה-ID של הפריט (כדי שהמסך הבא ידע את מי לערוך).
        adapter = new ItemAdapter(displayItemList, item -> {
            Intent intent = new Intent(Items.this, ItemId.class);
            intent.putExtra("itemId", item.getId());
            startActivity(intent);
        });

        rvItems.setLayoutManager(new LinearLayoutManager(this));
        rvItems.setAdapter(adapter);

        // ==============================================================================
        // 3. מאזין להקלדה בשורת החיפוש (TextWatcher)
        // ==============================================================================
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // מופעל בכל פעם שהמשתמש מוסיף או מוחק אות. שולח מיד את הטקסט לפונקציית הסינון.
                filterItems(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // ==============================================================================
        // 4. שאיבת הנתונים מהאינטרנט (הצינור ל-Firebase)
        // ==============================================================================
        // קורא לפונקציה בשירות שמורידה את כל הפריטים ב-Realtime.
        DatabaseService.getInstance().listenToItemsRealtime(new DatabaseService.DatabaseCallback<List<Item>>() {

            // onCompleted מופעלת אוטומטית כשהנתונים מסיימים לרדת מהענן לטלפון!
            @Override
            public void onCompleted(List<Item> items) {

                // שלב א': מכניסים את הכל ל"רשימת הגיבוי" כדי שלא יאבדו.
                masterItemList.clear();
                masterItemList.addAll(items);

                // שלב ב': מפעילים מיד את פונקציית הסינון לפי מה שכתוב כרגע בתיבת החיפוש.
                // (כדי שאם המשתמש כבר התחיל להקליד משהו, זה יסונן מיד עם הגעת הנתונים).
                filterItems(etSearch.getText().toString());
            }

            @Override
            public void onFailed(Exception e) {
                e.printStackTrace(); // מדפיס את השגיאה אם התקשורת נכשלה
            }
        });
    }

    /**
     * ==============================================================================
     * 5. פונקציית הפילטר (חיפוש דינמי)
     * ==============================================================================
     * תפקיד: מעדכנת את הרשימה שעל המסך (displayItemList) בהתאם למילים שהמשתמש חיפש.
     */
    private void filterItems(String text) {

        // שלב 1: מרוקנים את רשימת התצוגה לגמרי כדי להכין אותה לתוצאות החדשות
        displayItemList.clear();

        // שלב 2: בודקים האם תיבת החיפוש ריקה?
        if (text == null || text.trim().isEmpty()) {

            // אם היא ריקה -> אין חיפוש. פשוט מעתיקים את כל הפריטים מרשימת הגיבוי למסך.
            displayItemList.addAll(masterItemList);

        } else {
            // אם הוקלד משהו -> הופכים לאותיות קטנות (toLowerCase) כדי שהחיפוש לא יהיה רגיש לאותיות גדולות.
            String searchText = text.toLowerCase().trim();

            // עוברים אחד-אחד על *כל* הפריטים ששמורים ברשימת הגיבוי
            for (Item item : masterItemList) {
                // בודקים האם שם הפריט מכיל בתוכו (contains) את האותיות שהוקלדו
                if (item.getName() != null && item.getName().toLowerCase().contains(searchText)) {
                    // אם כן - מוסיפים אותו לרשימת התצוגה!
                    displayItemList.add(item);
                }
            }
        }

        // ==============================================================================
        // שלב 3: הפקודה שמציירת את המסך מחדש!
        // ==============================================================================
        // הפקודה notifyDataSetChanged צועקת לאדפטר: "היי! הנתונים ב-displayItemList השתנו,
        // תמחק את מה שיש כרגע ותצייר את המסך מחדש לפי הרשימה המעודכנת!".
        adapter.notifyDataSetChanged();
    }
}