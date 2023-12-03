package com.team25.neety;

import java.util.ArrayList;

public class Tag {

    private ArrayList<Item> itemsList;

    private String name;


    public Tag(String name, Item item) {
        itemsList.add(item);
        this.name = name;
    }
    public void addItem(Item item){
        itemsList.add(item);
    }

    public ArrayList<Item> getItemsList() {
        return itemsList;
    }

    public void setItemsList(ArrayList<Item> itemsList) {
        this.itemsList = itemsList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
