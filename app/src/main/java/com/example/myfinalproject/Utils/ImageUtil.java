package com.example.myfinalproject.Utils;

/**
 * מחלקת עזר (Utility Class) לניהול פעולות על תמונות באפליקציה.
 * המחלקה מספקת כלים לביקוש הרשאות מהמשתמש (מצלמה ואחסון)
 * וביצוע המרות בין אובייקטי תמונה (Bitmap/ImageView) לבין מחרוזות Base64,
 * מה שמאפשר שמירה ושליפה של תמונות בתוך בסיס הנתונים (Firebase).
 */

import android.Manifest;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Base64;
import android.widget.ImageView;

import androidx.core.app.ActivityCompat;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;

public class ImageUtil {

    /**
     * בקשת הרשאות גישה למצלמה ולאחסון חיצוני (לקריאה וכתיבה).
     * @param activity הפעילות (Activity) ממנה מתבצעת הבקשה.
     */
    public static void requestPermission(@NotNull Activity activity) {
        // בקשת הרשאות עבור המצלמה, כתיבה לאחסון וקריאה מהאחסון
        ActivityCompat.requestPermissions(activity,
                new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                }, 1);
    }

    /**
     * המרת תמונה המוצגת ב-ImageView למחרוזת בפורמט Base64.
     * שימושי לצורך העלאת תמונות ל-Firebase Database כשדה טקסט.
     * @param postImage רכיב ה-ImageView המכיל את התמונה.
     * @return מחרוזת ה-Base64 המייצגת את התמונה, או null אם אין תמונה.
     */
    public static @Nullable String convertTo64Base(@NotNull final ImageView postImage) {
        if (postImage.getDrawable() == null) {
            return null;
        }
        // שליפת ה-Bitmap מה-ImageView ודחיסתו לתוך זרם בתים (ByteArray)
        Bitmap bitmap = ((BitmapDrawable) postImage.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        // המרת המערך למחרוזת Base64
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    /**
     * המרת מחרוזת Base64 חזרה לאובייקט Bitmap שניתן להציג באפליקציה.
     * שימושי בעת שליפת נתוני מוצר מה-Database.
     * @param base64Code מחרוזת ה-Base64 של התמונה.
     * @return אובייקט Bitmap של התמונה, או null אם המחרוזת ריקה.
     */
    public static @Nullable Bitmap convertFrom64base(@NotNull final String base64Code) {
        if (base64Code.isEmpty()) {
            return null;
        }
        // פענוח המחרוזת בחזרה למערך בתים והמרתו ל-Bitmap
        byte[] decodedString = Base64.decode(base64Code, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
}
// הוספנו הערות מפורטות לכל מתודה כדי להקל על הבנת הלוגיקה של טיפול בתמונות בפרויקט.
// המחלקה משתמשת בספריות הסטנדרטיות של Android (Bitmap, Base64) לביצוע ההמרות בצורה יעילה.
// סיום התיעוד עבור הקובץ הנוכחי (ImageUtil).