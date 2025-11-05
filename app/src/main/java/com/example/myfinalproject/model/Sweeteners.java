package com.example.myfinalproject.model;

import java.util.ArrayList;
import java.util.List;

public class Sweeteners {
    protected static List<Ingredient> allSweeteners = new ArrayList<>();

    static {
        allSweeteners.add(new Ingredient("Honey", 64, 0, 17, 0, "bulk"));
        allSweeteners.add(new Ingredient("Stevia", 0, 0, 0, 0, "cutting"));
    }

    protected static List<Ingredient> getSweetenersByGoal(String goal) {
        List<Ingredient> result = new ArrayList<>();
        for (Ingredient i : allSweeteners) {
            if (i.goal.equals(goal)) result.add(i);
        }
        return result;
    }
}