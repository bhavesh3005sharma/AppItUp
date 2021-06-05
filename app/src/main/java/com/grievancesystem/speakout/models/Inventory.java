package com.grievancesystem.speakout.models;

public class Inventory {
    String itemName;
    String quantity;

    public Inventory(String itemName, String quantity) {
        this.itemName = itemName;
        this.quantity = quantity;
    }

    public String getItemName() {
        return itemName;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
