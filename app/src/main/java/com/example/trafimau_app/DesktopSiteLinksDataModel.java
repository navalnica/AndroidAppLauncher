package com.example.trafimau_app;

import java.util.HashMap;

public class DesktopSiteLinksDataModel {

    public final HashMap<Integer, SiteInfo> sites = new HashMap<>();

    public void putSiteLink(int ix, String link) {
        sites.put(ix, new SiteInfo(link));
    }

    public String getLink(int ix){
        SiteInfo info = sites.get(ix);
        if(info == null){
            return null;
        }
        return info.link;
    }

}
