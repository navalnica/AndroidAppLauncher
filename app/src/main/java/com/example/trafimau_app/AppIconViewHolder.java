package com.example.trafimau_app;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

class AppIconViewHolder extends RecyclerView.ViewHolder {

    AppIconViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    void bind(int color) {
        itemView.findViewById(R.id.appIcon).setBackgroundColor(color);
        int rgb = color & 0xffffff;
        final String hexString = "#" + Integer.toHexString(rgb);
        TextView appName = itemView.findViewById(R.id.appName);
        appName.setText(hexString);
    }
}
