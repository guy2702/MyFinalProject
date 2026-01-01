package com.example.myfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// ודא שיש לך import נכון ל-Items
import com.example.myfinalproject.Items;

public class AdminPage extends AppCompatActivity {

    private Button btnAddItem, btnItems, btnUsers, btnAllShakes, btnLogout;
    private TextView tvGreeting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_page);

        // התאמת Padding למערכת ה-bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // קישור לכפתורים
        btnAddItem = findViewById(R.id.btnAddItem);
        btnItems = findViewById(R.id.btnItems);
        btnUsers = findViewById(R.id.btnUserTable);

        btnLogout = findViewById(R.id.btnLogout);

        tvGreeting = findViewById(R.id.tvGreeting);

        // קבלת שם המשתמש מה-Intent
        String userName = getIntent().getStringExtra("USER_NAME");
        if (userName != null) {
            tvGreeting.setText("שלום " + userName + "!");
        }

        // פעולות הכפתורים
        btnAddItem.setOnClickListener(v -> startActivity(new Intent(AdminPage.this, AddItem.class)));
        btnItems.setOnClickListener(v -> startActivity(new Intent(AdminPage.this, Items.class)));
        btnUsers.setOnClickListener(v -> startActivity(new Intent(AdminPage.this, users.class)));

        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(AdminPage.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
