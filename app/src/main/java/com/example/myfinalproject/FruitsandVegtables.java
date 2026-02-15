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

public class FruitsandVegtables extends AppCompatActivity {

    private RecyclerView rvItems;
    private ItemAdapter adapter;
    private ArrayList<Item> itemList;
    private Button btnFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fruitsand_vegtables);

        // הגדרת פדינג למערכת
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // קבלת המטרה (מסה/חיטוב) מהמסך הקודם
        String selectedGoal = getIntent().getStringExtra("CHOICE");

        rvItems = findViewById(R.id.rvFruitsandVegetables);
        btnFinish = findViewById(R.id.btnFinish);
        itemList = new ArrayList<>();

        // יצירת האדפטר
        adapter = new ItemAdapter(itemList, item -> {
            // הלחיצה מנוהלת אוטומטית בתוך האדפטר (שינוי צבע) כי הפעלנו SelectionMode
        });

        // ✅ הפעלת מצב בחירה כדי שיופיע הצבע הירוק בלחיצה
        adapter.setSelectionMode(true);

        rvItems.setLayoutManager(new LinearLayoutManager(this));
        rvItems.setAdapter(adapter);

        // לחיצה על כפתור "המשך"
        btnFinish.setOnClickListener(v -> {
            // בדיקה האם לפחות פריט אחד נבחר (isSelected == true)
            boolean atLeastOneSelected = false;
            for (Item item : itemList) {
                if (item.isSelected()) {
                    atLeastOneSelected = true;
                    break;
                }
            }

            if (atLeastOneSelected) {
                // מעבר למסך הנוזלים (או למסך הבא בתור)
                Intent intent = new Intent(FruitsandVegtables.this, liquids.class);
                // אופציונלי: להעביר את ה-Choice הלאה אם צריך
                intent.putExtra("CHOICE", selectedGoal);
                startActivity(intent);
            } else {
                // הודעה למשתמש אם לא בחר כלום
                Toast.makeText(this, "חובה לבחור לפחות פריט אחד!", Toast.LENGTH_SHORT).show();
            }
        });

        // טעינת הנתונים מה-Firebase
        DatabaseService.getInstance().listenToItemsRealtime(new DatabaseService.DatabaseCallback<List<Item>>() {
            @Override
            public void onCompleted(List<Item> items) {
                itemList.clear();
                for (Item item : items) {
                    // סינון לפי המטרה שנבחרה (מסה/חיטוב) וסינון שזה לא נוזל
                    if (item.getGoal() != null && item.getGoal().equals(selectedGoal)) {
                        // וודא שאתה לא מציג נוזלים במסך הפירות והירקות
                        if (item.getType() != null && !item.getType().contains("נוזל")) {
                            item.setSelected(false); // איפוס בחירה בטעינה
                            itemList.add(item);
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