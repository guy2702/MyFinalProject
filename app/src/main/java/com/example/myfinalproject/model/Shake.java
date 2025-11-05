package com.example.myfinalproject.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class Shake {
    protected List<Ingredient> fruits = new ArrayList<>();
    protected List<Ingredient> vegetables = new ArrayList<>();
    protected List<Ingredient> proteins = new ArrayList<>();
    protected List<Ingredient> liquids = new ArrayList<>();
    protected List<Ingredient> fats = new ArrayList<>();
    protected List<Ingredient> sweeteners = new ArrayList<>();

    protected String goal; // "bulk" או "cutting"

    public Shake(String goal) {
        this.goal = goal;
    }


    public void addFruit(Ingredient fruit) { fruits.add(fruit); }
    public void addVegetable(Ingredient veg) { vegetables.add(veg); }
    public void addProtein(Ingredient protein) { proteins.add(protein); }
    public void addLiquid(Ingredient liquid) { liquids.add(liquid); }
    public void addFat(Ingredient fat) { fats.add(fat); }
    public void addSweetener(Ingredient sweetener) { sweeteners.add(sweetener); }


    public int getTotalCalories() {
        int total = 0;
        List<List<Ingredient>> allLists = Arrays.asList(fruits, vegetables, proteins, liquids, fats, sweeteners);
        for (List<Ingredient> list : allLists) {
            for (Ingredient ing : list) {
                total += ing.getCalories();
            }
        }
        return total;
    }


    public double getTotalProtein() {
        double total = 0;
        List<List<Ingredient>> allLists = Arrays.asList(fruits, vegetables, proteins, liquids, fats, sweeteners);
        for (List<Ingredient> list : allLists) {
            for (Ingredient ing : list) {
                total += ing.getProtein();
            }
        }
        return total;
    }


    public double getTotalCarbs() {
        double total = 0;
        List<List<Ingredient>> allLists = Arrays.asList(fruits, vegetables, proteins, liquids, fats, sweeteners);
        for (List<Ingredient> list : allLists) {
            for (Ingredient ing : list) {
                total += ing.getCarbs();
            }
        }
        return total;
    }


    public double getTotalFat() {
        double total = 0;
        List<List<Ingredient>> allLists = Arrays.asList(fruits, vegetables, proteins, liquids, fats, sweeteners);
        for (List<Ingredient> list : allLists) {
            for (Ingredient ing : list) {
                total += ing.getFat();
            }
        }
        return total;
    }
}