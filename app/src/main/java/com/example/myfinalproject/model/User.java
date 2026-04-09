package com.example.myfinalproject.model;

import java.io.Serializable;

public class User implements Serializable {

    private String id;
    private String fname;
    private String lname;
    private String phone;
    private String email;
    private String password;

    // 🔥 שינוי: במקום boolean → Object
    private Object isAdmin;

    // חובה ל-Firebase
    public User() {}

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

    // ✅ getter חכם – תמיד מחזיר boolean תקין
    public boolean isAdmin() {
        if (isAdmin instanceof Boolean) {
            return (Boolean) isAdmin;
        } else if (isAdmin instanceof String) {
            return ((String) isAdmin).equalsIgnoreCase("true");
        }
        return false;
    }

    // ✅ setter גמיש
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