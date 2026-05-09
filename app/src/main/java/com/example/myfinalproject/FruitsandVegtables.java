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

public class FruitsandVegtables extends AppCompatActivity {

    private TextView textView;
    private RecyclerView rvItems;
    private Button btnNext;
    private Button btnPrev;
    private ItemAdapter adapter;
    private ArrayList<Item> itemList;

    private String goal;
    private int cupSize;
    private int fruitsVegAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_fruitsand_vegtables);

            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });

            textView = findViewById(R.id.textView);
            rvItems = findViewById(R.id.rvFruitsandVegetables);
            btnNext = findViewById(R.id.btnNext);
            btnPrev = findViewById(R.id.btnPrev);

            goal = getIntent().getStringExtra("GOAL");
            cupSize = getIntent().getIntExtra("CUP_SIZE", 0);

            fruitsVegAmount = SmoothieCalculator.getCategoryAmount(
                    goal,
                    cupSize,
                    SmoothieCalculator.TYPE_FRUITS_VEGETABLES
            );

            String goalText = "";
            if ("MUSCLE".equalsIgnoreCase(goal)) {
                goalText = "בניית מסה";
            } else if ("CUT".equalsIgnoreCase(goal)) {
                goalText = "חיטוב";
            }

            // טקסט מעוצב וברור למשתמש
            textView.setText("בחר פירות וירקות\nכמות נדרשת למטרה שלך (" + goalText + "): " + fruitsVegAmount + " גרם");

            itemList = new ArrayList<>();
            adapter = new ItemAdapter(itemList, item -> {});
            adapter.setSelectionMode(true);

            rvItems.setLayoutManager(new LinearLayoutManager(this));
            rvItems.setAdapter(adapter);

            DatabaseService.getInstance().listenToItemsRealtime(new DatabaseService.DatabaseCallback<List<Item>>() {
                @Override
                public void onCompleted(List<Item> items) {
                    itemList.clear();

                    for (Item item : items) {
                        if (item == null) continue;

                        if (isFruitVegetable(item) && matchesGoal(item, goal)) {
                            itemList.add(item);
                        }
                    }

                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onFailed(Exception e) {
                    Toast.makeText(FruitsandVegtables.this,
                            "שגיאה בטעינת הנתונים מהמסד",
                            Toast.LENGTH_SHORT).show();
                }
            });

            btnPrev.setOnClickListener(v -> finish());

            btnNext.setOnClickListener(v -> {
                int totalAmount = 0;
                int selectedCount = 0;

                for (Item item : adapter.getItems()) {
                    if (item.isSelected()) {
                        selectedCount++;

                        if (item.getAmount() <= 0) {
                            Toast.makeText(FruitsandVegtables.this,
                                    "יש להזין כמות לכל פריט שנבחר",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        totalAmount += item.getAmount();
                    }
                }

                if (selectedCount == 0) {
                    Toast.makeText(FruitsandVegtables.this,
                            "יש לבחור לפחות פריט אחד",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.d("FRUITS_AMOUNT", "total=" + totalAmount + " allowed=" + fruitsVegAmount);

                if (totalAmount != fruitsVegAmount) {
                    Toast.makeText(FruitsandVegtables.this,
                            "הכמות שבחרת: " + totalAmount + " גרם\nיש לבחור בדיוק: " + fruitsVegAmount + " גרם",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    ShakeSelectionManager.setCategoryItems("fruits_vegetables", adapter.getItems());

                    Intent intent = new Intent(FruitsandVegtables.this, liquids.class);
                    intent.putExtra("GOAL", goal);
                    intent.putExtra("CUP_SIZE", cupSize);
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e("LIQUIDS_ERROR", "Crash opening liquids", e);
                    Toast.makeText(FruitsandVegtables.this,
                            "שגיאה במעבר למסך הנוזלים",
                            Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "שגיאה בטעינת המסך, נסה שוב", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private boolean isFruitVegetable(Item item) {
        String type = item.getType();
        if (type == null) return false;

        type = type.trim().toLowerCase(Locale.ROOT);

        return type.contains("fruit")
                || type.contains("vegetable")
                || type.contains("fruits")
                || type.contains("veg")
                || type.contains("פירות")
                || type.contains("ירקות");
    }

    private boolean matchesGoal(Item item, String selectedGoal) {
        String itemGoal = item.getGoal();
        if (itemGoal == null || selectedGoal == null) return false;

        itemGoal = itemGoal.trim().toLowerCase(Locale.ROOT);
        selectedGoal = selectedGoal.trim().toLowerCase(Locale.ROOT);

        if (selectedGoal.equals("muscle")) {
            return itemGoal.equals("muscle") || itemGoal.equals("מסה");
        }

        if (selectedGoal.equals("cut")) {
            return itemGoal.equals("cut") || itemGoal.equals("חיטוב");
        }

        return false;
    }
}