package com.team25.neety;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;


import java.io.Serializable;
import java.util.Locale;
import java.util.UUID;
/**
 * This class is the add item fragment for the app handles all logic for adding an item
 * @version 1.0
 *
 */
public class AddItem extends DialogFragment{

    private EditText modelName;
    private EditText makeName;
    private EditText estimatedValue;
    private EditText description;
    private EditText purchaseDate;
    private EditText serialNumber;
    private EditText comments;
    private ImageButton calendar_button;

    private OnFragmentInteractionListener listener;

    /**
     * This method is called when the fragment is first attached to its context.
     * onCreate(Bundle) will be called after this.
     * @param context
     */
    @Override
    //Attach on fragment listener to context(main)
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        }
        else {
            throw new RuntimeException(context.toString() + "OnFragmentInteractionListener is not implemented");
        }


    }
    /**
     * This method is called when the fragment is first attached to its context.
     * @param savedInstanceState
     * @return Dialog
     */
    static AddItem newInstance(Item item){
        Bundle args = new Bundle();
        args.putSerializable("item", item);

        AddItem fragment = new AddItem();
        fragment.setArguments(args);
        return fragment;
    }


    //For interaction such as pressing OK, Edit, and Delete
    public interface OnFragmentInteractionListener {
        void onOKPressed(Item item);
    }

    /**
     *  this function validates the date to make sure it is in the correct format
     * @param string
     * @return boolean
     */
    private boolean validateDate(String string){
        if (string.matches("\\d{4}-\\d{2}-\\d{2}")){
            return true;
        }
        return false;
    }
    /**
     *  this function validates the price to make sure it is in the correct format
     * @param string
     * @return boolean
     */
    private boolean validatePrice(EditText string){
        String input = string.getText().toString();
        try {
            Float num = Float.parseFloat(input);
            if (num > 0){
                return true;
            }
            return false;
        }
        catch (Exception e){
            return false;
        }
    }

    /**
     *  this function creates the dialog for adding an item
     * @param savedInstanceState
     * @return Dialog
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        //Initialize View
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.add_item_layout, null);

        //Four edit texts
        modelName = view.findViewById(R.id.model_edittext);
        makeName = view.findViewById(R.id.make_edittext);
        estimatedValue = view.findViewById(R.id.estimated_value_edittext);
        description = view.findViewById(R.id.description_edittext);
        purchaseDate = view.findViewById(R.id.purchase_date_edittext);
        serialNumber = view.findViewById(R.id.serial_number_edittext);
        comments = view.findViewById(R.id.comments_edittext);
        calendar_button = view.findViewById(R.id.calendar_button);

        Bundle bundle = getArguments();
        if (bundle != null) {
            String title = bundle.getString("title");
            String manufacturer = bundle.getString("manufacturer");
            String description_ = bundle.getString("description");
            String barcode = bundle.getString("barcode");

            modelName.setText(title);
            makeName.setText(manufacturer);
            description.setText(description_);
            serialNumber.setText(barcode);
        }

        purchaseDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // Perform click on calendar button
                    calendar_button.performClick();
                }
            }
        });

        purchaseDate.setFocusable(false);
        purchaseDate.setKeyListener(null);
        purchaseDate.setOnClickListener(v -> {
            calendar_button.performClick();
        });


        //Handle calendar button for getting date
        calendar_button.setOnClickListener(view1 -> {
            final Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            try {
                Date date = sdf.parse(purchaseDate.getText().toString());
                if (date != null) {
                    calendar.setTime(date);
                }
            } catch (ParseException e) {
                // Invalid date format, use current date
            }

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getActivity(), // or getActivity() if you're in a Fragment
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
                        purchaseDate.setText(date_inp);
                    },
                    year, month, day);
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
        });

        //Init Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustom);

        //Load arguments, if its empty, then its add, if it passes in expense it wants to edit
        Bundle args = getArguments();
        if (args != null) {
            return null;
        } else{
            return builder
                    .setView(view)
                    .setTitle("Add Item")
                    .setNegativeButton("Cancel", null)
                    // If add pressed then return all variables newly typed in only if it passes validation
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        /**
                         * this function handles the ok button
                         * @param dialog
                         * @param which
                         * 
                         * 
                         */
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String datestring = purchaseDate.getText().toString();
                            boolean checkDate = false;
                            boolean checkPrice = validatePrice(estimatedValue);

                            String make = makeName.getText().toString();
                            String model = modelName.getText().toString();
                            String desc = description.getText().toString();
                            String price = estimatedValue.getText().toString();
                            String serial = serialNumber.getText().toString();
                            String comment = comments.getText().toString();
                            if (datestring.equals("") && desc.equals("") && serial.equals("") && comment.equals("")){
                                listener.onOKPressed(new Item(make, model, Float.parseFloat(price)));
                            }
                            if (datestring != "") {
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
                                dateFormat.setLenient(false);
                                Date date = null;
                                if (validateDate(datestring)) {
                                    try {
                                        date = dateFormat.parse(datestring);
                                        checkDate = true;
                                    } catch (ParseException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                                if (checkPrice && checkDate) {
                                    listener.onOKPressed(new Item(UUID.randomUUID(), date, make, model, desc, serial, Float.parseFloat(price), comment));
                                }
                            }


                        }
                    }).create();
        }
    }


}
