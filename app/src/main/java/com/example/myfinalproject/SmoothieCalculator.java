package com.example.myfinalproject;

import java.util.HashMap;
import java.util.Map;

public class SmoothieCalculator {

    public static final String GOAL_MUSCLE = "MUSCLE";
    public static final String GOAL_CUT = "CUT";

    public static final String TYPE_FRUITS_VEGETABLES = "FruitsandVegetables";
    public static final String TYPE_LIQUIDS = "Liquids";
    public static final String TYPE_PROTEIN = "Protein";
    public static final String TYPE_SWEETENERS = "Sweeteners";
    public static final String TYPE_NUTS = "Nuts";

    public static Map<String, Integer> calculateCategoryAmounts(String goal, int cupSize) {
        Map<String, Integer> result = new HashMap<>();

        double fruitsVegPercent;
        double liquidsPercent;
        double proteinPercent;
        double nutsPercent;
        double sweetenersPercent;

        if (GOAL_MUSCLE.equalsIgnoreCase(goal)) {
            // מסה
            fruitsVegPercent = 0.35;
            liquidsPercent = 0.25;
            proteinPercent = 0.20;
            nutsPercent = 0.15;
            sweetenersPercent = 0.05;
        } else if (GOAL_CUT.equalsIgnoreCase(goal)) {
            // חיטוב
            fruitsVegPercent = 0.40;
            liquidsPercent = 0.30;
            proteinPercent = 0.22;
            nutsPercent = 0.05;
            sweetenersPercent = 0.03;
        } else {
            // ברירת מחדל
            fruitsVegPercent = 0.40;
            liquidsPercent = 0.30;
            proteinPercent = 0.20;
            nutsPercent = 0.05;
            sweetenersPercent = 0.05;
        }

        int fruitsVegAmount = (int) Math.round(cupSize * fruitsVegPercent);
        int liquidsAmount = (int) Math.round(cupSize * liquidsPercent);
        int proteinAmount = (int) Math.round(cupSize * proteinPercent);
        int nutsAmount = (int) Math.round(cupSize * nutsPercent);

        int usedAmount = fruitsVegAmount + liquidsAmount + proteinAmount + nutsAmount;
        int sweetenersAmount = cupSize - usedAmount;

        result.put(TYPE_FRUITS_VEGETABLES, fruitsVegAmount);
        result.put(TYPE_LIQUIDS, liquidsAmount);
        result.put(TYPE_PROTEIN, proteinAmount);
        result.put(TYPE_NUTS, nutsAmount);
        result.put(TYPE_SWEETENERS, sweetenersAmount);

        return result;
    }

    public static int getCategoryAmount(String goal, int cupSize, String type) {
        Map<String, Integer> amounts = calculateCategoryAmounts(goal, cupSize);
        Integer value = amounts.get(type);
        return value != null ? value : 0;
    }

    public static int calculateAmountPerItem(int totalCategoryAmount, int selectedItemsCount) {
        if (selectedItemsCount <= 0) {
            return 0;
        }
        return totalCategoryAmount / selectedItemsCount;
    }
}