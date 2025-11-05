package com.example.myfinalproject.model;

import java.util.ArrayList;
import java.util.List;

public class Fats {
    protected static List<Ingredient> allFats = new ArrayList<>();

    static {
        allFats.add(new Ingredient("Peanut Butter", 588, 25, 20, 50, "bulk"));
        allFats.add(new Ingredient("Almonds", 579, 21, 22, 50, "bulk"));
        allFats.add(new Ingredient("Chia Seeds", 486, 17, 42, 31, "cutting"));
        allFats.add(new Ingredient("Flax Seeds", 534, 18, 29, 42, "cutting"));
        allFats.add(new Ingredient("Avocado", 160, 2, 9, 15, "bulk"));
    }

    protected static List<Ingredient> getFatsByGoal(String goal) {
        List<Ingredient> result = new ArrayList<>();
        for (Ingredient i : allFats) {
            if (i.goal.equals(goal)) result.add(i);
        }
        return result;
    }
}