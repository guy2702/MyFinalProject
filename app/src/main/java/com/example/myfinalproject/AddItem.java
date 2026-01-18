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

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myfinalproject.Utils.ImageUtil;
import com.example.myfinalproject.model.Item;
import com.example.myfinalproject.services.DatabaseService;

import java.io.IOException;

public class AddItem extends AppCompatActivity {


    // constant to compare
    // the activity result code
    int SELECT_PICTURE = 200;
    private EditText inputName, inputCalories, inputProtein, inputFat, inputCarbs;
    private Spinner spinnerType;
    private Button btnAddItem, btnCamera, btnGallery;
    private ImageView itemImage;

    private ActivityResultLauncher<Intent> captureImageLauncher;



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
        spinnerType = findViewById(R.id.spinnerType);
        btnAddItem = findViewById(R.id.btnAddItem);
        itemImage = findViewById(R.id.itemImage);
        btnCamera = findViewById(R.id.btnCamera);
        btnGallery = findViewById(R.id.btnGallery);

        // --- Set spinner adapter ---
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.typeArr,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);

        // --- Button listeners ---
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

        String imagePic = ImageUtil.convertTo64Base(itemImage);


        if (name.isEmpty() || type.isEmpty()) {
            Toast.makeText(this, "אנא מלא שם וסוג מוצר", Toast.LENGTH_SHORT).show();
            return;
        }

        double calories = caloriesStr.isEmpty() ? 0 : Double.parseDouble(caloriesStr);
        double protein = proteinStr.isEmpty() ? 0 : Double.parseDouble(proteinStr);
        double fat = fatStr.isEmpty() ? 0 : Double.parseDouble(fatStr);
        double carbs = carbsStr.isEmpty() ? 0 : Double.parseDouble(carbsStr);

        // --- צור פריט חדש עם ID מסוג String ---
        Item item = new Item();
        item.setId(DatabaseService.getInstance().generateItemId());
        item.setName(name);
        item.setType(type);
        item.setCalories(calories);
        item.setProtein(protein);
        item.setFat(fat);
        item.setCarbs(carbs);
        item.setPic(imagePic);


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
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        captureImageLauncher.launch(takePictureIntent);
    }

    // --- Open Gallery ---
    private void openGallery() {

        imageChooser();

    }


    void imageChooser() {

        // create an instance of the
        // intent of the type image
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        // pass the constant to compare it
        // with the returned requestCode
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }

    // this function is triggered when user
    // selects the image from the imageChooser
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == SELECT_PICTURE) {
                // Get the url of the image from data
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // update the preview image in the layout
                    itemImage.setImageURI(selectedImageUri);
                }
            }
        }
    }
}


