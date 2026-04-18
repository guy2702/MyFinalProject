package com.example.myfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfinalproject.Adapter.UserShakeAdapter;
import com.example.myfinalproject.model.Shake;
import com.example.myfinalproject.model.ShakeSelectionManager;
import com.example.myfinalproject.services.DatabaseService;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class UserShake extends AppCompatActivity {

    private RecyclerView rvUserShakes;
    private UserShakeAdapter adapter;
    private ArrayList<Shake> shakeList;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_shake);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnBack = findViewById(R.id.btnBack);
        rvUserShakes = findViewById(R.id.rvUserShakes);
        shakeList = new ArrayList<>();

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(UserShake.this, UserHome.class);
            startActivity(intent);
            finish();
        });

        adapter = new UserShakeAdapter(shakeList, shake -> {
            ShakeSelectionManager.setCurrentViewedShake(shake);
            Intent intent = new Intent(UserShake.this, ShakeDetails.class);
            intent.putExtra("isAdminView", false);
            startActivity(intent);
        });

        rvUserShakes.setLayoutManager(new LinearLayoutManager(this));
        rvUserShakes.setAdapter(adapter);

        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) {
            Toast.makeText(this, "המשתמש לא מחובר", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseService.getInstance().listenToUserShakesRealtime(uid,
                new DatabaseService.DatabaseCallback<List<Shake>>() {
                    @Override
                    public void onCompleted(List<Shake> shakes) {
                        shakeList.clear();
                        shakeList.addAll(shakes);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailed(Exception e) {
                        Toast.makeText(UserShake.this, "שגיאה בטעינת השייקים", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}