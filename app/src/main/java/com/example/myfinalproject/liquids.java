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
import java.util.List;
import java.util.Locale;

public class liquids extends AppCompatActivity {

    private RecyclerView rvLiquids;
    private ItemAdapter adapter;
    private ArrayList<Item> liquidsList;
    private Button btnFinish;
    private TextView tvTitleLiquids;

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

        tvTitleLiquids = findViewById(R.id.tvTitleLiquids);
        rvLiquids = findViewById(R.id.rvLiquids);
        btnFinish = findViewById(R.id.btnFinishLiquids);

        selectedGoal = getIntent().getStringExtra("GOAL");
        cupSize = getIntent().getIntExtra("CUP_SIZE", 400);

        if (selectedGoal == null) {
            Toast.makeText(this, "שגיאה בקבלת המטרה", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        allowedGrams = SmoothieCalculator.getCategoryAmount(
                selectedGoal,
                cupSize,
                SmoothieCalculator.TYPE_LIQUIDS
        );

        final String goalText;
        if ("MUSCLE".equalsIgnoreCase(selectedGoal)) {
            goalText = "מסה";
        } else if ("CUT".equalsIgnoreCase(selectedGoal)) {
            goalText = "חיטוב";
        } else {
            goalText = "";
        }

        liquidsList = new ArrayList<>();

        Runnable updateTitle = () -> {
            int selectedCount = 0;

            for (Item item : liquidsList) {
                if (item.isSelected()) {
                    selectedCount++;
                }
            }

            int perItemGrams = selectedCount > 0 ? allowedGrams / selectedCount : 0;

            tvTitleLiquids.setText(
                    "בחר נוזל בסיס  " + allowedGrams + " גרם\n" +
                            "מטרה: " + goalText + "\n" +
                            "לכל פריט: " + perItemGrams + " גרם"
            );
        };

        adapter = new ItemAdapter(liquidsList, item -> {});
        adapter.setSelectionMode(true);

        rvLiquids.setLayoutManager(new LinearLayoutManager(this));
        rvLiquids.setAdapter(adapter);

        updateTitle.run();

        btnFinish.setOnClickListener(v -> {
            int selectedCount = 0;
            int totalAmount = 0;

            for (Item item : adapter.getItems()) {
                if (item.isSelected()) {
                    selectedCount++;

                    if (item.getAmount() <= 0) {
                        Toast.makeText(this, "יש להזין כמות לכל נוזל שנבחר", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    totalAmount += item.getAmount();
                }
            }

            if (selectedCount == 0) {
                Toast.makeText(this, "חובה לבחור לפחות נוזל אחד", Toast.LENGTH_SHORT).show();
                return;
            }

            if (totalAmount != allowedGrams) {
                Toast.makeText(this, "הכמות אינה תקינה", Toast.LENGTH_SHORT).show();
                return;
            }

            ShakeSelectionManager.setCategoryItems("liquids", adapter.getItems());

            Intent intent = new Intent(liquids.this, ProtienSupplements.class);
            intent.putExtra("GOAL", selectedGoal);
            intent.putExtra("CUP_SIZE", cupSize);
            startActivity(intent);
        });

        DatabaseService.getInstance().listenToItemsRealtime(new DatabaseService.DatabaseCallback<List<Item>>() {
            @Override
            public void onCompleted(List<Item> items) {
                liquidsList.clear();

                for (Item item : items) {
                    if (item == null) continue;

                    if (isLiquid(item) && matchesGoal(item, selectedGoal)) {
                        item.setSelected(false);
                        item.setAmount(0);
                        liquidsList.add(item);
                    }
                }

                adapter.notifyDataSetChanged();
                updateTitle.run();
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(liquids.this, "שגיאה בטעינת הנוזלים", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isLiquid(Item item) {
        String type = item.getType();
        if (type == null) return false;

        type = type.trim().toLowerCase(Locale.ROOT);

        return type.contains("liquid")
                || type.contains("liquids")
                || type.contains("נוזל")
                || type.contains("נוזלים");
    }

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