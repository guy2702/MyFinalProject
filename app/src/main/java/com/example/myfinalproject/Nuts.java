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

public class Nuts extends AppCompatActivity {

    private RecyclerView rvNuts;
    private ItemAdapter adapter;
    private ArrayList<Item> nutsList;
    private Button btnFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuts);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // קבלת מסה / חיטוב מהמסך הקודם
        String selectedGoal = getIntent().getStringExtra("CHOICE");

        rvNuts = findViewById(R.id.rvNuts);
        btnFinish = findViewById(R.id.btnFinishNuts);
        nutsList = new ArrayList<>();

        adapter = new ItemAdapter(nutsList, item -> {
            // הבחירה מנוהלת באדפטר
        });

        adapter.setSelectionMode(true);
        rvNuts.setLayoutManager(new LinearLayoutManager(this));
        rvNuts.setAdapter(adapter);

        btnFinish.setOnClickListener(v -> {
            boolean hasSelection = false;
            for (Item item : nutsList) {
                if (item.isSelected()) {
                    hasSelection = true;
                    break;
                }
            }

            if (hasSelection) {
                Toast.makeText(this, "נבחרו אגוזים בהצלחה!", Toast.LENGTH_SHORT).show();

                // מעבר לדף ShakeResults
                Intent intent = new Intent(Nuts.this, ShakeResults.class);
                intent.putExtra("CHOICE", selectedGoal); // מעביר את הבחירה מסה/חיטוב
                startActivity(intent);

                // סוגר את המסך הנוכחי
                finish();

            } else {
                Toast.makeText(this, "חובה לבחור לפחות אגוז אחד!", Toast.LENGTH_SHORT).show();
            }
        });

        DatabaseService.getInstance().listenToItemsRealtime(new DatabaseService.DatabaseCallback<List<Item>>() {
            @Override
            public void onCompleted(List<Item> items) {
                nutsList.clear();
                for (Item item : items) {

                    // סינון לפי סוג אגוזים + התאמה למטרה
                    if (item.getType() != null &&
                            (item.getType().equalsIgnoreCase("אגוזים") ||
                                    item.getType().equalsIgnoreCase("nuts") ||
                                    item.getType().equalsIgnoreCase("אגוז"))) {

                        if (item.getGoal() != null && item.getGoal().equals(selectedGoal)) {
                            item.setSelected(false);
                            nutsList.add(item);
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