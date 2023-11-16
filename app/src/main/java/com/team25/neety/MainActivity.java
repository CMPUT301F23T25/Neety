package com.team25.neety;

import android.app.Activity;
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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.team25.neety.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity implements AddItem.OnFragmentInteractionListener{

    private ActivityMainBinding binding;
    private ListView lv;
    private Button del_button;
    private Boolean is_deleting = Boolean.FALSE;

    private FirebaseFirestore db;
    private CollectionReference itemsRef;
    private Button filterButton;
    private Button addButton;
    private ArrayList<Item> itemsList;
    private static final int EDIT_ITEM_REQUEST = 1;
    private Item item_to_delete;
    private ItemsLvAdapter adapter;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        itemsRef = db.collection("items");
        itemsList = new ArrayList<>();

//        itemsList.add(new Item("Apple", "iPhone 13 Pro Max", (float) 255.32));
//        itemsList.add(new Item("Google", "Pixel 8 Pro", (float) 343.32));
//        itemsList.add(new Item(
//                new Date(),
//                "Samsung",
//                "Galaxy S23 5G Ultra Pro",
//                "This is a description for the Samsung S23 Ultra smartphone.",
//                "A233F1827G",
//                (float) 1312.45,
//                "This is a long winded comment for the Samsung Galaxy " +
//                        "S23 Ultra item stored in the Neety app. Here is some more text."));
//        itemsList.add(new Item(new Date(101, 2, 1), "RandomBrand", "RandomModel", "RandomDescription", "RandomSerial", (float) 99.99, "RandomComment"));
//        itemsList.add(new Item(new Date(98, 4, 15), "HardcodedBrand", "HardcodedModel", "HardcodedDescription", "HardcodedSerial", (float) 66.66, "HardcodedComment"));

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
                        sort_by_date(mView, adapter);// sorts by date if chosen
                        sort_by_estimated_value(mView, adapter);// sorts by est. value if chosen
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
//        lv.setOnItemClickListener((parent, view, position, id) -> {
//            Intent intent = new Intent(this, EditItemActivity.class);
//            intent.putExtra(Constants.INTENT_ITEM_KEY, itemsList.get(position));
//            startActivityForResult(intent, EDIT_ITEM_REQUEST);
//        });

        addButton = findViewById(R.id.button_additem);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AddItem().show(getSupportFragmentManager(), "add item");
            }
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

        itemsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null){
                    Log.e("Firestore", error.toString());
                    return;
                }
                if (querySnapshots != null){
                    itemsList.clear();
                    for (QueryDocumentSnapshot doc: querySnapshots){
                        String Model = doc.getId();
                        String Make = doc.getString("Make");
                        String Value = doc.getString("Value");
                        Value = Value.substring(1);
                        String Description = doc.getString("Description");
                        String PurchaseDate = doc.getString("PurchaseDate");
                        String Serial = doc.getString("Serial");
                        String Comments = doc.getString("Comments");
                        Log.d("Firestore", String.format("Model(%s, %s) fetched",
                                Model, Make));

                        dateFormat.setLenient(false);
                        Date date = null;
                        if (PurchaseDate != null){
                            try {
                                date = dateFormat.parse(PurchaseDate);
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        itemsList.add(new Item(date, Make, Model, Description, Serial, Float.parseFloat(Value), Comments));
                    }
                    adapter.notifyDataSetChanged();
                }
            }
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

    public void sort_by_make(View view,ItemsLvAdapter lv){
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


    public void sort_by_date(View view, ItemsLvAdapter lv){
        Chip sort_by_date_latest = view.findViewById(R.id.date_new);
        Chip sort_by_date_oldest = view.findViewById(R.id.date_old);

        if(sort_by_date_latest.isChecked()){
            Collections.sort(itemsList, new Comparator<Item>() {
                @Override
                public int compare(Item item1, Item item2) {
                    return item2.getPurchaseDate().compareTo(item1.getPurchaseDate());
                }
            });
            lv.notifyDataSetChanged();
        }

        if (sort_by_date_oldest.isChecked()){
            Collections.sort(itemsList, new Comparator<Item>() {
                @Override
                public int compare(Item item1, Item item2) {
                    return item1.getPurchaseDate().compareTo(item2.getPurchaseDate());
                }
            });
            lv.notifyDataSetChanged();
        }
    }

    public void sort_by_estimated_value(View view, ItemsLvAdapter lv){

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
            String serial = itemsList.get(i).getSerial();
            if (serial != null && serial.equals(updatedItem.getSerial())) {
                itemsList.set(i, updatedItem);
                break;
            }
        }

        adapter.notifyDataSetChanged(); // Notify the adapter of the data change
    }

    public void onOKPressed(Item item) {
        //Add to datalist
        HashMap<String, String> data = new HashMap<>();
        data.put("Make", item.getMake());
        data.put("Value", item.getEstimatedValueString());
        data.put("Description", item.getDescription());
        data.put("PurchaseDate", item.getPurchaseDateString());
        data.put("Serial", item.getSerial());
        data.put("Comments", item.getComments());

        itemsRef
                .document(item.getModel())
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firestore", "DocumentSnapshot successfully written!");
                    }
                });
    }

}

