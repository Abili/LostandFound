package com.raisac.lostandfound;

import android.net.Uri;

public class Item {
    private String itemName;
    private String itemDescription;
    private Uri itemImage;

    public Item() {
    }

    public Item(String itemName, String itemDescription, Uri itemImage) {
        this.itemName = itemName;
        this.itemDescription = itemDescription;
        this.itemImage = itemImage;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public Uri getItemImage() {
        return itemImage;
    }

    public void setItemImage(Uri itemImage) {
        this.itemImage = itemImage;
    }
}
