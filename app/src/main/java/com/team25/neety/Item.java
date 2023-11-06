package com.team25.neety;

import java.util.Date;

public class Item {
    private Date purchaseDate;
    private String make;
    private String model;
    private String description;
    private int serial;
    private float estimatedValue;
    private String comments;

    public Item(Date purchaseDate, String make, String model, String description, int serial, float estimatedValue, String comments) {
        this.purchaseDate = purchaseDate;
        this.make = make;
        this.model = model;
        this.description = description;
        this.estimatedValue = estimatedValue;
        this.comments = comments;

        if (serial != Constants.NO_SERIAL) {
            this.serial = serial;
        }

    }

    public Item(String make, String model, float estimatedValue) {
        this(new Date(), make, model, "", Constants.NO_SERIAL, estimatedValue, "");
    }


    public Date getPurchaseDate() {
        return purchaseDate;
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

    public int getSerial() {
        return serial;
    }

    public void setSerial(int serial) {
        this.serial = serial;
    }

    public float getEstimatedValue() {
        return estimatedValue;
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

}
