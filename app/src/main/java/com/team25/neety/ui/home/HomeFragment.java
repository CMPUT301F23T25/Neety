package com.team25.neety.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.team25.neety.Item;
import com.team25.neety.ItemsLvAdapter;
import com.team25.neety.R;
import com.team25.neety.databinding.FragmentHomeBinding;
import com.team25.neety.ui.dashboard.DashboardFragment;

import java.util.ArrayList;
import java.util.logging.Logger;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ListView lv;
    private ArrayList<Item> itemsList = new ArrayList<Item>();
    private FragmentManager fragmentManager;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        fragmentManager = getActivity().getSupportFragmentManager();




        itemsList.add(new Item("Apple", "iPhone 13 Pro Max", (float) 255.32));
        itemsList.add(new Item("Google", "Pixel 8 Pro", (float) 343.32));

        ItemsLvAdapter adapter = new ItemsLvAdapter(getActivity(), itemsList);

        lv = root.findViewById(R.id.items_list_view);
        lv.setAdapter(adapter);

        NavHostFragment navHostFragment =
                (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);

        NavController n = navHostFragment.getNavController();

        lv.setOnItemClickListener((parent, view, position, id) -> {
            Log.v("HELLO", "triggered");
            DashboardFragment fragment2 = new DashboardFragment();
            n.navigate(R.id.viewItemFragment);
//            DashboardFragment fragment2 = new DashboardFragment();
//
//            fragmentManager.beginTransaction()
//                    .replace(R.id.nav_host_fragment_activity_main, DashboardFragment.class, null)
//                    .setReorderingAllowed(true)
//                    .addToBackStack(null) // Name can be null
//                    .commit();
        });


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}