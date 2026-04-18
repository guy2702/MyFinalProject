package com.example.myfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myfinalproject.model.Item;
import com.example.myfinalproject.model.NutritionCalculator;
import com.example.myfinalproject.model.Shake;
import com.example.myfinalproject.model.ShakeSelectionManager;
import com.example.myfinalproject.services.DatabaseService;

import java.util.ArrayList;
import java.util.Locale;

public class ShakeResults extends AppCompatActivity {

    private TextView tvResults;
    private DatabaseService databaseService;
    private Shake newShake;
    private Button btnBackToNuts;
    private Button btnHomeNoSave;

    // הווספת משתנים לקריאת המטרה וגודל הכוס
    private String selectedGoal;
    private int cupSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_shake_results);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvResults = findViewById(R.id.tvResults);
        btnBackToNuts = findViewById(R.id.btnBackToNuts);
        btnHomeNoSave = findViewById(R.id.btnHomeNoSave);
        databaseService = DatabaseService.getInstance();

        // 1. קריאת הנתונים מה-Intent שהתחיל את האקטיביטי
        selectedGoal = getIntent().getStringExtra("GOAL");
        cupSize = getIntent().getIntExtra("CUP_SIZE", 400);

        ArrayList<Item> selectedItems = ShakeSelectionManager.getAllSelectedItems();

        NutritionCalculator.NutritionResult result =
                NutritionCalculator.calculate(selectedItems);

        String shakeId = databaseService.generateShakeId();
        newShake = new Shake(shakeId, selectedItems);

        String text =
                "תוצאות השייק\n\n" +
                        "קלוריות: " + String.format(Locale.getDefault(), "%.1f", result.calories) + "\n" +
                        "חלבון: " + String.format(Locale.getDefault(), "%.1f", result.protein) + " גרם\n" +
                        "פחמימות: " + String.format(Locale.getDefault(), "%.1f", result.carbs) + " גרם\n" +
                        "שומנים: " + String.format(Locale.getDefault(), "%.1f", result.fat) + " גרם\n" +
                        "סוכרים: " + String.format(Locale.getDefault(), "%.1f", result.sugar) + " גרם";

        tvResults.setText(text);

        btnBackToNuts.setOnClickListener(v -> {
            Intent intent = new Intent(ShakeResults.this, Nuts.class);
            // 2. העברת הנתונים בחזרה כשחוזרים ל-Nuts
            intent.putExtra("GOAL", selectedGoal); // העברת המטרה
            intent.putExtra("CUP_SIZE", cupSize);   // העברת גודל הכוס
            startActivity(intent);
            finish();
        });

        btnHomeNoSave.setOnClickListener(v -> {
            ShakeSelectionManager.clearAll();
            Intent intent = new Intent(ShakeResults.this, UserHome.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    public void goSaveShake(View view) {
        if (newShake == null) {
            Toast.makeText(this, "שגיאה ביצירת שייק", Toast.LENGTH_SHORT).show();
            return;
        }

        databaseService.createNewShake(newShake, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void object) {
                runOnUiThread(() -> {
                    Toast.makeText(ShakeResults.this, "השייק נשמר בהצלחה", Toast.LENGTH_SHORT).show();

                    ShakeSelectionManager.clearAll();

                    Intent intent = new Intent(ShakeResults.this, UserHome.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                });
            }

            @Override
            public void onFailed(Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(ShakeResults.this, "שגיאה בשמירה", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }
}