package com.team25.neety;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.team25.neety.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ListView lv;

    private Button filterButton;
    private ArrayList<Item> itemsList = new ArrayList<Item>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        itemsList.add(new Item("Apple", "iPhone 13 Pro Max", (float) 255.32));
        itemsList.add(new Item("Google", "Pixel 8 Pro", (float) 343.32));
        itemsList.add(new Item(
                new Date(),
                "Samsung",
                "Galaxy S23 5G Ultra Pro",
                "This is a description for the Samsung S23 Ultra smartphone.",
                "A233F1827G",
                (float) 1312.45,
                "This is a long winded comment for the Samsung Galaxy " +
                "S23 Ultra item stored in the Neety app. Here is some more text."));

        ItemsLvAdapter adapter = new ItemsLvAdapter(this, itemsList);

        //      For sorting item by specification and updating the screen according to it
        filterButton=findViewById(R.id.filter_button);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Filter_Button_click", "clicked ");
            }
        });

        lv = findViewById(R.id.items_list_view);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, ViewItemActivity.class);
            intent.putExtra(Constants.INTENT_ITEM_KEY, itemsList.get(position));
            startActivity(intent);
        });



    }

    public void sort_item_date(View view){
        ChipGroup chip_sort_date = findViewById(R.id.cg_sort_date);
        Chip sort_date_new = findViewById(R.id.date_new);
        Chip sort_date_old = findViewById(R.id.date_old);
        ChipGroup chip_sort_price = findViewById(R.id.cg_sort_price);
        Chip sort_price_highlow = findViewById(R.id.price_high_low);
        Chip sort_price_lowhigh = findViewById(R.id.price_low_high);


        sort_date_old.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    System.out.println("");
                }
            }
        });

        sort_date_new.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Chip 2 is selected
                    // Perform actions when Chip 2 is selected
                } else {
                    // Chip 2 is deselected
                    // Perform actions when Chip 2 is deselected
                }
            }
        });
        sort_price_highlow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Chip 1 is selected
                    // Perform actions when Chip 1 is selected
                } else {
                    // Chip 1 is deselected
                    // Perform actions when Chip 1 is deselected
                }
            }
        });

        sort_price_lowhigh.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Chip 2 is selected
                    // Perform actions when Chip 2 is selected
                } else {
                    // Chip 2 is deselected
                    // Perform actions when Chip 2 is deselected
                }
            }
        });

    }





}