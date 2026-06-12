package com.indira.studdybuddyapk.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.indira.studdybuddyapk.R;
import com.indira.studdybuddyapk.activities.LoginActivity;

public class SettingsFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        SwitchMaterial switchTheme = view.findViewById(R.id.switchTheme);
        SharedPreferences pref = getActivity().getSharedPreferences("settings", Context.MODE_PRIVATE);
        
        boolean isDarkMode = pref.getBoolean("dark_mode", false);
        switchTheme.setChecked(isDarkMode);
        
        switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            pref.edit().putBoolean("dark_mode", isChecked).apply();
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        // Setup Logout click listener
        View cardLogout = view.findViewById(R.id.cardLogout);
        if (cardLogout != null) {
            cardLogout.setOnClickListener(v -> logout());
        }
        
        return view;
    }

    private void logout() {
        // Hapus data login di StuddyBuddyPrefs
        SharedPreferences pref = getActivity().getSharedPreferences("StuddyBuddyPrefs", Context.MODE_PRIVATE);
        pref.edit().clear().apply();

        // Pindah kembali ke LoginActivity
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
    }
}