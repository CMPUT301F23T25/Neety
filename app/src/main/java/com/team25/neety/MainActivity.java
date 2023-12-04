package com.team25.neety;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;
import com.team25.neety.databinding.ActivityMainBinding;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import com.google.android.material.chip.Chip;
import android.view.MenuItem;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity implements AddItem.OnFragmentInteractionListener {

    private ActivityMainBinding binding;
    private FirebaseFirestore db;
    private CollectionReference usersRef, itemsRef;

    private ListView lv;
    private ArrayList<Item> itemsList;
    private ItemsLvAdapter adapter;

    private ImageButton filterButton, addButton, del_button, real_filterButton, barcodeButton;
    private TextView totalValueTv;
    private Boolean is_deleting = Boolean.FALSE;
    private String username;

    // Barcode scanning components
    private ActivityResultLauncher<Intent> cameraResultLauncher;
    private EditText editSerial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        username = sharedPreferences.getString("username", "");

        getSupportActionBar().setBackgroundDrawable(getDrawable(R.color.space_cadet));
        TextView tv = new TextView(getApplicationContext());
        ActionBar.LayoutParams lp = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(lp);
        tv.setText("  Neety.");
        tv.setTextColor(ResourcesCompat.getColor(getResources(), R.color.pale_dogwood, null));
        tv.setTextSize(26);

        Typeface tf = ResourcesCompat.getFont(this, R.font.pacifico);
        tv.setTypeface(tf);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(tv);

        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("users");
        itemsRef = usersRef.document(username).collection("items");
        itemsList = new ArrayList<>();

        adapter = new ItemsLvAdapter(this, itemsList);

        totalValueTv = findViewById(R.id.total_value_textview);

        filterButton = findViewById(R.id.filter_button);
        barcodeButton = findViewById(R.id.button_barcode);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View mView = LayoutInflater.from(MainActivity.this).inflate(R.layout.filter_layout, null, false);
                final PopupWindow popUp = new PopupWindow(mView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, false);
                popUp.setTouchable(true);
                popUp.setFocusable(true);
                popUp.setOutsideTouchable(true);
                popUp.showAtLocation(v, Gravity.BOTTOM,0,500);

                Button applyButton=mView.findViewById(R.id.btnApply);
                applyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sort_by_make(mView,adapter);
                        sort_by_date(mView, adapter);
                        sort_by_estimated_value(mView, adapter);
                        popUp.dismiss();
                    }
                });
                popUp.showAsDropDown(findViewById(R.id.filter_button));
            }
        });

        lv = findViewById(R.id.items_list_view);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, ViewItemActivity.class);
            intent.putExtra(Constants.INTENT_ITEM_ID_KEY, itemsList.get(position).getId());
            startActivity(intent);
        });

        addButton = findViewById(R.id.button_additem);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AddItem().show(getSupportFragmentManager(), "add item");
            }
        });

        real_filterButton = findViewById(R.id.real_filter_button);
        real_filterButton.setOnClickListener(v -> {
            // TODO: Implement real filter button here
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Real Filter Button Clicked")
                    .setMessage("Implement your real filter logic here.")
                    .setPositiveButton("OK", null)
                    .show();
        });

        barcodeButton = findViewById(R.id.button_barcode);
        barcodeButton.setOnClickListener(v -> {
            openCamera();
        });

        del_button = findViewById(R.id.button_deleteitem);

        del_button.setOnClickListener(v -> {
            if (!is_deleting) {
                del_button.setImageDrawable(getDrawable(R.drawable.check_icon));
                addButton.setVisibility(View.INVISIBLE);
                real_filterButton.setVisibility(View.INVISIBLE);
                filterButton.setVisibility(View.INVISIBLE);
                barcodeButton.setVisibility(View.INVISIBLE);
                is_deleting = Boolean.TRUE;
            } else {
                int selectedCount = 0;
                for (Item item : itemsList) {
                    if (item.isSelected()) {
                        selectedCount++;
                    }
                }

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
                                Iterator<Item> iterator = itemsList.iterator();
                                while (iterator.hasNext()) {
                                    Item item = iterator.next();
                                    if (item.isSelected()) {
                                        iterator.remove();
                                        item.deleteImagesFromStorage(username);
                                        itemsRef.document(item.getIdString()).delete();
                                    }
                                }
                                adapter.notifyDataSetChanged();
                            }));
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
                del_button.setImageDrawable(getDrawable(R.drawable.trash));
                addButton.setVisibility(View.VISIBLE);
                real_filterButton.setVisibility(View.VISIBLE);
                filterButton.setVisibility(View.VISIBLE);
                barcodeButton.setVisibility(View.VISIBLE);
                is_deleting = Boolean.FALSE;
            }

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
                    float total = 0;
                    for (QueryDocumentSnapshot doc: querySnapshots){
                        Log.d("D", doc.toString());
                        try {
                            Item i = Item.getItemFromDocument(doc);
                            itemsList.add(i);
                            total += i.getEstimatedValue();
                        } catch (Exception e) {
                            Drawable dr = getResources().getDrawable(android.R.drawable.ic_dialog_info);
                            dr.setTint(getResources().getColor(R.color.black));
                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("Oops...")
                                    .setMessage("There was an error parsing the database data. Please try again later.\n\nError:\n" +
                                            Log.getStackTraceString(e) + "\n\nItem Id: " +
                                            doc.getId())
                                    .setPositiveButton("CLOSE", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            finishAndRemoveTask();
                                        }
                                    })
                                    .setCancelable(false)
                                    .setIcon(android.R.drawable.ic_dialog_info)
                                    .show();
                        }
                    }

                    totalValueTv.setText(Helpers.floatToPriceString(total));
                    adapter.notifyDataSetChanged();
                }
            }
        });

        // Initialize barcode scanning components
        editSerial = findViewById(R.id.edit_serial);

        // Set up the result launcher for the camera activity
        cameraResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
                    Bitmap b = result.getData().getExtras().getParcelable("data", Bitmap.class);

                    if (b == null) return;

                    InputImage image = InputImage.fromBitmap(b, 0);
                    BarcodeScanner scanner = BarcodeScanning.getClient();
                    scanner.process(image)
                            .addOnSuccessListener(barcodes -> {
                                Log.i("Scanned Barcodes", barcodes.toString());
                                if (barcodes.size() > 0) {
                                    String scannedValue = barcodes.get(0).getRawValue();
                                    editSerial.setText(scannedValue);
                                    retrieveProductDescription(scannedValue);
                                }
                            });
                }
            }
        });
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraResultLauncher.launch(cameraIntent);
    }

    private void sort_by_make(View view, ItemsLvAdapter lv) {
        Chip sort_make_A_Z = view.findViewById(R.id.cg_make_ascending);
        Chip sort_make_Z_A = view.findViewById(R.id.cg_make_descending);
        if (sort_make_A_Z.isChecked()) {
            Collections.sort(itemsList, new Comparator<Item>() {
                @Override
                public int compare(Item item1, Item item2) {
                    return item1.getMake().compareTo(item2.getMake());
                }
            });
            lv.notifyDataSetChanged();
        }
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


    private void sort_by_date(View view, ItemsLvAdapter lv) {
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


    private void sort_by_estimated_value(View view, ItemsLvAdapter lv) {
        Chip sort_by_high_low = view.findViewById(R.id.price_high_low);
        Chip sort_by_low_high = view.findViewById(R.id.price_low_high);
        if (sort_by_high_low.isChecked() || sort_by_low_high.isChecked()) {
            Collections.sort(itemsList, new Comparator<Item>() {
                @Override
                public int compare(Item item1, Item item2) {
                    float difference = item1.getEstimatedValue() - item2.getEstimatedValue();

                    if (sort_by_high_low.isChecked()) {
                        difference = -difference;
                    }

                    return (int) Math.round(difference);
                }
            });

            lv.notifyDataSetChanged();
        }
    }

    private void retrieveProductDescription(String barcode) {
        // TODO: Implement logic to retrieve product description based on the scanned barcode
        Item foundItem = findItemByBarcode(barcode);

        // Display item information in a dialog
        if (foundItem != null) {
            String itemDetails = "Make: " + foundItem.getMake() + "\n" +
                    "Purchase Date: " + foundItem.getPurchaseDate() + "\n" +
                    "Estimated Value: $" + foundItem.getEstimatedValue();

            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Product Description")
                    .setMessage("Scanned Barcode: " + barcode + "\n" + itemDetails)
                    .setPositiveButton("OK", null)
                    .show();
        } else {
            // Handle the case where the item with the scanned barcode is not found
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Product Not Found")
                    .setMessage("No information found for scanned barcode: " + barcode)
                    .setPositiveButton("OK", null)
                    .show();
        }
    }

    private Item findItemByBarcode(String barcode) {
        // Iterate through the list of items to find the one with the scanned barcode
        for (Item item : itemsList) {
            if (barcode.equals(item.getSerial())) {
                return item;
            }
        }
        return null; // Return null if the item is not found
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_button) {
            itemsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        int itemCount = task.getResult().size();
                        new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogCustom)
                                .setTitle("User Profile")
                                .setIcon(R.drawable.profile_ic)
                                .setMessage(Html.fromHtml("You are logging in as " + "<b>" + username + "</b>" + ".<br>"
                                        +"Your total number of items is " + itemCount + ".", Html.FROM_HTML_MODE_LEGACY))
                                .setPositiveButton("Back", null)
                                .setNegativeButton("Log Out", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Finish the activity
                                        MainActivity.this.finish();
                                    }
                                })
                                .show();
                    } else {
                        Log.d("Firestore", "Error getting documents: ", task.getException());
                    }
                }
            });
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onOKPressed(Item item) {
        HashMap<String, String> data = Item.getFirestoreDataFromItem(item);

        itemsRef.document(item.getIdString())
                .set(data)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "DocumentSnapshot successfully written!"));
    }
}
