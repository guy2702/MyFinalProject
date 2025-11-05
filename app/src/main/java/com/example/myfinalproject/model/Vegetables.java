package com.example.myfinalproject.model;

import java.util.ArrayList;
import java.util.List;

public class Vegetables {
    protected static List<Ingredient> allVegetables = new ArrayList<>();

    static {
        allVegetables.add(new Ingredient("Spinach", 23, 2.9, 3.6, 0.4, "cutting"));
        allVegetables.add(new Ingredient("Kale", 49, 4.3, 9, 0.9, "cutting"));
        allVegetables.add(new Ingredient("Celery", 16, 0.7, 3, 0.2, "cutting"));
        allVegetables.add(new Ingredient("Carrot", 41, 0.9, 10, 0.2, "bulk"));
    }

    protected static List<Ingredient> getVegetablesByGoal(String goal) {
        List<Ingredient> result = new ArrayList<>();
        for (Ingredient i : allVegetables) {
            if (i.goal.equals(goal)) result.add(i);
        }
        return result;
    }
}