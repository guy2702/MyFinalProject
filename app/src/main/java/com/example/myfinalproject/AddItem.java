package com.example.myfinalproject;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myfinalproject.model.Item;
import com.example.myfinalproject.services.DatabaseService;

import java.io.IOException;

public class AddItem extends AppCompatActivity {

    private static final int REQUEST_CAMERA = 100;
    private static final int REQUEST_GALLERY = 101;
    private static final int PERMISSION_REQUEST = 200;

    private EditText inputName, inputCalories, inputProtein, inputFat, inputCarbs;
    private Spinner spinnerType;
    private Button btnAddItem, btnCamera, btnGallery;
    private ImageView itemImage;
    private Uri imageUri = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        // Find views
        inputName = findViewById(R.id.inputName);
        inputCalories = findViewById(R.id.inputCalories);
        inputProtein = findViewById(R.id.inputProtein);
        inputFat = findViewById(R.id.inputFat);
        inputCarbs = findViewById(R.id.inputCarbs);
        spinnerType = findViewById(R.id.spinnerType);
        btnAddItem = findViewById(R.id.btnAddItem);
        itemImage = findViewById(R.id.itemImage);
        btnCamera = findViewById(R.id.btnCamera);
        btnGallery = findViewById(R.id.btnGallery);

        // Set spinner adapter
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.typeArr,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);

        // Button listeners
        btnAddItem.setOnClickListener(v -> addItem());
        btnCamera.setOnClickListener(v -> openCamera());
        btnGallery.setOnClickListener(v -> openGallery());
    }

    // --- Add Item to Database ---
    private void addItem() {
        String name = inputName.getText().toString().trim();
        String type = spinnerType.getSelectedItem() != null ? spinnerType.getSelectedItem().toString() : "";
        String caloriesStr = inputCalories.getText().toString().trim();
        String proteinStr = inputProtein.getText().toString().trim();
        String fatStr = inputFat.getText().toString().trim();
        String carbsStr = inputCarbs.getText().toString().trim();

        if (name.isEmpty() || type.isEmpty()) {
            Toast.makeText(this, "אנא מלא שם וסוג מוצר", Toast.LENGTH_SHORT).show();
            return;
        }

        double calories = caloriesStr.isEmpty() ? 0 : Double.parseDouble(caloriesStr);
        double protein = proteinStr.isEmpty() ? 0 : Double.parseDouble(proteinStr);
        double fat = fatStr.isEmpty() ? 0 : Double.parseDouble(fatStr);
        double carbs = carbsStr.isEmpty() ? 0 : Double.parseDouble(carbsStr);

        // צור פריט חדש
        Item item = new Item();
        item.setId(DatabaseService.getInstance().generateItemId());
        item.setName(name);
        item.setType(type);
        item.setCalories(calories);
        item.setProtein(protein);
        item.setFat(fat);
        item.setCarbs(carbs);

        // תמונה (אם קיימת)
        if (imageUri != null) {
            item.setPic(imageUri.toString());
        }

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

    // --- Open Camera ---
    private void openCamera() {
        if (checkPermissions()) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, REQUEST_CAMERA);
        }
    }

    // --- Open Gallery ---
    private void openGallery() {
        if (checkPermissions()) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_GALLERY);
        }
    }

    // --- Permission Check ---
    private boolean checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_MEDIA_IMAGES
                }, PERMISSION_REQUEST);
                return false;
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                }, PERMISSION_REQUEST);
                return false;
            }
        }
        return true;
    }

    // --- Handle permission result ---
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST) {
            boolean granted = true;
            for (int res : grantResults) {
                if (res != PackageManager.PERMISSION_GRANTED) {
                    granted = false;
                    break;
                }
            }
            if (!granted) {
                Toast.makeText(this, "יש לאשר הרשאות מצלמה וגלריה", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // --- Handle Camera/Gallery result ---
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null) {
            try {
                if (requestCode == REQUEST_GALLERY) {
                    imageUri = data.getData();
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    itemImage.setImageBitmap(bitmap);
                } else if (requestCode == REQUEST_CAMERA) {
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    itemImage.setImageBitmap(bitmap);
                    // לשמור URI זמני
                    imageUri = data.getData();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "שגיאה בטעינת התמונה", Toast.LENGTH_SHORT).show();
            }
        }
    }
}