package com.example.trafimau_app;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class LauncherActivityAppAdapter extends RecyclerView.Adapter<LauncherActivityAppViewHolder> {

    private DataModel dataModel;

    LauncherActivityAppAdapter(DataModel dataModel) {
        this.dataModel = dataModel;
    }

    @NonNull
    @Override
    public LauncherActivityAppViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.launcher_activity_app_view, viewGroup, false);
        return new LauncherActivityAppViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LauncherActivityAppViewHolder launcherActivityAppViewHolder, int i) {
        launcherActivityAppViewHolder.bind(dataModel.getColor(i));
    }

    @Override
    public int getItemCount() {
        return dataModel.getItemCount();
    }
}
