package com.example.myfinalproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myfinalproject.R;
import com.example.myfinalproject.model.User;
import com.example.myfinalproject.services.DatabaseService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class login extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private DatabaseService databaseService;
    private FirebaseAuth mAuth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        databaseService = DatabaseService.getInstance();
        mAuth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.emailInput);
        etPassword = findViewById(R.id.passwordInput);
        btnLogin = findViewById(R.id.loginBtn);
        tvRegister = findViewById(R.id.registerText);

        btnLogin.setOnClickListener(this);
        tvRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == btnLogin.getId()) {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (!checkInput(email, password)) {
                return;
            }

            loginUser(email, password);

        } else if (id == tvRegister.getId()) {
            Intent registerIntent = new Intent(login.this, register.class);
            startActivity(registerIntent);
        }
    }

    private boolean checkInput(String email, String password) {
        if (email.isEmpty() || !email.contains("@")) {
            etEmail.setError("נא להכניס אימייל תקין");
            etEmail.requestFocus();
            return false;
        }

        if (password.isEmpty() || password.length() < 6) {
            etPassword.setError("סיסמה חייבת להיות לפחות 6 תווים");
            etPassword.requestFocus();
            return false;
        }

        return true;
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser == null) {
                            etPassword.setError("שגיאה בכניסה, נסה שוב");
                            etPassword.requestFocus();
                            return;
                        }
                        String uid = firebaseUser.getUid();

                        databaseService.getUser(uid, new DatabaseService.DatabaseCallback<User>() {
                            @Override
                            public void onCompleted(User user) {
                                Log.d(TAG, "Login success, user: " + user.getid());

                                Intent homepageIntent = new Intent(login.this, homepage.class);
                                homepageIntent.putExtra("USER_ID", user.getid());
                                homepageIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(homepageIntent);
                            }

                            @Override
                            public void onFailed(Exception e) {
                                Log.e(TAG, "Failed to get user data", e);
                                etPassword.setError("שגיאה בטעינת נתוני המשתמש");
                                etPassword.requestFocus();
                            }
                        });

                    } else {
                        etPassword.setError("אימייל או סיסמה שגויים");
                        etPassword.requestFocus();
                        Log.e(TAG, "Login failed", task.getException());
                    }
                });
    }
}
