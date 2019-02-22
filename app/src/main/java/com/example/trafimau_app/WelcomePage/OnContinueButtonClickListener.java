package com.example.trafimau_app.WelcomePage;

import android.view.View;

public interface OnContinueButtonClickListener {
    void onContinueButtonClick(View view);
    static String getErrorMessage(String contextName){
        return contextName + " must implement OnContinueButtonClickListener";
    }
}
