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

    // רשימה ראשית השומרת את כל הנתונים כפי שהגיעו מ-Firebase
    private ArrayList<Item> masterItemList;
    // רשימת התצוגה המועברת לאדפטר ומתעדכנת בהתאם לסינון/חיפוש
    private ArrayList<Item> displayItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        // הגדרת טיפול בשוליים (Insets) כדי להתאים את הממשק למערכת ההפעלה (System Bars)
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

        // אתחול האדפטר עם רשימת התצוגה והגדרת מאזין למעבר למסך פרטי פריט
        adapter = new ItemAdapter(displayItemList, item -> {
            Intent intent = new Intent(Items.this, ItemId.class);
            intent.putExtra("itemId", item.getId());
            startActivity(intent);
        });

        rvItems.setLayoutManager(new LinearLayoutManager(this));
        rvItems.setAdapter(adapter);

        // מאזין לשורת החיפוש - מופעל בכל פעם שהמשתמש מקליד או מוחק אות
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ביצוע סינון בזמן אמת תוך כדי הקלדה
                filterItems(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // האזנה אקטיבית לנתונים מ-Firebase (סנכרון בזמן אמת)
        DatabaseService.getInstance().listenToItemsRealtime(new DatabaseService.DatabaseCallback<List<Item>>() {
            @Override
            public void onCompleted(List<Item> items) {
                masterItemList.clear();
                masterItemList.addAll(items);
                // ברגע שהנתונים מגיעים, נסנן אותם לפי מה שכתוב כרגע בחיפוש
                filterItems(etSearch.getText().toString());
            }

            @Override
            public void onFailed(Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * פונקציית סינון המעדכנת את הרשימה המוצגת (displayItemList) בהתאם למחרוזת החיפוש.
     */
    private void filterItems(String text) {
        displayItemList.clear();

        // אם תיבת החיפוש ריקה, הצג את כל הפריטים הקיימים במערכת
        if (text == null || text.trim().isEmpty()) {
            displayItemList.addAll(masterItemList);
        } else {
            // חיפוש חלקי של שם הפריט בתוך הרשימה הראשית
            String searchText = text.toLowerCase().trim();
            for (Item item : masterItemList) {
                if (item.getName() != null && item.getName().toLowerCase().contains(searchText)) {
                    displayItemList.add(item);
                }
            }
        }

        // הודעה לאדפטר על כך שהנתונים השתנו כדי לרענן את ה-RecyclerView
        adapter.notifyDataSetChanged();
    }
}