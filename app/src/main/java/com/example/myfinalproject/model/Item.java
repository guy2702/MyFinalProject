package com.example.myfinalproject.model;

/**
 * מחלקה המייצגת פריט בודד (Item) במערכת (כמו פרי, ירק, אגוזים וכו').
 * המחלקה מכילה את כל הנתונים התזונתיים של הפריט וכן את מצב הבחירה שלו
 * על ידי המשתמש בתהליך הרכבת השייק.
 */

import androidx.annotation.NonNull;

public class Item {

    private String id;
    private String name;
    private String type; // קטגוריית הפריט (למשל: "פירות", "ירקות")
    private String goal; // מטרה תזונתית משויכת (למשל: "מסה" או "חיטוב")

    // ערכים תזונתיים ל-100 גרם
    private double calories;
    private double protein;
    private double fat;
    private double carbs;
    private double sugar = 0;

    private String pic; // מחרוזת בפורמט Base64 המייצגת את תמונת הפריט

    // משתני עזר לניהול בחירה על ידי המשתמש
    private boolean selected = false;
    private int amount = 0; // כמות בגרמים שהמשתמש בחר

    /**
     * בנאי ריק (Default Constructor) – חובה לצורך עבודה עם Firebase Realtime Database.
     */
    public Item() {}

    /**
     * בנאי מלא לאתחול פריט חדש.
     */
    public Item(String id, String name, String type, String goal,
                double calories, double protein, double fat, double carbs,
                double sugar, String pic) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.goal = goal;
        this.calories = calories;
        this.protein = protein;
        this.fat = fat;
        this.carbs = carbs;
        this.sugar = sugar;
        this.pic = pic;
    }

    // --- Getters & Setters ---

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getGoal() { return goal; }
    public void setGoal(String goal) { this.goal = goal; }

    public double getCalories() { return calories; }
    public void setCalories(double calories) { this.calories = calories; }

    public double getProtein() { return protein; }
    public void setProtein(double protein) { this.protein = protein; }

    public double getFat() { return fat; }
    public void setFat(double fat) { this.fat = fat; }

    public double getCarbs() { return carbs; }
    public void setCarbs(double carbs) { this.carbs = carbs; }

    public double getSugar() { return sugar; }
    public void setSugar(double sugar) { this.sugar = sugar; }

    public String getPic() { return pic; }
    public void setPic(String pic) { this.pic = pic; }

    public boolean isSelected() { return selected; }
    public void setSelected(boolean selected) { this.selected = selected; }

    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }

    @NonNull
    @Override
    public String toString() {
        return "Item{name='" + name + "', type='" + type + "', amount=" + amount + "}";
    }
}
// הוספנו הערות Javadoc כדי להסביר את מבנה המחלקה והשדות.
// המחלקה מייצגת את האובייקט הבסיסי ביותר במערכת המוצרים.