package com.example.myfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

        // קבלת המטרה (מסה/חיטוב) מהמסך הקודם
        String selectedGoal = getIntent().getStringExtra("CHOICE");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rvSupplements = findViewById(R.id.rvSupplements);
        btnFinish = findViewById(R.id.btnFinishSupplements);
        supplementsList = new ArrayList<>();

        // יצירת האדפטר
        adapter = new ItemAdapter(supplementsList, item -> {
            // לחיצה מנוהלת באדפטר (בחירה)
        });
        adapter.setSelectionMode(true);

        rvSupplements.setLayoutManager(new LinearLayoutManager(this));
        rvSupplements.setAdapter(adapter);

        btnFinish.setOnClickListener(v -> {
            boolean atLeastOneSelected = false;
            for (Item item : supplementsList) {
                if (item.isSelected()) {
                    atLeastOneSelected = true;
                    break;
                }
            }

            if (atLeastOneSelected) {
                Intent intent = new Intent(ProtienSupplements.this, Sweeteners.class);
                intent.putExtra("CHOICE", selectedGoal); // מעבירים הלאה את המטרה
                startActivity(intent);
            } else {
                Toast.makeText(this, "חובה לבחור לפחות תוסף אחד!", Toast.LENGTH_SHORT).show();
            }
        });

        // טעינת הנתונים מה-Firebase
        DatabaseService.getInstance().listenToItemsRealtime(new DatabaseService.DatabaseCallback<List<Item>>() {
            @Override
            public void onCompleted(List<Item> items) {
                supplementsList.clear();
                for (Item item : items) {
                    // ✅ סינון לפי סוג (חלבון/תוסף) וגם לפי המטרה
                    if (item.getType() != null &&
                            (item.getType().toLowerCase().contains("חלבון") ||
                                    item.getType().toLowerCase().contains("תוסף"))) {

                        if (item.getGoal() != null && item.getGoal().equals(selectedGoal)) {
                            item.setSelected(false); // איפוס בחירה
                            supplementsList.add(item);
                            Log.d("ProtienSupplements", "Item added: " + item.getName());
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