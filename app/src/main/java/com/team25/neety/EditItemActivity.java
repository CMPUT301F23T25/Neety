package com.team25.neety;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class EditItemActivity extends AppCompatActivity {

    private Item item;
    private EditText editMake, editModel, editValue, editDescription, editSerial, editComments;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item2);

        item = getIntent().getSerializableExtra(Constants.INTENT_ITEM_KEY,Item.class);

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

        Button saveButton = findViewById(R.id.save_button);
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
        Item updatedItem = new Item(item.getPurchaseDate(), make, model, description, serial, value, comments);

        // Send the updated item back to the calling activity
        Intent resultIntent = new Intent();
        resultIntent.putExtra(Constants.INTENT_ITEM_KEY, updatedItem);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}