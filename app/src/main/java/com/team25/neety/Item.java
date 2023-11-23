package com.team25.neety;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

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
        refreshImageUrls();
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

    private void refreshImageUrls() {
        List<String> urls = new ArrayList<>();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        String path = "images/" + this.id + "/";
        StorageReference imagesRef = storageRef.child(path);

        imagesRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference item : listResult.getItems()) {
                            // Get the download URL for each file
                            item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            // Got the download URL
                                            String downloadUrl = uri.toString();
                                            urls.add(downloadUrl);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e("GetURL", "Error getting URL", e);
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Log errors
                        Log.e("ViewItemActivity", "Error getting images", exception);
                    }
                });
        this.imageUrls = urls;
    }

    public List<String> getImageUrls() {
        refreshImageUrls();
        return this.imageUrls;
    }

    private void uploadImageToFirebase(Context context, Uri photoURI) {
        if (photoURI != null) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            String path = "images/" + this.id + "/";
            StorageReference imageRef = storageRef.child(path + photoURI.getLastPathSegment());
            UploadTask uploadTask = imageRef.putFile(photoURI);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    Log.e("Upload Failure", "Upload failed: " + exception.getMessage());
                    Toast.makeText(context, "Upload failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Clean up the temporary image file here if necessary
                    Toast.makeText(context, "Upload successful ", Toast.LENGTH_SHORT).show();
                    Log.d("Upload Success", "Upload successful: " + taskSnapshot.getMetadata().getReference().getPath());
                    refreshImageUrls();
                }
            });
        } else {
            Toast.makeText(context, "No photo to upload", Toast.LENGTH_SHORT).show();
            Log.w("Upload Failure", "No photo to upload");
        }
    }

    public void deleteImagesFromStorage() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        String path = "images/" + this.id.toString() + "/";
        StorageReference imagesRef = storageRef.child(path);

        imagesRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference item : listResult.getItems()) {
                            item.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // File deleted successfully
                                    Log.d("Item", "File deleted successfully");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Uh-oh, an error occurred!
                                    Log.e("Item", "Failed to delete file", exception);
                                }
                            });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Uh-oh, an error occurred!
                        Log.e("Item", "Failed to list files", exception);
                    }
                });
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
        Log.d("Firestore", String.format("Model(%s, %s) fetched",
                model, make));


        return new Item(UUID.fromString(id), purchaseDate, make, model, description, serial, Float.parseFloat(value), comments);
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

        return data;
    }
}
