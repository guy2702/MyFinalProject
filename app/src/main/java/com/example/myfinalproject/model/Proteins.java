package com.example.myfinalproject.model;

import java.util.ArrayList;
import java.util.List;

public class Proteins {
    protected static List<Ingredient> allProteins = new ArrayList<>();

    static {
        allProteins.add(new Ingredient("Whey Protein", 120, 24, 3, 1.5, "bulk"));
        allProteins.add(new Ingredient("Cottage Cheese", 98, 11, 3, 4.3, "bulk"));
        allProteins.add(new Ingredient("Tofu", 76, 8, 1.9, 4.8, "cutting"));
        allProteins.add(new Ingredient("Pea Protein", 100, 20, 1, 1.5, "cutting"));
    }

    protected static List<Ingredient> getProteinsByGoal(String goal) {
        List<Ingredient> result = new ArrayList<>();
        for (Ingredient i : allProteins) {
            if (i.goal.equals(goal)) result.add(i);
        }
        return result;
    }
}