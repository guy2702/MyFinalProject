// =================================================================================================
// מטרת העמוד: ניהול וצפייה במשתמשי המערכת. העמוד מציג רשימה של כל המשתמשים הרשומים באפליקציה
// ומאפשר למנהל (Admin) לבצע חיפוש מהיר וסינון של משתמשים לפי שם או מזהה.
// =================================================================================================

package com.example.myfinalproject;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfinalproject.Adapter.UsersAdapter;
import com.example.myfinalproject.model.User;
import com.example.myfinalproject.services.DatabaseService;

import java.util.List;

public class users extends AppCompatActivity {

    private RecyclerView rvUsers; // רכיב להצגת רשימת המשתמשים
    private EditText etSearch;    // שדה טקסט לחיפוש משתמשים
    private UsersAdapter adapter; // ה-Adapter שמנהל את תצוגת השורות

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // מאפשר תצוגה מקצה לקצה של המסך[cite: 1]
        setContentView(R.layout.activity_users); // טעינת הממשק הגרפי[cite: 1]

        // הגנה על התוכן כדי שלא יוסתר מאחורי מערכת ההפעלה
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rvUsers = findViewById(R.id.rvUsers);
        etSearch = findViewById(R.id.etSearch);
        rvUsers.setLayoutManager(new LinearLayoutManager(this)); // הגדרת סידור רשימה אנכי[cite: 1]

        // משיכת רשימת המשתמשים ממסד הנתונים[cite: 1]
        DatabaseService.getInstance().getUserList(new DatabaseService.DatabaseCallback<List<User>>() {
            @Override
            public void onCompleted(List<User> users) {
                // לאחר קבלת הנתונים, חיבור הרשימה ל-Adapter[cite: 1]
                adapter = new UsersAdapter(users);
                rvUsers.setAdapter(adapter);
            }

            @Override
            public void onFailed(Exception e) {
                e.printStackTrace(); // טיפול במקרה של שגיאה בשליפת הנתונים[cite: 1]
            }
        });

        // הוספת מאזין לחיפוש בזמן אמת[cite: 1]
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                // בכל הקלדה, הפעלת פונקציית סינון (filter) ב-Adapter[cite: 1]
                if(adapter != null) adapter.filter(s.toString());
            }

            @Override public void afterTextChanged(Editable s) {}
        });
    }
}