package com.example.trafimau_app.Launcher;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;

import com.example.trafimau_app.MyApplication;
import com.example.trafimau_app.R;

public class PreferencesFragment
        extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener {


    private MyApplication app;

    private String keyLayoutList;
    ListPreference layoutListPreference;
    private String[] layoutDescriptions;


    @Override
    public void onCreatePreferences(Bundle bundle, String s) {

        addPreferencesFromResource(R.xml.preferences);

        layoutDescriptions = getResources().getStringArray(R.array.layout_descriptions);

        Activity activity = getActivity();
        if(activity == null){
            Log.d(MyApplication.LOG_TAG, "getActivity() returned null");
            throw new NullPointerException();
        }

        app = (MyApplication) activity.getApplication();

        keyLayoutList = getResources().getString(R.string.keyLayoutListPreference);
        layoutListPreference = (ListPreference) findPreference(keyLayoutList);

        initializeViews();
    }

    private void initializeViews() {
        String keyDarkThemeSwitch = getResources().getString(R.string.keyDarkThemeSwitch);
        SwitchPreference nightThemeToggle = (SwitchPreference) findPreference(keyDarkThemeSwitch);
        nightThemeToggle.setChecked(app.isNighModeEnabled());

        layoutListPreference.setValue(Boolean.toString(app.isCompactLayoutEnabled()));
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

        // update summary for currentLayout picker
        if (key.equals(keyLayoutList)) {
            final String value = layoutListPreference.getValue();
            boolean compactModeEnabled = Boolean.valueOf(value);
            app.setCompactLayoutEnabled(compactModeEnabled);
            setSummaryForLayoutListPreference();
        }
    }

    private void setSummaryForLayoutListPreference(){
        int valueIndex = layoutListPreference.findIndexOfValue(
                layoutListPreference.getValue()
        );

        CharSequence summary = layoutListPreference.getEntries()[valueIndex]
                + ":\n\n" + layoutDescriptions[valueIndex];
        layoutListPreference.setSummary(summary);
    }
}