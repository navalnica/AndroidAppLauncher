package com.example.trafimau_app.data;

import android.graphics.Bitmap;

import com.example.trafimau_app.data.db.DesktopSiteItem;

public class DesktopSiteItemWithIcon extends DesktopSiteItem {
    public Bitmap icon;

    public DesktopSiteItemWithIcon(int index, String siteLink) {
        super(index, siteLink);
    }

    public DesktopSiteItemWithIcon(DesktopSiteItem item){
        super(item.index, item.uri.toString());
    }
}
