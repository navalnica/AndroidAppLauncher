package com.example.trafimau_app;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

class GridFragmentViewHolder extends RecyclerView.ViewHolder {

    GridFragmentViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    void bind(int color) {
        itemView.findViewById(R.id.gridFragmentAppIcon).setBackgroundColor(color);
        int rgb = color & 0xffffff;
        final String hexString = "#" + Integer.toHexString(rgb);
        TextView appName = itemView.findViewById(R.id.gridFragmentAppName);
        appName.setText(hexString);
    }
}
