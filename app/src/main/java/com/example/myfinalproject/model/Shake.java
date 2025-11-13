package com.example.myfinalproject.model;

import java.util.ArrayList;

public class Shake {

    private ArrayList<Item> items;

    // בנאי – יוצר שייק ריק בהתחלה
    public Shake() {
        items = new ArrayList<>();
    }


    public void addItem(Item item) {
        items.add(item);
    }

    //
    public void removeItem(Item item) {
        items.remove(item);
    }

    // חישוב סך הקלוריות
    public double getTotalCalories() {
        double total = 0;
        for (Item item : items) {
            total += item.getCalories();
        }
        return total;
    }

    // חישוב סך החלבונים
    public double getTotalProtein() {
        double total = 0;
        for (Item item : items) {
            total += item.getProtein();
        }
        return total;
    }

    // חישוב סך השומנים
    public double getTotalFat() {
        double total = 0;
        for (Item item : items) {
            total += item.getFat();
        }
        return total;
    }

    // חישוב סך הפחמימות
    public double getTotalCarbs() {
        double total = 0;
        for (Item item : items) {
            total += item.getCarbs();
        }
        return total;
    }

    // הדפסת סיכום השייק
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

    // Getter – אם תרצה לגשת לרשימת הרכיבים
    public ArrayList<Item> getItems() {
        return items;
    }
}
