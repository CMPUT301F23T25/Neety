package com.team25.neety;

public class DataHolder {
    private static DataHolder instance = new DataHolder();
    private Item item;

    public static DataHolder getInstance() {
        return instance;
    }

    public void setData(Item item) {
        this.item = item;
    }

    public Item getData() {
        return item;
    }
}
