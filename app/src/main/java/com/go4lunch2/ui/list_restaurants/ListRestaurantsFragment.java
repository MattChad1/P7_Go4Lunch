package com.go4lunch2.ui.list_restaurants;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.go4lunch2.ViewModelFactory;
import com.go4lunch2.databinding.FragmentListRestaurantsBinding;

import java.util.ArrayList;
import java.util.List;

public class ListRestaurantsFragment extends Fragment {

    String TAG = "MyLog ListRestaurantsFragment";
    FragmentListRestaurantsBinding binding;
    ListRestaurantsViewModel vm;

    List<RestaurantViewState> datas = new ArrayList<>();
    RecyclerView rv;

    public ListRestaurantsFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ListRestaurantsFragment newInstance() {
        ListRestaurantsFragment fragment = new ListRestaurantsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentListRestaurantsBinding.inflate(inflater, container, false);

        vm = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(ListRestaurantsViewModel.class);

        rv = binding.rvListRestaurants;
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        RestaurantsAdapter adapter = new RestaurantsAdapter(getActivity(), datas);
        rv.setAdapter(adapter);

        vm.getAllRestaurantsViewStateLiveData().observe(getViewLifecycleOwner(), listRestaurants -> {
            datas.clear();
            datas.addAll(listRestaurants);
            adapter.notifyDataSetChanged();
        });

        initList();
        View view = binding.getRoot();
        return view;
    }

    public void initList() {


    }
}