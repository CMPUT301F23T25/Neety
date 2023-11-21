package com.team25.neety;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.chip.Chip;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.team25.neety.databinding.ActivityMainBinding;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements AddItem.OnFragmentInteractionListener {

    private ListView lv;
    private Button del_button;
    private Boolean is_deleting = Boolean.FALSE;
    private ArrayList<Item> itemsList;
    private static final int EDIT_ITEM_REQUEST = 1;
    private ItemsLvAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        itemsList = new ArrayList<>();
        adapter = new ItemsLvAdapter(this, itemsList);

        // For sorting items by specification and updating the screen accordingly
        setupFilterButton();

        lv = findViewById(R.id.items_list_view);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, EditItemActivity.class);
            intent.putExtra(Constants.INTENT_ITEM_KEY, itemsList.get(position));
            startActivityForResult(intent, EDIT_ITEM_REQUEST);
        });

        setupAddButton();

        del_button = findViewById(R.id.button_deleteitem);
        setupDeleteButton();

        // Load items from Firestore
        loadItemsFromFirestore();
    }

    private void setupFilterButton() {
        Button filterButton = findViewById(R.id.filter_button);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View mView = getLayoutInflater().inflate(R.layout.filter_layout, null, false);
                final PopupWindow popUp = new PopupWindow(mView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, false);
                popUp.setTouchable(true);
                popUp.setFocusable(true);
                popUp.setOutsideTouchable(true);
                popUp.showAtLocation(v, Gravity.BOTTOM, 0, 500);

                // This code is for clicking apply button
                Button applyButton = mView.findViewById(R.id.btnApply);
                applyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sort_by_make(mView, adapter);// sorts by make if chosen
                        sort_by_date(mView, adapter);// sorts by date if chosen
                        sort_by_estimated_value(mView, adapter);// sorts by est. value if chosen
                        popUp.dismiss(); // Close the popup when the close button is clicked
                    }
                });
                popUp.showAsDropDown(findViewById(R.id.filter_button));
            }
        });
    }

    private void setupAddButton() {
        Button addButton = findViewById(R.id.button_additem);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AddItem().show(getSupportFragmentManager(), "add item");
            }
        });
    }

    private void setupDeleteButton() {
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
                                        FirebaseFirestore itemsRef = null;
                                        itemsRef.document(item.getModel()).delete();
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

    private void loadItemsFromFirestore() {
        try {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference itemsRef = db.collection("items");

            itemsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Log.e("Firestore", "Error fetching items", error);
                        return;
                    }
                    if (querySnapshots != null) {
                        itemsList.clear();
                        for (QueryDocumentSnapshot doc : querySnapshots) {
                            try {
                                String model = doc.getId();
                                String make = doc.getString("make");
                                String value = doc.getString("value");
                                Log.d("Firestore", String.format("Model(%s, %s) fetched", model, make));
                                itemsList.add(new Item(model, make, Float.parseFloat(value)));
                            } catch (Exception e) {
                                Log.e("Firestore", "Error parsing item", e);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            });
        } catch (Exception e) {
            Log.e("Firestore", "Error loading items from Firestore", e);
        }
    }



    public void sort_by_make(View view, ItemsLvAdapter lv) {
        Chip sort_make_A_Z = view.findViewById(R.id.cg_make_ascending);
        Chip sort_make_Z_A = view.findViewById(R.id.cg_make_descending);
        // sort by ascending alphabet (A-Z)
        if (sort_make_A_Z.isChecked()) {
            Collections.sort(itemsList, new Comparator<Item>() {
                @Override
                public int compare(Item item1, Item item2) {
                    return item1.getMake().compareTo(item2.getMake());
                }
            });
            lv.notifyDataSetChanged();
        }
        // sort by descending alphabet (Z-A)
        if (sort_make_Z_A.isChecked()) {
            Collections.sort(itemsList, new Comparator<Item>() {
                @Override
                public int compare(Item item1, Item item2) {
                    return item2.getMake().compareTo(item1.getMake());
                }
            });
            lv.notifyDataSetChanged();
        }
    }

    public void sort_by_date(View view, ItemsLvAdapter lv) {
        Chip sort_by_date_latest = view.findViewById(R.id.date_new);
        Chip sort_by_date_oldest = view.findViewById(R.id.date_old);

        if (sort_by_date_latest.isChecked()) {
            Collections.sort(itemsList, new Comparator<Item>() {
                @Override
                public int compare(Item item1, Item item2) {
                    return item2.getPurchaseDate().compareTo(item1.getPurchaseDate());
                }
            });
            lv.notifyDataSetChanged();
        }

        if (sort_by_date_oldest.isChecked()) {
            Collections.sort(itemsList, new Comparator<Item>() {
                @Override
                public int compare(Item item1, Item item2) {
                    return item1.getPurchaseDate().compareTo(item2.getPurchaseDate());
                }
            });
            lv.notifyDataSetChanged();
        }
    }

    public void sort_by_estimated_value(View view, ItemsLvAdapter lv) {
        Chip sort_by_high_low = view.findViewById(R.id.price_high_low);
        Chip sort_by_low_high = view.findViewById(R.id.price_low_high);
        if (sort_by_high_low.isChecked() || sort_by_low_high.isChecked()) {
            Collections.sort(itemsList, new Comparator<Item>() {
                @Override
                public int compare(Item item1, Item item2) {
                    float difference = item1.getEstimatedValue() - item2.getEstimatedValue();

                    if (sort_by_high_low.isChecked()) {
                        // For low to high sorting, reverse the order
                        difference = -difference;
                    }

                    // Cast the result to int or use Math.round() for rounded sorting
                    return (int) Math.round(difference);
                }
            });

            // Notify the adapter that the dataset has changed
            lv.notifyDataSetChanged();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDIT_ITEM_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Item updatedItem = (Item) data.getSerializableExtra(Constants.INTENT_ITEM_KEY);
            if (updatedItem != null) {
                updateItemInList(updatedItem);
            }
        }
    }

    private void updateItemInList(Item updatedItem) {
        for (int i = 0; i < itemsList.size(); i++) {
            String model = itemsList.get(i).getModel();
            if (model != null && model.equals(updatedItem.getModel())) {
                itemsList.set(i, updatedItem);
                break;
            }
        }

        adapter.notifyDataSetChanged(); // Notify the adapter of the data change
    }

    public void onOKPressed(Item item) {
        // Add to datalist
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference itemsRef = db.collection("items");

        Map<String, Object> data = new HashMap<>();
        data.put("make", item.getMake());
        data.put("value", item.getEstimatedValue());

        itemsRef.document(item.getModel())
                .set(data)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "DocumentSnapshot successfully written!"))
                .addOnFailureListener(e -> Log.w("Firestore", "Error writing document", e));
    }
}
