package com.example.myfinalproject;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myfinalproject.model.Item;
import com.example.myfinalproject.model.Shake;
import com.example.myfinalproject.model.ShakeSelectionManager;

import java.util.Locale;

public class ShakeDetails extends AppCompatActivity {

    private TextView tvShakeDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake_details);

        tvShakeDetails = findViewById(R.id.tvShakeDetails);

        boolean isAdminView = getIntent().getBooleanExtra("isAdminView", false);

        Shake shake = ShakeSelectionManager.getCurrentViewedShake();

        if (shake == null || shake.getItems() == null || shake.getItems().isEmpty()) {
            tvShakeDetails.setText("לא נמצאו פרטים על השייק");
            return;
        }

        StringBuilder builder = new StringBuilder();

        if (isAdminView) {
            String userName = (shake.getUserName() != null && !shake.getUserName().isEmpty())
                    ? shake.getUserName()
                    : "משתמש לא מזוהה";

            builder.append("שייק של: ").append(userName).append("\n\n");
            builder.append("מספר שייק: ").append(shake.getShakeId()).append("\n\n");
        }

        builder.append("רכיבי השייק:\n\n");

        double totalCalories = 0;
        double totalProtein = 0;
        double totalCarbs = 0;
        double totalFat = 0;
        double totalSugar = 0;

        for (Item item : shake.getItems()) {
            builder.append("• ")
                    .append(item.getName())
                    .append(" - ")
                    .append(item.getAmount())
                    .append(" גרם\n");

            double factor = item.getAmount() / 100.0;
            totalCalories += item.getCalories() * factor;
            totalProtein += item.getProtein() * factor;
            totalCarbs += item.getCarbs() * factor;
            totalFat += item.getFat() * factor;
            totalSugar += item.getSugar() * factor;
        }

        builder.append("\nסה\"כ:\n");
        builder.append("קלוריות: ")
                .append(String.format(Locale.getDefault(), "%.1f", totalCalories))
                .append("\n");
        builder.append("חלבון: ")
                .append(String.format(Locale.getDefault(), "%.1f", totalProtein))
                .append(" גרם\n");
        builder.append("פחמימות: ")
                .append(String.format(Locale.getDefault(), "%.1f", totalCarbs))
                .append(" גרם\n");
        builder.append("שומנים: ")
                .append(String.format(Locale.getDefault(), "%.1f", totalFat))
                .append(" גרם\n");
        builder.append("סוכרים: ")
                .append(String.format(Locale.getDefault(), "%.1f", totalSugar))
                .append(" גרם\n");

        tvShakeDetails.setText(builder.toString());
    }
}