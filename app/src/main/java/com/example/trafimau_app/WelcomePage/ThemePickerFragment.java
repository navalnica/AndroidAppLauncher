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
import com.yandex.metrica.YandexMetrica;

public class ThemePickerFragment extends Fragment {

    private OnContinueButtonClickListener continueButtonClickListener;

    private Activity activity;
    private MyApplication app;

    private RadioButton lightThemeRB;
    private RadioButton darkThemeRB;
    private boolean nightModeEnabled = false;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        activity = getActivity();
        if (activity == null) {
            final String msg = "ThemePickerFragment: getActivity() returned null";
            Log.d(MyApplication.LOG_TAG, msg);
            YandexMetrica.reportEvent(msg);
            throw new NullPointerException(msg);
        }
        activity = getActivity();
        app = (MyApplication) activity.getApplication();

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
    }

    @Override
    public void onDetach() {
        super.onDetach();
        continueButtonClickListener = null;
    }

    private void onRadioButtonBlockClick(View v, boolean darkThemeClicked) {
        Log.d(MyApplication.LOG_TAG, "ThemePickerFragment: onRadioButtonBlockClick");

        if(darkThemeClicked == nightModeEnabled){
            return;
        }

        Log.d(MyApplication.LOG_TAG,
                "ThemePickerFragment.onRadioButtonBlockClick: changing night mode state");

        nightModeEnabled = darkThemeClicked;
        app.setNightModeEnabled(nightModeEnabled);
        setRadioButtonsState();

        YandexMetrica.reportEvent("Recreating WelcomePage Activity");
        activity.recreate();
    }

    private void setRadioButtonsState() {
        lightThemeRB.setChecked(!nightModeEnabled);
        darkThemeRB.setChecked(nightModeEnabled);
    }
}
