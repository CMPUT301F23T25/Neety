package com.team25.neety;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.rpc.Help;

import java.io.Serializable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import java.text.NumberFormat;
import java.util.Date;
import java.util.HashMap;

import java.text.NumberFormat;
import java.util.Date;
import java.util.HashMap;

import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
/**
 * This class is the item class
 * -id            id of item
 * -purchaseDate  date of purchase
 * -make          make of item
 * -model         model of item
 * -description   description of item
 * -serial        serial number of item
 * -estimatedValue estimated value of item
 * -comments      comments of item
 * -isSelected    if item is selected
 * -imageUrls     list of image urls
 * -id            id of item
 *                 
 */
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
    private List<String> imageUrls;
    private List<String> tags = new ArrayList<>();

    /**
     * This is the constructor for the item class
     */
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

    public Item(UUID id, Date purchaseDate, String make, String model, String description, String serial, float estimatedValue, String comments, List<String> tags) {
        this.id = id;
        this.purchaseDate = purchaseDate;
        this.make = make;
        this.model = model;
        this.description = description;
        this.estimatedValue = estimatedValue;
        this.comments = comments;
        this.serial = serial;
        this.tags = tags;
    }

    public Item(String make, String model, float estimatedValue) {
        this(UUID.randomUUID(), new Date(), make, model, null, null, estimatedValue, null, null);
    }

    public void addTag(String s) {
        tags.add(s);
    }


    public List<String> getTags() {
        return tags;
    }

    /**
     * This is the getter and setter for the item class
     * @return purchaseDate
     */
    public Date getPurchaseDate() {
        return purchaseDate;
    }
    /**
     * This is the getter  for the date purchased in string format
     * @return purchaseDate
     */
    public String getPurchaseDateString() {
        return Helpers.getStringFromDate(purchaseDate);
    }
    /**
     * This is the  setter for the item class
     * @param purchaseDate
     */
    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }
    /**
     * This is the getter for the make of the item
     * @return make
     */
    public String getMake() {
        return make;
    }
    /**
     * This is the setter for the make of the item
     * @param make
     */
    public void setMake(String make) {
        this.make = make;
    }
    /**
     * This is the getter for the model of the item
     * @return model
     */
    public String getModel() {
        return model;
    }
    /**
     * This is the setter for the model of the item
     * @param model
     */
    public void setModel(String model) {
        this.model = model;
    }
    /*
     * This is the getter for the description of the item
     * @return description
     */
    public String getDescription() {
        return description;
    }
    /**
     * This is the setter for the description of the item
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    public String getSerial() {
        return serial;
    }
    /**
     * This is the setter for the serial number of the item
     * @param serial
     */
    public void setSerial(String serial) {
        this.serial = serial;
    }
    /**
     * This is the getter for the estimated value of the item
     * @return estimatedValue
     */
    public float getEstimatedValue() {
        return estimatedValue;
    }
    /**
     * This is the getter for the estimated value of the item in string format
     * @return estimatedValue
     */
    public String getEstimatedValueString() {
        return Helpers.floatToPriceString(estimatedValue);
    }
    /**
     * This is the setter for the estimated value of the item
     * @param estimatedValue
     */
    public void setEstimatedValue(float estimatedValue) {
        this.estimatedValue = estimatedValue;
    }
    /**
     * This is the getter for the comments of the item
     * @return comments
     */
    public String getComments() {
        return comments;
    }
    /**
     * This is the setter for the comments of the item
     * @param comments
     */
    public void setComments(String comments) {
        this.comments = comments;
    }
    /**
     * This is the getter for the image urls of the item
     * @return imageUrls
     */
    public boolean isSelected() {
        return isSelected;
    }
    /**
     * This is the setter for the image urls of the item
     * @param imageUrls
     */
    public void setSelected(boolean selected) {
        isSelected = selected;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Item item = (Item) obj;
        return Objects.equals(id, item.id); // replace 'model' with your actual fields
    }

    public UUID getId() {
        return id;
    }

    public String getIdString() {
        return id.toString();
    }

    // Commenting out since this could be a potential foot-gun for us.
    // If you ever need to set an ID please think through it thoroughly
    //      before uncommenting the function below
    /* public void setId(UUID id) {
        this.id = id;
    } */

    public interface ImageUrlsCallback {
        void onCallback(List<String> imageUrls);
    }

    public void getImageUrls(ImageUrlsCallback callback, String username) {
        refreshImageUrls(callback, username);
    }

    /**
     * This refreshes the image urls
     * @param callback
     * @param username
     */
    private void refreshImageUrls(ImageUrlsCallback callback, String username) {

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        String path = "images/" + username + "/" + this.id + "/";
        StorageReference imagesRef = storageRef.child(path);
        List<String> Urls = new ArrayList<>();

        imagesRef.listAll()
                .addOnSuccessListener(listResult -> {
                    List<Task<Uri>> tasks = new ArrayList<>();
                    for (StorageReference item : listResult.getItems()) {
                        // Get the download URL for each file
                        tasks.add(item.getDownloadUrl());
                    }
                    Tasks.whenAllSuccess(tasks).addOnSuccessListener(objects -> {
                        for (Object object : objects) {
                            Urls.add(object.toString());
                        }
                        callback.onCallback(Urls);
                    });
                })
                .addOnFailureListener(exception -> {
                    // Handle any errors
                    Log.e("ViewItemActivity", "Error getting images", exception);
                });
    }

    public interface UploadCallback {
        void onCallback();
    }


    /**
     * This uploads the image to firebase
     * @param context
     * @param photoURI
     * @param username
     * @param callback
     */
    public void uploadImageToFirebase(Context context, Uri photoURI,String username, UploadCallback callback) {

        if (photoURI != null) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            String path = "images/" + username + "/" + this.id + "/";

            // Format the current date and time as a string
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
            String dateTime = sdf.format(new Date());

            StorageReference imageRef = storageRef.child(path + username + "_" + dateTime + ".jpg");
            UploadTask uploadTask = imageRef.putFile(photoURI);
            uploadTask.addOnFailureListener(exception -> {
                // Handle unsuccessful uploads
                Log.e("Upload Failure", "Upload failed: " + exception.getMessage());
                Toast.makeText(context, "Upload failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
            }).addOnSuccessListener(taskSnapshot -> {
                Toast.makeText(context, "Upload successful ", Toast.LENGTH_SHORT).show();
                Log.d("Upload Success", "Upload successful: " + taskSnapshot.getMetadata().getReference().getPath());

                callback.onCallback();
            });
        } else {
            Toast.makeText(context, "No photo to upload", Toast.LENGTH_SHORT).show();
            Log.w("Upload Failure", "No photo to upload");
        }
    }
    /**
     * This deletes the image from firebase
     * @param username 
     */
    public void deleteImagesFromStorage(String username) {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        String path = "images/" + username + "/" + this.id.toString() + "/";
        StorageReference imagesRef = storageRef.child(path);

        imagesRef.listAll()
                .addOnSuccessListener(listResult -> {
                    for (StorageReference item : listResult.getItems()) {
                        item.delete().addOnSuccessListener(aVoid -> {
                            // File deleted successfully
                            Log.d("Item", "File deleted successfully");
                        }).addOnFailureListener(exception -> {
                            // Uh-oh, an error occurred!
                            Log.e("Item", "Failed to delete file", exception);
                        });
                    }
                })
                .addOnFailureListener(exception -> {
                    // Uh-oh, an error occurred!
                    Log.e("Item", "Failed to list files", exception);
                });
    }
    /**
     * This gets the item from the document
     * @param doc
     * @return item
     */
    public static Item getItemFromDocument(DocumentSnapshot doc) {
        String id = doc.getId();
        String model = doc.getString("Model");
        String make = doc.getString("Make");
        String value = doc.getString("Value").substring(1);
        String description = doc.getString("Description");
        Date purchaseDate = Helpers.getDateFromString(doc.getString("PurchaseDate"));
        String serial = doc.getString("Serial");
        String comments = doc.getString("Comments");
        String tags = doc.getString("Tags");
        Log.d("Firestore", String.format("Model(%s, %s) fetched",
                model, make));

        float valueNumber = Helpers.priceStringToFloat(value);

        Log.d("FIRESTORE", value);


        return new Item(UUID.fromString(id), purchaseDate, make, model, description, serial, valueNumber, comments, Helpers.convertStringToTags(tags));
    }
    /**
     * this makes a hashmap of the item data
     * @param item
     * @return data
     */
    public static HashMap<String, String> getFirestoreDataFromItem(Item item) {
        HashMap<String, String> data = new HashMap<>();
        data.put("Model", item.getModel());
        data.put("Make", item.getMake());
        data.put("Value", item.getEstimatedValueString());
        data.put("Description", item.getDescription());
        data.put("PurchaseDate", item.getPurchaseDateString());
        data.put("Serial", item.getSerial());
        data.put("Comments", item.getComments());
        data.put("Tags", Helpers.getPrintableTags(item.getTags()));

        return data;
    }


}
