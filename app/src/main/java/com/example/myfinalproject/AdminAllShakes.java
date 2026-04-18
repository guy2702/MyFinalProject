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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfinalproject.Adapter.AdminAllShakesAdapter;
import com.example.myfinalproject.model.Shake;
import com.example.myfinalproject.model.ShakeSelectionManager;
import com.example.myfinalproject.services.DatabaseService;

import java.util.ArrayList;
import java.util.List;

public class AdminAllShakes extends AppCompatActivity {

    private RecyclerView rvAllShakes;
    private Button btnBack;
    private TextView tvEmpty;
    private AdminAllShakesAdapter adapter;
    private ArrayList<Shake> shakeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_all_shakes);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rvAllShakes = findViewById(R.id.rvAllShakes);
        btnBack = findViewById(R.id.btnBack);
        tvEmpty = findViewById(R.id.tvEmpty);

        shakeList = new ArrayList<>();

        adapter = new AdminAllShakesAdapter(shakeList, shake -> {
            ShakeSelectionManager.setCurrentViewedShake(shake);

            Intent intent = new Intent(AdminAllShakes.this, ShakeDetails.class);
            intent.putExtra("isAdminView", true);
            startActivity(intent);
        });

        rvAllShakes.setLayoutManager(new LinearLayoutManager(this));
        rvAllShakes.setAdapter(adapter);

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(AdminAllShakes.this, AdminPage.class);
            startActivity(intent);
            finish();
        });

        DatabaseService.getInstance().getShakeList(new DatabaseService.DatabaseCallback<List<Shake>>() {
            @Override
            public void onCompleted(List<Shake> object) {
                shakeList.clear();
                shakeList.addAll(object);
                adapter.notifyDataSetChanged();

                if (shakeList.isEmpty()) {
                    tvEmpty.setVisibility(View.VISIBLE);
                } else {
                    tvEmpty.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(AdminAllShakes.this, "שגיאה בטעינת השייקים", Toast.LENGTH_SHORT).show();
            }
        });
    }
}