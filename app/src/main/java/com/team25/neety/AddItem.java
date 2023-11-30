package com.team25.neety;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
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
import java.util.UUID;

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

    /*
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
    /*
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


    private boolean validateDate(String string){
        if (string.matches("\\d{4}-\\d{2}-\\d{2}")){
            return true;
        }
        return false;
    }

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


    @NonNull
    @Override
    //View
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

        //Handle calendar button for getting date
        calendar_button.setOnClickListener(view1 -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getContext(),
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
