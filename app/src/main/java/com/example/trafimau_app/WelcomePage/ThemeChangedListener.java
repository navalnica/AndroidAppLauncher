package com.example.trafimau_app.WelcomePage;

import android.support.v4.app.Fragment;

public interface ThemeChangedListener {
    void resetTheme();
    static String getErrorMessage(String contextName){
        return contextName + " must implement ThemeChangedListener";
    }
}
