package com.example.trafimau_app;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

public class Launcher extends AppCompatActivity {

    private final String logTag = "TOOLBAR";
    private MyApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        app = (MyApplication) getApplicationContext();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        configRecyclerView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            Intent intentToMainActivity = new Intent(this, MainActivity.class);
            startActivity(intentToMainActivity);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void configRecyclerView() {
        RecyclerView rv = findViewById(R.id.launcher_recyclerView);
        rv.setAdapter(new ColorPlatesAdapter());

        int gridSpanCount;
        if (getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_PORTRAIT) {
            gridSpanCount = app.layout.portraitGridSpanCount;
        } else {
            gridSpanCount = app.layout.landscapeGridSpanCount;
        }
        GridLayoutManager layoutManager = new GridLayoutManager(this, gridSpanCount);
        rv.setLayoutManager(layoutManager);

        int offset = getResources().getDimensionPixelOffset(R.dimen.offset);
        rv.addItemDecoration(new ColorPlateDecorator(offset));
    }
}
