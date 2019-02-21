package com.example.trafimau_app.WelcomePage;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.example.trafimau_app.MyApplication;
import com.example.trafimau_app.R;

import java.util.ArrayList;
import java.util.List;

public class ThemePickerFragment extends Fragment {

    private OnContinueButtonClickListener continueButtonClickListener;

    private MyApplication app;
    private List<BindedTheme> themes;

    final class BindedTheme {
        final View layoutBlock;
        final RadioButton radioButton;
        final MyApplication.Theme theme;

        BindedTheme(View layoutBlock, RadioButton radioButton, MyApplication.Theme theme) {
            this.layoutBlock = layoutBlock;
            this.radioButton = radioButton;
            this.theme = theme;
        }
    }

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

        final RadioButton lightThemeRB = rootView.findViewById(R.id.themePickerLightThemeRadioButton);
        final View lightThemeBlock = rootView.findViewById(R.id.themePickerLightBlock);
        final RadioButton darkThemeRB = rootView.findViewById(R.id.themePickerDarkThemeRadioButton);
        final View darkThemeBlock = rootView.findViewById(R.id.themePickerDarkBlock);

        themes = new ArrayList<>();
        themes.add(new BindedTheme(lightThemeBlock, lightThemeRB, MyApplication.Theme.LIGHT));
        themes.add(new BindedTheme(darkThemeBlock, darkThemeRB, MyApplication.Theme.DARK));
        lightThemeBlock.setOnClickListener(this::onRadioButtonBlockClick);
        darkThemeBlock.setOnClickListener(this::onRadioButtonBlockClick);

        lightThemeRB.setChecked(true);

        rootView.findViewById(R.id.themePickerContinueButton).setOnClickListener(
                v -> continueButtonClickListener.onContinueButtonClick(v));

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            continueButtonClickListener = (OnContinueButtonClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    context.toString() + " must implement OnArticleSelectedListener");
        }
    }

    private void onRadioButtonBlockClick(View v) {
        final int clickedBlockId = v.getId();
        for (BindedTheme bt : themes) {
            if (bt.layoutBlock.getId() != clickedBlockId) {
                bt.radioButton.setChecked(false);
            } else {
                bt.radioButton.setChecked(true);
                app.theme = bt.theme;
            }
        }
    }
}
