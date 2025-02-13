package com.sp.mad;

import java.util.ArrayList;
import java.util.List;

public class SavedItemsManager {
    private static final List<Item> savedItems = new ArrayList<>();

    public static void addItem(Item item) {
        savedItems.add(item);
    }

    public static boolean isItemAlreadySaved(Item item) {
        for (Item savedItem : savedItems) {
            if (savedItem.getItemId().equals(item.getItemId())) {

                return true;
            }
        }
        return false;
    }

    public static List<Item> getSavedItems() {
        return new ArrayList<>(savedItems);
    }
}
