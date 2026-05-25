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
 * מחלקה זו (Activity) מציגה את מסך רשימת הפריטים במערכת.
 * היא אחראית על שליפת הנתונים מ-Firebase, ניהול חיפוש דינמי והצגתם ב-RecyclerView.
 */
public class Items extends AppCompatActivity {

    private RecyclerView rvItems;
    private ItemAdapter adapter;
    private EditText etSearch;

    // 1. ההכנות והמשתנים החשובים
    // רשימה ראשית השומרת את כל הנתונים כפי שהגיעו מ-Firebase.
    // זוהי "רשימת האב" - היא אף פעם לא נמחקת או מצטמצמת, היא הגיבוי שלנו.
    private ArrayList<Item> masterItemList;

    // רשימת התצוגה המועברת לאדפטר ומתעדכנת בהתאם לסינון/חיפוש.
    // בהתחלה היא זהה לרשימת האב, אבל בחיפוש היא מתרוקנת ומתמלאת רק בתוצאות.
    private ArrayList<Item> displayItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        // 2. כשפותחים את המסך: סידור תצוגה
        // הגדרת טיפול בשוליים (Insets) כדי להתאים את הממשק למערכת ההפעלה.
        // זה מונע מהרשימה לעלות על שורת הסוללה והשעון של הטלפון.
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

        // 2. כשפותחים את המסך: הגדרת לחיצה
        // אתחול האדפטר עם רשימת התצוגה והגדרת מאזין למעבר למסך פרטי פריט.
        // אנחנו אומרים לאדפטר: "אם לחצו על פריט, תעבור למסך ItemId ותמסור לו את ה-ID של הפריט".
        adapter = new ItemAdapter(displayItemList, item -> {
            Intent intent = new Intent(Items.this, ItemId.class);
            intent.putExtra("itemId", item.getId());
            startActivity(intent);
        });

        rvItems.setLayoutManager(new LinearLayoutManager(this));
        rvItems.setAdapter(adapter);

        // 2. כשפותחים את המסך: האזנה לחיפוש
        // מאזין לשורת החיפוש - מופעל בכל פעם שהמשתמש מקליד או מוחק אות.
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // בכל פעם שמוקלדת או נמחקת אות, מופעלת מיד פונקציית הסינון
                filterItems(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // 3. משיכת הנתונים מהאינטרנט
        // פנייה ל-Firebase לבקשת רשימת הפריטים. בזכות ה-Realtime, כל הוספת פריט בשרת תעדכן מיד את המסך.
        DatabaseService.getInstance().listenToItemsRealtime(new DatabaseService.DatabaseCallback<List<Item>>() {
            @Override
            public void onCompleted(List<Item> items) {
                // כשהנתונים מגיעים, מנקים את "רשימת האב" הישנה ומכניסים אליה את כל הנתונים החדשים מהשרת.
                masterItemList.clear();
                masterItemList.addAll(items);
                // ברגע שהנתונים מגיעים, נסנן אותם מיד לפי מה שכתוב כרגע בשורת החיפוש
                // (כדי למנוע באגים אם המשתמש הקליד משהו בזמן שהנתונים נטענו).
                filterItems(etSearch.getText().toString());
            }

            @Override
            public void onFailed(Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 4. פונקציית הקסם: הסינון
     * פונקציה זו מעדכנת את הרשימה המוצגת (displayItemList) בהתאם למחרוזת החיפוש.
     */
    private void filterItems(String text) {
        // קודם כל מרוקנים את "רשימת התצוגה" כדי להכין אותה לתוצאות החדשות.
        displayItemList.clear();

        // בדיקה: האם תיבת החיפוש ריקה?
        if (text == null || text.trim().isEmpty()) {
            // אם כן (המשתמש לא חיפש כלום) -> מעתיקים את כל הפריטים מ"רשימת האב" ל"רשימת התצוגה".
            displayItemList.addAll(masterItemList);
        } else {
            // אם לא (המשתמש הקליד משהו) -> הופכים הכל לאותיות קטנות (לאנגלית) וחותכים רווחים מיותרים.
            String searchText = text.toLowerCase().trim();
            // עוברים בלולאה על כל הפריטים ב"רשימת האב" (הגיבוי שלנו).
            for (Item item : masterItemList) {
                // בודקים: האם השם של הפריט מכיל את האותיות שהוקלדו?
                if (item.getName() != null && item.getName().toLowerCase().contains(searchText)) {
                    // רק פריטים שמתאימים לחיפוש מוכנסים ל"רשימת התצוגה".
                    displayItemList.add(item);
                }
            }
        }

        // צועקים לאדפטר: "היי! הנתונים השתנו, תצייר את ה-RecyclerView (המסך) מחדש!".
        adapter.notifyDataSetChanged();
    }
}