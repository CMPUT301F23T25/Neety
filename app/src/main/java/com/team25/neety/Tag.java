package com.team25.neety;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class Tag {

    private ArrayList<Item> itemsList;

    private String name;

    private boolean isSelected;


    public Tag(String name, ArrayList<Item> items) {
        itemsList.addAll(items);
        this.name = name;
    }

    public Tag(String name){
        this.itemsList = new ArrayList<Item>();
        this.name = name;
    }

    public void addItem(Item item){
        itemsList.add(item);
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
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


    @Override
    public String toString() {
        return name;
    }
}
