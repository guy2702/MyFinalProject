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
import com.example.myfinalproject.model.ShakeSelectionManager;
import com.example.myfinalproject.services.DatabaseService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Nuts extends AppCompatActivity {

    private RecyclerView rvNuts;
    private ItemAdapter adapter;
    private ArrayList<Item> nutsList;
    private Button btnFinish;
    private Button btnPrev;
    private TextView tvTitleNuts;

    private String selectedGoal;
    private int cupSize;
    private int allowedGrams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuts);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        selectedGoal = getIntent().getStringExtra("GOAL");
        cupSize = getIntent().getIntExtra("CUP_SIZE", 400);

        if (selectedGoal == null) {
            Toast.makeText(this, "שגיאה בקבלת המטרה", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        rvNuts = findViewById(R.id.rvNuts);
        btnFinish = findViewById(R.id.btnFinishNuts);
        btnPrev = findViewById(R.id.btnPrevNuts);
        tvTitleNuts = findViewById(R.id.tvTitleNuts);

        nutsList = new ArrayList<>();

        allowedGrams = SmoothieCalculator.getCategoryAmount(
                selectedGoal,
                cupSize,
                SmoothieCalculator.TYPE_NUTS
        );

        final String goalText;
        if ("MUSCLE".equalsIgnoreCase(selectedGoal)) {
            goalText = "מסה";
        } else if ("CUT".equalsIgnoreCase(selectedGoal)) {
            goalText = "חיטוב";
        } else {
            goalText = "";
        }

        Runnable updateTitle = () -> {
            // הוסר החישוב של selectedCount ו-perItemGrams כי אין בהם צורך יותר

            tvTitleNuts.setText(
                    "בחר אגוזים  " + allowedGrams + " גרם\n" +
                            "מטרה: " + goalText
                    // השורה הבאה הוסרה:
                    // + "\n" + "לכל פריט: " + perItemGrams + " גרם"
            );
        };

        adapter = new ItemAdapter(nutsList, item -> {});
        adapter.setSelectionMode(true);

        rvNuts.setLayoutManager(new LinearLayoutManager(this));
        rvNuts.setAdapter(adapter);

        updateTitle.run();

        btnPrev.setOnClickListener(v -> {
            Intent intent = new Intent(Nuts.this, Sweeteners.class);
            intent.putExtra("GOAL", selectedGoal);
            intent.putExtra("CUP_SIZE", cupSize);
            startActivity(intent);
            finish();
        });

        btnFinish.setOnClickListener(v -> {
            int selectedCount = 0;
            int totalAmount = 0;

            for (Item item : adapter.getItems()) {
                if (item.isSelected()) {
                    selectedCount++;

                    if (item.getAmount() <= 0) {
                        Toast.makeText(this, "יש להזין כמות לכל אגוז שנבחר", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    totalAmount += item.getAmount();
                }
            }

            if (selectedCount == 0) {
                Toast.makeText(this, "חובה לבחור לפחות אגוז אחד", Toast.LENGTH_SHORT).show();
                return;
            }

            if (totalAmount != allowedGrams) {
                Toast.makeText(this, "הכמות אינה תקינה", Toast.LENGTH_SHORT).show();
                return;
            }

            ShakeSelectionManager.setCategoryItems("nuts", adapter.getItems());

            Intent intent = new Intent(Nuts.this, ShakeResults.class);
            intent.putExtra("GOAL", selectedGoal);
            intent.putExtra("CUP_SIZE", cupSize);
            startActivity(intent);
            finish();
        });

        DatabaseService.getInstance().listenToItemsRealtime(new DatabaseService.DatabaseCallback<List<Item>>() {
            @Override
            public void onCompleted(List<Item> items) {
                nutsList.clear();

                for (Item item : items) {
                    if (item == null) continue;

                    if (isNut(item) && matchesGoal(item, selectedGoal)) {
                        item.setSelected(false);
                        item.setAmount(0);
                        nutsList.add(item);
                    }
                }

                adapter.notifyDataSetChanged();
                updateTitle.run();
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(Nuts.this, "שגיאה בטעינת האגוזים", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isNut(Item item) {
        String type = item.getType();
        if (type == null) return false;

        type = type.trim().toLowerCase(Locale.ROOT);

        return type.contains("nut")
                || type.contains("nuts")
                || type.contains("אגוז")
                || type.contains("אגוזים");
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