package com.example.myfinalproject;

/**
 * מחלקה זו (ItemId) אחראית על ניהול מסך הצגת ועדכון פרטי פריט בודד במערכת.
 * המסך מיועד למנהל המערכת (Admin) ומאפשר לו לצפות בפרטי הפריט, לערוך את נתוניו
 * (ערכים תזונתיים, שם, סוג ומטרה), ולבצע פעולות תחזוקה כגון עדכון או מחיקה מה-Database.
 */

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

    // רכיבי ממשק המשתמש להצגת נתוני הפריט במצב "קריאה בלבד"
    private ImageView ivImage;
    private TextView tvName, tvType, tvGoal, tvCalories, tvProtein, tvFat, tvCarbs, tvSugar;

    // שדות קלט לעריכת פרטי הפריט (EditText)
    private EditText etName, etCalories, etProtein, etFat, etCarbs, etSugar;

    // רכיבי בחירה מתקדמים לעריכת סוג ומטרה (Spinner ו-RadioGroup)
    private Spinner spinnerType;
    private RadioGroup rgGoal;
    private RadioButton rbMuscle, rbCut;

    private Button btnDeleteItem, btnUpdateItem; // כפתורי פעולה למחיקה ועדכון
    private String itemId; // מזהה הפריט הנוכחי
    private String currentPic; // שמירת מחרוזת התמונה (Base64)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_item_id);

        // הגדרת Padding דינמי למסך כדי להתחשב בסרגלי המערכת (System Bars)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // אתחול רכיבי התצוגה (TextViews) מה-XML
        ivImage = findViewById(R.id.ivItemImageDetail);
        tvName = findViewById(R.id.tvItemNameDetail);
        tvType = findViewById(R.id.tvItemTypeDetail);
        tvGoal = findViewById(R.id.tvItemGoalDetail);
        tvCalories = findViewById(R.id.tvItemCaloriesDetail);
        tvProtein = findViewById(R.id.tvItemProteinDetail);
        tvFat = findViewById(R.id.tvItemFatDetail);
        tvCarbs = findViewById(R.id.tvItemCarbsDetail);
        tvSugar = findViewById(R.id.tvItemSugarDetail);

        // אתחול שדות העריכה (EditTexts) מה-XML
        etName = findViewById(R.id.etName);
        etCalories = findViewById(R.id.etCalories);
        etProtein = findViewById(R.id.etProtein);
        etFat = findViewById(R.id.etFat);
        etCarbs = findViewById(R.id.etCarbs);
        etSugar = findViewById(R.id.etSugar);

        // אתחול רכיבי הבחירה מה-XML
        spinnerType = findViewById(R.id.spinnerType);
        rgGoal = findViewById(R.id.rgGoal);
        rbMuscle = findViewById(R.id.rbMuscle);
        rbCut = findViewById(R.id.rbCut);

        // אתחול כפתורי הפעולה מה-XML
        btnDeleteItem = findViewById(R.id.btnDeleteItem);
        btnUpdateItem = findViewById(R.id.btnUpdateItem);

        // הגדרת מערך ערכים לספינר (סוגי הפריטים)
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.typeArr, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);

        // קבלת מזהה הפריט שנשלח מהמסך הקודם
        itemId = getIntent().getStringExtra("itemId");

        // אם המזהה אינו תקין, סגור את המסך
        if (itemId == null || itemId.isEmpty()) {
            finish();
            return;
        }

        // טעינת הנתונים מ-DatabaseService באופן אסינכרוני
        DatabaseService.getInstance().getItem(itemId, new DatabaseService.DatabaseCallback<Item>() {
            @Override
            public void onCompleted(Item item) {
                if (item == null) { finish(); return; }

                // הצגת ערכי הפריט ב-TextView לצפייה בלבד
                tvName.setText(item.getName());
                tvType.setText(item.getType());
                tvGoal.setText(item.getGoal());
                tvCalories.setText(String.valueOf(item.getCalories()));
                tvProtein.setText(String.valueOf(item.getProtein()));
                tvFat.setText(String.valueOf(item.getFat()));
                tvCarbs.setText(String.valueOf(item.getCarbs()));
                tvSugar.setText(String.valueOf(item.getSugar()));

                // מילוי שדות העריכה בערכי הפריט הנוכחיים
                etName.setText(item.getName());
                etCalories.setText(String.valueOf(item.getCalories()));
                etProtein.setText(String.valueOf(item.getProtein()));
                etFat.setText(String.valueOf(item.getFat()));
                etCarbs.setText(String.valueOf(item.getCarbs()));
                etSugar.setText(String.valueOf(item.getSugar()));

                // בחירת סוג הפריט בספינר
                if (item.getType() != null) {
                    int spinnerPosition = adapter.getPosition(item.getType());
                    spinnerType.setSelection(spinnerPosition);
                }

                // בחירת המטרה (מסה או חיטוב) ב-RadioButtons
                if ("מסה".equals(item.getGoal())) rbMuscle.setChecked(true);
                else if ("חיטוב".equals(item.getGoal())) rbCut.setChecked(true);

                // המרת תמונה מקודדת (Base64) והצגתה ברכיב ImageView
                currentPic = item.getPic();
                if (currentPic != null && !currentPic.isEmpty()) {
                    ivImage.setImageBitmap(ImageUtil.convertFrom64base(currentPic));
                }
            }

            @Override
            public void onFailed(Exception e) { finish(); }
        });

        // לוגיקת עדכון פריט: איסוף נתונים חדשים מהשדות ושליחה לעדכון בשרת
        btnUpdateItem.setOnClickListener(v -> {
            Item updated = new Item();
            updated.setId(itemId);
            updated.setName(etName.getText().toString().trim());

            // קבלת סוג ומטרה נבחרים מהרכיבים
            updated.setType(spinnerType.getSelectedItem().toString());
            String goal = "";
            if (rbMuscle.isChecked()) goal = "מסה";
            else if (rbCut.isChecked()) goal = "חיטוב";
            updated.setGoal(goal);

            // ניתוח שדות טקסט למספרים ושמירת הנתונים
            updated.setCalories(parse(etCalories.getText().toString()));
            updated.setProtein(parse(etProtein.getText().toString()));
            updated.setFat(parse(etFat.getText().toString()));
            updated.setCarbs(parse(etCarbs.getText().toString()));
            updated.setSugar(parse(etSugar.getText().toString()));
            updated.setPic(currentPic);

            // ביצוע עדכון בבסיס הנתונים
            DatabaseService.getInstance().updateItem(updated, new DatabaseService.DatabaseCallback<Void>() {
                @Override
                public void onCompleted(Void object) {
                    runOnUiThread(() -> {
                        Toast.makeText(ItemId.this, "עודכן בהצלחה", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }
                @Override public void onFailed(Exception e) { /* טיפול בשגיאת עדכון */ }
            });
        });

        // לוגיקת מחיקת פריט: מחיקת הפריט הספציפי מהשרת לפי ה-ID שלו
        btnDeleteItem.setOnClickListener(v -> {
            DatabaseService.getInstance().deleteItem(itemId, new DatabaseService.DatabaseCallback<Void>() {
                @Override
                public void onCompleted(Void object) {
                    Toast.makeText(ItemId.this, "נמחק", Toast.LENGTH_SHORT).show();
                    finish();
                }
                @Override public void onFailed(Exception e) { /* טיפול בשגיאת מחיקה */ }
            });
        });
    }

    /**
     * פונקציית עזר להמרת String ל-Double.
     * מונעת קריסת האפליקציה במידה והמשתמש הזין ערך שאינו מספר.
     */
    private double parse(String s) {
        try { return Double.parseDouble(s); } catch (Exception e) { return 0; }
    }
}