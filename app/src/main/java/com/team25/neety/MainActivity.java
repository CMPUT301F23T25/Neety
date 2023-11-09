package com.team25.neety;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.team25.neety.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ListView lv;
    private Button del_button;
    private Boolean is_deleting = Boolean.FALSE;
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

            CharSequence text = String.format("%s is deleted.", item_to_delete.getModel()) ;
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(MainActivity.this, text, duration);
            toast.show();
        }
    }

}