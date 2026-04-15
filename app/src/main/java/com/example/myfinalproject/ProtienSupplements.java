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

public class ProtienSupplements extends AppCompatActivity {

    private RecyclerView rvSupplements;
    private ItemAdapter adapter;
    private ArrayList<Item> supplementsList;
    private Button btnFinish;
    private TextView tvTitleSupplements;

    private String selectedGoal;
    private int cupSize;
    private int allowedGrams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protien_supplements);

        selectedGoal = getIntent().getStringExtra("GOAL");
        cupSize = getIntent().getIntExtra("CUP_SIZE", 400);

        if (selectedGoal == null) {
            Toast.makeText(this, "שגיאה בקבלת המטרה", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rvSupplements = findViewById(R.id.rvSupplements);
        btnFinish = findViewById(R.id.btnFinishSupplements);
        tvTitleSupplements = findViewById(R.id.tvTitleSupplements);

        supplementsList = new ArrayList<>();

        allowedGrams = SmoothieCalculator.getCategoryAmount(
                selectedGoal,
                cupSize,
                SmoothieCalculator.TYPE_PROTEIN
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
            int selectedCount = 0;

            for (Item item : supplementsList) {
                if (item.isSelected()) {
                    selectedCount++;
                }
            }

            int perItemGrams = selectedCount > 0 ? allowedGrams / selectedCount : 0;

            tvTitleSupplements.setText(
                    "בחר תוספי חלבון  " + allowedGrams + " גרם\n" +
                            "מטרה: " + goalText + "\n" +
                            "לכל פריט: " + perItemGrams + " גרם"
            );
        };

        adapter = new ItemAdapter(supplementsList, item -> {});
        adapter.setSelectionMode(true);

        rvSupplements.setLayoutManager(new LinearLayoutManager(this));
        rvSupplements.setAdapter(adapter);

        updateTitle.run();

        btnFinish.setOnClickListener(v -> {
            int selectedCount = 0;
            int totalAmount = 0;

            for (Item item : adapter.getItems()) {
                if (item.isSelected()) {
                    selectedCount++;

                    if (item.getAmount() <= 0) {
                        Toast.makeText(this, "יש להזין כמות לכל תוסף שנבחר", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    totalAmount += item.getAmount();
                }
            }

            if (selectedCount == 0) {
                Toast.makeText(this, "חובה לבחור לפחות תוסף אחד", Toast.LENGTH_SHORT).show();
                return;
            }

            if (totalAmount != allowedGrams) {
                Toast.makeText(this, "הכמות אינה תקינה", Toast.LENGTH_SHORT).show();
                return;
            }

            ShakeSelectionManager.setCategoryItems("protein", adapter.getItems());

            Intent intent = new Intent(ProtienSupplements.this, Sweeteners.class);
            intent.putExtra("GOAL", selectedGoal);
            intent.putExtra("CUP_SIZE", cupSize);
            startActivity(intent);
        });

        DatabaseService.getInstance().listenToItemsRealtime(new DatabaseService.DatabaseCallback<List<Item>>() {
            @Override
            public void onCompleted(List<Item> items) {
                supplementsList.clear();

                for (Item item : items) {
                    if (item == null) continue;

                    if (isProteinSupplement(item) && matchesGoal(item, selectedGoal)) {
                        item.setSelected(false);
                        item.setAmount(0);
                        supplementsList.add(item);
                    }
                }

                adapter.notifyDataSetChanged();
                updateTitle.run();
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(ProtienSupplements.this, "שגיאה בטעינת תוספי החלבון", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isProteinSupplement(Item item) {
        String type = item.getType();
        if (type == null) return false;

        type = type.trim().toLowerCase(Locale.ROOT);

        return type.contains("protein")
                || type.contains("supplement")
                || type.contains("חלבון")
                || type.contains("תוסף");
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