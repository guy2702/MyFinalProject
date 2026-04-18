package com.example.myfinalproject.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Shake implements Serializable {

    private String shakeId;
    private ArrayList<Item> items;
    private String userId;
    private String userName;

    public Shake() {
    }

    public Shake(String shakeId, ArrayList<Item> items) {
        this.shakeId = shakeId;
        this.items = items;
    }

    public Shake(String shakeId, ArrayList<Item> items, String userId, String userName) {
        this.shakeId = shakeId;
        this.items = items;
        this.userId = userId;
        this.userName = userName;
    }

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