package com.team25.neety;

import static com.google.common.base.Throwables.getRootCause;
import static com.team25.neety.Constants.REQUEST_CAMERA_PERMISSION_CODE;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;
/**
 * This class is the main activity for the app handles all logic for the main activity
 * @version 1.0
 */
public class MainActivity extends AppCompatActivity implements AddItem.OnFragmentInteractionListener{

    private ActivityMainBinding binding;
    private FirebaseFirestore db;
    private CollectionReference usersRef, itemsRef;

    private ListView lv;
    private ArrayList<Item> itemsList;

    private ArrayList<Item> originalItemsList;
    private ItemsLvAdapter adapter;



    private ImageButton sortButton, addButton, del_button, filterButton, barcodeButton;
    private TextView totalValueTv;
    private Boolean is_deleting = Boolean.FALSE;
    private String username;

    private ActivityResultLauncher<Intent> cameraResultLauncher;
    private Bundle bundle;


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
                ActionBar.LayoutParams.WRAP_CONTENT, // Width of TextView
                ActionBar.LayoutParams.WRAP_CONTENT); // Height of TextView
        tv.setLayoutParams(lp);
        tv.setText("  Neety.");
        tv.setTextColor(ResourcesCompat.getColor(getResources(), R.color.pale_dogwood, null));
        tv.setTextSize(26);

        // Set the Typeface
        Typeface tf = ResourcesCompat.getFont(this, R.font.pacifico);
        tv.setTypeface(tf);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(tv);

        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("users");
        itemsRef = usersRef.document(username).collection("items");
        itemsList = new ArrayList<>();
        originalItemsList = new ArrayList<>();
        adapter = new ItemsLvAdapter(this, itemsList);

        totalValueTv = findViewById(R.id.total_value_textview);

        //      For sorting item by specification and updating the screen according to it
        // This filter is actually sort button
        sortButton = findViewById(R.id.filter_button);
        System.out.println(itemsList);
        sortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View mView = LayoutInflater.from(MainActivity.this).inflate(R.layout.sorting_layout, null, false);
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
            intent.putExtra(Constants.INTENT_ITEM_ID_KEY, itemsList.get(position).getId());
            startActivity(intent);
        });

        // Handle Add Button
        addButton = findViewById(R.id.button_additem);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AddItem().show(getSupportFragmentManager(), "add item");
            }
        });

        // Handle Filter Button aka Real Filter Button
        filterButton = findViewById(R.id.real_filter_button);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View mView = LayoutInflater.from(MainActivity.this).inflate(R.layout.filtering_layout, null, false);
                final PopupWindow popUp = new PopupWindow(mView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, false);
                popUp.setTouchable(true);
                popUp.setFocusable(true);
                popUp.setOutsideTouchable(true);
                popUp.showAtLocation(v, Gravity.BOTTOM,0,500);// location of pop ip
//                popUp.showAsDropDown(findViewById(R.id.filter_button)
                // This code is for clicking apply button
                Button applyButton=mView.findViewById(R.id.filter_confirm_button);
                applyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText selectMake = mView.findViewById(R.id.edit_make2);
                        EditText selectDesc = mView.findViewById(R.id.edit_description);
                        resetAdapter(adapter);
                        EditText start = mView.findViewById(R.id.edit_date);
                        EditText end = mView.findViewById(R.id.edit_date2);
                        String startDate = start.getText().toString();
                        String endDate = end.getText().toString();
                        filter_by_date_range(adapter, startDate, endDate);
                        filter_by_description(adapter, selectDesc.getText().toString());
                        filter_by_make(adapter, selectMake.getText().toString());
                        popUp.dismiss(); // Close the popup when the close button is clicked
                    }
                });

                Button resetButton = mView.findViewById(R.id.filter_reset_button);
                resetButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        resetAdapter(adapter);
                        popUp.dismiss();
                    }
                });
                popUp.showAsDropDown(findViewById(R.id.real_filter_button));

                ImageButton calendar_button1 = mView.findViewById(R.id.calendar_button);
                calendar_button1.setOnClickListener(v1 -> {
                    final Calendar calendar = Calendar.getInstance();
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH);
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    DatePickerDialog datePickerDialog = new DatePickerDialog(
                            v1.getContext(),
                            (datePicker, i, i1, i2) -> {
                                String month_of_year;
                                String day_of_month;

                                if (i1 + 1 < 10) {
                                    month_of_year = "0" + (i1 + 1);
                                } else month_of_year = String.valueOf(i1 + 1);

                                if (i2 < 10) {
                                    day_of_month = "0" + i2;
                                } else day_of_month = String.valueOf(i2);

                                String date_inp = i + "-" + month_of_year + "-" + day_of_month;
                                EditText start = mView.findViewById(R.id.edit_date);
                                start.setText(date_inp);
                            },
                            year, month, day);
                    datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                    datePickerDialog.show();
                });

                ImageButton calendar_button2 = mView.findViewById(R.id.calendar_button2);
                calendar_button2.setOnClickListener(v2 -> {
                    final Calendar calendar = Calendar.getInstance();
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH);
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    DatePickerDialog datePickerDialog = new DatePickerDialog(
                            v2.getContext(),
                            (datePicker, i, i1, i2) -> {
                                String month_of_year;
                                String day_of_month;

                                if (i1 + 1 < 10) {
                                    month_of_year = "0" + (i1 + 1);
                                } else month_of_year = String.valueOf(i1 + 1);

                                if (i2 < 10) {
                                    day_of_month = "0" + i2;
                                } else day_of_month = String.valueOf(i2);

                                String date_inp = i + "-" + month_of_year + "-" + day_of_month;
                                EditText end = mView.findViewById(R.id.edit_date2);
                                end.setText(date_inp);
                            },
                            year, month, day);
                    datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                    datePickerDialog.show();
                });
            }
        });
        // Set up the result launcher for the camera activity
        cameraResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
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
                                getProductDetailsApiCall(scannedValue);
                            }
                        });
            }
        });

        // Handle Barcode button
        barcodeButton = findViewById(R.id.button_barcode);
        barcodeButton.setOnClickListener(v -> {
            // TODO: Implement item lookup by barcode here
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION_CODE);
            } else {
                startCamera();
            }
        });


        // Handle Delete Button
        del_button = findViewById(R.id.button_deleteitem);

        del_button.setOnClickListener(v -> {
            if (!is_deleting) {
                del_button.setImageDrawable(getDrawable(R.drawable.check_icon));
                addButton.setVisibility(View.INVISIBLE);
                filterButton.setVisibility(View.INVISIBLE);
                sortButton.setVisibility(View.INVISIBLE);
                barcodeButton.setVisibility(View.INVISIBLE);
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
                                        item.deleteImagesFromStorage(username);
                                        itemsRef.document(item.getIdString()).delete();
                                    }

                                }
                                // Notify the adapter that the data has changed
                                adapter.notifyDataSetChanged();
                            }));
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
                del_button.setImageDrawable(getDrawable(R.drawable.trash));
                addButton.setVisibility(View.VISIBLE);
                filterButton.setVisibility(View.VISIBLE);
                sortButton.setVisibility(View.VISIBLE);
                barcodeButton.setVisibility(View.VISIBLE);
                is_deleting = Boolean.FALSE;
            }

            // Update the flag in the adapter and notify it that the data has changed
            adapter.setDeleting(is_deleting);
            adapter.notifyDataSetChanged();
        });

        itemsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            /**
             * this function handles when the event is triggered
             * @param querySnapshots
             * @param error
             * 
             */
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null){
                    Log.e("Firestore", error.toString());
                    return;
                }
                if (querySnapshots != null){
                    itemsList.clear();
                    originalItemsList.clear();
                    float total = 0;
                    for (QueryDocumentSnapshot doc: querySnapshots){
                        Log.d("D", doc.toString());
                        try {
                            Item i = Item.getItemFromDocument(doc);
                            itemsList.add(i);
                            originalItemsList.add(i);
                            total += i.getEstimatedValue();
                        } catch (Exception e) {
                            Drawable dr = getResources().getDrawable(android.R.drawable.ic_dialog_info);
                            dr.setTint(getResources().getColor(R.color.black));
                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("Oops...")
                                    .setMessage("There was an error parsing the database data. Please try again later.\n\nError:\n" +
                                            getRootCause(e).getClass().getCanonicalName() + "\n\nItem Id: " +
                                            doc.getId())
                                    // Specifying a listener allows you to take an action before dismissing the dialog.
                                    // The dialog is automatically dismissed when a dialog button is clicked.
                                    .setPositiveButton("CLOSE", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            finishAndRemoveTask();
                                        }
                                    })
                                    .setCancelable(false)
                                    // A null listener allows the button to dismiss the dialog and take no further action.
                                    .setIcon(android.R.drawable.ic_dialog_info)
                                    .show();
                        }
                    }

                    totalValueTv.setText(Helpers.floatToPriceString(total));
                    adapter.notifyDataSetChanged();
                }
            }
        });


    }

    private void startCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraResultLauncher.launch(cameraIntent);
    }

    private void getProductDetailsApiCall(String barcode) {
        String apiKey = "sdjfmpd944wq740j3vgzhpgdegkzy8";
        String apiUrl = "https://api.barcodelookup.com/v3/products?barcode=" + barcode + "&formatted=y&key=" + apiKey;

        new Thread(() -> {
            try {
                String response = makeApiCall(apiUrl);
                Map<String, String> productDetails = parseApiResponse(response);
                updateProductDetails(productDetails, barcode);
            } catch (Exception e) {
                e.printStackTrace();
                handleError("Error retrieving product information.");
            }
        }).start();
    }

    private String makeApiCall(String apiUrl) throws IOException {
        URL url = new URL(apiUrl);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            return readStream(in);
        } finally {
            urlConnection.disconnect();
        }
    }

    private String readStream(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line).append('\n');
        }
        return stringBuilder.toString();
    }

    private Map<String, String> parseApiResponse(String response) {
        Map<String, String> productDetails = new HashMap<>();
        try {
            JSONObject jsonResponse = new JSONObject(response);
            JSONArray productsArray = jsonResponse.getJSONArray("products");

            if (productsArray.length() > 0) {
                JSONObject productObject = productsArray.getJSONObject(0);
                productDetails.put("title", !productObject.isNull("title") ? productObject.getString("title") : "N/A");
                productDetails.put("manufacturer", !productObject.isNull("manufacturer") ? productObject.getString("manufacturer") : "N/A");
                productDetails.put("description", !productObject.isNull("description") ? productObject.getString("description") : "N/A");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return productDetails;
    }

    private void updateProductDetails(Map<String, String> productDetails, String barcode) {
        new Handler(Looper.getMainLooper()).post(() -> {
            Date date = new Date();
            String make = productDetails.get("manufacturer");
            String model = productDetails.get("title");
            String desc = productDetails.get("description");
            String serial = barcode;
            String price = "0.00";
            Item item = new Item(UUID.randomUUID(), date, make, model, desc, serial, Float.parseFloat(price), null);

            Intent intent = new Intent(this, EditItemActivity.class);
            intent.putExtra(Constants.ITEM_MAIN_TO_EDIT, item);
            Log.d("Item Model", "updateProductDetails: " + item.getModel());
            startActivity(intent);
        });
    }

    private void handleError(String errorMessage) {
        new Handler(Looper.getMainLooper()).post(() -> {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Error")
                    .setMessage(errorMessage)
                    .setPositiveButton("OK", null)
                    .show();
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, "Camera Permission is Required to Use Camera.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * This sorts items by make
     * @param view
     * @param lv
     */
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

    /**
     * this sorts by date
     * @param view
     * @param lv
     */
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

    /**
     * this sorts by estimated value
     * @param view
     * @param lv
     */
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

    public void filter_by_make(ItemsLvAdapter lv, String selectedMake) {

        if (!selectedMake.matches("")){
            // Create a new list to store the filtered items
            ArrayList<Item> filteredList = new ArrayList<>();

            // Iterate through the original list of items
            for (Item item : itemsList) {
                // Check if the make of the item matches the selected make
                if (item.getMake().equalsIgnoreCase(selectedMake)) {
                    // Add the item to the filtered list
                    filteredList.add(item);
                }
            }

            // Clear the existing items in the adapter
            lv.clear();

            // Add the filtered items to the adapter
            lv.addAll(filteredList);

            // Update the total value based on the filtered list
            float total = calculateTotalValue(filteredList);
            totalValueTv.setText(Helpers.floatToPriceString(total));

            // Notify the adapter that the data has changed
            lv.notifyDataSetChanged();
        }
    }

    private float calculateTotalValue(ArrayList<Item> itemList) {
        float total = 0;
        for (Item item : itemList) {
            total += item.getEstimatedValue();
        }
        return total;
    }
    public void resetAdapter(ItemsLvAdapter lv) {
        // Clear the existing items in the adapter
        lv.clear();


        System.out.println(originalItemsList);
        // Add the original items to the adapter
        lv.addAll(originalItemsList);

        // Update the total value based on the original items list
        float total = calculateTotalValue(originalItemsList);
        totalValueTv.setText(Helpers.floatToPriceString(total));

        // Notify the adapter that the data has changed
        lv.notifyDataSetChanged();
    }

    public void filter_by_description(ItemsLvAdapter lv, String keywords) {
        // Create a new list to store the filtered items

        if (!keywords.matches("")){
            ArrayList<Item> filteredList = new ArrayList<>();

            // Iterate through the original list of items
            for (Item item : itemsList) {
                // Check if the description of the item contains the specified keywords (case-insensitive)
                if (item.getDescription()!= null && item.getDescription().toLowerCase().contains(keywords.toLowerCase()) && !keywords.matches("")) {
                    filteredList.add(item);
                }
            }

            // Clear the existing items in the adapter
            lv.clear();

            // Add the filtered items to the adapter
            lv.addAll(filteredList);

            // Update the total value based on the filtered list
            float total = calculateTotalValue(filteredList);
            totalValueTv.setText(Helpers.floatToPriceString(total));

            // Notify the adapter that the data has changed
            lv.notifyDataSetChanged();
        }

    }

    public void filter_by_date_range(ItemsLvAdapter lv, String start, String end) {

        if (!(start.matches("") || end.matches(""))){
            Date startDate = Helpers.getDateFromString(start);
            Date endDate = Helpers.getDateFromString(end);


            // Create a new list to store the filtered items
            ArrayList<Item> filteredList = new ArrayList<>();

            // Iterate through the original list of items
            for (Item item : itemsList) {
                Date purchaseDate = item.getPurchaseDate();

                // Check if the purchase date of the item is within the specified range
                if (purchaseDate != null && (purchaseDate.after(startDate) || purchaseDate.equals(startDate))
                        && (purchaseDate.before(endDate) || purchaseDate.equals(endDate))) {
                    // Add the item to the filtered list
                    filteredList.add(item);
                }
            }

            // Clear the existing items in the adapter
            lv.clear();

            // Add the filtered items to the adapter
            lv.addAll(filteredList);

            // Update the total value based on the filtered list
            float total = calculateTotalValue(filteredList);
            totalValueTv.setText(Helpers.floatToPriceString(total));

            // Notify the adapter that the data has changed
            lv.notifyDataSetChanged();
        }

    }



    public void filterByDate(){

    }
    public void filterByDescription(){

    }

    /**
     * this handles the on ok pressed
     * @param item
     */
    public void onOKPressed(Item item) {
        //Add to datalist
        HashMap<String, String> data = Item.getFirestoreDataFromItem(item);

        itemsRef
                .document(item.getIdString())
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firestore", "DocumentSnapshot successfully written!");
                    }
                });
    }
    /**
     * this creates the options menu
     * @param menu
     * @return boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * this handles the action bar
     */
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
}

