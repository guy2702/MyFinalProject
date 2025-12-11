package com.example.myfinalproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.myfinalproject.Utils.ImageUtil;
import com.example.myfinalproject.model.Item;
import com.example.myfinalproject.services.DatabaseService;

public class AddItem extends AppCompatActivity {

    private EditText inputName, inputCalories, inputProtein, inputFat, inputCarbs;
    private Spinner spinnerType;
    private ImageView itemImage;
    private Button btnAddItem, btnCamera, btnGallery;

    private Bitmap cameraBitmap = null;

    private ActivityResultLauncher<String> galleryLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<String> cameraPermissionLauncher;

    private DatabaseService databaseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        initViews();
        databaseService = DatabaseService.getInstance();

        // Spinner setup
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.typeArr,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);

        // Camera permission
        cameraPermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                        isGranted -> {
                            if (isGranted) openCamera();
                            else Toast.makeText(this, "אין הרשאת מצלמה", Toast.LENGTH_SHORT).show();
                        });

        // Gallery launcher
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        itemImage.setImageURI(uri);
                        cameraBitmap = null;
                    }
                }
        );

        // Camera launcher (thumbnail)
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bundle extras = result.getData().getExtras();
                        if (extras != null) {
                            cameraBitmap = (Bitmap) extras.get("data");
                            itemImage.setImageBitmap(cameraBitmap);
                        }
                    }
                }
        );

        // Buttons
        btnCamera.setOnClickListener(v -> openCamera());
        btnGallery.setOnClickListener(v -> openGallery());
        btnAddItem.setOnClickListener(v -> saveItem());
    }

    private void initViews() {
        inputName = findViewById(R.id.inputName);
        inputCalories = findViewById(R.id.inputCalories);
        inputProtein = findViewById(R.id.inputProtein);
        inputFat = findViewById(R.id.inputFat);
        inputCarbs = findViewById(R.id.inputCarbs);
        spinnerType = findViewById(R.id.spinnerType);
        itemImage = findViewById(R.id.itemImage);
        btnCamera = findViewById(R.id.btnCamera);
        btnGallery = findViewById(R.id.btnGallery);
        btnAddItem = findViewById(R.id.btnAddItem);
    }

    private void openGallery() {
        galleryLauncher.launch("image/*");
    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
            return;
        }

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraLauncher.launch(cameraIntent);
    }

    private void saveItem() {
        String name = inputName.getText().toString().trim();
        String calories = inputCalories.getText().toString().trim();
        String protein = inputProtein.getText().toString().trim();
        String fat = inputFat.getText().toString().trim();
        String carbs = inputCarbs.getText().toString().trim();
        String type = spinnerType.getSelectedItem().toString();

        if (name.isEmpty() || calories.isEmpty() || protein.isEmpty()
                || fat.isEmpty() || carbs.isEmpty()) {
            Toast.makeText(this, "אנא מלא את כל השדות", Toast.LENGTH_SHORT).show();
            return;
        }

        if (itemImage.getDrawable() == null) {
            Toast.makeText(this, "נא לבחור תמונה", Toast.LENGTH_SHORT).show();
            return;
        }

        int id = Integer.parseInt(databaseService.generateItemId());

        // Convert image to Base64
        String base64Image = ImageUtil.convertTo64Base(itemImage);

        Item item = new Item(
                id,
                name,
                type,
                Double.parseDouble(calories),
                Double.parseDouble(protein),
                Double.parseDouble(fat),
                Double.parseDouble(carbs),
                base64Image
        );

        databaseService.createNewItem(item, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void object) {
                Toast.makeText(AddItem.this, "המוצר נוסף בהצלחה!", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(AddItem.this, "שגיאה בהוספת מוצר", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
