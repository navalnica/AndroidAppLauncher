package com.example.trafimau_app.activity.launcher;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.trafimau_app.MyApplication;

public abstract class AppsContainerBaseFragment extends Fragment {

    protected MyApplication app;
    protected ActivityLauncher activity;

    protected int layoutResId;
    protected View rootView;
    protected LauncherAppAdapter launcherAppAdapter;

    protected abstract void configRecyclerView();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(layoutResId, container, false);
        configRecyclerView();
        return rootView;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        activity.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return activity.onContextItemSelected(item);
    }

}
