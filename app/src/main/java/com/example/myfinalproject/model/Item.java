package com.example.myfinalproject.model;

public class Item {

    private int id;
    private String name;
    private String type;
    private double calories;
    private double protein;
    private double fat;
    private double carbs;

    String pic;


    public Item() {
    }

    public Item(int id, String name, String type, double calories, double protein, double fat, double carbs, String pic) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.calories = calories;
        this.protein = protein;
        this.fat = fat;
        this.carbs = carbs;
        this.pic = pic;
    }

    // --- Getters & Setters ---
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public double getCalories() {
        return calories;
    }

    public double getProtein() {
        return protein;
    }

    public double getFat() {
        return fat;
    }

    public double getCarbs() {
        return carbs;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public void setProtein(double protein) {
        this.protein = protein;
    }

    public void setFat(double fat) {
        this.fat = fat;
    }

    public void setCarbs(double carbs) {
        this.carbs = carbs;
    }


    public void setId(int id) {
        this.id = id;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    // --- מתודה שתדפיס את כל הנתונים ---
    @Override
    public String toString() {
        return String.format("Item{id=%d, name='%s', type='%s', calories=%.1f, protein=%.1f, fat=%.1f, carbs=%.1f}",
                id, name, type, calories, protein, fat, carbs);
    }
}
