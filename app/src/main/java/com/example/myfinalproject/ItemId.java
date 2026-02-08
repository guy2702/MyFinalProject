package com.example.myfinalproject;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myfinalproject.Utils.ImageUtil;
import com.example.myfinalproject.model.Item;
import com.example.myfinalproject.services.DatabaseService;

public class ItemId extends AppCompatActivity {

    private ImageView ivImage;
    private TextView tvName, tvType, tvGoal, tvCalories, tvProtein, tvFat, tvCarbs;
    private Button btnDeleteItem;

    private String itemId; // נשמור את ה‑ID של המוצר

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_id);

        // תמיכה ב‑Insets (סטטוס בר / ניווט בר)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // --- Find views ---
        ivImage = findViewById(R.id.ivItemImageDetail);
        tvName = findViewById(R.id.tvItemNameDetail);
        tvType = findViewById(R.id.tvItemTypeDetail);
        tvGoal = findViewById(R.id.tvItemGoalDetail);
        tvCalories = findViewById(R.id.tvItemCaloriesDetail);
        tvProtein = findViewById(R.id.tvItemProteinDetail);
        tvFat = findViewById(R.id.tvItemFatDetail);
        tvCarbs = findViewById(R.id.tvItemCarbsDetail);
        btnDeleteItem = findViewById(R.id.btnDeleteItem);

        // --- קבלת itemId מה‑Intent ---
        itemId = getIntent().getStringExtra("itemId");

        // --- טעינת פרטי מוצר מה‑DatabaseService ---
        if (itemId != null && !itemId.isEmpty()) {
            DatabaseService.getInstance().getItem(itemId, new DatabaseService.DatabaseCallback<Item>() {
                @Override
                public void onCompleted(Item item) {
                    tvName.setText(item.getName());
                    tvType.setText(item.getType());
                    tvGoal.setText("מטרה: " + item.getGoal());
                    tvCalories.setText("קלוריות: " + item.getCalories());
                    tvProtein.setText("חלבון: " + item.getProtein());
                    tvFat.setText("שומן: " + item.getFat());
                    tvCarbs.setText("פחמימות: " + item.getCarbs());

                    // טעינת תמונה מ־Base64
                    if (item.getPic() != null && !item.getPic().isEmpty()) {
                        ivImage.setImageBitmap(ImageUtil.convertFrom64base(item.getPic()));
                    }
                }

                @Override
                public void onFailed(Exception e) {
                    e.printStackTrace();
                    Toast.makeText(ItemId.this, "שגיאה בטעינת המוצר", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // --- כפתור מחיקה ---
        btnDeleteItem.setOnClickListener(v -> {
            if (itemId != null && !itemId.isEmpty()) {
                DatabaseService.getInstance().deleteItem(itemId, new DatabaseService.DatabaseCallback<Void>() {
                    @Override
                    public void onCompleted(Void unused) {
                        runOnUiThread(() -> {
                            Toast.makeText(ItemId.this, "המוצר נמחק בהצלחה!", Toast.LENGTH_SHORT).show();
                            finish(); // סגירת המסך לאחר מחיקה
                        });
                    }

                    @Override
                    public void onFailed(Exception e) {
                        runOnUiThread(() ->
                                Toast.makeText(ItemId.this, "שגיאה במחיקת מוצר: " + e.getMessage(), Toast.LENGTH_LONG).show()
                        );
                    }
                });
            }
        });
    }
}
