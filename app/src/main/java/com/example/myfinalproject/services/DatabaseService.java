package com.example.myfinalproject.services;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

    /**
     * הבנאי (Constructor) של המחלקה.
     * מוגדר כ-private (פרטי) בגלל תבנית ה-Singleton.
     * הוא מתחבר ל-Firebase ומייצר את החיבור הראשוני למסד הנתונים.
     */
    private DatabaseService() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    /**
     * הפעולה שמחזירה את המופע היחיד של המחלקה (Singleton).
     * אם השירות עדיין לא נוצר, היא יוצרת אותו. אם הוא קיים, היא מחזירה אותו.
     */
    public static DatabaseService getInstance() {
        if (instance == null) {
            instance = new DatabaseService();
        }
        return instance;
    }

    // --- Private Helper Methods (Abstraction) ---
    // אלו מתודות הפשטה (Abstraction) - מונעות כפילות קוד (DRY - Don't Repeat Yourself).
    // במקום לכתוב קוד Firebase בכל מסך, הכל עובר דרך מתודות אלו.

    /**
     * הפונקציה writeData היא פונקציית עזר גנרית (Helper Method) שנועדה לרכז
     * את כל פעולות השמירה והעדכון מול Firebase במקום אחד, כדי למנוע כפילות קוד (עיקרון DRY).
     * * היא מקבלת:
     * 1. path - נתיב (לאיזו תיקייה ב-Firebase לשמור).
     * 2. data - אובייקט (מה לשמור, מוגדר כ-Object ולכן יכול לקבל כל סוג של נתון).
     * 3. callback - "מכשיר קשר" כדי לדווח חזרה למסך.
     * * היא משתמשת בפקודת setValue כדי לכתוב או לדרוס את הנתונים בענן.
     */
    private void writeData(@NotNull final String path, @NotNull final Object data, final @Nullable DatabaseCallback<Void> callback) {
        readData(path).setValue(data, (error, ref) -> {
            if (error != null) {
                if (callback != null) callback.onFailed(error.toException());
            } else {
                if (callback != null) callback.onCompleted(null);
            }
        });
    }

    /**
     * פונקציית עזר גנרית למחיקת נתונים.
     * מקבלת נתיב (Path) ומוחקת את כל מה שנמצא בו מתוך ה-Firebase.
     */
    private void deleteData(@NotNull final String path, @Nullable final DatabaseCallback<Void> callback) {
        readData(path).removeValue((error, ref) -> {
            if (error != null) {
                if (callback != null) callback.onFailed(error.toException());
            } else {
                if (callback != null) callback.onCompleted(null);
            }
        });
    }

    /**
     * פונקציית עזר המייצרת "הצבעה" (Reference) לתיקייה ספציפית בעץ הנתונים.
     */
    private DatabaseReference readData(@NotNull final String path) {
        return databaseReference.child(path);
    }

    /**
     * פונקציית עזר גנרית לשליפת אובייקט בודד (למשל משתמש אחד או פריט אחד).
     * מבצעת קריאה חד-פעמית (get) ולא מאזינה לשינויים.
     */
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

    /**
     * פונקציית עזר גנרית לשליפת רשימת אובייקטים (למשל רשימת משתמשים).
     * קוראת את הנתונים פעם אחת, רצה בלולאה על הילדים בעץ, וממירה אותם לרשימה ב-Java.
     */
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

    /**
     * פונקציית עזר שמחוללת מזהה (ID) אקראי וייחודי ב-Firebase.
     * משתמשת בפקודה push().getKey() שמייצרת מחרוזת ייחודית שמבוססת על זמן (Timestamp).
     */
    private String generateNewId(@NotNull final String path) {
        return databaseReference.child(path).push().getKey();
    }

    // --- User Section ---

    /**
     * הרשמה משולבת:
     * 1. יוצרת משתמש חדש במערכת האימות (FirebaseAuth) עם אימייל וסיסמה.
     * 2. אם ההרשמה הצליחה, שומרת את שאר פרטי המשתמש (שם, טלפון וכו') ב-Realtime Database תחת ה-UID שנוצר.
     */
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

    /**
     * שולפת פרטים של משתמש בודד לפי ה-UID (המזהה הייחודי) שלו.
     */
    public void getUser(@NotNull final String uid, @NotNull final DatabaseCallback<User> callback) {
        getData(USERS_PATH + "/" + uid, User.class, callback);
    }

    /**
     * שולפת את כל המשתמשים שרשומים באפליקציה. (שימושי למסך ניהול משתמשים של ה-Admin).
     */
    public void getUserList(@NotNull final DatabaseCallback<List<User>> callback) {
        getDataList(USERS_PATH, User.class, callback);
    }

    /**
     * מוחקת משתמש ספציפי ממסד הנתונים לפי ה-UID שלו.
     */
    public void deleteUser(@NotNull final String uid, @Nullable final DatabaseCallback<Void> callback) {
        deleteData(USERS_PATH + "/" + uid, callback);
    }

    /**
     * מעדכנת את פרטי המשתמש (כגון שינוי שם או מספר טלפון). דורסת את המידע הישן עם החדש.
     */
    public void updateUser(@NotNull final User user, @Nullable final DatabaseCallback<Void> callback) {
        writeData(USERS_PATH + "/" + user.getId(), user, callback);
    }

    // --- Item Section ---

    /**
     * יצירת הזמנת שייק חדשה.
     * מציגה קשרים (Referential Integrity): הפונקציה שומרת את השייק ב-2 מקומות במקביל:
     * 1. בתיקיית השייקים הכללית (לשימוש המנהל).
     * 2. בתיקיית השייקים הפרטית של המשתמש שהזמין (כדי שיוכל לראות את ההיסטוריה שלו).
     */
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

    /**
     * מעדכנת נתונים של פריט/חומר גלם קיים (למשל, עדכון שם או ערך תזונתי של פרי).
     */
    public void updateItem(@NotNull final Item item, @Nullable final DatabaseCallback<Void> callback) {
        writeData(ITEMS_PATH + "/" + item.getId(), item, callback);
    }

    /**
     * שולפת פריט (Item) בודד לפי המזהה שלו.
     */
    public void getItem(@NotNull final String itemId, @NotNull final DatabaseCallback<Item> callback) {
        getData(ITEMS_PATH + "/" + itemId, Item.class, callback);
    }

    /**
     * מוסיפה פריט/חומר גלם חדש למלאי החנות.
     */
    public void createNewItem(@NotNull final Item item, @Nullable final DatabaseCallback<Void> callback) {
        writeData(ITEMS_PATH + "/" + item.getId(), item, callback);
    }

    /**
     * מחוללת ID ייחודי לפריט חדש (מופעלת לפני שיוצרים פריט חדש כדי לתת לו מזהה).
     */
    public String generateItemId() {
        return generateNewId(ITEMS_PATH);
    }

    /**
     * מוחקת פריט מרשימת הפריטים שבמערכת.
     */
    public void deleteItem(@NotNull final String itemId, @Nullable final DatabaseCallback<Void> callback) {
        deleteData(ITEMS_PATH + "/" + itemId, callback);
    }

    /**
     *
     * הפונקציה listenToItemsRealtime אחראית על שאיבת כל הפריטים מ-Firebase, והאזנה לשינויים בזמן אמת.
     * * איך היא עובדת?
     * 1. מאזין חי (addValueEventListener): בניגוד לשליפה חד-פעמית, הפקודה הזו יוצרת חיבור קבוע לשרת.
     * זה אומר שאם מנהל מוסיף, מעדכן או מוחק מוצר בפיירבייס - הפונקציה תופעל אוטומטית שוב,
     * והמסך של המשתמש יתעדכן באותו רגע בלי שהוא יצטרך לרענן את האפליקציה!
     * 2. סריקה והמרה: הפונקציה מקבלת מהשרת את הנתונים בצורה של עץ (DataSnapshot). היא עוברת על כל
     * הילדים בעץ (getChildren), וממירה אוטומטית כל רשומה לאובייקט ג'אווה מסוג Item בעזרת getValue.
     * 3. העברה למסך (Callback): הפונקציה אורזת את כל הפריטים שהיא מצאה לתוך רשימה (ArrayList),
     * ומחזירה אותה למסך שביקש אותה דרך הפונקציה callback.onCompleted(items).
     */
    public void listenToItemsRealtime(@NotNull final DatabaseCallback<List<Item>> callback) {
        readData(ITEMS_PATH).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Item> items = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Item item = ds.getValue(Item.class);
                    if (item != null) items.add(item);
                }
                callback.onCompleted(items);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailed(error.toException());
            }
        });
    }

    // --- Shake Section ---

    /**
     * שולפת את הרשימה המלאה של כל הזמנות השייקים במערכת (בלי פילטר).
     * פונקציה זו משמשת את מסך המנהל (AdminAllShakes) כדי לראות את כל ההזמנות.
     */
    public void getShakeList(@NotNull final DatabaseCallback<List<Shake>> callback) {
        getDataList(SHAKES_PATH, Shake.class, callback);
    }

    /**
     * הפעולה listenToUserShakesRealtime משמשת כצינור נתונים חכם ומאובטח המקשר בין מסד הנתונים בענן למסך האפליקציה.
     * היא מקבלת את מזהה המשתמש הייחודי (uid) ומנווטת ישירות לתיקייה הפרטית שלו (userShake/uid) כדי לבודד ולהציג
     * אך ורק את השייקים שלו, תוך שהיא משאירה מאזין קבוע (addValueEventListener) המעדכן את המסך אוטומטית ובזמן אמת
     * בכל פעם שנוסף או נמחק שייק בענן. ברגע שהמידע הגולמי מתקבל מהשרת בתור DataSnapshot, הפעולה מייצרת רשימה
     * חדשה וריקה, עוברת בלולאה קובץ-קובץ כדי לתרגם ולהמיר את נתוני ה-JSON לאובייקטים אמיתיים ב-Java מסוג Shake,
     * ומכניסה אותם לסל המקומי, כאשר בסיום התהליך היא משגרת את הרשימה המלאה והמעודכנת בחזרה למסך דרך
     * ה-callback.onCompleted(shakes) כדי שהאדפטר יוכל להציג אותה למשתמש ללא צורך בריענון ידני.
     */
    public void listenToUserShakesRealtime(@NotNull final String uid, @NotNull final DatabaseCallback<List<Shake>> callback) {

        // 1. ניגשים לנתיב הספציפי של המשתמש (לדוגמה: "userShake/12345") ומוסיפים מאזין שפועל בזמן אמת.
        readData(USERS_PATH_SHAKE + "/" + uid).addValueEventListener(new ValueEventListener() {

            // 2. הפונקציה הזו מופעלת אוטומטית ברגע שהנתונים ירדו בהצלחה מהשרת, או כשיש שינוי בנתונים.
            // המשתנה snapshot (תמונת מצב) מכיל את כל עץ הנתונים שחזר מ-Firebase.
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // 3. יוצרים רשימה חדשה וריקה, אליה "נאסוף" את כל השייקים שקיבלנו.
                List<Shake> shakes = new ArrayList<>();

                // 4. לולאת for שעוברת על כל ה"ילדים" (כלומר, כל הזמנות השייקים) שנמצאים בתוך התיקייה של המשתמש.
                for (DataSnapshot ds : snapshot.getChildren()) {

                    // 5. פעולת ההמרה (Deserialization): לוקחים את המידע הגולמי מפיירבייס (JSON)
                    // ומתרגמים אותו אוטומטית לאובייקט ג'אווה מסוג Shake.
                    Shake shake = ds.getValue(Shake.class);

                    // 6. בדיקת הגנה (Validation): מוודאים שההמרה הצליחה והאובייקט לא ריק,
                    // ורק אז מכניסים אותו לרשימה שלנו.
                    if (shake != null) shakes.add(shake);
                }

                // 7. קוראים ל"מכשיר הקשר" (callback) ומשדרים את הרשימה המלאה והמוכנה בחזרה למסך (Activity) שביקש אותה.
                callback.onCompleted(shakes);
            }

            // 8. הפונקציה הזו מופעלת רק במקרה של תקלה (למשל: אין אינטרנט או שאין למשתמש הרשאות קריאה).
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                // 9. משדרים למסך הודעת כישלון עם פירוט השגיאה, כדי שהמסך יוכל להציג Toast למשתמש.
                callback.onFailed(error.toException());
            }
        });
    }

    /**
     * מחוללת ID ייחודי עבור הזמנת שייק חדשה.
     */
    public String generateShakeId() {
        return generateNewId(SHAKES_PATH);
    }

    /**
     * מוחקת שייק מהמערכת.
     * מכיוון שהשייק נשמר ב-2 מקומות (בכללי ובפרטי), הפונקציה מוחקת אותו קודם מהרשימה הכללית,
     * ורק כשהיא מסיימת בהצלחה, היא מוחקת אותו גם מההיסטוריה האישית של הלקוח.
     */
    public void deleteShake(@NotNull final String shakeId, @NotNull final String userId, @Nullable final DatabaseCallback<Void> callback) {
        deleteData(SHAKES_PATH + "/" + shakeId, new DatabaseCallback<Void>() {
            @Override public void onCompleted(Void object) {
                deleteData(USERS_PATH_SHAKE + "/" + userId + "/" + shakeId, callback);
            }
            @Override public void onFailed(Exception e) { if (callback != null) callback.onFailed(e); }
        });
    }
}