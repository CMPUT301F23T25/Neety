package com.team25.neety;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Item implements Serializable {
    private Date purchaseDate;
    private String make;
    private String model;
    private String description;
    private String serial;
    private float estimatedValue;
    private String comments;

    public Item(Date purchaseDate, String make, String model, String description, String serial, float estimatedValue, String comments) {
        this.purchaseDate = purchaseDate;
        this.make = make;
        this.model = model;
        this.description = description;
        this.estimatedValue = estimatedValue;
        this.comments = comments;
        this.serial = serial;
    }

    public Item(String make, String model, float estimatedValue) {
        this(new Date(), make, model, null, null, estimatedValue, null);
    }


    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public String getPurchaseDateString() {
        // TODO: Perhaps add locale here?
        DateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT_PATTERN);
        return df.format(purchaseDate);
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
        // TODO: Perhaps add locale here?
        return String.format("$%,.2f", estimatedValue);
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
