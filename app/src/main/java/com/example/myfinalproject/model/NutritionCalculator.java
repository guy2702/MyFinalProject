package com.example.myfinalproject.model;

/**
 * מחלקת עזר (Utility) לחישוב הערכים התזונתיים של שייק המורכב ממספר פריטים.
 * המחלקה מבצעת חישוב יחסי לפי משקל (גרמים) של כל רכיב בהשוואה לערכים התזונתיים ל-100 גרם.
 */

import java.util.List;

public class NutritionCalculator {

    /**
     * מחלקה פנימית המייצגת את התוצאה הסופית של החישוב התזונתי.
     */
    public static class NutritionResult {
        public double calories;
        public double protein;
        public double fat;
        public double carbs;
        public double sugar;
    }

    /**
     * פונקציה לחישוב הערכים התזונתיים הכוללים של השייק.
     * החישוב מתבצע לפי הנוסחה: (ערך ל-100 גרם * משקל הפריט) / 100.
     *
     * @param items רשימת הפריטים שנבחרו להרכבת השייק.
     * @return אובייקט NutritionResult המכיל את סיכום הערכים התזונתיים.
     */
    public static NutritionResult calculate(List<Item> items) {
        NutritionResult result = new NutritionResult();

        for (Item item : items) {
            // חישוב מקדם הכמות (Factor) ביחס ל-100 גרם
            double factor = item.getAmount() / 100.0;

            // סיכום הערכים התזונתיים לפי הפקטור
            result.calories += item.getCalories() * factor;
            result.protein += item.getProtein() * factor;
            result.fat += item.getFat() * factor;
            result.carbs += item.getCarbs() * factor;
            result.sugar += item.getSugar() * factor;
        }

        return result;
    }
}
