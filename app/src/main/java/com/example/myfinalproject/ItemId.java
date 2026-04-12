package com.example.myfinalproject;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myfinalproject.Utils.ImageUtil;
import com.example.myfinalproject.model.Item;
import com.example.myfinalproject.services.DatabaseService;

public class ItemId extends AppCompatActivity {

    private ImageView ivImage;

    private TextView tvName, tvType, tvGoal,
            tvCalories, tvProtein, tvFat, tvCarbs, tvSugar;

    private EditText etName, etType, etGoal,
            etCalories, etProtein, etFat, etCarbs, etSugar;

    private Button btnDeleteItem, btnUpdateItem;

    private String itemId;

    // 🔥 חשוב: לשמור את התמונה
    private String currentPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_id);

        ivImage = findViewById(R.id.ivItemImageDetail);

        tvName = findViewById(R.id.tvItemNameDetail);
        tvType = findViewById(R.id.tvItemTypeDetail);
        tvGoal = findViewById(R.id.tvItemGoalDetail);
        tvCalories = findViewById(R.id.tvItemCaloriesDetail);
        tvProtein = findViewById(R.id.tvItemProteinDetail);
        tvFat = findViewById(R.id.tvItemFatDetail);
        tvCarbs = findViewById(R.id.tvItemCarbsDetail);
        tvSugar = findViewById(R.id.tvItemSugarDetail);

        etName = findViewById(R.id.etName);
        etType = findViewById(R.id.etType);
        etGoal = findViewById(R.id.etGoal);
        etCalories = findViewById(R.id.etCalories);
        etProtein = findViewById(R.id.etProtein);
        etFat = findViewById(R.id.etFat);
        etCarbs = findViewById(R.id.etCarbs);
        etSugar = findViewById(R.id.etSugar);

        btnDeleteItem = findViewById(R.id.btnDeleteItem);
        btnUpdateItem = findViewById(R.id.btnUpdateItem);

        itemId = getIntent().getStringExtra("itemId");

        if (itemId == null || itemId.isEmpty()) {
            Toast.makeText(this, "אין מזהה מוצר", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        DatabaseService.getInstance().getItem(itemId,
                new DatabaseService.DatabaseCallback<Item>() {
                    @Override
                    public void onCompleted(Item item) {

                        if (item == null) {
                            Toast.makeText(ItemId.this, "מוצר לא נמצא", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }

                        tvName.setText(item.getName());
                        tvType.setText(item.getType());
                        tvGoal.setText(item.getGoal());

                        tvCalories.setText(String.valueOf(item.getCalories()));
                        tvProtein.setText(String.valueOf(item.getProtein()));
                        tvFat.setText(String.valueOf(item.getFat()));
                        tvCarbs.setText(String.valueOf(item.getCarbs()));
                        tvSugar.setText(String.valueOf(item.getSugar()));

                        etName.setText(item.getName());
                        etType.setText(item.getType());
                        etGoal.setText(item.getGoal());

                        etCalories.setText(String.valueOf(item.getCalories()));
                        etProtein.setText(String.valueOf(item.getProtein()));
                        etFat.setText(String.valueOf(item.getFat()));
                        etCarbs.setText(String.valueOf(item.getCarbs()));
                        etSugar.setText(String.valueOf(item.getSugar()));

                        // 🔥 שמירת תמונה קיימת
                        currentPic = item.getPic();

                        if (currentPic != null && !currentPic.isEmpty()) {
                            ivImage.setImageBitmap(ImageUtil.convertFrom64base(currentPic));
                        }
                    }

                    @Override
                    public void onFailed(Exception e) {
                        Toast.makeText(ItemId.this, "שגיאה בטעינה", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

        // 🗑 מחיקה
        btnDeleteItem.setOnClickListener(v -> {
            DatabaseService.getInstance().deleteItem(itemId,
                    new DatabaseService.DatabaseCallback<Void>() {
                        @Override
                        public void onCompleted(Void object) {
                            Toast.makeText(ItemId.this, "נמחק בהצלחה", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        @Override
                        public void onFailed(Exception e) {
                            Toast.makeText(ItemId.this, "שגיאה במחיקה", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // ✏️ עדכון
        btnUpdateItem.setOnClickListener(v -> {

            Item updated = new Item();
            updated.setId(itemId);

            updated.setName(etName.getText().toString().trim());
            updated.setType(etType.getText().toString().trim());
            updated.setGoal(etGoal.getText().toString().trim());

            updated.setCalories(parse(etCalories.getText().toString()));
            updated.setProtein(parse(etProtein.getText().toString()));
            updated.setFat(parse(etFat.getText().toString()));
            updated.setCarbs(parse(etCarbs.getText().toString()));
            updated.setSugar(parse(etSugar.getText().toString()));

            // 🔥 לא לאבד תמונה
            updated.setPic(currentPic);

            DatabaseService.getInstance().updateItem(updated,
                    new DatabaseService.DatabaseCallback<Void>() {
                        @Override
                        public void onCompleted(Void object) {
                            runOnUiThread(() -> {
                                Toast.makeText(ItemId.this, "עודכן בהצלחה", Toast.LENGTH_SHORT).show();
                                finish(); // חזרה לרשימה
                            });
                        }

                        @Override
                        public void onFailed(Exception e) {
                            runOnUiThread(() ->
                                    Toast.makeText(ItemId.this, "שגיאה בעדכון", Toast.LENGTH_SHORT).show()
                            );
                        }
                    });
        });
    }

    private double parse(String s) {
        try {
            return Double.parseDouble(s);
        } catch (Exception e) {
            return 0;
        }
    }
}