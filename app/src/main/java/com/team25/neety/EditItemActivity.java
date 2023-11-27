package com.team25.neety;


import static com.team25.neety.Constants.REQUEST_CAMERA_PERMISSION_CODE;
import static java.security.AccessController.getContext;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.Manifest;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class EditItemActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private CollectionReference itemsRef;

    private UUID itemId;
    private EditText editMake, editModel, editValue, editDescription, editSerial, editComments, editDate;
    private Button saveButton;
    private ImageButton calendar_button;
    private ImageButton cameraButton;

    private ActivityResultLauncher<Intent> cameraResultLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        assert getSupportActionBar() != null;   // null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   // show back button
        getSupportActionBar().setTitle("Edit");
        getSupportActionBar().setBackgroundDrawable(getDrawable(R.color.space_cadet));

        // Initialize EditText fields
        editMake = findViewById(R.id.edit_make);
        editModel = findViewById(R.id.edit_model);
        editValue = findViewById(R.id.edit_value);
        editDescription = findViewById(R.id.edit_description);
        editSerial = findViewById(R.id.edit_serial);
        editComments = findViewById(R.id.edit_comments);
        editDate = findViewById(R.id.edit_date);
        calendar_button = findViewById(R.id.calendar_button);

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
                                Log.i("Scanned Barcodes", barcodes.toString());
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


        //Handle calendar button for getting date
        calendar_button.setOnClickListener(view1 -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this, // or getActivity() if you're in a Fragment
                    (datePicker, i, i1, i2) -> {
                        String month_of_year;
                        String day_of_month;

                        if (i1 + 1 < 10) {
                            month_of_year = "0" + (i1 + 1);
                        } else month_of_year = String.valueOf(i1 + 1);

                        if (i2 < 10) {
                            day_of_month = "0" + i2;
                        } else day_of_month = String.valueOf(i2);

                        String date_inp = i + "-" + month_of_year + "-" + day_of_month;
                        editDate.setText(date_inp);
                    },
                    year, month, day);
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
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
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION_CODE);
            } else {
                startCamera();
            }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, "Camera Permission is Required to Use Camera.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraResultLauncher.launch(cameraIntent);
    }
}