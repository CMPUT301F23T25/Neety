package com.team25.neety;

import android.util.Log;
import com.google.firebase.firestore.DocumentSnapshot;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class Item implements Serializable {
    private UUID id;
    private Date purchaseDate;
    private String make;
    private String model;
    private String description;
    private String serial;
    private float estimatedValue;
    private String comments;
    private boolean isSelected;
    private String userId;

    public Item(UUID id, Date purchaseDate, String make, String model, String description, String serial, float estimatedValue, String comments) {
        this.id = id;
        this.purchaseDate = purchaseDate;
        this.make = make;
        this.model = model;
        this.description = description;
        this.estimatedValue = estimatedValue;
        this.comments = comments;
        this.serial = serial;
    }

    public Item(String make, String model, float estimatedValue) {
        this(UUID.randomUUID(), new Date(), make, model, null, null, estimatedValue, null);
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public String getPurchaseDateString() {
        return Helpers.getStringFromDate(purchaseDate);
    }

    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public float getEstimatedValue() {
        return estimatedValue;
    }

    public String getEstimatedValueString() {
        return Helpers.floatToPriceString(estimatedValue);
    }

    public void setEstimatedValue(float estimatedValue) {
        this.estimatedValue = estimatedValue;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Item item = (Item) obj;
        return Objects.equals(id, item.id);
    }

    public UUID getId() {
        return id;
    }

    public String getIdString() {
        return id.toString();
    }

    public static Item getItemFromDocument(DocumentSnapshot doc) {
        String id = doc.getId();
        String model = doc.getString("Model");
        String make = doc.getString("Make");
        String value = doc.getString("Value").substring(1);
        String description = doc.getString("Description");
        Date purchaseDate = Helpers.getDateFromString(doc.getString("PurchaseDate"));
        String serial = doc.getString("Serial");
        String comments = doc.getString("Comments");
        String userId = doc.getString("UserId");

        Log.d("Firestore", String.format("Model(%s, %s) fetched", model, make));

        Item item = new Item(UUID.fromString(id), purchaseDate, make, model, description, serial, Float.parseFloat(value), comments);
        item.setUserId(userId);

        return item;
    }

    public static HashMap<String, String> getFirestoreDataFromItem(Item item) {
        HashMap<String, String> data = new HashMap<>();
        data.put("Model", item.getModel());
        data.put("Make", item.getMake());
        data.put("Value", item.getEstimatedValueString());
        data.put("Description", item.getDescription());
        data.put("PurchaseDate", item.getPurchaseDateString());
        data.put("Serial", item.getSerial());
        data.put("Comments", item.getComments());
        data.put("UserId", item.getUserId());

        return data;
    }
}
