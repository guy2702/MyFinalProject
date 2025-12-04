package com.example.myfinalproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.myfinalproject.model.Item;

public class AddItem extends AppCompatActivity {

    private EditText etName, etCalories, etProtein, etFat, etCarbs;
    private Spinner spinnerType;
    private ImageView itemImage;
    private Button btnAddItem;

    private Uri selectedImageUri = null;

    private ActivityResultLauncher<String> galleryLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        etName = findViewById(R.id.inputName);
        etCalories = findViewById(R.id.inputCalories);
        etProtein = findViewById(R.id.inputProtein);
        etFat = findViewById(R.id.inputFat);
        etCarbs = findViewById(R.id.inputCarbs);
        spinnerType = findViewById(R.id.spinnerType);
        itemImage = findViewById(R.id.itemImage);
        btnAddItem = findViewById(R.id.btnAddItem);

        // מחברים את ה-Spinner ל-string-array
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.typeArr,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);

        // --- Launchers ---
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        if (result != null) {
                            selectedImageUri = result;
                            itemImage.setImageURI(selectedImageUri);
                        }
                    }
                });

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bundle extras = result.getData().getExtras();
                        Bitmap photoBitmap = (Bitmap) extras.get("data");
                        itemImage.setImageBitmap(photoBitmap);

                        // שמירה זמנית של התמונה ב-URI null (ניתן להמיר ל-Uri או להעלות ל-Firebase)
                        selectedImageUri = null;
                    }
                });

        // --- לחיצות ---
        itemImage.setOnClickListener(v -> {
            // בחר בין גלריה למצלמה
            chooseImage();
        });

        btnAddItem.setOnClickListener(v -> addItem());
    }

    private void chooseImage() {
        // אפשר לפתוח תפריט עם אפשרות גלריה/מצלמה, פה דוגמא פשוטה לגלריה
        galleryLauncher.launch("image/*");
    }

    private void addItem() {
        String name = etName.getText().toString().trim();
        String type = spinnerType.getSelectedItem().toString();
        String caloriesStr = etCalories.getText().toString().trim();
        String proteinStr = etProtein.getText().toString().trim();
        String fatStr = etFat.getText().toString().trim();
        String carbsStr = etCarbs.getText().toString().trim();

        if (name.isEmpty() || caloriesStr.isEmpty() || proteinStr.isEmpty() || fatStr.isEmpty() || carbsStr.isEmpty()) {
            Toast.makeText(this, "נא למלא את כל השדות", Toast.LENGTH_SHORT).show();
            return;
        }

        double calories = Double.parseDouble(caloriesStr);
        double protein = Double.parseDouble(proteinStr);
        double fat = Double.parseDouble(fatStr);
        double carbs = Double.parseDouble(carbsStr);

        String picString = selectedImageUri != null ? selectedImageUri.toString() : null;

        // יצירת Item עם הפרמטרים + תמונה
        Item newItem = new Item(0, name, type, calories, protein, fat, carbs, picString);

        Toast.makeText(this, "מוצר נוסף: " + newItem.getName(), Toast.LENGTH_SHORT).show();

        // ניקוי השדות
        etName.setText("");
        etCalories.setText("");
        etProtein.setText("");
        etFat.setText("");
        etCarbs.setText("");
        spinnerType.setSelection(0);
        itemImage.setImageResource(R.drawable.ic_launcher_foreground);
        selectedImageUri = null;
    }
}