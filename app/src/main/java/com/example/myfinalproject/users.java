package com.example.myfinalproject;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfinalproject.Adapter.UsersAdapter;
import com.example.myfinalproject.model.User;
import com.example.myfinalproject.services.DatabaseService;

import java.util.List;

public class users extends AppCompatActivity {

    private RecyclerView rvUsers;
    private EditText etSearch;
    private UsersAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        rvUsers = findViewById(R.id.rvUsers);
        etSearch = findViewById(R.id.etSearch);
        rvUsers.setLayoutManager(new LinearLayoutManager(this));

        DatabaseService.getInstance().getUserList(new DatabaseService.DatabaseCallback<List<User>>() {
            @Override
            public void onCompleted(List<User> users) {
                adapter = new UsersAdapter(users);
                rvUsers.setAdapter(adapter);
            }

            @Override
            public void onFailed(Exception e) {
                e.printStackTrace();
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(adapter != null) adapter.filter(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }
}