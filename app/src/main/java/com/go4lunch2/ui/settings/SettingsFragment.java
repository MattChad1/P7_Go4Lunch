package com.go4lunch2.ui.settings;

import static com.go4lunch2.MyApplication.PREFS_CENTER;
import static com.go4lunch2.MyApplication.PREFS_CENTER_COMPANY;
import static com.go4lunch2.MyApplication.PREFS_CENTER_GPS;
import static com.go4lunch2.MyApplication.PREFS_NAME;
import static com.go4lunch2.MyApplication.PREFS_NOTIFS;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.go4lunch2.MyApplication;
import com.go4lunch2.R;
import com.go4lunch2.databinding.FragmentSettingsBinding;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SettingsFragment extends Fragment {
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    FragmentSettingsBinding binding;

    public SettingsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentSettingsBinding.inflate(inflater, container, false);

        MaterialToolbar toolbar = requireActivity().findViewById(R.id.topAppBar);
        toolbar.setTitle(getString(R.string.settings));
        toolbar.getMenu().getItem(0).setVisible(false);
        toolbar.getMenu().getItem(1).setVisible(false);

        settings = MyApplication.getInstance().getSharedPreferences(PREFS_NAME, 0);

        if (settings.getBoolean(PREFS_NOTIFS, true)) binding.switchNotif.setChecked(true);
        if (settings.getString(PREFS_CENTER, "").equals(PREFS_CENTER_GPS)) binding.btnRadioCentermap2.setChecked(true);
        else binding.btnRadioCentermap1.setChecked(true);

        editor = settings.edit();
        binding.switchNotif.setOnClickListener(v -> {
            editor.putBoolean(PREFS_NOTIFS, ((SwitchCompat) v).isChecked());
            editor.commit();
        });
        binding.btnRadioCentermap1.setOnClickListener(v -> {
            editor.putString(PREFS_CENTER, PREFS_CENTER_COMPANY);
            editor.commit();
        });
        binding.btnRadioCentermap2.setOnClickListener(v -> {
            editor.putString(PREFS_CENTER, PREFS_CENTER_GPS);
            editor.commit();
        });

        BottomNavigationView bottombar = requireActivity().findViewById(R.id.bottom_navigation);
        bottombar.setSelectedItemId(0);

        return binding.getRoot();
    }
}