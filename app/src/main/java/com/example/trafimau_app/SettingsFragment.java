package com.example.trafimau_app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

public class SettingsFragment
        extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {

        // TODO: set default values for settings
        // TODO: summary is not saved after screen rotation
        addPreferencesFromResource(R.xml.preferences_root);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);

        // update summary for layout picker
        if (preference instanceof ListPreference &&
                preference.getKey().equals(
                        getResources().getString(R.string.keyLayoutListPreference))) {

            String[] descriptions = getResources().getStringArray(R.array.layout_descriptions);

            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(sharedPreferences.getString(key, ""));
            if (prefIndex >= 0) {
                String summary = listPreference.getEntries()[prefIndex].toString()
                        + ":\n\n" + descriptions[prefIndex];
                preference.setSummary(summary);
            }

        }
    }
}