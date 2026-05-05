package com.example.myfinalproject;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myfinalproject.Utils.ImageUtil;
import com.example.myfinalproject.model.Item;
import com.example.myfinalproject.services.DatabaseService;

public class ItemId extends AppCompatActivity {

    private ImageView ivImage;
    private TextView tvName, tvType, tvGoal, tvCalories, tvProtein, tvFat, tvCarbs, tvSugar;
    private EditText etName, etCalories, etProtein, etFat, etCarbs, etSugar;

    // רכיבים חדשים
    private Spinner spinnerType;
    private RadioGroup rgGoal;
    private RadioButton rbMuscle, rbCut;

    private Button btnDeleteItem, btnUpdateItem;
    private String itemId;
    private String currentPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_item_id);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // אתחול תצוגה
        ivImage = findViewById(R.id.ivItemImageDetail);
        tvName = findViewById(R.id.tvItemNameDetail);
        tvType = findViewById(R.id.tvItemTypeDetail);
        tvGoal = findViewById(R.id.tvItemGoalDetail);
        tvCalories = findViewById(R.id.tvItemCaloriesDetail);
        tvProtein = findViewById(R.id.tvItemProteinDetail);
        tvFat = findViewById(R.id.tvItemFatDetail);
        tvCarbs = findViewById(R.id.tvItemCarbsDetail);
        tvSugar = findViewById(R.id.tvItemSugarDetail);

        // אתחול עריכה
        etName = findViewById(R.id.etName);
        etCalories = findViewById(R.id.etCalories);
        etProtein = findViewById(R.id.etProtein);
        etFat = findViewById(R.id.etFat);
        etCarbs = findViewById(R.id.etCarbs);
        etSugar = findViewById(R.id.etSugar);

        spinnerType = findViewById(R.id.spinnerType);
        rgGoal = findViewById(R.id.rgGoal);
        rbMuscle = findViewById(R.id.rbMuscle);
        rbCut = findViewById(R.id.rbCut);

        btnDeleteItem = findViewById(R.id.btnDeleteItem);
        btnUpdateItem = findViewById(R.id.btnUpdateItem);

        // הגדרת הספינר (משתמש באותו מערך של AddItem)
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.typeArr, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);

        itemId = getIntent().getStringExtra("itemId");

        if (itemId == null || itemId.isEmpty()) {
            finish();
            return;
        }

        // טעינת נתונים
        DatabaseService.getInstance().getItem(itemId, new DatabaseService.DatabaseCallback<Item>() {
            @Override
            public void onCompleted(Item item) {
                if (item == null) { finish(); return; }

                // תצוגה
                tvName.setText(item.getName());
                tvType.setText(item.getType());
                tvGoal.setText(item.getGoal());
                tvCalories.setText(String.valueOf(item.getCalories()));
                tvProtein.setText(String.valueOf(item.getProtein()));
                tvFat.setText(String.valueOf(item.getFat()));
                tvCarbs.setText(String.valueOf(item.getCarbs()));
                tvSugar.setText(String.valueOf(item.getSugar()));

                // עריכה - טקסט
                etName.setText(item.getName());
                etCalories.setText(String.valueOf(item.getCalories()));
                etProtein.setText(String.valueOf(item.getProtein()));
                etFat.setText(String.valueOf(item.getFat()));
                etCarbs.setText(String.valueOf(item.getCarbs()));
                etSugar.setText(String.valueOf(item.getSugar()));

                // עריכה - בחירת סוג בספינר
                if (item.getType() != null) {
                    int spinnerPosition = adapter.getPosition(item.getType());
                    spinnerType.setSelection(spinnerPosition);
                }

                // עריכה - בחירת מטרה ברדיו
                if ("מסה".equals(item.getGoal())) rbMuscle.setChecked(true);
                else if ("חיטוב".equals(item.getGoal())) rbCut.setChecked(true);

                currentPic = item.getPic();
                if (currentPic != null && !currentPic.isEmpty()) {
                    ivImage.setImageBitmap(ImageUtil.convertFrom64base(currentPic));
                }
            }

            @Override
            public void onFailed(Exception e) { finish(); }
        });

        // לוגיקת עדכון
        btnUpdateItem.setOnClickListener(v -> {
            Item updated = new Item();
            updated.setId(itemId);
            updated.setName(etName.getText().toString().trim());

            // קבלת ערכים מהספינר והרדיו
            updated.setType(spinnerType.getSelectedItem().toString());
            String goal = "";
            if (rbMuscle.isChecked()) goal = "מסה";
            else if (rbCut.isChecked()) goal = "חיטוב";
            updated.setGoal(goal);

            updated.setCalories(parse(etCalories.getText().toString()));
            updated.setProtein(parse(etProtein.getText().toString()));
            updated.setFat(parse(etFat.getText().toString()));
            updated.setCarbs(parse(etCarbs.getText().toString()));
            updated.setSugar(parse(etSugar.getText().toString()));
            updated.setPic(currentPic);

            DatabaseService.getInstance().updateItem(updated, new DatabaseService.DatabaseCallback<Void>() {
                @Override
                public void onCompleted(Void object) {
                    runOnUiThread(() -> {
                        Toast.makeText(ItemId.this, "עודכן בהצלחה", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }
                @Override public void onFailed(Exception e) { /* ... */ }
            });
        });

        btnDeleteItem.setOnClickListener(v -> {
            DatabaseService.getInstance().deleteItem(itemId, new DatabaseService.DatabaseCallback<Void>() {
                @Override
                public void onCompleted(Void object) {
                    Toast.makeText(ItemId.this, "נמחק", Toast.LENGTH_SHORT).show();
                    finish();
                }
                @Override public void onFailed(Exception e) { /* ... */ }
            });
        });
    }

    private double parse(String s) {
        try { return Double.parseDouble(s); } catch (Exception e) { return 0; }
    }
}