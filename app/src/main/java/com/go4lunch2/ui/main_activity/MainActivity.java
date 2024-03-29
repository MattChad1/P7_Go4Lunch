package com.go4lunch2.ui.main_activity;

import static com.go4lunch2.MyApplication.PREFS_CENTER;
import static com.go4lunch2.MyApplication.PREFS_CENTER_GPS;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.go4lunch2.BaseActivity;
import com.go4lunch2.MyApplication;
import com.go4lunch2.R;
import com.go4lunch2.ViewModelFactory;
import com.go4lunch2.data.model.CustomUser;
import com.go4lunch2.data.repositories.SortRepository;
import com.go4lunch2.databinding.ActivityMainBinding;
import com.go4lunch2.ui.detail_restaurant.DetailRestaurantActivity;
import com.go4lunch2.ui.list_restaurants.ListRestaurantsFragment;
import com.go4lunch2.ui.list_workmates.ListWorkmatesFragment;
import com.go4lunch2.ui.map.MapsFragment;
import com.go4lunch2.ui.settings.SettingsFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements LocationListener {

    DrawerLayout drawer;
    FirebaseUser user;
    CustomUser currentCustomUser;
    RecyclerView rv;
    List<SearchViewStateItem> searchResults = new ArrayList<>();
    SearchAdapter adapter;
    MainActivityViewModel vm;
    LocationManager locationManager;
    Location userLocation;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        vm = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(MainActivityViewModel.class);

        user = FirebaseAuth.getInstance().getCurrentUser();
        vm.getCurrentCustomUser(user != null ? user.getUid() : null).observe(this, value -> currentCustomUser = value);

        binding = ActivityMainBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();
        setContentView(view);

        MaterialToolbar toolbar = binding.topAppBar;
        setSupportActionBar(toolbar);

        drawer = binding.drawerLayout;
        toolbar.setNavigationOnClickListener(v -> drawer.openDrawer(GravityCompat.START));

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.main_fragment, ListRestaurantsFragment.class, null)
                    .commit();
        }

        // Set up the location manager if user wants to use its GPS
        if (MyApplication.settings.getString(PREFS_CENTER, "").equals(PREFS_CENTER_GPS)) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 50, this);
        }

        // Set up the content of the navigation drawer
        if (user != null) {
            NavigationView navigationView = findViewById(R.id.navigation_drawer);
            View headerView = navigationView.getHeaderView(0);
            TextView tvMailUser = headerView.findViewById(R.id.nav_drawer_tv_email);
            TextView tvNameUser = headerView.findViewById(R.id.nav_drawer_name);
            ImageView ivAvatarUser = headerView.findViewById(R.id.nav_drawer_avatar);

            for (UserInfo profile : user.getProviderData()) {
                tvMailUser.setText(profile.getEmail());
                tvNameUser.setText(profile.getDisplayName());
                Glide.with(this).load(profile.getPhotoUrl())
                        .transform(new CircleCrop())
                        .into(ivAvatarUser);
            }
        }

        binding.navigationDrawer.setNavigationItemSelectedListener(menuItem -> {
            if (menuItem.getItemId() == R.id.menu_drawer_logout) {
                signOut();
            }

            else if (menuItem.getItemId() == R.id.menu_drawer_yourlunch) {
                if (currentCustomUser.getIdRestaurantChosen() != null) {
                    Intent i = new Intent(this, DetailRestaurantActivity.class);
                    i.putExtra(DetailRestaurantActivity.RESTAURANT_SELECTED, currentCustomUser.getIdRestaurantChosen());
                    startActivity(i);
                }
                else Toast.makeText(this, R.string.no_restaurant, Toast.LENGTH_SHORT).show();
            }

            else if (menuItem.getItemId() == R.id.menu_drawer_settings) {
                getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.main_fragment, SettingsFragment.class, null)
                        .commit();
            }

            drawer.closeDrawer(GravityCompat.START);
            return true;
        });

        // Bottom Navigation
        BottomNavigationView bottombar = binding.bottomNavigation;
        bottombar.setSelectedItemId(R.id.menu_bb_listview);
        bottombar.setOnItemSelectedListener(view1 -> {
            if (view1.getItemId() == R.id.menu_bb_mapview) {
                Bundle bundle = new Bundle();
                bundle.putDouble("centerLatitude", userLocation.getLatitude());
                bundle.putDouble("centerLongitude", userLocation.getLongitude());
                MapsFragment frag = new MapsFragment();
                frag.setArguments(bundle);
                getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.main_fragment, frag, null)
                        .commit();
            }
            else if (view1.getItemId() == R.id.menu_bb_listview) {
                getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.main_fragment, ListRestaurantsFragment.class, null)
                        .commit();
            }
            else if (view1.getItemId() == R.id.menu_bb_workmates) {
                getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.main_fragment, ListWorkmatesFragment.class, null)
                        .commit();
            }

            return true;
        });

        // Display list of restaurants in RecyclerView
        rv = binding.lvSearchResults;
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapter = new SearchAdapter(this, searchResults);
        rv.setAdapter(adapter);

        vm.getSearchResultsLiveData().observe(this, values -> {
            searchResults.clear();
            searchResults.addAll(values);
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // For searching restaurants in the toolbar
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint(getString(R.string.search_restaurant));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s.length() == 0) {
                    rv.setVisibility(View.GONE);
                    binding.mainFragment.setVisibility(View.VISIBLE);
                }

                else if (s.length() > 2) {
                    rv.setVisibility(View.VISIBLE);
                    binding.mainFragment.setVisibility(View.GONE);
                    vm.getSearchResults(s);
                }

                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // function for the list to be sorted
        if (item.getItemId() == R.id.menu_order_name) vm.updateOrderLiveData(SortRepository.OrderBy.NAME);
        else if (item.getItemId() == R.id.menu_order_distance) vm.updateOrderLiveData(SortRepository.OrderBy.DISTANCE);
        else if (item.getItemId() == R.id.menu_order_rating) vm.updateOrderLiveData(SortRepository.OrderBy.RATING);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        vm.updateCenter(location);
        userLocation = location;
    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {

    }

    @Override
    public void onFlushComplete(int requestCode) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}