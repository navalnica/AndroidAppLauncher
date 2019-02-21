package com.example.trafimau_app.Launcher;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.example.trafimau_app.R;

public class PreferencesFragment
        extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private final String TAG = "settings";
    private SharedPreferences sharedPreferences;

    private String layoutListKey;
    ListPreference layoutListPreference;
    private String[] layoutDescriptions;


    @Override
    public void onCreatePreferences(Bundle bundle, String s) {

        addPreferencesFromResource(R.xml.preferences);

        layoutDescriptions = getResources().getStringArray(R.array.layout_descriptions);

        Activity activity = getActivity();
        if(activity == null){
            Log.d(TAG, "getActivity() returned null");
            throw new NullPointerException();
        }

        layoutListKey = getResources().getString(R.string.keyLayoutListPreference);
        layoutListPreference = (ListPreference) findPreference(layoutListKey);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);

        setSummaryForLayoutListPreference();
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
        if (key.equals(getResources().getString(R.string.keyLayoutListPreference))) {
            setSummaryForLayoutListPreference();
        }
    }

    private void setSummaryForLayoutListPreference(){
        int valueIndex = layoutListPreference.findIndexOfValue(
                sharedPreferences.getString(layoutListKey, ""));

        // TODO: move parameters initialization into Welcome Page!
        if(valueIndex < 0){
            valueIndex = 0;
        }

        CharSequence summary = layoutListPreference.getEntries()[valueIndex]
                + ":\n\n" + layoutDescriptions[valueIndex];
        layoutListPreference.setSummary(summary);
    }
}