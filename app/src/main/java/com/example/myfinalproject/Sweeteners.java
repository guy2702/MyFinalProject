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

public class Sweeteners extends AppCompatActivity {

    private RecyclerView rvSweeteners;
    private ItemAdapter adapter;
    private ArrayList<Item> sweetenersList;
    private Button btnFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sweeteners);

        // פדינג לפי מערכת
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // קבלת הבחירה הקודמת (מסה/חיטוב)
        String selectedGoal = getIntent().getStringExtra("CHOICE");

        rvSweeteners = findViewById(R.id.rvSweeteners);
        btnFinish = findViewById(R.id.btnFinishSweeteners);
        sweetenersList = new ArrayList<>();

        // יצירת האדפטר והפעלת מצב בחירה
        adapter = new ItemAdapter(sweetenersList, item -> {
            // בחירה מנוהלת בתוך האדפטר (צבע ירוק)
        });
        adapter.setSelectionMode(true);

        rvSweeteners.setLayoutManager(new LinearLayoutManager(this));
        rvSweeteners.setAdapter(adapter);

        // לחיצה על כפתור "המשך"
        btnFinish.setOnClickListener(v -> {
            boolean hasSelection = false;
            for (Item item : sweetenersList) {
                if (item.isSelected()) {
                    hasSelection = true;
                    break;
                }
            }

            if (hasSelection) {
                // כאן תוכל לעבור למסך הבא, אם יש צורך
                Toast.makeText(this, "נבחרו ממתיקים בהצלחה!", Toast.LENGTH_SHORT).show();
                // Intent intent = new Intent(Sweeteners.this, NextActivity.class);
                // startActivity(intent);
            } else {
                Toast.makeText(this, "חובה לבחור לפחות ממתיק אחד!", Toast.LENGTH_SHORT).show();
            }
        });

        // טעינת הנתונים מה-Firebase
        DatabaseService.getInstance().listenToItemsRealtime(new DatabaseService.DatabaseCallback<List<Item>>() {
            @Override
            public void onCompleted(List<Item> items) {
                sweetenersList.clear();
                for (Item item : items) {
                    // סינון לפי סוג: ממתיקים בלבד + לפי מטרה (מסה/חיטוב)
                    if (item.getType() != null &&
                            (item.getType().toLowerCase().contains("ממתיק") ||
                                    item.getType().toLowerCase().contains("sweetener"))) {

                        if (item.getGoal() != null && item.getGoal().equals(selectedGoal)) {
                            item.setSelected(false); // איפוס בחירה
                            sweetenersList.add(item);
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