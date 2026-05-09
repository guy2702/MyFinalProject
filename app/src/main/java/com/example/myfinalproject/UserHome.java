package com.example.myfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myfinalproject.model.User;
import com.example.myfinalproject.services.DatabaseService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserHome extends AppCompatActivity {

    private TextView tvWelcome;
    private Button btnStartShake, btnMyShakes, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_home);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvWelcome = findViewById(R.id.tvWelcome);
        btnStartShake = findViewById(R.id.btnStartShake);
        btnMyShakes = findViewById(R.id.btnMyShakes);
        btnLogout = findViewById(R.id.btnLogout);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            // טקסט זמני עד שהנתונים יגיעו
            tvWelcome.setText("טוען נתונים...");

            // משיכת פרטי המשתמש מהמסד לפי ה-UID
            DatabaseService.getInstance().getUser(firebaseUser.getUid(), new DatabaseService.DatabaseCallback<User>() {
                @Override
                public void onCompleted(User user) {
                    if (user != null && user.getFname() != null && !user.getFname().isEmpty()) {
                        // אם נמצא השם הפרטי של המשתמש - נציג אותו
                        tvWelcome.setText("ברוך הבא, " + user.getFname());
                    } else {
                        // גיבוי: אם אין שם, נציג את האימייל
                        String nameFallback = firebaseUser.getEmail() != null ? firebaseUser.getEmail().split("@")[0] : "";
                        tvWelcome.setText("ברוך הבא, " + nameFallback);
                    }
                }

                @Override
                public void onFailed(Exception e) {
                    // גיבוי במקרה של שגיאה במשיכת הנתונים
                    Log.e("UserHome", "Error fetching user data", e);
                    String nameFallback = firebaseUser.getEmail() != null ? firebaseUser.getEmail().split("@")[0] : "";
                    tvWelcome.setText("ברוך הבא, " + nameFallback);
                }
            });
        } else {
            tvWelcome.setText("ברוך הבא");
        }

        btnStartShake.setOnClickListener(v -> {
            Intent intent = new Intent(UserHome.this, Choise.class);
            startActivity(intent);
        });

        btnMyShakes.setOnClickListener(v -> {
            Intent intent = new Intent(UserHome.this, UserShake.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(UserHome.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}