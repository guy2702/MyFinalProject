package com.example.myfinalproject.model;

/**
 * מחלקה זו (AdminShakeItem) מייצגת אובייקט תצוגה מופשט של שייק עבור מנהל המערכת.
 * המטרה שלה היא להציג מידע תמציתי על שייקים שנוצרו במערכת בתוך טבלאות או רשימות ניהול,
 * ללא צורך בטעינת כל פרטי המרכיבים המלאים בכל פעם.
 */

public class AdminShakeItem {

    private String shakeId;    // מזהה השייק הייחודי
    private String userId;     // מזהה המשתמש שיצר את השייק
    private String userName;   // שם המשתמש שיצר את השייק
    private int itemsCount;    // מספר הפריטים המרכיבים את השייק

    /**
     * בנאי ריק (Default Constructor) – חובה לצורך עבודה עם Firebase Realtime Database.
     */
    public AdminShakeItem() {
    }

    /**
     * בנאי מלא לאתחול אובייקט AdminShakeItem.
     */
    public AdminShakeItem(String shakeId, String userId, String userName, int itemsCount) {
        this.shakeId = shakeId;
        this.userId = userId;
        this.userName = userName;
        this.itemsCount = itemsCount;
    }

    // --- Getters & Setters ---

    public String getShakeId() {
        return shakeId;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public int getItemsCount() {
        return itemsCount;
    }

    public void setShakeId(String shakeId) {
        this.shakeId = shakeId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setItemsCount(int itemsCount) {
        this.itemsCount = itemsCount;
    }
}
// הוספנו הערות Javadoc כדי להסביר את תפקיד המחלקה והשדות שלה.
// המחלקה מותאמת לייצוג קל משקל של נתוני השייקים עבור ממשק המנהל.
// סיום התיעוד עבור הקובץ הנוכחי.