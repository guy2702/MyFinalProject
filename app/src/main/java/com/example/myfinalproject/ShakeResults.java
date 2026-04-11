package com.example.myfinalproject;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myfinalproject.model.Item;

import java.util.ArrayList;
import java.util.Locale;

public class ShakeResults extends AppCompatActivity {

    private TextView tvResults;

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

        ArrayList<Item> selectedItems = ShakeSelectionManager.getAllSelectedItems();

        NutritionCalculator.NutritionResult result = NutritionCalculator.calculate(selectedItems);

        String text =
                "תוצאות השייק:\n\n" +
                        "קלוריות: " + String.format(Locale.getDefault(), "%.1f", result.calories) + "\n" +
                        "חלבון: " + String.format(Locale.getDefault(), "%.1f", result.protein) + " גרם\n" +
                        "פחמימות: " + String.format(Locale.getDefault(), "%.1f", result.carbs) + " גרם\n" +
                        "שומנים: " + String.format(Locale.getDefault(), "%.1f", result.fat) + " גרם\n" +
                        "סוכרים: " + String.format(Locale.getDefault(), "%.1f", result.sugar) + " גרם";

        tvResults.setText(text);
    }
}