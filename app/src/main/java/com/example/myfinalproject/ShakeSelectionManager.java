package com.example.myfinalproject;

import com.example.myfinalproject.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShakeSelectionManager {

    private static final Map<String, ArrayList<Item>> selectionsByCategory = new HashMap<>();

    public static void setCategoryItems(String category, List<Item> items) {
        ArrayList<Item> selected = new ArrayList<>();

        for (Item item : items) {
            if (item.isSelected() && item.getAmount() > 0) {
                selected.add(item);
            }
        }

        selectionsByCategory.put(category, selected);
    }

    public static ArrayList<Item> getAllSelectedItems() {
        ArrayList<Item> allItems = new ArrayList<>();

        for (ArrayList<Item> list : selectionsByCategory.values()) {
            allItems.addAll(list);
        }

        return allItems;
    }

    public static void clearAll() {
        selectionsByCategory.clear();
    }
}