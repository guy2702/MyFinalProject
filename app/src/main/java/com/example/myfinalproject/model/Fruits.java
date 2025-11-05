package com.example.myfinalproject.model;

import java.util.ArrayList;
import java.util.List;

public class Fruits {
    protected static List<Ingredient> allFruits = new ArrayList<>();

    static {
        allFruits.add(new Ingredient("Banana", 100, 1.3, 27, 0.3, "bulk"));
        allFruits.add(new Ingredient("Strawberry", 32, 0.7, 7.7, 0.3, "cutting"));
        allFruits.add(new Ingredient("Blueberry", 57, 0.7, 14, 0.3, "cutting"));
        allFruits.add(new Ingredient("Mango", 99, 1.4, 25, 0.6, "bulk"));
        allFruits.add(new Ingredient("Apple", 52, 0.3, 14, 0.2, "cutting"));
    }

    protected static List<Ingredient> getFruitsByGoal(String goal) {
        List<Ingredient> result = new ArrayList<>();
        for (Ingredient i : allFruits) {
            if (i.goal.equals(goal)) result.add(i);
        }
        return result;
    }
}