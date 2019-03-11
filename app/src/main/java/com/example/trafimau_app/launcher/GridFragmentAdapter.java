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

// TODO: consider creating common super class for GridFragmentAdapter and ListFragmentAdapter
// and deal with different view holders. maybe use generics?

public class GridFragmentAdapter extends RecyclerView.Adapter<GridFragmentViewHolder> {

    private final MyApplication app;

    GridFragmentAdapter(MyApplication app) {
        this.app = app;
    }

    @NonNull
    @Override
    public GridFragmentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.grid_app_item, viewGroup, false);
        return new GridFragmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GridFragmentViewHolder viewHolder, int i) {
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
