package com.example.myfinalproject;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfinalproject.Adapter.ItemAdapter;
import com.example.myfinalproject.model.Item;
import com.example.myfinalproject.services.DatabaseService;

import java.util.ArrayList;
import java.util.List;

public class Items extends AppCompatActivity {

    private RecyclerView rvItems;
    private ItemAdapter adapter;
    private ArrayList<Item> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.items), (v, insets) -> {
            v.setPadding(
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).left,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).top,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).right,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            );
            return insets;
        });

        rvItems = findViewById(R.id.rvItems);

        itemList = new ArrayList<>();

        adapter = new ItemAdapter(itemList, item -> {
            Intent intent = new Intent(Items.this, ItemId.class);
            intent.putExtra("itemId", item.getId());
            startActivity(intent);
        });

        rvItems.setLayoutManager(new LinearLayoutManager(this));
        rvItems.setAdapter(adapter);

        DatabaseService.getInstance().listenToItemsRealtime(new DatabaseService.DatabaseCallback<List<Item>>() {
            @Override
            public void onCompleted(List<Item> items) {
                itemList.clear();
                itemList.addAll(items);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailed(Exception e) {
                e.printStackTrace();
            }
        });
    }
}