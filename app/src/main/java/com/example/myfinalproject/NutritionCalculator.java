package com.example.myfinalproject;

import com.example.myfinalproject.model.Item;

import java.util.List;

public class NutritionCalculator {

    public static class NutritionResult {
        public double calories;
        public double protein;
        public double fat;
        public double carbs;
        public double sugar;
    }

    public static NutritionResult calculate(List<Item> items) {
        NutritionResult result = new NutritionResult();

        for (Item item : items) {
            double factor = item.getAmount() / 100.0;

            result.calories += item.getCalories() * factor;
            result.protein += item.getProtein() * factor;
            result.fat += item.getFat() * factor;
            result.carbs += item.getCarbs() * factor;
            result.sugar += item.getSugar() * factor;
        }

        return result;
    }
}