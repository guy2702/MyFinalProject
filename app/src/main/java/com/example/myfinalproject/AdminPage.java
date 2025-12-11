package com.example.myfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AdminPage extends AppCompatActivity implements View.OnClickListener {

    private Button btnAddItem;
    private Button btnUsers;
    private Button btnLogout; // כפתור התנתקות

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_page);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnAddItem = findViewById(R.id.btnAddItem);
        btnUsers = findViewById(R.id.btnUserTable);
        btnLogout = findViewById(R.id.btnLogout);

        // שימוש ב-this לכל הכפתורים
        btnAddItem.setOnClickListener(this);
        btnUsers.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == btnAddItem.getId()) {
            Intent intent = new Intent(AdminPage.this, AddItem.class);
            startActivity(intent);
        } else if (id == btnUsers.getId()) {
            Intent intent = new Intent(AdminPage.this, users.class);
            startActivity(intent);
        } else if (id == btnLogout.getId()) {
            // מעבר לעמוד הבית MainActivity וניקוי Back Stack
            Intent intent = new Intent(AdminPage.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }
}