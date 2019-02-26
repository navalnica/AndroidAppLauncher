package com.example.trafimau_app.Launcher;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.trafimau_app.MyApplication;
import com.example.trafimau_app.R;
import com.yandex.metrica.YandexMetrica;

public class ProfileFragment extends Fragment {

    private AppCompatActivity activity;
    private ActionBar fragmentActionBar;
    private ActionBar activityActionBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            fragmentActionBar = activity.getSupportActionBar();
        }

        // enable support of current fragment toolbar events
        setHasOptionsMenu(true);

        Log.d(MyApplication.LOG_TAG, "ProfileFragment.onCreate");
        YandexMetrica.reportEvent("authors profile is shown");
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);


        // hide old toolbar and save its instance
        activityActionBar = activity.getSupportActionBar();
        if (activityActionBar != null) {
            activityActionBar.hide();
        }

        // set the new toolbar from CollapsingToolbarLayout
        Toolbar toolbar = rootView.findViewById(R.id.profileToolbar);
        activity.setSupportActionBar(toolbar);
        fragmentActionBar = activity.getSupportActionBar();

        if (fragmentActionBar != null) {
            fragmentActionBar.setDisplayHomeAsUpEnabled(true);
        }

        return rootView;
    }

    @Override
    public void onStop() {
        Log.d(MyApplication.LOG_TAG, "ProfileFragment.onStop");

        // set the old toolbar
        activity.setSupportActionBar(activity.findViewById(R.id.launcherToolbar));
        activityActionBar.show();
        fragmentActionBar.hide();
        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(MyApplication.LOG_TAG, "ProfileFragment.onOptionsItemSelected");
        if (item.getItemId() == android.R.id.home) {
            activity.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
