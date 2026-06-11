package com.example.myfinalproject.model;

import java.util.HashMap;
import java.util.Map;

/**
 * מחלקה זו (SmoothieCalculator) מהווה את "המוח" החישובי של האפליקציה.
 * תפקידה לחשב את כמויות המרכיבים (בגרמים) עבור כל קטגוריה בשייק,
 * בהתאם למטרה האישית של המשתמש (מסה או חיטוב) וגודל הכוס שנבחר.
 */
public class SmoothieCalculator {

    // הגדרת קבועים למטרות ולקטגוריות השונות למניעת טעויות הקלדה
    public static final String GOAL_MUSCLE = "MUSCLE";
    public static final String GOAL_CUT = "CUT";

    public static final String TYPE_FRUITS_VEGETABLES = "FruitsandVegetables";
    public static final String TYPE_LIQUIDS = "Liquids";
    public static final String TYPE_PROTEIN = "Protein";
    public static final String TYPE_SWEETENERS = "Sweeteners";
    public static final String TYPE_NUTS = "Nuts";

    /**
     * פונקציה ראשית שמבצעת את חישוב הכמויות לכל קטגוריה.
     * השיטה משתמשת באחוזים מוגדרים מראש לכל מטרה.
     */
    public static Map<String, Integer> calculateCategoryAmounts(String goal, int cupSize) {
        Map<String, Integer> result = new HashMap<>();

        double fruitsVegPercent;
        double liquidsPercent;
        double proteinPercent;
        double nutsPercent;
        double sweetenersPercent;

        // התאמת אחוזי המרכיבים בהתאם למטרת המשתמש (מסה vs חיטוב)
        if (GOAL_MUSCLE.equalsIgnoreCase(goal)) {
            // הגדרת אחוזים למטרת בניית מסה (יותר אגוזים וחלבון)
            fruitsVegPercent = 0.35;
            liquidsPercent = 0.25;
            proteinPercent = 0.20;
            nutsPercent = 0.15;
            sweetenersPercent = 0.05;
        } else if (GOAL_CUT.equalsIgnoreCase(goal)) {
            // הגדרת אחוזים למטרת חיטוב (יותר פירות וירקות, פחות אגוזים)
            fruitsVegPercent = 0.40;
            liquidsPercent = 0.30;
            proteinPercent = 0.22;
            nutsPercent = 0.05;
            sweetenersPercent = 0.03;
        } else {
            // ערכי ברירת מחדל למקרה של תקלה או בחירה לא מזוהה
            fruitsVegPercent = 0.40;
            liquidsPercent = 0.30;
            proteinPercent = 0.20;
            nutsPercent = 0.05;
            sweetenersPercent = 0.05;
        }

        // חישוב הכמות בגרמים עבור כל קטגוריה לפי גודל הכוס
        int fruitsVegAmount = (int) Math.round(cupSize * fruitsVegPercent);
        int liquidsAmount = (int) Math.round(cupSize * liquidsPercent);
        int proteinAmount = (int) Math.round(cupSize * proteinPercent);
        int nutsAmount = (int) Math.round(cupSize * nutsPercent);

        // חישוב הממתיקים כשארית כדי להשלים ל-100% (גודל הכוס המלא)
        int usedAmount = fruitsVegAmount + liquidsAmount + proteinAmount + nutsAmount;
        int sweetenersAmount = cupSize - usedAmount;

        // אחסון התוצאות במפה (Map) לצורך שליפה נוחה במסכים השונים
        result.put(TYPE_FRUITS_VEGETABLES, fruitsVegAmount);
        result.put(TYPE_LIQUIDS, liquidsAmount);
        result.put(TYPE_PROTEIN, proteinAmount);
        result.put(TYPE_NUTS, nutsAmount);
        result.put(TYPE_SWEETENERS, sweetenersAmount);

        return result;
    }

    // פונקציית עזר להחזרת כמות של קטגוריה ספציפית
    public static int getCategoryAmount(String goal, int cupSize, String type) {
        Map<String, Integer> amounts = calculateCategoryAmounts(goal, cupSize);
        Integer value = amounts.get(type);
        return value != null ? value : 0;
    }

    // פונקציה לחישוב חלוקת הכמות לכל פריט בודד (בתוך הקטגוריה)
    public static int calculateAmountPerItem(int totalCategoryAmount, int selectedItemsCount) {
        if (selectedItemsCount <= 0) {
            return 0;
        }
        return totalCategoryAmount / selectedItemsCount;
    }
}