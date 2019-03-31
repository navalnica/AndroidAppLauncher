package com.example.trafimau_app.activity.launcher;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;

import com.example.trafimau_app.MyApplication;
import com.example.trafimau_app.R;
import com.yandex.metrica.YandexMetrica;

public class FragmentPreferences
        extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private Activity activity;
    private MyApplication app;

    private String keyLanguageList;
    private String keyDarkThemeSwitch;
    private String keyLayoutList;
    private String keyShowWelcomePageCheckbox;
    private String keySortAscendingSwitch;
    private String keySortModeList;

    private ListPreference languageListPreference;
    private SwitchPreference darkThemeSwitch;
    private ListPreference layoutListPreference; // todo: make it switch preference
    private CheckBoxPreference showWelcomePageCheckbox;
    private SwitchPreference sortAscendingSwitch;
    private ListPreference sortModeListPreference;

    private String[] languageSummary;
    private String[] layoutSummary;
    private String[] sortModeSummary;

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {

        addPreferencesFromResource(R.xml.preferences);

        languageSummary = getResources().getStringArray(R.array.entries_language);
        layoutSummary = getResources().getStringArray(R.array.summary_layout);
        sortModeSummary = getResources().getStringArray(R.array.summary_sort_mode);

        activity = getActivity();
        if (activity == null) {
            final String msg = "FragmentPreferences: getActivity() returned null";
            Log.d(MyApplication.LOG_TAG, msg);
            YandexMetrica.reportEvent(msg);
            throw new NullPointerException(msg);
        }
        app = (MyApplication) activity.getApplication();

        keyLanguageList = getString(R.string.prefKeyLanguageListPreference);
        keyDarkThemeSwitch = getString(R.string.prefKeyDarkThemeSwitch);
        keyLayoutList = getString(R.string.prefKeyLayoutListPreference);
        keyShowWelcomePageCheckbox = getString(R.string.prefKeyShowWelcomePageCheckbox);
        keySortAscendingSwitch = getString(R.string.prefKeySortAscendingSwitch);
        keySortModeList = getString(R.string.prefKeySortModeListPreference);

        languageListPreference = (ListPreference) findPreference(keyLanguageList);
        darkThemeSwitch = (SwitchPreference) findPreference(keyDarkThemeSwitch);
        layoutListPreference = (ListPreference) findPreference(keyLayoutList);
        showWelcomePageCheckbox = (CheckBoxPreference) findPreference(keyShowWelcomePageCheckbox);
        sortAscendingSwitch = (SwitchPreference) findPreference(keySortAscendingSwitch);
        sortModeListPreference = (ListPreference) findPreference(keySortModeList);

        initializeViews();
    }

    private void initializeViews() {
        languageListPreference.setValue(app.getLanguage());
        setListPreferenceSummaryFromArray(languageListPreference, languageSummary);

        darkThemeSwitch.setChecked(app.isNighModeEnabled());
        showWelcomePageCheckbox.setChecked(app.isShowWelcomePage());
        layoutListPreference.setValue(Boolean.toString(app.isCompactLayoutEnabled()));
        setSummaryForLayoutListPreference();

        sortAscendingSwitch.setChecked(app.isSortAscending());
        sortModeListPreference.setValue(app.getSortMode());
        setListPreferenceSummaryFromArray(sortModeListPreference, sortModeSummary);
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
        if (key.equals(keyLanguageList)){
//            app.setLanguage(languageListPreference.getValue());
//            setListPreferenceSummaryFromArray(languageListPreference, languageSummary);
        } else if (key.equals(keyLayoutList)) {
            final String value = layoutListPreference.getValue();
            boolean compactModeEnabled = Boolean.valueOf(value);
            app.setCompactLayoutEnabled(compactModeEnabled);
            setSummaryForLayoutListPreference();
        } else if (key.equals(keyDarkThemeSwitch)) {
            app.setNightModeEnabled(darkThemeSwitch.isChecked());
            activity.recreate();
        } else if (key.equals(keyShowWelcomePageCheckbox)) {
            app.setShowWelcomePage(showWelcomePageCheckbox.isChecked());
        } else if (key.equals(keySortAscendingSwitch)) {
            app.setSortAscending(sortAscendingSwitch.isChecked());
        } else if (key.equals(keySortModeList)) {
            app.setSortMode(sortModeListPreference.getValue());
            setListPreferenceSummaryFromArray(sortModeListPreference, sortModeSummary);
        }
    }

    private void setSummaryForLayoutListPreference() {
        int valueIndex = layoutListPreference.findIndexOfValue(
                layoutListPreference.getValue()
        );
        CharSequence summary = layoutListPreference.getEntries()[valueIndex]
                + ":\n\n" + layoutSummary[valueIndex];
        layoutListPreference.setSummary(summary);
    }

    private void setListPreferenceSummaryFromArray(ListPreference listPreference, String[] summaryArray){
        int valueIndex = listPreference.findIndexOfValue(listPreference.getValue());
        CharSequence summary = summaryArray[valueIndex];
        listPreference.setSummary(summary);
    }
}