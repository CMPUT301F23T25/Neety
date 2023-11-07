package com.team25.neety;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.team25.neety.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ListView lv;
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

        lv = findViewById(R.id.items_list_view);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, ViewItemActivity.class);
            intent.putExtra(Constants.INTENT_ITEM_KEY, itemsList.get(position));
            startActivity(intent);
        });


    }

}