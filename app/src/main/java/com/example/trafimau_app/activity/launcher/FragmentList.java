package com.example.trafimau_app.activity.launcher;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.trafimau_app.MyApplication;
import com.example.trafimau_app.R;

public class FragmentList extends AppsContainerBaseFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = (ActivityLauncher) getActivity();
        if (activity == null) {
            throw new NullPointerException("FragmentList.onCreate: getActivity() returned null");
        }
        app = (MyApplication) activity.getApplication();
        layoutResId = R.layout.fragment_list;
        launcherAppAdapter = new LauncherAppAdapter(app, this, R.layout.list_app_item);
        activity.addAppsChangedListener(launcherAppAdapter);
    }

    @Override
    protected void configRecyclerView() {
        RecyclerView rv = rootView.findViewById(R.id.listFragmentRecyclerView);
        rv.setAdapter(launcherAppAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(rv.getContext());
        rv.setLayoutManager(layoutManager);
    }
}
