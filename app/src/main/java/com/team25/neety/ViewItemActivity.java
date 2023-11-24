package com.team25.neety;

import static com.team25.neety.Constants.REQUEST_CAMERA_PERMISSION_CODE;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class ViewItemActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private CollectionReference itemsRef;
    private UUID itemId;
    private TextView tvMake, tvModel, tvEstimatedValue, tvDescription, tvPurchaseDate, tvSerial, tvComments;
    private RecyclerView rvImages;
    private Button del_button, take_photo_button;
    private Button edit_button;
    private Uri photoURI;
    private ActivityResultLauncher<Intent> takePictureLauncher;
    private Item item;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item);

        assert getSupportActionBar() != null;   // null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   // show back button

        tvMake = findViewById(R.id.make_textview);
        tvModel = findViewById(R.id.model_textview);
        tvEstimatedValue = findViewById(R.id.ev_textview);
        tvDescription = findViewById(R.id.description_textview);
        tvPurchaseDate = findViewById(R.id.purchase_date_textview);
        tvSerial = findViewById(R.id.serial_textview);
        tvComments = findViewById(R.id.comments_textview);
        rvImages = findViewById(R.id.images_recyclerView);

        db = FirebaseFirestore.getInstance();
        itemsRef = db.collection("items");

        itemId = getIntent().getSerializableExtra(Constants.INTENT_ITEM_ID_KEY, UUID.class);

        refresh();

        // Handle edit button
        edit_button = findViewById(R.id.edit_button);
        edit_button.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditItemActivity.class);
            intent.putExtra(Constants.INTENT_ITEM_ID_KEY, itemId);
            startActivity(intent);
        });

        // Handle delete button
        del_button = findViewById(R.id.del_button_item_view);
        del_button.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete entry")
                    .setMessage("Are you sure you want to delete this entry?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // Delete item's images from storage
                        item.deleteImagesFromStorage();
                        // Delete item from database
                        itemsRef.document(itemId.toString()).delete();
                        finish();
                    })
                    .setNegativeButton("No", null)
                    .setIcon(R.drawable.alert)
                    .show();
        });
        // handle gallery photo
        Button add_image_button = findViewById(R.id.gallery_button);
        add_image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, 100);
            }
        });


        // Handle take photo button
        takePictureLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        // Handle the result here
                        if (result.getData() != null) {
                            item.uploadImageToFirebase(this, photoURI, this::refresh);
                        }
                    }
                });
        take_photo_button = findViewById(R.id.take_photo_button);
        take_photo_button.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted, request it
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        REQUEST_CAMERA_PERMISSION_CODE);
            } else {
                setupCamera();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try {
                // Get the selected image URI
                Uri imageUri = data.getData();
                // Set the image to the ImageView
                item.uploadImageToFirebase(this, imageUri, this::refresh);

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(ViewItemActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }
        else {
            Toast.makeText(ViewItemActivity.this, "You haven't picked an image", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {
        itemsRef.document(itemId.toString()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        item = Item.getItemFromDocument(document);
                        populateFields(item);
                        item.getImageUrls(imageUrls -> populateImages(imageUrls));

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
        if (item == null) return;

        tvMake.setText(item.getMake());
        tvModel.setText(item.getModel());
        tvEstimatedValue.setText(item.getEstimatedValueString());
        tvDescription.setText((item.getDescription() != null) ? item.getDescription() : "No description" );
        tvPurchaseDate.setText(item.getPurchaseDateString());
        tvSerial.setText((item.getSerial() != null) ? item.getSerial() : "No serial");
        tvComments.setText((item.getComments() != null) ? item.getComments() : "No comments");
    }
    private void populateImages(List<String> imageUrls) {
        rvImages.setLayoutManager(new LinearLayoutManager(this));
        ImageAdapter adapter = new ImageAdapter(this, imageUrls);
        rvImages.setAdapter(adapter);
    }

    private void setupCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                photoURI = createImageFile();
                if (photoURI != null) {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    takePictureLauncher.launch(takePictureIntent);
                } else {
                    Toast.makeText(this, "Error creating image file", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException ex) {
                Toast.makeText(this, "Error creating image file", Toast.LENGTH_SHORT).show();
                Log.e("CreateImageFileError", "Error creating image file: " + ex.getMessage());
            }
        } else {
            Toast.makeText(this, "No camera app found", Toast.LENGTH_SHORT).show();
        }
    }

    private Uri createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "Neety_" + timeStamp;
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, imageFileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        photoURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        return photoURI;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupCamera();
            } else {
                Toast.makeText(this, "Camera and storage permissions are required to use the camera", Toast.LENGTH_SHORT).show();
            }
        }
    }

}