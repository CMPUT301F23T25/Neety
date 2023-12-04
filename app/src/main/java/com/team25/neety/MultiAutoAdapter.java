package com.team25.neety;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MultiAutoAdapter extends ArrayAdapter<Tag> {

    private ArrayList<Tag> originalTags;
    private ArrayList<Tag> filteredTags;

    public MultiAutoAdapter(Context context, int resource, ArrayList<Tag> objects) {
        super(context, resource, objects);
        originalTags = new ArrayList<>(objects);
        filteredTags = new ArrayList<>(objects);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                ArrayList<Tag> tempList = new ArrayList<>();

                if (constraint != null) {
                    String lowerCaseConstraint = constraint.toString().toLowerCase().trim();
                    // Perform filtering based on the tag name
                    for (Tag tag : originalTags) {
                        if (tag.getName().toLowerCase().contains(lowerCaseConstraint) && !tempList.contains(tag)) {
                            tempList.add(tag);
                        }
                    }

                    filterResults.values = tempList;
                    filterResults.count = tempList.size();
                }

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredTags.clear();
                if (results != null && results.count > 0) {
                    filteredTags.addAll((ArrayList<Tag>) results.values);
                }

                notifyDataSetChanged();
            }
        };
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
        }

        if (position < filteredTags.size()) {
            // Check if position is within the bounds of the filteredTags list
            // Display the tag name in the MultiAutoCompleteTextView
            TextView textView = view.findViewById(android.R.id.text1);
            textView.setText(filteredTags.get(position).getName());
        }

        return view;
    }
}
