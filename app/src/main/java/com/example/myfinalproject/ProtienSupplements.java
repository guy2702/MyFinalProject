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

public class ProtienSupplements extends AppCompatActivity {

    private RecyclerView rvSupplements;
    private ItemAdapter adapter;
    private ArrayList<Item> supplementsList;
    private Button btnFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protien_supplements);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // קבלת המטרה (מסה/חיטוב) מהמסך הקודם
        String selectedGoal = getIntent().getStringExtra("CHOICE");

        rvSupplements = findViewById(R.id.rvSupplements);
        btnFinish = findViewById(R.id.btnFinishSupplements);
        supplementsList = new ArrayList<>();

        adapter = new ItemAdapter(supplementsList, item -> {
            // בחירה מנוהלת באדפטר
        });

        // הפעלת מצב בחירה לצבע ירוק
        adapter.setSelectionMode(true);

        rvSupplements.setLayoutManager(new LinearLayoutManager(this));
        rvSupplements.setAdapter(adapter);

        btnFinish.setOnClickListener(v -> {
            // כאן המשתמש לא חייב לבחור (יכול להיות שייק בלי אבקה),
            // אבל אם תרצה לחייב - תוכל להוסיף את הלוגיקה של hasSelection
            Intent intent = new Intent(ProtienSupplements.this, Sweeteners.class);
            intent.putExtra("CHOICE", selectedGoal);
            startActivity(intent);
        });

        DatabaseService.getInstance().listenToItemsRealtime(new DatabaseService.DatabaseCallback<List<Item>>() {
            @Override
            public void onCompleted(List<Item> items) {
                supplementsList.clear();
                for (Item item : items) {
                    // סינון לפי סוג תוספים והתאמה למטרה
                    if (item.getType() != null &&
                            (item.getType().equalsIgnoreCase("תוספים") || item.getType().equalsIgnoreCase("Supplements"))) {

                        if (item.getGoal() != null && item.getGoal().equals(selectedGoal)) {
                            item.setSelected(false);
                            supplementsList.add(item);
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