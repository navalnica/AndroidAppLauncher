package com.example.trafimau_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.mainAuthorImage).setOnLongClickListener(v -> {
            Intent intent = new Intent(this, WelcomePageStart.class);
            startActivity(intent);
            return true;
        });
    }
}
