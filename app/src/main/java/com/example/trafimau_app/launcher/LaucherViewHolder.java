package com.example.trafimau_app.launcher;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.trafimau_app.MyAppInfo;
import com.example.trafimau_app.R;

public class LaucherViewHolder extends RecyclerView.ViewHolder {

    private AppCompatImageView appIconView;
    private TextView appLabelView;

    public LaucherViewHolder(@NonNull View itemView) {
        super(itemView);
        appIconView = itemView.findViewById(R.id.launcherAppIcon);
        appLabelView = itemView.findViewById(R.id.launcherAppLabel);
    }

    public void bind(MyAppInfo appInfo) {
        appIconView.setImageDrawable(appInfo.icon);
        appLabelView.setText(appInfo.label);
    }
}
