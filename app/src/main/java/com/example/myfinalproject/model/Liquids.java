package com.example.myfinalproject.model;

import java.util.ArrayList;
import java.util.List;

public class Liquids {
    protected static List<Ingredient> allLiquids = new ArrayList<>();

    static {
        allLiquids.add(new Ingredient("Water", 0, 0, 0, 0, "cutting"));
        allLiquids.add(new Ingredient("Milk", 42, 3.4, 5, 1, "bulk"));
        allLiquids.add(new Ingredient("Almond Milk", 17, 0.6, 0.3, 1.1, "cutting"));
        allLiquids.add(new Ingredient("Yogurt", 59, 10, 3.6, 0.4, "bulk"));
    }

    protected static List<Ingredient> getLiquids() {
        return allLiquids;
    }
}