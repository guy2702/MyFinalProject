package com.example.myfinalproject.model;

public class Ingredient {
    protected String name;
    protected double calories;
    protected double protein;
    protected double carbs;
    protected double fat;
    protected String goal;

    public Ingredient(String name, double calories, double protein, double carbs, double fat, String goal) {
        this.name = name;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fat = fat;
        this.goal = goal;
    }

    // פונקציות getter כדי שה-Shake יוכל לקרוא את הערכים
    public String getName() {
        return name;
    }

    public double getCalories() {
        return calories;
    }

    public double getProtein() {
        return protein;
    }

    public double getCarbs() {
        return carbs;
    }

    public double getFat() {
        return fat;
    }

    public String getGoal() {
        return goal;
    }

    @Override
    public String toString() {
        return name + " (" + goal + ")";
    }
}