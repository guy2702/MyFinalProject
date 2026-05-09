package com.example.myfinalproject;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.myfinalproject.model.Item;
import com.example.myfinalproject.model.Shake;
import com.example.myfinalproject.model.User;
import com.example.myfinalproject.model.ShakeSelectionManager;
import com.example.myfinalproject.services.DatabaseService;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

public class ShakeDetails extends AppCompatActivity {

    private CardView cardAdminInfo;
    private TextView tvAdminDetails;
    private TextView tvIngredients;
    private TextView tvNutrition;
    private Button btnBackFromDetails;
    private Button btnDeleteShake;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_shake_details);

            // חיבור רכיבי ה-UI
            cardAdminInfo = findViewById(R.id.cardAdminInfo);
            tvAdminDetails = findViewById(R.id.tvAdminDetails);
            tvIngredients = findViewById(R.id.tvIngredients);
            tvNutrition = findViewById(R.id.tvNutrition);
            btnBackFromDetails = findViewById(R.id.btnBackFromDetails);
            btnDeleteShake = findViewById(R.id.btnDeleteShake);

            // לחיצה על כפתור חזור
            btnBackFromDetails.setOnClickListener(v -> finish());

            boolean isAdminView = getIntent().getBooleanExtra("isAdminView", false);
            Shake shake = ShakeSelectionManager.getCurrentViewedShake();

            if (shake == null || shake.getItems() == null || shake.getItems().isEmpty()) {
                tvIngredients.setText("לא נמצאו פרטים על השייק.");
                tvNutrition.setText("אין נתונים.");
                return;
            }

            // --- לוגיקת הצגה וניהול למנהל בלבד ---
            if (isAdminView) {
                cardAdminInfo.setVisibility(View.VISIBLE);
                btnDeleteShake.setVisibility(View.VISIBLE);

                int shakeNumber = getIntent().getIntExtra("SHAKE_NUMBER", -1);

                // נשים טקסט זמני עד שהנתונים יגיעו מהמסד
                tvAdminDetails.setText("טוען פרטי משתמש...");

                // משיכת פרטי המשתמש המלאים (כולל אימייל התחברות) לפי ה-ID שלו
                DatabaseService.getInstance().getUser(shake.getUserId(), new DatabaseService.DatabaseCallback<User>() {
                    @Override
                    public void onCompleted(User user) {
                        runOnUiThread(() -> {
                            String adminText = "";

                            if (user != null) {
                                String fullName = user.getFname() + " " + user.getLname();
                                String email = user.getEmail();

                                adminText += "👤 שם היוצר: " + fullName + "\n";
                                adminText += "✉️ אימייל (התחברות): " + email + "\n";
                            } else {
                                // אם איכשהו המשתמש לא נמצא, נשתמש בשם ששמור על השייק
                                String fallbackName = (shake.getUserName() != null && !shake.getUserName().isEmpty())
                                        ? shake.getUserName() : "משתמש לא מזוהה";
                                adminText += "👤 שם היוצר: " + fallbackName + "\n";
                            }

                            if (shakeNumber != -1) {
                                adminText += "🥤 שייק מספר: " + shakeNumber;
                            } else {
                                adminText += "🔑 מזהה שייק: " + shake.getShakeId();
                            }

                            tvAdminDetails.setText(adminText);
                        });
                    }

                    @Override
                    public void onFailed(Exception e) {
                        runOnUiThread(() -> {
                            // במקרה של שגיאה בטעינת המשתמש נציג מידע בסיסי
                            String fallbackName = (shake.getUserName() != null && !shake.getUserName().isEmpty())
                                    ? shake.getUserName() : "משתמש לא מזוהה";
                            String adminText = "👤 שם היוצר: " + fallbackName + "\n";
                            if (shakeNumber != -1) {
                                adminText += "🥤 שייק מספר: " + shakeNumber;
                            } else {
                                adminText += "🔑 מזהה שייק: " + shake.getShakeId();
                            }
                            tvAdminDetails.setText(adminText);
                        });
                    }
                });

                // לחיצה על כפתור מחיקה
                btnDeleteShake.setOnClickListener(v -> showDeleteConfirmationDialog(shake.getShakeId(), shake.getUserId()));

            } else {
                cardAdminInfo.setVisibility(View.GONE);
                btnDeleteShake.setVisibility(View.GONE);
            }

            // בניית המרכיבים והערכים התזונתיים
            StringBuilder ingredientsBuilder = new StringBuilder();
            double totalCalories = 0, totalProtein = 0, totalCarbs = 0, totalFat = 0, totalSugar = 0;

            for (Item item : shake.getItems()) {
                if (item != null) {
                    ingredientsBuilder.append("• ")
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
            }

            tvIngredients.setText(ingredientsBuilder.toString().trim());

            // ערכים תזונתיים
            String nutritionText =
                    "קלוריות: " + String.format(Locale.getDefault(), "%.1f", totalCalories) + "\n" +
                            "חלבון: " + String.format(Locale.getDefault(), "%.1f", totalProtein) + " גרם\n" +
                            "פחמימות: " + String.format(Locale.getDefault(), "%.1f", totalCarbs) + " גרם\n" +
                            "שומנים: " + String.format(Locale.getDefault(), "%.1f", totalFat) + " גרם\n" +
                            "סוכרים: " + String.format(Locale.getDefault(), "%.1f", totalSugar) + " גרם";

            tvNutrition.setText(nutritionText);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "שגיאה בטעינת הנתונים, מנסה שוב...", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    // פונקציה שמציגה התראת אישור ומוחקת את השייק ממסד הנתונים
    private void showDeleteConfirmationDialog(String shakeId, String userId) {
        new AlertDialog.Builder(this)
                .setTitle("מחיקת שייק")
                .setMessage("האם אתה בטוח שברצונך למחוק שייק זה לצמיתות? הפעולה תמחק את השייק גם למשתמש.")
                .setPositiveButton("כן, מחק", (dialog, which) -> {
                    // מחיקה בעזרת הפונקציה המעודכנת ב-DatabaseService
                    DatabaseService.getInstance().deleteShake(shakeId, userId, new DatabaseService.DatabaseCallback<Void>() {
                        @Override
                        public void onCompleted(Void object) {
                            Toast.makeText(ShakeDetails.this, "השייק נמחק בהצלחה", Toast.LENGTH_SHORT).show();
                            finish(); // חזרה לרשימה
                        }

                        @Override
                        public void onFailed(Exception e) {
                            Toast.makeText(ShakeDetails.this, "שגיאה במחיקת השייק", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("ביטול", null)
                .show();
    }
}