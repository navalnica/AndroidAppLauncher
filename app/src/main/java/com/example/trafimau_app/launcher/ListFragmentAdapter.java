package com.example.trafimau_app.launcher;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.trafimau_app.MyAppInfo;
import com.example.trafimau_app.MyApplication;
import com.example.trafimau_app.R;

public class ListFragmentAdapter extends RecyclerView.Adapter<ListFragmentViewHolder> {

    private final MyApplication app;

    ListFragmentAdapter(MyApplication app) {
        this.app = app;
    }

    @NonNull
    @Override
    public ListFragmentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_app_item, viewGroup, false);
        return new ListFragmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListFragmentViewHolder viewHolder, int i) {
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
