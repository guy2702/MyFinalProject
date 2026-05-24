package com.example.myfinalproject.model;

/**
 * מחלקה המייצגת משתמש במערכת (User).
 * המחלקה מממשת את הממשק Serializable כדי לאפשר העברת אובייקטים בין פעילויות (Activities).
 * מכילה נתונים אישיים, פרטי התחברות, והרשאות ניהול.
 */

import java.io.Serializable;

public class User implements Serializable {

    private String id;
    private String fname;
    private String lname;
    private String phone;
    private String email;
    private String password;

    // 🔥 שדה המנהל מוגדר כ-Object כדי לתמוך בגמישות מול Firebase (סוגים משתנים)
    private Object isAdmin;

    /**
     * בנאי ריק (Default Constructor) – חובה לצורך עבודה עם Firebase Realtime Database.
     */
    public User() {}

    /**
     * בנאי ליצירת משתמש חדש עם כל הפרטים.
     */
    public User(String id, String fname, String lname,
                String phone, String email,
                String password, boolean isAdmin) {

        this.id = id;
        this.fname = fname;
        this.lname = lname;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.isAdmin = isAdmin;
    }

    // --- Getters & Setters ---

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * ✅ getter חכם: בודק את סוג הנתון ב-isAdmin ומחזיר ערך בוליאני תקין.
     * מיועד לטפל בהבדלים בין פורמטים של נתונים (Boolean מול String) בבסיס הנתונים.
     */
    public boolean isAdmin() {
        if (isAdmin instanceof Boolean) {
            return (Boolean) isAdmin;
        } else if (isAdmin instanceof String) {
            return ((String) isAdmin).equalsIgnoreCase("true");
        }
        return false;
    }

    /**
     * ✅ setter גמיש המאפשר להגדיר את הרשאת המנהל כ-Object.
     */
    public void setAdmin(Object admin) {
        this.isAdmin = admin;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", fname='" + fname + '\'' +
                ", lname='" + lname + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", isAdmin=" + isAdmin +
                '}';
    }
}