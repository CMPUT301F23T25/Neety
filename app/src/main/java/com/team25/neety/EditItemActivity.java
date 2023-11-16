package com.team25.neety;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.UUID;

public class EditItemActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private CollectionReference itemsRef;

    private UUID itemId;
    private EditText editMake, editModel, editValue, editDescription, editSerial, editComments;
    private Button saveButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        // Initialize EditText fields
        editMake = findViewById(R.id.edit_make);
        editModel = findViewById(R.id.edit_model);
        editValue = findViewById(R.id.edit_value);
        editDescription = findViewById(R.id.edit_description);
        editSerial = findViewById(R.id.edit_serial);
        editComments = findViewById(R.id.edit_comments);

        saveButton = findViewById(R.id.save_button);
        saveButton.setEnabled(false);

        db = FirebaseFirestore.getInstance();
        itemsRef = db.collection("items");

        itemId = getIntent().getSerializableExtra(Constants.INTENT_ITEM_ID_KEY, UUID.class);

        itemsRef.document(itemId.toString()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        populateFields(Item.getItemFromDocument(document));
                    } else {
                        Log.d("ViewItemActivity", "No such document");
                        finish();
                    }
                } else {
                    Log.d("ViewItemActivity", "get failed with ", task.getException());
                    finish();
                }
            }
        });


    }

    private void populateFields(Item item) {
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

        saveButton.setOnClickListener(v -> saveEditedItem());
        saveButton.setEnabled(true);
    }

    private void saveEditedItem() {
        String make = editMake.getText().toString();
        String model = editModel.getText().toString();
        float value = Float.parseFloat(editValue.getText().toString());
        String description = editDescription.getText().toString();
        String serial = editSerial.getText().toString();
        String comments = editComments.getText().toString();
        // TODO: FIX THIS
        Date purchaseDate = new Date();

        // Create an updated Item object
        Item updatedItem = new Item(itemId, purchaseDate, make, model, description, serial, value, comments);

        // Save the new item to the db replacing the old item
        itemsRef
                .document(itemId.toString())
                .set(Item.getFirestoreDataFromItem(updatedItem))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firestore", "DocumentSnapshot successfully written!");
                        finish();
                    }
                });
    }
}