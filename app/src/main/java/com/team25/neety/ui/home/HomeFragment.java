package com.team25.neety.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.team25.neety.Item;
import com.team25.neety.ItemsLvAdapter;
import com.team25.neety.R;
import com.team25.neety.databinding.FragmentHomeBinding;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ListView lv;
    private ArrayList<Item> itemsList = new ArrayList<Item>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        itemsList.add(new Item("Apple", "iPhone 13 Pro Max", (float) 255.32));
        itemsList.add(new Item("Google", "Pixel 8 Pro", (float) 343.32));

        ItemsLvAdapter adapter = new ItemsLvAdapter(getActivity(), itemsList);

        lv = root.findViewById(R.id.items_list_view);
        lv.setAdapter(adapter);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}