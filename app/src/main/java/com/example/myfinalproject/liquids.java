package com.example.myfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
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

public class liquids extends AppCompatActivity {

    private RecyclerView rvLiquids;
    private ItemAdapter adapter;
    private ArrayList<Item> liquidsList;
    private Button btnFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liquids);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // ✅ קבלת המטרה (מסה/חיטוב) מהמסך הקודם
        String selectedGoal = getIntent().getStringExtra("CHOICE");

        rvLiquids = findViewById(R.id.rvLiquids);
        btnFinish = findViewById(R.id.btnFinishLiquids);
        liquidsList = new ArrayList<>();

        adapter = new ItemAdapter(liquidsList, item -> {
            // בחירה מנוהלת באדפטר
        });

        adapter.setSelectionMode(true);
        rvLiquids.setLayoutManager(new LinearLayoutManager(this));
        rvLiquids.setAdapter(adapter);

        btnFinish.setOnClickListener(v -> {
            boolean hasSelection = false;
            for (Item item : liquidsList) {
                if (item.isSelected()) {
                    hasSelection = true;
                    break;
                }
            }

            if (hasSelection) {
                Intent intent = new Intent(liquids.this, ProtienSupplements.class);
                // מעבירים את הבחירה הלאה גם למסך הבא אם צריך
                intent.putExtra("CHOICE", selectedGoal);
                startActivity(intent);
            } else {
                Toast.makeText(this, "חובה לבחור לפחות נוזל אחד!", Toast.LENGTH_SHORT).show();
            }
        });

        DatabaseService.getInstance().listenToItemsRealtime(new DatabaseService.DatabaseCallback<List<Item>>() {
            @Override
            public void onCompleted(List<Item> items) {
                liquidsList.clear();
                for (Item item : items) {
                    // ✅ סינון כפול: גם סוג "נוזל" וגם התאמה למטרה (מסה/חיטוב)
                    if (item.getType() != null &&
                            (item.getType().equalsIgnoreCase("נוזלים") ||
                                    item.getType().equalsIgnoreCase("liquids") ||
                                    item.getType().equalsIgnoreCase("נוזל"))) {

                        // בדיקה אם המטרה של הנוזל תואמת לבחירת המשתמש
                        if (item.getGoal() != null && item.getGoal().equals(selectedGoal)) {
                            item.setSelected(false);
                            liquidsList.add(item);
                        }
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
}