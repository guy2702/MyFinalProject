package com.example.myfinalproject.model;

/**
 * מחלקה זו (Shake) מייצגת אובייקט של שייק מוכן שהורכב על ידי משתמש.
 * המחלקה כוללת את מזהה השייק, רשימת המרכיבים (Items) שנבחרו עבורו,
 * ופרטי המשתמש שיצר אותו. המחלקה מממשת את Serializable כדי לאפשר
 * העברת אובייקט שייק בין מסכי האפליקציה השונים.
 */

import java.io.Serializable;
import java.util.ArrayList;

public class Shake implements Serializable {

    private String shakeId;        // מזהה ייחודי לשייק (Shake ID)
    private ArrayList<Item> items; // רשימת המרכיבים שמרכיבים את השייק
    private String userId;         // מזהה המשתמש שיצר את השייק
    private String userName;       // שם המשתמש שיצר את השייק

    /**
     * בנאי ריק (Default Constructor) – חובה לצורך עבודה עם Firebase Realtime Database.
     */
    public Shake() {
    }

    /**
     * בנאי ליצירת שייק עם מזהה ורשימת מרכיבים בלבד.
     */
    public Shake(String shakeId, ArrayList<Item> items) {
        this.shakeId = shakeId;
        this.items = items;
    }

    /**
     * בנאי מלא ליצירת שייק כולל פרטי המשתמש שיצר אותו.
     */
    public Shake(String shakeId, ArrayList<Item> items, String userId, String userName) {
        this.shakeId = shakeId;
        this.items = items;
        this.userId = userId;
        this.userName = userName;
    }

    // --- Getters & Setters ---

    public String getShakeId() {
        return shakeId;
    }

    public void setShakeId(String shakeId) {
        this.shakeId = shakeId;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
// הוספנו הערות Javadoc כדי להסביר את מטרת המחלקה והשדות שלה.
// המבנה נשמר כפי שהיה, והקוד מוכן להגשה כחלק מתיק הפרויקט.