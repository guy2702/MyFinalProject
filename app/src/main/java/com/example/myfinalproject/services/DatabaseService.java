package com.example.myfinalproject.services;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myfinalproject.model.AdminShakeItem;
import com.example.myfinalproject.model.Item;
import com.example.myfinalproject.model.Shake;
import com.example.myfinalproject.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * מחלקה זו היא ה-Service המרכזי של האפליקציה.
 * עיצוב: Singleton - מבטיח שיש רק מופע אחד של השירות באפליקציה כדי למנוע יצירת עומס על בסיס הנתונים.
 */
public class DatabaseService {

    private static final String TAG = "DatabaseService";
    // ניהול נתיבים במקום אחד מרכזי מאפשר שינוי קל אם מבנה ה-DB משתנה
    private static final String USERS_PATH = "users",
            USERS_PATH_SHAKE = "userShake",
            ITEMS_PATH = "item",
            SHAKES_PATH = "shake";

    // ממשק Callback - משמש לטיפול באסינכרוניות.
    // בגלל שפעולות מול שרת לוקחות זמן, אנחנו לא רוצים לתקוע את מסך המשתמש.
    public interface DatabaseCallback<T> {
        void onCompleted(T object);
        void onFailed(Exception e);
    }

    private static DatabaseService instance;
    private final DatabaseReference databaseReference;

    private DatabaseService() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public static DatabaseService getInstance() {
        if (instance == null) {
            instance = new DatabaseService();
        }
        return instance;
    }

    // --- Private Helper Methods (Abstraction) ---
    // אלו מתודות הפשטה (Abstraction) - מונעות כפילות קוד (DRY - Don't Repeat Yourself).
    // במקום לכתוב קוד Firebase בכל מסך, הכל עובר דרך מתודות אלו.

    private void writeData(@NotNull final String path, @NotNull final Object data, final @Nullable DatabaseCallback<Void> callback) {
        readData(path).setValue(data, (error, ref) -> {
            if (error != null) {
                if (callback != null) callback.onFailed(error.toException());
            } else {
                if (callback != null) callback.onCompleted(null);
            }
        });
    }

    private void deleteData(@NotNull final String path, @Nullable final DatabaseCallback<Void> callback) {
        readData(path).removeValue((error, ref) -> {
            if (error != null) {
                if (callback != null) callback.onFailed(error.toException());
            } else {
                if (callback != null) callback.onCompleted(null);
            }
        });
    }

    private DatabaseReference readData(@NotNull final String path) {
        return databaseReference.child(path);
    }

    private <T> void getData(@NotNull final String path, @NotNull final Class<T> clazz, @NotNull final DatabaseCallback<T> callback) {
        readData(path).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e(TAG, "Error getting data", task.getException());
                callback.onFailed(task.getException());
                return;
            }
            T data = task.getResult().getValue(clazz);
            callback.onCompleted(data);
        });
    }

    private <T> void getDataList(@NotNull final String path, @NotNull final Class<T> clazz, @NotNull final DatabaseCallback<List<T>> callback) {
        readData(path).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e(TAG, "Error getting data", task.getException());
                callback.onFailed(task.getException());
                return;
            }
            List<T> tList = new ArrayList<>();
            for (DataSnapshot ds : task.getResult().getChildren()) {
                T t = ds.getValue(clazz);
                if (t != null) tList.add(t);
            }
            callback.onCompleted(tList);
        });
    }

    private String generateNewId(@NotNull final String path) {
        return databaseReference.child(path).push().getKey();
    }

    // --- User Section ---

    // הרשמה משולבת: יצירת משתמש ב-Auth (אימות) ושמירת נתונים ב-Realtime DB (מסד נתונים)
    public void createNewUser(@NotNull final User user, @Nullable final DatabaseCallback<String> callback) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        user.setId(uid);
                        // שמירת המשתמש תחת ה-UID שלו כ-Key ייחודי
                        writeData(USERS_PATH + "/" + uid, user, new DatabaseCallback<Void>() {
                            @Override public void onCompleted(Void v) { if (callback != null) callback.onCompleted(uid); }
                            @Override public void onFailed(Exception e) { if (callback != null) callback.onFailed(e); }
                        });
                    } else {
                        if (callback != null) callback.onFailed(task.getException());
                    }
                });
    }

    public void getUser(@NotNull final String uid, @NotNull final DatabaseCallback<User> callback) {
        getData(USERS_PATH + "/" + uid, User.class, callback);
    }

    public void getUserList(@NotNull final DatabaseCallback<List<User>> callback) {
        getDataList(USERS_PATH, User.class, callback);
    }

    public void deleteUser(@NotNull final String uid, @Nullable final DatabaseCallback<Void> callback) {
        deleteData(USERS_PATH + "/" + uid, callback);
    }

    public void updateUser(@NotNull final User user, @Nullable final DatabaseCallback<Void> callback) {
        writeData(USERS_PATH + "/" + user.getId(), user, callback);
    }

    // --- Item Section ---

    // פעולה מורכבת: כתיבה בשני מקומות (SHAKES_PATH ו-USERS_PATH_SHAKE)
    // זו דוגמה לשימוש ב-Referential Integrity (שלמות התייחסותית) ברמה בסיסית.
    public void createNewShake(@NotNull final Shake shake, @Nullable final DatabaseCallback<Void> callback) {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) {
            if (callback != null) callback.onFailed(new Exception("המשתמש לא מחובר"));
            return;
        }
        shake.setUserId(uid);

        getUser(uid, new DatabaseCallback<User>() {
            @Override public void onCompleted(User user) {
                shake.setUserName(user != null ? (user.getFname() + " " + user.getLname()).trim() : "לא ידוע");
                writeData(SHAKES_PATH + "/" + shake.getShakeId(), shake, new DatabaseCallback<Void>() {
                    @Override public void onCompleted(Void object) {
                        // כתיבה כפולה כדי לאפשר שליפה מהירה גם לפי משתמש וגם כללית
                        writeData(USERS_PATH_SHAKE + "/" + uid + "/" + shake.getShakeId(), shake, callback);
                    }
                    @Override public void onFailed(Exception e) { if (callback != null) callback.onFailed(e); }
                });
            }
            @Override public void onFailed(Exception e) {
                // טיפול בשגיאות בצורה אלגנטית
                shake.setUserName("לא ידוע");
                writeData(SHAKES_PATH + "/" + shake.getShakeId(), shake, new DatabaseCallback<Void>() {
                    @Override public void onCompleted(Void object) {
                        writeData(USERS_PATH_SHAKE + "/" + uid + "/" + shake.getShakeId(), shake, callback);
                    }
                    @Override public void onFailed(Exception ex) { if (callback != null) callback.onFailed(ex); }
                });
            }
        });
    }

    public void updateItem(@NotNull final Item item, @Nullable final DatabaseCallback<Void> callback) {
        writeData(ITEMS_PATH + "/" + item.getId(), item, callback);
    }

    public void getItem(@NotNull final String itemId, @NotNull final DatabaseCallback<Item> callback) {
        getData(ITEMS_PATH + "/" + itemId, Item.class, callback);
    }

    public void createNewItem(@NotNull final Item item, @Nullable final DatabaseCallback<Void> callback) {
        writeData(ITEMS_PATH + "/" + item.getId(), item, callback);
    }

    public String generateItemId() {
        return generateNewId(ITEMS_PATH);
    }

    public void deleteItem(@NotNull final String itemId, @Nullable final DatabaseCallback<Void> callback) {
        deleteData(ITEMS_PATH + "/" + itemId, callback);
    }

    // Real-time listener: הופך את המסך ל"חי" ומעדכן את הממשק אוטומטית בכל שינוי ב-DB.
    public void listenToItemsRealtime(@NotNull final DatabaseCallback<List<Item>> callback) {
        readData(ITEMS_PATH).addValueEventListener(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Item> items = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Item item = ds.getValue(Item.class);
                    if (item != null) items.add(item);
                }
                callback.onCompleted(items);
            }
            @Override public void onCancelled(@NonNull DatabaseError error) { callback.onFailed(error.toException()); }
        });
    }

    // --- Shake Section ---

    public void listenToAllShakesForAdmin(@NotNull final DatabaseCallback<List<AdminShakeItem>> callback) {
        readData(SHAKES_PATH).addValueEventListener(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<AdminShakeItem> list = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Shake shake = ds.getValue(Shake.class);
                    if (shake != null) {
                        String userName = shake.getUserName() != null ? shake.getUserName() : "לא ידוע";
                        int itemsCount = shake.getItems() != null ? shake.getItems().size() : 0;
                        list.add(new AdminShakeItem(shake.getShakeId(), shake.getUserId(), userName, itemsCount));
                    }
                }
                callback.onCompleted(list);
            }
            @Override public void onCancelled(@NonNull DatabaseError error) { callback.onFailed(error.toException()); }
        });
    }

    public void getShakeList(@NotNull final DatabaseCallback<List<Shake>> callback) {
        getDataList(SHAKES_PATH, Shake.class, callback);
    }

    public void listenToUserShakesRealtime(@NotNull final String uid, @NotNull final DatabaseCallback<List<Shake>> callback) {
        readData(USERS_PATH_SHAKE + "/" + uid).addValueEventListener(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Shake> shakes = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Shake shake = ds.getValue(Shake.class);
                    if (shake != null) shakes.add(shake);
                }
                callback.onCompleted(shakes);
            }
            @Override public void onCancelled(@NonNull DatabaseError error) { callback.onFailed(error.toException()); }
        });
    }

    public String generateShakeId() {
        return generateNewId(SHAKES_PATH);
    }

    public void deleteShake(@NotNull final String shakeId, @NotNull final String userId, @Nullable final DatabaseCallback<Void> callback) {
        deleteData(SHAKES_PATH + "/" + shakeId, new DatabaseCallback<Void>() {
            @Override public void onCompleted(Void object) {
                deleteData(USERS_PATH_SHAKE + "/" + userId + "/" + shakeId, callback);
            }
            @Override public void onFailed(Exception e) { if (callback != null) callback.onFailed(e); }
        });
    }
}