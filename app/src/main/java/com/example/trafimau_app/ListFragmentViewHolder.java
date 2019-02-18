package com.example.trafimau_app;

import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class ListFragmentViewHolder extends RecyclerView.ViewHolder {

    public ListFragmentViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    void bind(int color, String itemDescription){
        View appIcon = itemView.findViewById(R.id.listActivityAppIcon);
        GradientDrawable gradientDrawable = (GradientDrawable) appIcon.getBackground();
        gradientDrawable.setColor(color);

        int rgb = color & 0xffffff;
        final String hexString = "#" + Integer.toHexString(rgb);
        TextView appName = itemView.findViewById(R.id.listActivityAppName);
        appName.setText(hexString);

        TextView appDescription = itemView.findViewById(R.id.listActivityAppDescription);
        appDescription.setText(itemDescription);
    }
}
