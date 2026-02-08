package com.example.myfinalproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myfinalproject.Utils.ImageUtil;
import com.example.myfinalproject.model.Item;
import com.example.myfinalproject.services.DatabaseService;

import java.util.ArrayList;

public class AddItem extends AppCompatActivity {

    private static final int SELECT_PICTURE = 200;

    private EditText inputName, inputCalories, inputProtein, inputFat, inputCarbs, inputSugar;
    private Spinner spinnerType;
    private Button btnAddItem, btnCamera, btnGallery;
    private ImageView itemImage;
    private RadioGroup rgGoal;
    private RadioButton rbMuscle, rbCut;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        // --- Find views ---
        inputName = findViewById(R.id.inputName);
        inputCalories = findViewById(R.id.inputCalories);
        inputProtein = findViewById(R.id.inputProtein);
        inputFat = findViewById(R.id.inputFat);
        inputCarbs = findViewById(R.id.inputCarbs);
        inputSugar = findViewById(R.id.inputSugar); // <-- שדה סוכר
        spinnerType = findViewById(R.id.spinnerType);
        btnAddItem = findViewById(R.id.btnAddItem);
        itemImage = findViewById(R.id.itemImage);
        btnCamera = findViewById(R.id.btnCamera);
        btnGallery = findViewById(R.id.btnGallery);

        rgGoal = findViewById(R.id.rgGoal);
        rbMuscle = findViewById(R.id.rbMuscle);
        rbCut = findViewById(R.id.rbCut);

        // --- Prepare Spinner Adapter from string-array ---
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.typeArr,  // מערך סוגי המוצרים
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);

        // --- Button listeners ---
        btnAddItem.setOnClickListener(v -> addItem());
        btnCamera.setOnClickListener(v -> openCamera());
        btnGallery.setOnClickListener(v -> openGallery());
    }

    private void addItem() {
        String name = inputName.getText().toString().trim();
        String type = spinnerType.getSelectedItem() != null ? spinnerType.getSelectedItem().toString() : "";
        String caloriesStr = inputCalories.getText().toString().trim();
        String proteinStr = inputProtein.getText().toString().trim();
        String fatStr = inputFat.getText().toString().trim();
        String carbsStr = inputCarbs.getText().toString().trim();
        String sugarStr = inputSugar.getText().toString().trim();

        String imagePic = ImageUtil.convertTo64Base(itemImage);

        // --- Get selected goal ---
        String goal = "";
        int selectedId = rgGoal.getCheckedRadioButtonId();
        if (selectedId == R.id.rbMuscle) goal = "מסה";
        else if (selectedId == R.id.rbCut) goal = "חיטוב";

        if (name.isEmpty() || type.isEmpty() || goal.isEmpty()) {
            Toast.makeText(this, "אנא מלא שם, סוג ומטרה", Toast.LENGTH_SHORT).show();
            return;
        }

        double calories = caloriesStr.isEmpty() ? 0 : Double.parseDouble(caloriesStr);
        double protein = proteinStr.isEmpty() ? 0 : Double.parseDouble(proteinStr);
        double fat = fatStr.isEmpty() ? 0 : Double.parseDouble(fatStr);
        double carbs = carbsStr.isEmpty() ? 0 : Double.parseDouble(carbsStr);
        double sugar = sugarStr.isEmpty() ? 0 : Double.parseDouble(sugarStr);

        Item item = new Item();
        item.setId(DatabaseService.getInstance().generateItemId());
        item.setName(name);
        item.setType(type);
        item.setCalories(calories);
        item.setProtein(protein);
        item.setFat(fat);
        item.setCarbs(carbs);
        item.setPic(imagePic);
        item.setGoal(goal);

        // --- Save sugar value אם Item כולל שדה sugar ---
        // item.setSugar(sugar);

        DatabaseService.getInstance().createNewItem(item, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void object) {
                runOnUiThread(() -> {
                    Toast.makeText(AddItem.this, "המוצר נוסף בהצלחה!", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }

            @Override
            public void onFailed(Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(AddItem.this, "שגיאה בהוספת מוצר: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, SELECT_PICTURE);
    }

    private void openGallery() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "בחר תמונה"), SELECT_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == SELECT_PICTURE && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                itemImage.setImageURI(selectedImageUri);
            }
        }
    }
}