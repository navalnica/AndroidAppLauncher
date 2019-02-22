package com.example.trafimau_app.Launcher;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.trafimau_app.DataModel;
import com.example.trafimau_app.MyAppInfo;
import com.example.trafimau_app.R;

public class GridFragmentAdapter extends RecyclerView.Adapter<GridFragmentViewHolder> {

    private DataModel dataModel;

    GridFragmentAdapter(DataModel dataModel) {
        this.dataModel = dataModel;
    }

    @NonNull
    @Override
    public GridFragmentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.grid_fragment_app_view, viewGroup, false);
        return new GridFragmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GridFragmentViewHolder viewHolder, int i) {
        final MyAppInfo appInfo = dataModel.apps.get(i);
        viewHolder.bind(appInfo);
        viewHolder.itemView.setOnClickListener(v -> viewHolder.itemView.getContext()
                .startActivity(appInfo.launchIntent)
        );
    }

    @Override
    public int getItemCount() {
        return dataModel.apps.size();
    }
}
