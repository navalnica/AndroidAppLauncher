package com.example.trafimau_app.launcher;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.trafimau_app.MyAppInfo;
import com.example.trafimau_app.R;

public class GridFragmentViewHolder extends RecyclerView.ViewHolder {

    private AppCompatImageView appIconView;
    private TextView appLabelView;

    public GridFragmentViewHolder(@NonNull View itemView) {
        super(itemView);
        appIconView = itemView.findViewById(R.id.gridFragmentAppIcon);
        appLabelView = itemView.findViewById(R.id.gridFragmentAppLabel);
    }

    public void bind(MyAppInfo appInfo) {
        appIconView.setBackground(appInfo.icon);
        appLabelView.setText(appInfo.label);
    }
}
