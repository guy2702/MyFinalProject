package com.example.myfinalproject.model;

/**
 * מחלקה זו (Manager) אחראית על ניהול בחירות המשתמש בזמן הרכבת השייק.
 * היא משמשת כמרכז נתונים זמני (Singleton) המאחסן את רשימת הפריטים שנבחרו
 * לכל קטגוריה (כגון פירות, נוזלים, אגוזים) לאורך תהליך הבנייה של השייק.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShakeSelectionManager {

    // מפה המקשרת בין שם קטגוריה לרשימת הפריטים שנבחרו עבורה
    private static final Map<String, ArrayList<Item>> selectionsByCategory = new HashMap<>();

    // אובייקט השייק הנוכחי שנמצא בצפייה (עבור מסכי הפירוט)
    private static Shake currentViewedShake;

    /**
     * סינון ושמירת פריטים שנבחרו עבור קטגוריה ספציפית.
     * רק פריטים שסומנו (isSelected) ובעלי כמות גדולה מ-0 יישמרו.
     */
    public static void setCategoryItems(String category, List<Item> items) {
        ArrayList<Item> selected = new ArrayList<>();

        for (Item item : items) {
            if (item.isSelected() && item.getAmount() > 0) {
                selected.add(item);
            }
        }

        selectionsByCategory.put(category, selected);
    }

    /**
     * איסוף כל הפריטים שנבחרו מכל הקטגוריות השונות לרשימה אחת מאוחדת.
     */
    public static ArrayList<Item> getAllSelectedItems() {
        ArrayList<Item> allItems = new ArrayList<>();

        for (ArrayList<Item> list : selectionsByCategory.values()) {
            allItems.addAll(list);
        }

        return allItems;
    }

    /**
     * הגדרת השייק הנוכחי לצורך צפייה בפרטיו במסכים אחרים.
     */
    public static void setCurrentViewedShake(Shake shake) {
        currentViewedShake = shake;
    }

    /**
     * שליפת השייק הנוכחי המוצג.
     */
    public static Shake getCurrentViewedShake() {
        return currentViewedShake;
    }

    /**
     * ניקוי כל נתוני הבחירות והשייק הנצפה – מופעל בסיום הרכבת שייק חדש.
     */
    public static void clearAll() {
        selectionsByCategory.clear();
        currentViewedShake = null;
    }
}
// תיעוד סופי: מחלקת ShakeSelectionManager משמשת כ-Helper מרכזי לניהול מצב (State)
// באפליקציה. בזכות העבודה עם המפה (HashMap), ניתן לשמור בחירות של קטגוריות שונות
// מבלי לדרוס נתונים קודמים. הניהול הסטטי מבטיח גישה קלה לנתונים מכל Activity.
// סיום קובץ ShakeSelectionManager.