package com.example.justrun.fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.example.justrun.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }
}