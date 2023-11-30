package com.team25.neety;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

public class ItemsLvAdapter extends ArrayAdapter<Item> {

    private final Activity context;
    private final ArrayList<Item> itemList;
    private boolean isDeleting;
    /*
     * this is the constructor for the list view adapter
     * @param context
     * @param list
     */
    public ItemsLvAdapter(Activity context, ArrayList<Item> list) {
        super(context, R.layout.listitem, list);
        // TODO Auto-generated constructor stub
        this.context=context;
        this.itemList=list;

    }
    /*
     * this gets the view for the list view
     * @param position
     * @param view
     * @param parent
     * @return view
     */
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.listitem, null,true);

        TextView titleText = rowView.findViewById(R.id.list_item_model);
        TextView subtitleText = rowView.findViewById(R.id.list_item_make);
        TextView estimatedText = rowView.findViewById(R.id.list_item_estimatedval);

        titleText.setText(itemList.get(position).getModel());
        subtitleText.setText(itemList.get(position).getMake());

        estimatedText.setText(itemList.get(position).getEstimatedValueString());

        CheckBox checkBox = rowView.findViewById(R.id.item_checkbox);
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Item item = itemList.get(position);
            item.setSelected(isChecked);
        });

        if (isDeleting) {
            checkBox.setVisibility(View.VISIBLE);
            estimatedText.setVisibility(View.INVISIBLE);
            checkBox.setChecked(itemList.get(position).isSelected());
        } else {
            checkBox.setVisibility(View.GONE);
            estimatedText.setVisibility(View.VISIBLE);
        }
        return rowView;

    };
    /*
     * this gets the item list
     * @return itemList
     */
    public void setDeleting(boolean isDeleting) {
        this.isDeleting = isDeleting;
    }

}
