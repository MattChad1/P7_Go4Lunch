package com.natationpourtous.go4lunch.ui;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toolbar;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.natationpourtous.go4lunch.R;
import com.natationpourtous.go4lunch.databinding.ActivityMainBinding;
import com.natationpourtous.go4lunch.ui.list_restaurants.ListRestaurantsFragment;
import com.natationpourtous.go4lunch.ui.list_workmates.ListWorkmatesFragment;
import com.natationpourtous.go4lunch.ui.map.MapsFragment;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.main_fragment, ListRestaurantsFragment.class, null)
                    .commit();
        }

        MaterialToolbar toolbar = binding.topAppBar;
        setSupportActionBar(toolbar);
        drawer = binding.drawerLayout;
        toolbar.setNavigationOnClickListener(v -> {
            drawer.openDrawer(GravityCompat.START);
        });


        binding.navigationDrawer.setNavigationItemSelectedListener ( menuItem -> {
                // Handle menu item selected
                //menuItem.isChecked = true;
        drawer.closeDrawer(GravityCompat.START);
           return true;
        });

        BottomNavigationView bottombar = binding.bottomNavigation;
        bottombar.setOnItemSelectedListener(view1 -> {
            Class linkTo;
            Log.i("Onclick", "onCreate: ");
            switch (view1.getItemId()) {
                case R.id.menu_bb_mapview:
                toolbar.setTitle(getString(R.string.map_view_desc));
                linkTo = MapsFragment.class;
                break;
                case R.id.menu_bb_listview:
                    toolbar.setTitle(getString(R.string.list_restaurants_desc));
                    linkTo = ListRestaurantsFragment.class;
                    break;
                case R.id.menu_bb_workmates:
                    toolbar.setTitle(getString(R.string.list_workmates_desc));
                    linkTo = ListWorkmatesFragment.class;
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + view1.getItemId());
            }






            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.main_fragment, linkTo, null)
                    .commit();

            return true;

        });




    }


    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}