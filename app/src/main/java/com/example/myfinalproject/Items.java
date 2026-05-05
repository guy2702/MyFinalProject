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

public class Items extends AppCompatActivity {

    private RecyclerView rvItems;
    private ItemAdapter adapter;
    private EditText etSearch;

    // רשימה ראשית שומרת את כל הנתונים מ-Firebase
    private ArrayList<Item> masterItemList;
    // רשימת התצוגה היא זו שמועברת לאדפטר ומשתנה לפי החיפוש
    private ArrayList<Item> displayItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        // טיפול בשוליים (Insets)
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

        // אנחנו מעבירים לאדפטר רק את רשימת התצוגה
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
                filterItems(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // האזנה לנתונים מ-Firebase
        DatabaseService.getInstance().listenToItemsRealtime(new DatabaseService.DatabaseCallback<List<Item>>() {
            @Override
            public void onCompleted(List<Item> items) {
                masterItemList.clear();
                masterItemList.addAll(items);
                // ברגע שהנתונים מגיעים, נסנן אותם לפי מה שכתוב כרגע בחיפוש (אם כתוב)
                filterItems(etSearch.getText().toString());
            }

            @Override
            public void onFailed(Exception e) {
                e.printStackTrace();
            }
        });
    }

    // פונקציית הסינון
    private void filterItems(String text) {
        displayItemList.clear();

        // אם תיבת החיפוש ריקה, נציג את כל הפריטים
        if (text == null || text.trim().isEmpty()) {
            displayItemList.addAll(masterItemList);
        } else {
            // אם יש טקסט, נחפש אותו בשמות של הפריטים
            String searchText = text.toLowerCase().trim();
            for (Item item : masterItemList) {
                if (item.getName() != null && item.getName().toLowerCase().contains(searchText)) {
                    displayItemList.add(item);
                }
            }
        }

        // מעדכנים את האדפטר שהרשימה השתנתה
        adapter.notifyDataSetChanged();
    }
}