package com.example.trafimau_app;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class ListActivity extends AppCompatActivity {

    private MyApplication app;
    private ListActivityAppAdapter listActivityAppAdapter;
    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        MyApplication app = (MyApplication) getApplication();
        listActivityAppAdapter = new ListActivityAppAdapter(app.dataModel);

        Toolbar toolbar = findViewById(R.id.listActivityToolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        FloatingActionButton fab = findViewById(R.id.listActivityFab);
        fab.setOnClickListener(view -> {
            Snackbar.make(view, "FAB clicked", Snackbar.LENGTH_LONG).show();
            app.dataModel.addRandomColorToFront();
            listActivityAppAdapter.notifyDataSetChanged();
            layoutManager.scrollToPosition(0);
        });

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
        RecyclerView rv = findViewById(R.id.listActivityRecyclerView);
        rv.setAdapter(listActivityAppAdapter);
        layoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(layoutManager);
    }
}
