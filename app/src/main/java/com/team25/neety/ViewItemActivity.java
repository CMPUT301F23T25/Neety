package com.team25.neety;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class ViewItemActivity extends AppCompatActivity {

    private Item item;
    private TextView tvMake, tvModel, tvEstimatedValue, tvDescription, tvPurchaseDate, tvSerial, tvComments;
    private Button del_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item);

        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button


        item = getIntent().getSerializableExtra(Constants.INTENT_ITEM_KEY, Item.class);

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

        del_button = findViewById(R.id.del_button_item_view);
        del_button.setOnClickListener(v -> {
            DataHolder.getInstance().setData(item);
            finish();
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}