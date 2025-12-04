package com.example.myfinalproject.model;

public class User {
    private String id;
    private String fname;
    private String lname;
    private String phone;
    private String email;
    private String password;
    private boolean isAdmin;


    public User() {
        // קונסטרקטור ריק — נדרש לפעמים על ידי Firebase
    }


    public User(String id, String fname,String lname,String phone, String email,String password,boolean isAdmin){

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

    // Getter / Setter עבור lname
    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    // Getter / Setter עבור phone
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    // Getter / Setter עבור email
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Getter / Setter עבור password
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Getter / Setter עבור isAdmin (boolean)
    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
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
