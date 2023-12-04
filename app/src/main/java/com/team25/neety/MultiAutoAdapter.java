package com.team25.neety;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class MultiAutoAdapter extends ArrayAdapter<String> {
    public MultiAutoAdapter(Context context, int resource, ArrayList<String> objects) {
        super(context, resource, objects);
    }
}
