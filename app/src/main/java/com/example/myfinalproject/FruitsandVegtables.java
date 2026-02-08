package com.example.myfinalproject;

import android.content.Intent;
import android.os.Bundle;

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

public class FruitsandVegtables extends AppCompatActivity {

    private RecyclerView rvItems;
    private ItemAdapter adapter;
    private ArrayList<Item> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fruitsand_vegtables);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // קבלת הבחירה מהדף הקודם
        String selectedGoal = getIntent().getStringExtra("CHOICE"); // "מסה" או "חיטוב"

        rvItems = findViewById(R.id.rvFruitsandVegetables);
        itemList = new ArrayList<>();

        adapter = new ItemAdapter(itemList, item -> {
            Intent intent = new Intent(FruitsandVegtables.this, ItemId.class);
            intent.putExtra("itemId", item.getId());
            startActivity(intent);
        });

        rvItems.setLayoutManager(new LinearLayoutManager(this));
        rvItems.setAdapter(adapter);

        // קבלת כל המוצרים עם סינון לפי המטרה
        DatabaseService.getInstance().listenToItemsRealtime(new DatabaseService.DatabaseCallback<List<Item>>() {
            @Override
            public void onCompleted(List<Item> items) {
                itemList.clear();
                for (Item item : items) {
                    if (item.getGoal() != null && item.getGoal().equals(selectedGoal)) {
                        itemList.add(item);
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