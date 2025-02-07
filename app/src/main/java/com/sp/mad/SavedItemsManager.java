package com.sp.mad;

import java.util.ArrayList;
import java.util.List;

public class SavedItemsManager {
    private static List<Item> savedItems = new ArrayList<>();

    public static void addItem(Item item) {
        savedItems.add(item);
    }

    public static List<Item> getSavedItems() {
        return savedItems;
    }

    // Check if an item is already saved
    public static boolean isItemAlreadySaved(Item newItem) {
        for (Item item : savedItems) {
            if (item.getTitle().equals(newItem.getTitle()) &&
                    item.getPrice().equals(newItem.getPrice()) &&
                    item.getImageResid() == newItem.getImageResid()) {
                return true; // Item is already saved
            }
        }
        return false;
    }
}
