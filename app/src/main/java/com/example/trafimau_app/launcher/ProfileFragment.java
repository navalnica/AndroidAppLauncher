package com.example.trafimau_app.launcher;

import android.content.Intent;
import android.net.Uri;
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

import java.util.Locale;

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
        YandexMetrica.reportEvent("showing authors profile");
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

        setOnClickListeners(rootView);

        return rootView;
    }

    private void setOnClickListeners(View rootView) {
        rootView.findViewById(R.id.mobilePhone).setOnClickListener(v -> {
            Uri uri = Uri.parse("tel:" + getString(R.string.mobilePhoneNumber));
            Intent intent = new Intent(Intent.ACTION_DIAL, uri);
            startActivity(intent);
        });

        rootView.findViewById(R.id.workPhone).setOnClickListener(v -> {
            Uri uri = Uri.parse("tel:" + getString(R.string.workPhoneNumber));
            Intent intent = new Intent(Intent.ACTION_DIAL, uri);
            startActivity(intent);
        });

        rootView.findViewById(R.id.email).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:" + getString(R.string.email)));
            startActivity(intent);
        });

        rootView.findViewById(R.id.github).setOnClickListener(v -> {
            Uri uri = Uri.parse("https://www.github.com/" + getString(R.string.githubProfile));
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });


        rootView.findViewById(R.id.inspiringPlace).setOnClickListener(v -> {
            final double latitude = 38.48247594504772;
            final double longitude = 22.500576004385948;
            String stringUri = String.format(
                    Locale.getDefault(),
                    "geo:%f,%f?q=Ancient+Theatre,Delphi,Greece", latitude, longitude);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(stringUri));
            startActivity(intent);
        });
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
