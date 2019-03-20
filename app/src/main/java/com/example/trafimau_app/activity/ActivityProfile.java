package com.example.trafimau_app.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trafimau_app.MyApplication;
import com.example.trafimau_app.R;
import com.example.trafimau_app.activity.launcher.ActivityLauncher;
import com.yandex.metrica.YandexMetrica;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ActivityProfile extends AppCompatActivity {

    private int karmaValue;
    private Date karmaLastChangeDate;
    private final int deltaKarma = 5;

    private TextView karmaValueTV;
    private TextView karmaLastChangeValueTV;

    private MyApplication app;
    private BroadcastReceiver karmaUpdatedreceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        configureToolbar();
        setOnClickListeners();

        app = (MyApplication) getApplication();

        karmaValueTV = findViewById(R.id.currentKarmaValue);
        karmaLastChangeValueTV = findViewById(R.id.karmaLastChangeValue);

        karmaValue = app.getKarmaValue();
        karmaLastChangeDate = app.getKarmaLastChangeDate();

        updateKarmaValueTV();
        updateKarmaLastChangeDateTV();

        findViewById(R.id.karma).setOnClickListener(v -> {
            karmaValue += deltaKarma;
            app.setKarmaValue(karmaValue);
            updateKarmaValueTV();
        });

        karmaUpdatedreceiver = new KarmaUpdatedReceiver();
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MyApplication.KARMA_UPDATED_FROM_SILENT_PUSH_ACTION);
        registerReceiver(karmaUpdatedreceiver, intentFilter);

        Log.d(MyApplication.LOG_TAG, "ActivityProfile.onCreate");
        YandexMetrica.reportEvent("showing authors profile");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(karmaUpdatedreceiver);
    }

    private void updateKarmaValueTV() {
        karmaValueTV.setText(String.valueOf(karmaValue));
    }

    private void updateKarmaLastChangeDateTV() {
        String text;
        if(karmaLastChangeDate == null){
            text = getString(R.string.never);
        }
        else{
            text = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    .format(karmaLastChangeDate);
        }
        karmaLastChangeValueTV.setText(text);
    }

    private void configureToolbar() {
        Toolbar toolbar = findViewById(R.id.profileToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (isTaskRoot()) {
                    Log.d(MyApplication.LOG_TAG, "ActivityProfile.onOptionsItemSelected: " +
                            "creating parent activity because current ActivityProfile is task root");
                    Intent intent = new Intent(this, ActivityLauncher.class);
                    startActivity(intent);
                    finish();
                }
        }
        return super.onOptionsItemSelected(item);
    }

    private void setOnClickListeners() {
        findViewById(R.id.mobilePhone).setOnClickListener(v -> {
            Uri uri = Uri.parse("tel:" + getString(R.string.mobilePhoneNumber));
            Intent intent = new Intent(Intent.ACTION_DIAL, uri);
            startActivity(intent);
        });

        findViewById(R.id.workPhone).setOnClickListener(v -> {
            Uri uri = Uri.parse("tel:" + getString(R.string.workPhoneNumber));
            Intent intent = new Intent(Intent.ACTION_DIAL, uri);
            startActivity(intent);
        });

        findViewById(R.id.email).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:" + getString(R.string.email)));
            startActivity(intent);
        });

        findViewById(R.id.github).setOnClickListener(v -> {
            Uri uri = Uri.parse("https://www.github.com/" + getString(R.string.githubProfile));
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });

        findViewById(R.id.inspiringPlace).setOnClickListener(v -> {
            final double latitude = 38.48247594504772;
            final double longitude = 22.500576004385948;
            String stringUri = String.format(
                    Locale.getDefault(),
                    "geo:%f,%f?q=Ancient+Theatre,Delphi,Greece", latitude, longitude);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(stringUri));
            startActivity(intent);
        });
    }

    private class KarmaUpdatedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            karmaValue = app.getKarmaValue();
            karmaLastChangeDate = app.getKarmaLastChangeDate();

            updateKarmaValueTV();
            updateKarmaLastChangeDateTV();

            Log.d(MyApplication.LOG_TAG, "KarmaUpdatedReceiver: updating karma from silent push");
            Toast.makeText(context.getApplicationContext(),
                    "Karma updated by silent push!", Toast.LENGTH_LONG).show();
        }
    }
}
