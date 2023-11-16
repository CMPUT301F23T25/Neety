package com.team25.neety;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.UUID;

public class ViewItemActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private CollectionReference itemsRef;


    private UUID itemId;
    private TextView tvMake, tvModel, tvEstimatedValue, tvDescription, tvPurchaseDate, tvSerial, tvComments;
    private Button del_button;
    private Button edit_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item);

        assert getSupportActionBar() != null;   // null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   // show back button

        db = FirebaseFirestore.getInstance();
        itemsRef = db.collection("items");

        itemId = getIntent().getSerializableExtra(Constants.INTENT_ITEM_ID_KEY, UUID.class);

        final Item[] item = new Item[1];
        itemsRef.document(itemId.toString()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        item[0] = Item.getItemFromDocument(document);
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

        Log.d("D", "UUID: " + itemId.toString());

        populateFields(item[0]);

        edit_button = findViewById(R.id.edit_button);
        edit_button.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditItemActivity.class);
            intent.putExtra(Constants.INTENT_ITEM_KEY, item[0]);
            startActivityForResult(intent, Constants.EDIT_ITEM_ACTIVITY_CODE);
        });
        del_button = findViewById(R.id.del_button_item_view);
        del_button.setOnClickListener(v -> {
            itemsRef.document(item[0].getIdString()).delete();
            finish();
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.EDIT_ITEM_ACTIVITY_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Item result = data.getSerializableExtra(Constants.INTENT_ITEM_KEY, Item.class);
                // result holds the new item
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // Write your code if there's no result
            }
        }
    } //onActivityResult

    protected void populateFields(Item item) {
        if (item == null) return;

        tvMake = findViewById(R.id.make_textview);
        tvModel = findViewById(R.id.model_textview);
        tvEstimatedValue = findViewById(R.id.ev_textview);
        tvDescription = findViewById(R.id.description_textview);
        tvPurchaseDate = findViewById(R.id.purchase_date_textview);
        tvSerial = findViewById(R.id.serial_textview);
        tvComments = findViewById(R.id.comments_textview);

        tvMake.setText(item.getMake());
        tvModel.setText(item.getModel());
        tvEstimatedValue.setText(item.getEstimatedValueString());
        tvDescription.setText((item.getDescription() != null) ? item.getDescription() : "No description" );
        tvPurchaseDate.setText(item.getPurchaseDateString());
        tvSerial.setText((item.getSerial() != null) ? item.getSerial() : "No serial");
        tvComments.setText((item.getComments() != null) ? item.getComments() : "No comments");
    }
}