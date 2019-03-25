package com.example.trafimau_app.activity.launcher;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.trafimau_app.MyApplication;
import com.example.trafimau_app.R;

public class FragmentGrid extends AppsContainerBaseFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = (ActivityLauncher) getActivity();
        if (activity == null) {
            throw new NullPointerException("FragmentGrid.onCreate: getActivity() returned null");
        }
        app = (MyApplication) activity.getApplication();
        layoutResId = R.layout.fragment_grid;
        launcherAppAdapter = new LauncherAppAdapter(app, this, R.layout.grid_app_item);
        activity.addAppsChangedListener(launcherAppAdapter);
    }

    @Override
    protected void configRecyclerView() {
        RecyclerView rv = rootView.findViewById(R.id.gridFragmentRecyclerView);
        rv.setAdapter(launcherAppAdapter);
        int gridSpanCount = 1;
        if (getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_PORTRAIT) {
            gridSpanCount = app.currentLayout.portraitGridSpanCount;
        } else {
            gridSpanCount = app.currentLayout.landscapeGridSpanCount;
        }
        LinearLayoutManager layoutManager = new GridLayoutManager(rv.getContext(), gridSpanCount);
        rv.setLayoutManager(layoutManager);

        int offset = getResources().getDimensionPixelOffset(R.dimen.smallOffset);
        rv.addItemDecoration(new GridFragmentDecorator(offset));
    }
}
