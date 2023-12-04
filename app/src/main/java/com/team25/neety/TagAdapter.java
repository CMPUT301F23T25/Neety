package com.team25.neety;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TagAdapter extends ArrayAdapter<String> {

    private List<Integer> selectedPositions = new ArrayList<>();

    public TagAdapter(Context context, List<String> tags) {
        super(context, 0, tags);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        String tag = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        // Lookup view for data population
        TextView tvName = convertView.findViewById(android.R.id.text1);

        // Populate the data into the template view using the data object
        tvName.setText(tag);


//         Set the background color based on the isSelected attribute
        if (selectedPositions.contains(position)) {
            convertView.setBackgroundColor(getContext().getResources().getColor(R.color.white)); // Replace with your color resource
        } else {
            convertView.setBackgroundColor(Color.TRANSPARENT);
        }

        // Return the completed view to render on screen
        return convertView;
    }



    // Add a method to toggle the selection state of an item
    public void toggleSelection(int position) {
        if (selectedPositions.contains(position)) {
            selectedPositions.remove(Integer.valueOf(position));
        } else {
            selectedPositions.add(position);
        }

        // Notify the adapter that the dataset has changed
        notifyDataSetChanged();
    }

    public void clearSelection(){
        selectedPositions.clear();
    }
}