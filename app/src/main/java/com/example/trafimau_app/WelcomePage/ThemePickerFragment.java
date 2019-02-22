package com.example.trafimau_app.WelcomePage;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.example.trafimau_app.MyApplication;
import com.example.trafimau_app.R;

public class ThemePickerFragment extends Fragment {

    private OnContinueButtonClickListener continueButtonClickListener;
    private ThemeChangedListener themeChangedListener;

    private MyApplication app;
    private RadioButton lightThemeRB;
    private RadioButton darkThemeRB;
    private boolean nightModeEnabled = false;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Activity activity = getActivity();
        if (activity != null) {
            app = (MyApplication) getActivity().getApplication();
        }
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_welcome_theme_picker, container, false);

        lightThemeRB = rootView.findViewById(R.id.themePickerLightThemeRadioButton);
        darkThemeRB = rootView.findViewById(R.id.themePickerDarkThemeRadioButton);
        final View lightThemeBlock = rootView.findViewById(R.id.themePickerLightBlock);
        final View darkThemeBlock = rootView.findViewById(R.id.themePickerDarkBlock);

        lightThemeBlock.setOnClickListener(v -> onRadioButtonBlockClick(v, false));
        darkThemeBlock.setOnClickListener(v -> onRadioButtonBlockClick(v, true));

        rootView.findViewById(R.id.themePickerContinueButton).setOnClickListener(
                v -> continueButtonClickListener.onContinueButtonClick(v));

        nightModeEnabled = app.isNighModeEnabled();
        setRadioButtonsState();

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            continueButtonClickListener = (OnContinueButtonClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    OnContinueButtonClickListener.getErrorMessage(context.toString()));
        }
        try {
            themeChangedListener = (ThemeChangedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    ThemeChangedListener.getErrorMessage(context.toString()));
        }
    }

    private void onRadioButtonBlockClick(View v, boolean nightThemeClicked) {
        Log.d(MyApplication.LOG_TAG, "ThemePickerFragment: onRadioButtonBlockClick");

        if(nightThemeClicked == nightModeEnabled){
            return;
        }

        Log.d(MyApplication.LOG_TAG,
                "ThemePickerFragment.onRadioButtonBlockClick: changing night mode state");

        nightModeEnabled = nightThemeClicked;
        app.setNightModeEnabled(nightModeEnabled);
        setRadioButtonsState();

        themeChangedListener.resetTheme();
    }

    private void setRadioButtonsState() {
        lightThemeRB.setChecked(!nightModeEnabled);
        darkThemeRB.setChecked(nightModeEnabled);
    }
}
