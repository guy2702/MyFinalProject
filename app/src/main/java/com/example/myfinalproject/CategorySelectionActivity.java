package com.example.myfinalproject;

import android.content.Intent;
import android.os.Bundle;
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
import com.example.myfinalproject.services.DatabaseService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategorySelectionActivity extends AppCompatActivity {

    private TextView tvTitle;
    private RecyclerView rvItems;
    private Button btnNext;

    private ArrayList<Item> itemList;
    private ItemAdapter adapter;

    private String selectedGoal;
    private String category;
    private int allowedGrams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_selection);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvTitle = findViewById(R.id.tvTitleCategory);
        rvItems = findViewById(R.id.rvItems);
        btnNext = findViewById(R.id.btnNextCategory);

        selectedGoal = getIntent().getStringExtra("CHOICE");
        category = getIntent().getStringExtra("CATEGORY");

        // --- כמות מותרים לפי קטגוריה ומטרה (אפשר להרחיב לכל קטגוריה) ---
        Map<String, Integer> allowedGramsMap = new HashMap<>();
        if ("מסה".equals(selectedGoal)) {
            allowedGramsMap.put("פירות/ירקות", 200);
            allowedGramsMap.put("נוזלים", 250);
            allowedGramsMap.put("תוספי חלבון", 100);
            allowedGramsMap.put("אגוזים", 50);
            allowedGramsMap.put("ממתיקים", 30);
        } else { // חיטוב
            allowedGramsMap.put("פירות/ירקות", 180);
            allowedGramsMap.put("נוזלים", 300);
            allowedGramsMap.put("תוספי חלבון", 80);
            allowedGramsMap.put("אגוזים", 40);
            allowedGramsMap.put("ממתיקים", 20);
        }

        allowedGrams = allowedGramsMap.getOrDefault(category, 100);
        tvTitle.setText("בחר " + category + " - עליך לבחור בדיוק " + allowedGrams + " גרם");

        itemList = new ArrayList<>();
        adapter = new ItemAdapter(itemList, item -> {
            // הלחיצה מנוהלת באדפטר – אין צורך להוסיף כאן
        });
        adapter.setSelectionMode(true);

        rvItems.setLayoutManager(new LinearLayoutManager(this));
        rvItems.setAdapter(adapter);

        btnNext.setOnClickListener(v -> {
            int totalSelectedGrams = 0;
            boolean atLeastOneSelected = false;

            for (Item item : itemList) {
                if (item.isSelected()) {
                    atLeastOneSelected = true;
                    int grams = 0;
                    try {
                        grams = Integer.parseInt(item.getAmountText());
                    } catch (NumberFormatException e) {
                        grams = 0;
                    }
                    totalSelectedGrams += grams;
                }
            }

            if (!atLeastOneSelected) {
                Toast.makeText(this, "חובה לבחור לפחות פריט אחד!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (totalSelectedGrams != allowedGrams) {
                Toast.makeText(this, "בחרת כמות לא נכונה! עליך לבחור בדיוק " + allowedGrams + " גרם", Toast.LENGTH_SHORT).show();
                return;
            }

            // כל החישובים תקינים → ממשיכים למסך הבא
            // אפשר להעביר את הקטגוריה הבאה ל־Activity הבא
            Intent intent = new Intent(CategorySelectionActivity.this, CategorySelectionActivity.class);
            intent.putExtra("CHOICE", selectedGoal);
            intent.putExtra("CATEGORY", getNextCategory(category)); // פונקציה שמחזירה את הקטגוריה הבאה
            startActivity(intent);
        });

        // טעינת נתונים מה-Firebase לפי קטגוריה
        DatabaseService.getInstance().listenToItemsRealtime(new DatabaseService.DatabaseCallback<List<Item>>() {
            @Override
            public void onCompleted(List<Item> items) {
                itemList.clear();
                for (Item item : items) {
                    if (selectedGoal.equals(item.getGoal()) &&
                            category.equals(item.getCategory())) {
                        item.setSelected(false);
                        itemList.add(item);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailed(Exception e) {
                e.printStackTrace();
            }
        });
    }

    // פונקציה שמחזירה את הקטגוריה הבאה לפי סדר
    private String getNextCategory(String current) {
        switch (current) {
            case "פירות/ירקות": return "נוזלים";
            case "נוזלים": return "תוספי חלבון";
            case "תוספי חלבון": return "אגוזים";
            case "אגוזים": return "ממתיקים";
            default: return "סיום"; // או מסך תוצאות
        }
    }
}