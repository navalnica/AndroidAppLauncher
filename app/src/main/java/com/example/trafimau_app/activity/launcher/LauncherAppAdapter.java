package com.example.trafimau_app.activity.launcher;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.trafimau_app.data.MyAppInfo;
import com.example.trafimau_app.MyApplication;

public class LauncherAppAdapter extends RecyclerView.Adapter<LaucherViewHolder> {

    private final MyApplication app;
    private Fragment fragment;

    private int itemLayoutResourceId;

    LauncherAppAdapter(MyApplication app, Fragment fragment, int itemLayoutResourceId) {
        this.app = app;
        this.fragment = fragment;
        this.itemLayoutResourceId = itemLayoutResourceId;
    }

    @NonNull
    @Override
    public LaucherViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).
                inflate(itemLayoutResourceId, viewGroup, false);
        return new LaucherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LaucherViewHolder viewHolder, int i) {
        final MyAppInfo appInfo = app.getAppInfo(i);
        viewHolder.bind(appInfo);
        viewHolder.itemView.setTag(i);
        viewHolder.itemView.setOnClickListener(v -> {
                    app.increaseAppLaunchedCount(appInfo.packageName, i);
                    viewHolder.itemView.getContext()
                            .startActivity(appInfo.launchIntent);
                }
        );
        fragment.registerForContextMenu(viewHolder.itemView);
    }

    @Override
    public int getItemCount() {
        return app.getInstalledAppsCount();
    }
}
