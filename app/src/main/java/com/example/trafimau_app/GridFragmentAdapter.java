package com.example.trafimau_app;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
    public void onBindViewHolder(@NonNull GridFragmentViewHolder gridFragmentViewHolder, int i) {
        gridFragmentViewHolder.bind(dataModel.getColor(i));
    }

    @Override
    public int getItemCount() {
        return dataModel.getItemCount();
    }
}
