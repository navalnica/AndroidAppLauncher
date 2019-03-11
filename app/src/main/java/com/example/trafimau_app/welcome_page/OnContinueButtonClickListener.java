package com.example.trafimau_app.welcome_page;

import android.view.View;

public interface OnContinueButtonClickListener {
    void onContinueButtonClick(View view);
    static String getErrorMessage(String contextName){
        return contextName + " must implement OnContinueButtonClickListener";
    }
}
