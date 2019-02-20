package com.example.trafimau_app;

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

public class ProfileFragment extends Fragment {

    private AppCompatActivity activity;
    private ActionBar actionBar;
    private ActionBar activityActionBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            actionBar = activity.getSupportActionBar();
        }
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        Toolbar toolbar = rootView.findViewById(R.id.profileToolbar);

        // hide old toolbar and save its instance
        activityActionBar = activity.getSupportActionBar();
        if (activityActionBar != null){
            activityActionBar.hide();
        }

        // set the new toolbar from CollapsingToolbarLayout
        activity.setSupportActionBar(toolbar);
        actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        return rootView;
    }

    @Override
    public void onStop() {
        // set the old toolbar
        activity.setSupportActionBar(activity.findViewById(R.id.launcherToolbar));
        activityActionBar.show();
        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("profile", "onOptionsItemSelected");
        if (item.getItemId() == android.R.id.home) {
            activity.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
