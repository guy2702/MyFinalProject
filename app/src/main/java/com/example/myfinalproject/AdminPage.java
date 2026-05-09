package com.example.myfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myfinalproject.model.User;
import com.example.myfinalproject.services.DatabaseService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AdminPage extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "AdminPage";

    private Button btnAddItem, btnItems, btnUsers, btnAllShakes, btnLogout;
    private TextView tvGreeting;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_page);

        View rootLayout = findViewById(R.id.main);
        if (rootLayout != null) {
            ViewCompat.setOnApplyWindowInsetsListener(rootLayout, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        mAuth = FirebaseAuth.getInstance();

        btnAddItem = findViewById(R.id.btnAddItem);
        btnItems = findViewById(R.id.btnItems);
        btnUsers = findViewById(R.id.btnUserTable);
        btnAllShakes = findViewById(R.id.btnAllShakes);
        btnLogout = findViewById(R.id.btnLogout);
        tvGreeting = findViewById(R.id.tvGreeting);

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // טקסט זמני עד שהנתונים יגיעו
            tvGreeting.setText("טוען נתונים...");

            // משיכת פרטי המנהל מהמסד לפי ה-UID
            DatabaseService.getInstance().getUser(currentUser.getUid(), new DatabaseService.DatabaseCallback<User>() {
                @Override
                public void onCompleted(User user) {
                    if (user != null && user.getFname() != null && !user.getFname().isEmpty()) {
                        // אם נמצא השם הפרטי - נציג אותו
                        tvGreeting.setText("שלום " + user.getFname() + " (מנהל)!");
                    } else {
                        // גיבוי: אם אין שם, נציג את חיתוך האימייל
                        String nameFallback = currentUser.getEmail() != null ? currentUser.getEmail().split("@")[0] : "מנהל";
                        tvGreeting.setText("שלום " + nameFallback + "!");
                    }
                }

                @Override
                public void onFailed(Exception e) {
                    // גיבוי במקרה של שגיאה במשיכת הנתונים
                    Log.e(TAG, "Error fetching admin data", e);
                    String nameFallback = currentUser.getEmail() != null ? currentUser.getEmail().split("@")[0] : "מנהל";
                    tvGreeting.setText("שלום " + nameFallback + "!");
                }
            });
        } else {
            tvGreeting.setText("שלום מנהל!");
        }

        btnAddItem.setOnClickListener(this);
        btnItems.setOnClickListener(this);
        btnUsers.setOnClickListener(this);
        btnAllShakes.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() == null) {
            Intent intent = new Intent(AdminPage.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btnAddItem) {
            startActivity(new Intent(AdminPage.this, AddItem.class));
        } else if (id == R.id.btnItems) {
            startActivity(new Intent(AdminPage.this, Items.class));
        } else if (id == R.id.btnUserTable) {
            startActivity(new Intent(AdminPage.this, users.class));
        } else if (id == R.id.btnAllShakes) {
            startActivity(new Intent(AdminPage.this, AdminAllShakes.class));
        } else if (id == R.id.btnLogout) {
            handleLogout();
        }
    }

    private void handleLogout() {
        mAuth.signOut();
        Log.d(TAG, "Admin logged out.");

        Intent intent = new Intent(AdminPage.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}