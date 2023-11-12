package com.team25.neety;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditItemActivity extends AppCompatActivity {

    private Item item;
    private EditText editMake, editModel, editValue, editDescription, editSerial, editComments;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        item = (Item) getIntent().getSerializableExtra(Constants.INTENT_ITEM_KEY);

        // Initialize EditText fields
        editMake = findViewById(R.id.edit_make);
        editModel = findViewById(R.id.edit_model);
        editValue = findViewById(R.id.edit_value);
        editDescription = findViewById(R.id.edit_description);
        editSerial = findViewById(R.id.edit_serial);
        editComments = findViewById(R.id.edit_comments);

        // Set existing item details in EditText fields
        editMake.setText(item.getMake());
        editMake.setEnabled(true);
        editModel.setText(item.getModel());
        editModel.setEnabled(true);
        editValue.setText(String.valueOf(item.getEstimatedValue()));
        editValue.setEnabled(true);
        editDescription.setText(item.getDescription());
        editDescription.setEnabled(true);
        editSerial.setText(item.getSerial());
        editSerial.setEnabled(true);
        editComments.setText(item.getComments());
        editComments.setEnabled(true);

        Button saveButton = findViewById(R.id.edit_button);
        saveButton.setOnClickListener(v -> saveEditedItem());
    }

    private void saveEditedItem() {
        String make = editMake.getText().toString();
        String model = editModel.getText().toString();
        float value = Float.parseFloat(editValue.getText().toString());
        String description = editDescription.getText().toString();
        String serial = editSerial.getText().toString();
        String comments = editComments.getText().toString();

        // Create an updated Item object
        Item updatedItem = new Item(model, make, value);
        updatedItem.setDescription(description);
        updatedItem.setSerial(serial);
        updatedItem.setComments(comments);

        // Update the item in Firestore
        updateItemInFirestore(updatedItem);

        // Send the updated item back to the calling activity
        Intent resultIntent = new Intent();
        resultIntent.putExtra(Constants.INTENT_ITEM_KEY, updatedItem);
        setResult(RESULT_OK, resultIntent);

        finish();
    }

    private void updateItemInFirestore(Item updatedItem) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference itemsRef = db.collection("items");

        // Update the item in Firestore
        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("make", updatedItem.getMake());
        updatedData.put("value", String.valueOf(updatedItem.getEstimatedValue()));

        DocumentReference itemRef = itemsRef.document(updatedItem.getModel());
        itemRef.update(updatedData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firestore", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Firestore", "Error updating document", e);
                    }
                });
    }
}
