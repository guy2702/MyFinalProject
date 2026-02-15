package com.example.myfinalproject.model;

import androidx.annotation.NonNull;

public class Item {

    private String id;
    private String name;
    private String type;
    private String goal;
    private double calories;
    private double protein;
    private double fat;
    private double carbs;
    private double sugar;
    private String pic;

    // ✅ משתנה חדש לניהול מצב הבחירה באפליקציה
    private boolean selected = false;

    public Item() {
        // חובה ל-Firebase
    }

    public Item(String id, String name, String type, String goal,
                double calories, double protein, double fat, double carbs,
                double sugar, String pic) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.goal = goal;
        this.calories = calories;
        this.protein = protein;
        this.fat = fat;
        this.carbs = carbs;
        this.sugar = sugar;
        this.pic = pic;
    }

    // --- Getter & Setter למשתנה החדש ---
    public boolean isSelected() { return selected; }
    public void setSelected(boolean selected) { this.selected = selected; }

    // --- שאר ה-Getters & Setters הקיימים ---
    public String getId() { return id; }
    public void setId(Object id) { this.id = (id != null) ? String.valueOf(id) : null; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getGoal() { return goal; }
    public void setGoal(String goal) { this.goal = goal; }
    public double getCalories() { return calories; }
    public void setCalories(double calories) { this.calories = calories; }
    public double getProtein() { return protein; }
    public void setProtein(double protein) { this.protein = protein; }
    public double getFat() { return fat; }
    public void setFat(double fat) { this.fat = fat; }
    public double getCarbs() { return carbs; }
    public void setCarbs(double carbs) { this.carbs = carbs; }
    public double getSugar() { return sugar; }
    public void setSugar(double sugar) { this.sugar = sugar; }
    public String getPic() { return pic; }
    public void setPic(String pic) { this.pic = pic; }

    @NonNull
    @Override
    public String toString() {
        return String.format(
                "Item{id='%s', name='%s', type='%s', goal='%s', calories=%.1f, protein=%.1f, fat=%.1f, carbs=%.1f, sugar=%.1f}",
                id, name, type, goal, calories, protein, fat, carbs, sugar
        );
    }
}