package com.team25.neety;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ItemsLvAdapter extends ArrayAdapter<Item> {

    private final Activity context;
    private final ArrayList<Item> itemList;

    public ItemsLvAdapter(Activity context, ArrayList<Item> list) {
        super(context, R.layout.listitem, list);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.itemList=list;

    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.listitem, null,true);

        TextView titleText = rowView.findViewById(R.id.list_item_model);
        TextView subtitleText = rowView.findViewById(R.id.list_item_make);
        TextView estimatedText = rowView.findViewById(R.id.list_item_estimatedval);

        titleText.setText(itemList.get(position).getModel());
        subtitleText.setText(itemList.get(position).getMake());

        estimatedText.setText(itemList.get(position).getEstimatedValueString());

        return rowView;

    };
}
