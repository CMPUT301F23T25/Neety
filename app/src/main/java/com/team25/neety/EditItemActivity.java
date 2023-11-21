package com.team25.neety;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

import java.util.Date;
import java.util.UUID;

public class EditItemActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private CollectionReference itemsRef;

    private UUID itemId;
    private EditText editMake, editModel, editValue, editDescription, editSerial, editComments, editDate;
    private Button saveButton;
    private ImageButton cameraButton;

    private ActivityResultLauncher<Intent> cameraResultLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        assert getSupportActionBar() != null;   // null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   // show back button

        // Initialize EditText fields
        editMake = findViewById(R.id.edit_make);
        editModel = findViewById(R.id.edit_model);
        editValue = findViewById(R.id.edit_value);
        editDescription = findViewById(R.id.edit_description);
        editSerial = findViewById(R.id.edit_serial);
        editComments = findViewById(R.id.edit_comments);
        editDate = findViewById(R.id.edit_date);

        saveButton = findViewById(R.id.save_button);
        saveButton.setEnabled(false);
        cameraButton = findViewById(R.id.camera_button);
        cameraButton.setEnabled(false);

        cameraResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
                    Bitmap b = result.getData().getExtras().getParcelable("data", Bitmap.class);

                    if (b == null) return;

                    InputImage image = InputImage.fromBitmap(b, 0);
                    BarcodeScanner scanner = BarcodeScanning.getClient();
                    scanner.process(image)
                            .addOnSuccessListener(barcodes -> {
                                if (barcodes.size() > 0) {
                                    String scannedValue = barcodes.get(0).getRawValue();
                                    editSerial.setText(scannedValue);
                                }
                            });
                }
            }
        });

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
        editDate.setText(item.getPurchaseDateString());
        editDate.setEnabled(true);

        saveButton.setOnClickListener(v -> saveEditedItem());
        saveButton.setEnabled(true);
        cameraButton.setEnabled(true);
        cameraButton.setOnClickListener(v -> {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            cameraResultLauncher.launch(cameraIntent);
        });
    }

    private void saveEditedItem() {
        String make = editMake.getText().toString();
        String model = editModel.getText().toString();
        float value = Float.parseFloat(editValue.getText().toString());
        String description = editDescription.getText().toString();
        String serial = editSerial.getText().toString();
        String comments = editComments.getText().toString();
        Date purchaseDate = Helpers.getDateFromString(editDate.getText().toString());

        // TODO: FIX THIS
        //Date purchaseDate = new Date();

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

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}