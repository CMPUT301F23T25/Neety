package com.team25.neety.ui.home;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.team25.neety.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ViewItemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewItemFragment extends Fragment {

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflatedLayout = inflater.inflate(R.layout.fragment_view_item, container, false);



        return inflatedLayout;
    }
}