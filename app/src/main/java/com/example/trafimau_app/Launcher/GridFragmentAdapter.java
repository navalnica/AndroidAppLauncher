package com.example.trafimau_app.Launcher;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.trafimau_app.AppsDataModel;
import com.example.trafimau_app.MyAppInfo;
import com.example.trafimau_app.R;

public class GridFragmentAdapter extends RecyclerView.Adapter<GridFragmentViewHolder> {

    private AppsDataModel appsDataModel;

    GridFragmentAdapter(AppsDataModel appsDataModel) {
        this.appsDataModel = appsDataModel;
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
        final MyAppInfo appInfo = appsDataModel.apps.get(i);
        viewHolder.bind(appInfo);
        viewHolder.itemView.setOnClickListener(v -> viewHolder.itemView.getContext()
                .startActivity(appInfo.launchIntent)
        );
    }

    @Override
    public int getItemCount() {
        return appsDataModel.apps.size();
    }
}
