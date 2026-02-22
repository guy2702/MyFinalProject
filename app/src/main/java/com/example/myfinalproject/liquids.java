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

public class liquids extends AppCompatActivity {

    private RecyclerView rvLiquids;
    private ItemAdapter adapter;
    private ArrayList<Item> liquidsList;
    private Button btnFinish;
    private TextView tvTitleLiquids;

    private String category = "נוזלים";
    private String selectedGoal;
    private int cupSize;
    private int allowedGrams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liquids);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // נתונים מהמסך הקודם
        selectedGoal = getIntent().getStringExtra("CHOICE");
        cupSize = getIntent().getIntExtra("CUP_SIZE", 400);

        tvTitleLiquids = findViewById(R.id.tvTitleLiquids);
        rvLiquids = findViewById(R.id.rvLiquids);
        btnFinish = findViewById(R.id.btnFinishLiquids);

        liquidsList = new ArrayList<>();

        // אחוזים לפי קטגוריה ומטרה
        Map<String, Double> percentages = new HashMap<>();
        if (selectedGoal.equals("מסה")) {
            percentages.put("נוזלים", 0.25);
            percentages.put("פירות/ירקות", 0.20);
            percentages.put("תוספי חלבון", 0.25);
            percentages.put("ממתיקים", 0.10);
            percentages.put("אגוזים", 0.20);
        } else { // חיטוב
            percentages.put("נוזלים", 0.30);
            percentages.put("פירות/ירקות", 0.35);
            percentages.put("תוספי חלבון", 0.20);
            percentages.put("ממתיקים", 0.05);
            percentages.put("אגוזים", 0.10);
        }

        // גרם מותרים לקטגוריה מחושב לפי אחוז מהכוס
        allowedGrams = (int) (cupSize * percentages.get(category));

        Runnable updateTitle = () -> {
            int selectedCount = 0;
            for (Item item : liquidsList) {
                if (item.isSelected()) selectedCount++;
            }
            int perItemGrams = selectedCount > 0 ? allowedGrams / selectedCount : 0;
            tvTitleLiquids.setText("בחר נוזל בסיס - כל פריט יקבל ~" + perItemGrams + " גרם מתוך " + allowedGrams + " גרם");
        };

        adapter = new ItemAdapter(liquidsList, item -> {
            item.setSelected(!item.isSelected());
            updateTitle.run();
            adapter.notifyDataSetChanged();
        });

        adapter.setSelectionMode(true);
        rvLiquids.setLayoutManager(new LinearLayoutManager(this));
        rvLiquids.setAdapter(adapter);

        updateTitle.run();

        btnFinish.setOnClickListener(v -> {
            int selectedCount = 0;
            for (Item item : liquidsList) {
                if (item.isSelected()) selectedCount++;
            }

            if (selectedCount == 0) {
                Toast.makeText(this, "חובה לבחור לפחות נוזל אחד!", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(liquids.this, ProtienSupplements.class);
                intent.putExtra("CHOICE", selectedGoal);
                intent.putExtra("CUP_SIZE", cupSize);
                startActivity(intent);
            }
        });

        DatabaseService.getInstance().listenToItemsRealtime(new DatabaseService.DatabaseCallback<List<Item>>() {
            @Override
            public void onCompleted(List<Item> items) {
                liquidsList.clear();
                for (Item item : items) {
                    if (item.getType() != null &&
                            (item.getType().equalsIgnoreCase("נוזלים") ||
                                    item.getType().equalsIgnoreCase("liquids") ||
                                    item.getType().equalsIgnoreCase("נוזל"))) {

                        if (item.getGoal() != null && item.getGoal().equals(selectedGoal)) {
                            item.setSelected(false);
                            liquidsList.add(item);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                updateTitle.run();
            }

            @Override
            public void onFailed(Exception e) {
                e.printStackTrace();
            }
        });
    }
}