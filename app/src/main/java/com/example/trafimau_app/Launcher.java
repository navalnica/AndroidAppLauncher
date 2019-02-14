package com.example.trafimau_app;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

public class Launcher extends AppCompatActivity {

    private MyApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        app = (MyApplication) getApplicationContext();

        showSetup();
        configRecyclerView();
    }

    private void configRecyclerView() {
        RecyclerView rv = findViewById(R.id.launcher_recyclerView);
        rv.setAdapter(new ColorPlatesAdapter());
        GridLayoutManager layoutManager = new GridLayoutManager(this, app.layout.portraitGridSpanCount);
        rv.setLayoutManager(layoutManager);
        int offset = getResources().getDimensionPixelOffset(R.dimen.offset);
        rv.addItemDecoration(new ColorPlateDecorator(offset));
    }

    private void showSetup() {
        String sb = "Theme: " + app.theme + "\nLayout: " + app.layout;
        Snackbar.make(findViewById(R.id.launcher_recyclerView), sb, Snackbar.LENGTH_LONG).show();
    }
}
