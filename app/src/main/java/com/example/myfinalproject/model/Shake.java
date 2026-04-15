package com.example.myfinalproject.model;

import java.util.ArrayList;

public class Shake {

    private String shakeId;

    private ArrayList<Item> items;

    // Constructors
    public Shake() {
        items = new ArrayList<>();
    }

    public Shake(String shakeId, ArrayList<Item> items) {
        this.shakeId = shakeId;
        this.items = items;
    }

    // Getters & Setters
    public String getShakeId() {
        return shakeId;
    }

    public void setShakeId(String shakeId) {
        this.shakeId = shakeId;
    }



    public ArrayList<Item> getItems() {
        return items;
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }

    // Methods to manage items
    public void addItem(Item item) {
        items.add(item);
    }

    public void removeItem(Item item) {
        items.remove(item);
    }

    // Nutritional totals
    public double getTotalCalories() {
        double total = 0;
        for (Item item : items) {
            total += item.getCalories();
        }
        return total;
    }

    public double getTotalProtein() {
        double total = 0;
        for (Item item : items) {
            total += item.getProtein();
        }
        return total;
    }

    public double getTotalFat() {
        double total = 0;
        for (Item item : items) {
            total += item.getFat();
        }
        return total;
    }

    public double getTotalCarbs() {
        double total = 0;
        for (Item item : items) {
            total += item.getCarbs();
        }
        return total;
    }

    // Print summary
    public void printSummary() {
        System.out.println("🍹 סיכום השייק שלך:");
        if (items.isEmpty()) {
            System.out.println("אין רכיבים בשייק.");
            return;
        }

        for (Item item : items) {
            System.out.println("- " + item.getName() + " (" + item.getCalories() + " קלוריות)");
        }

        System.out.println("----------------------------------");
        System.out.println("סה\"כ קלוריות: " + getTotalCalories());
        System.out.println("סה\"כ חלבונים: " + getTotalProtein() + " גרם");
        System.out.println("סה\"כ שומנים: " + getTotalFat() + " גרם");
        System.out.println("סה\"כ פחמימות: " + getTotalCarbs() + " גרם");
        System.out.println("----------------------------------");
    }
}
