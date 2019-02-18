package com.example.trafimau_app;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.distribute.Distribute;

public class WelcomePageStart extends AppCompatActivity {

    private static final String APP_CENTER_KEY = "837acbd8-490f-4613-8c34-8cadf9bd3268";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Fabric.with(this, new Crashlytics());
        AppCenter.start(getApplication(), APP_CENTER_KEY, Distribute.class);

        setContentView(R.layout.activity_welcome_page_start);

        findViewById(R.id.welcomePageStartNextButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AppDescription.class);
                startActivity(intent);
//                TODO: finish all welcome page activites on success
            }
        });
    }

}
