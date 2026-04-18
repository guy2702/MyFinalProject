package com.example.myfinalproject.model;

public class AdminShakeItem {

    private String shakeId;
    private String userId;
    private String userName;
    private int itemsCount;

    public AdminShakeItem() {
    }

    public AdminShakeItem(String shakeId, String userId, String userName, int itemsCount) {
        this.shakeId = shakeId;
        this.userId = userId;
        this.userName = userName;
        this.itemsCount = itemsCount;
    }

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