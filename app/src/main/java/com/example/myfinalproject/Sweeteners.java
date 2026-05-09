package com.example.myfinalproject;

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
import com.example.myfinalproject.services.DatabaseService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Sweeteners extends AppCompatActivity {

    private RecyclerView rvSweeteners;
    private ItemAdapter adapter;
    private ArrayList<Item> sweetenersList;
    private Button btnFinish;
    private Button btnPrev;
    private TextView tvTitleSweeteners;

    private String selectedGoal;
    private int cupSize;
    private int allowedGrams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_sweeteners);

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

            rvSweeteners = findViewById(R.id.rvSweeteners);
            btnFinish = findViewById(R.id.btnFinishSweeteners);
            btnPrev = findViewById(R.id.btnPrevSweeteners);
            tvTitleSweeteners = findViewById(R.id.tvTitleSweeteners);

            sweetenersList = new ArrayList<>();

            allowedGrams = SmoothieCalculator.getCategoryAmount(
                    selectedGoal,
                    cupSize,
                    SmoothieCalculator.TYPE_SWEETENERS
            );

            final String goalText;
            if ("MUSCLE".equalsIgnoreCase(selectedGoal)) {
                goalText = "בניית מסה";
            } else if ("CUT".equalsIgnoreCase(selectedGoal)) {
                goalText = "חיטוב";
            } else {
                goalText = "";
            }

            Runnable updateTitle = () -> {
                // עיצוב כותרת ברור ואחיד
                String title = "בחר ממתיקים\nכמות נדרשת למטרה שלך (" + goalText + "): " + allowedGrams + " גרם";
                tvTitleSweeteners.setText(title);
            };

            adapter = new ItemAdapter(sweetenersList, item -> {});
            adapter.setSelectionMode(true);

            rvSweeteners.setLayoutManager(new LinearLayoutManager(this));
            rvSweeteners.setAdapter(adapter);

            updateTitle.run();

            btnPrev.setOnClickListener(v -> {
                Intent intent = new Intent(Sweeteners.this, ProtienSupplements.class);
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
                            Toast.makeText(this, "יש להזין כמות לכל ממתיק שנבחר", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        totalAmount += item.getAmount();
                    }
                }

                if (selectedCount == 0) {
                    Toast.makeText(this, "חובה לבחור לפחות ממתיק אחד", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (totalAmount != allowedGrams) {
                    Toast.makeText(this, "הכמות שבחרת: " + totalAmount + " גרם\nיש לבחור בדיוק: " + allowedGrams + " גרם", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    ShakeSelectionManager.setCategoryItems("sweeteners", adapter.getItems());

                    Intent intent = new Intent(Sweeteners.this, Nuts.class);
                    intent.putExtra("GOAL", selectedGoal);
                    intent.putExtra("CUP_SIZE", cupSize);
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e("NUTS_ERROR", "Crash opening nuts", e);
                    Toast.makeText(Sweeteners.this, "שגיאה במעבר למסך הבא", Toast.LENGTH_SHORT).show();
                }
            });

            DatabaseService.getInstance().listenToItemsRealtime(new DatabaseService.DatabaseCallback<List<Item>>() {
                @Override
                public void onCompleted(List<Item> items) {
                    sweetenersList.clear();

                    for (Item item : items) {
                        if (item == null) continue;

                        if (isSweetener(item) && matchesGoal(item, selectedGoal)) {
                            item.setSelected(false);
                            item.setAmount(0);
                            sweetenersList.add(item);
                        }
                    }

                    adapter.notifyDataSetChanged();
                    updateTitle.run();
                }

                @Override
                public void onFailed(Exception e) {
                    Toast.makeText(Sweeteners.this, "שגיאה בטעינת הממתיקים", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "שגיאה בטעינת המסך. נסה שוב.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private boolean isSweetener(Item item) {
        String type = item.getType();
        if (type == null) return false;

        type = type.trim().toLowerCase(Locale.ROOT);

        return type.contains("sweet")
                || type.contains("sweetener")
                || type.contains("ממתיק")
                || type.contains("ממתיקים");
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