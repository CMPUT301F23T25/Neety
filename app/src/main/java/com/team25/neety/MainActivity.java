package com.team25.neety;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.widget.Button;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import android.widget.LinearLayout;

import android.widget.CheckBox;
import android.widget.CompoundButton;

import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.team25.neety.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ListView lv;
    private Button del_button;
    private Boolean is_deleting = Boolean.FALSE;

    private Button filterButton;
    private ArrayList<Item> itemsList = new ArrayList<Item>();
    private Item item_to_delete;
    private ItemsLvAdapter adapter;

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


        adapter = new ItemsLvAdapter(this, itemsList);

        //      For sorting item by specification and updating the screen according to it
        filterButton = findViewById(R.id.filter_button);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View mView = LayoutInflater.from(MainActivity.this).inflate(R.layout.filter_layout, null, false);
                final PopupWindow popUp = new PopupWindow(mView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, false);
                popUp.setTouchable(true);
                popUp.setFocusable(true);
                popUp.setOutsideTouchable(true);
                popUp.showAtLocation(v, Gravity.BOTTOM,0,500);// location of pop ip
//                popUp.showAsDropDown(findViewById(R.id.filter_button)
                // This code is for clicking apply button
                Button applyButton=mView.findViewById(R.id.btnApply);
                applyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sort_by_make(mView,adapter);// sorts by make if chosen
                        popUp.dismiss(); // Close the popup when the close button is clicked
                    }
                });
                popUp.showAsDropDown(findViewById(R.id.filter_button));
            }
        });

        lv = findViewById(R.id.items_list_view);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, ViewItemActivity.class);
            intent.putExtra(Constants.INTENT_ITEM_KEY, itemsList.get(position));
            startActivity(intent);
        });

        del_button = findViewById(R.id.button_deleteitem);

        del_button.setOnClickListener(v -> {
            if (!is_deleting) {
                del_button.setText("Done");
                is_deleting = Boolean.TRUE;
            } else {
                // Count how many items are selected
                int selectedCount = 0;
                for (Item item : itemsList) {
                    if (item.isSelected()) {
                        selectedCount++;
                    }
                }

                // If any items are selected, show the AlertDialog
                if (selectedCount > 0) {
                    String Msg = String.format("Do you want to delete %d selected item(s)?", selectedCount);
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder
                            .setMessage(Msg)
                            .setTitle("Deleting items")
                            .setNegativeButton("No", ((dialog, which) -> {
                                dialog.cancel();
                            }))
                            .setPositiveButton("Yes", ((dialog, which) -> {
                                // Remove all selected items
                                Iterator<Item> iterator = itemsList.iterator();
                                while (iterator.hasNext()) {
                                    Item item = iterator.next();
                                    if (item.isSelected()) {
                                        iterator.remove();
                                    }

                                }
                                // Notify the adapter that the data has changed
                                adapter.notifyDataSetChanged();
                            }));
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
                del_button.setText("Delete");
                is_deleting = Boolean.FALSE;
            }

            // Update the flag in the adapter and notify it that the data has changed
            adapter.setDeleting(is_deleting);
            adapter.notifyDataSetChanged();
        });

    }
    @Override
    protected void onResume() {
        super.onResume();
        item_to_delete = DataHolder.getInstance().getData();

        if (item_to_delete != null) {
            itemsList.remove(item_to_delete);
            adapter.notifyDataSetChanged();

            DataHolder.getInstance().setData(null);

            CharSequence text = String.format("%s is deleted.", item_to_delete.getModel());
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(MainActivity.this, text, duration);
            toast.show();
        }
    }

    public  void sort_by_make(View view,ItemsLvAdapter lv){
        Chip sort_make_A_Z = view.findViewById(R.id.cg_make_ascending);
        Chip sort_make_Z_A = view.findViewById(R.id.cg_make_descending);
        // sort by ascending alphabet (A-Z)
        if(sort_make_A_Z.isChecked()){
            Collections.sort(itemsList, new Comparator<Item>() {
                @Override
                public int compare(Item item1, Item item2) {
                    return item1.getMake().compareTo(item2.getMake());
                }
            });
            lv.notifyDataSetChanged();
        }
        // sort by descending alphabet (Z-A)
        if(sort_make_Z_A.isChecked()){
            Collections.sort(itemsList, new Comparator<Item>() {
                @Override
                public int compare(Item item1, Item item2) {
                        return item2.getMake().compareTo(item1.getMake());
                }
            });
            lv.notifyDataSetChanged();
        }
    }



//    public void sort_item_date(View view){
//        ChipGroup chip_sort_date = findViewById(R.id.cg_sort_date);
//        Chip sort_date_new = findViewById(R.id.date_new);
//        Chip sort_date_old = findViewById(R.id.date_old);
//
//        ChipGroup chip_sort_price = findViewById(R.id.cg_sort_price);
//        Chip sort_price_highlow = findViewById(R.id.price_high_low);
//        Chip sort_price_lowhigh = findViewById(R.id.price_low_high);
//
//        ChipGroup chip_sort_make=findViewById(R.id.cg_sort_make);
//        Chip sort_make_A_Z = findViewById(R.id.cg_make_ascending);
//        Chip sort_make_Z_A = findViewById(R.id.cg_make_descending);
//
//
//
//        sort_date_old.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    System.out.println("");
//                }
//            }
//        });
//
//        sort_date_new.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    // Chip 2 is selected
//                    // Perform actions when Chip 2 is selected
//                } else {
//                    // Chip 2 is deselected
//                    // Perform actions when Chip 2 is deselected
//                }
//            }
//        });
//        sort_price_highlow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    // Chip 1 is selected
//                    // Perform actions when Chip 1 is selected
//                } else {
//                    // Chip 1 is deselected
//                    // Perform actions when Chip 1 is deselected
//                }
//            }
//        });
//
//        sort_price_lowhigh.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    // Chip 2 is selected
//                    // Perform actions when Chip 2 is selected
//                } else {
//                    // Chip 2 is deselected
//                    // Perform actions when Chip 2 is deselected
//                }
//            }
//        });
//
//    }





}

