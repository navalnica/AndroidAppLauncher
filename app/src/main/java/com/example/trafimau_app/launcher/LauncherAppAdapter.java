package com.example.trafimau_app.launcher;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.trafimau_app.MyAppInfo;
import com.example.trafimau_app.MyApplication;

public class LauncherAppAdapter extends RecyclerView.Adapter<LaucherViewHolder> {

    private final MyApplication app;

    private int itemLayoutResourceId;

    LauncherAppAdapter(MyApplication app, int itemLayoutResourceId) {
        this.app = app;
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
        final MyAppInfo appInfo = app.getAppInfoFromLocalVar(i);
        viewHolder.bind(appInfo);
        viewHolder.itemView.setOnClickListener(v -> {
                    app.increaseAppLaunchedCount(appInfo.packageName, i);
                    viewHolder.itemView.getContext()
                            .startActivity(appInfo.launchIntent);
                }
        );
        viewHolder.itemView.setOnLongClickListener(v -> {
            // TODO: show text in context menu
            final String text = "Launched times: " + appInfo.launchedCount +
                    "\nLast launch: " + appInfo.lastLaunched;
            Toast.makeText(viewHolder.itemView.getContext(), text, Toast.LENGTH_LONG).show();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return app.getInstalledAppsCount();
    }
}
