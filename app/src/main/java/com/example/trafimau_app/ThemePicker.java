package com.example.trafimau_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;

import java.util.ArrayList;
import java.util.List;

public class ThemePicker extends AppCompatActivity {

    final class BindedTheme {
        final RadioButton radioButton;
        final MyApplication.Theme theme;

        BindedTheme(RadioButton radioButton, MyApplication.Theme theme) {
            this.radioButton = radioButton;
            this.theme = theme;
        }
    }

    private MyApplication app;
    private List<BindedTheme> themes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme_picker);

        app = (MyApplication) getApplicationContext();

        final RadioButton lightThemeRB = findViewById(R.id.themePickerLightThemeRadioButton);
        final RadioButton darkThemeRB = findViewById(R.id.themePickerDarkThemeRadioButton);

        themes = new ArrayList<>();
        themes.add(new BindedTheme(lightThemeRB, MyApplication.Theme.LIGHT));
        themes.add(new BindedTheme(darkThemeRB, MyApplication.Theme.DARK));
        lightThemeRB.setOnClickListener(this::onRadioButtonClick);
        darkThemeRB.setOnClickListener(this::onRadioButtonClick);

        lightThemeRB.setChecked(true);

        findViewById(R.id.themePickerNextButton).setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), LayoutPicker.class);
            startActivity(intent);
        });
    }

    private void onRadioButtonClick(View v) {
        final int clickedRBId = v.getId();
        for (BindedTheme bt : themes) {
            if (bt.radioButton.getId() != clickedRBId) {
                bt.radioButton.setChecked(false);
            } else {
                app.theme = bt.theme;
            }
        }
    }
}
