package com.example.myfinalproject.model;

public class Item {

    private String id;          // ← String במקום int
    private String name;
    private String type;
    private double calories;
    private double protein;
    private double fat;
    private double carbs;
    private String pic;

    public Item() {
    }

    public Item(String id, String name, String type, double calories, double protein, double fat, double carbs, String pic) {
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
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public double getCalories() { return calories; }
    public void setCalories(double calories) { this.calories = calories; }

    public double getProtein() { return protein; }
    public void setProtein(double protein) { this.protein = protein; }

    public double getFat() { return fat; }
    public void setFat(double fat) { this.fat = fat; }

    public double getCarbs() { return carbs; }
    public void setCarbs(double carbs) { this.carbs = carbs; }

    public String getPic() { return pic; }
    public void setPic(String pic) { this.pic = pic; }

    @Override
    public String toString() {
        return String.format(
                "Item{id='%s', name='%s', type='%s', calories=%.1f, protein=%.1f, fat=%.1f, carbs=%.1f}",
                id, name, type, calories, protein, fat, carbs
        );
    }
}
