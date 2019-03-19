package com.example.trafimau_app.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.example.trafimau_app.MyApplication;
import com.example.trafimau_app.R;
import com.yandex.metrica.YandexMetrica;

import java.util.Locale;

public class ActivityProfile extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        configureToolbar();
        setOnClickListeners();

        Log.d(MyApplication.LOG_TAG, "ActivityProfile.onCreate");
        YandexMetrica.reportEvent("showing authors profile");
    }

    private void configureToolbar() {
        Toolbar toolbar = findViewById(R.id.profileToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
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
}
