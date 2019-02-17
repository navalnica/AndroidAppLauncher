package com.example.trafimau_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

import java.util.ArrayList;
import java.util.List;

public class LayoutPicker extends AppCompatActivity {

    final class BindedLayout {
        final RadioButton radioButton;
        final MyApplication.Layout layout;

        BindedLayout(RadioButton radioButton, MyApplication.Layout layout) {
            this.radioButton = radioButton;
            this.layout = layout;
        }
    }

    private MyApplication app;
    private List<BindedLayout> layouts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout_picker);

        app = (MyApplication) getApplicationContext();

        final RadioButton standardLayoutRB = findViewById(R.id.layoutPickerStandardRadioButton);
        final RadioButton compactLayoutRB = findViewById(R.id.layoutPickerCompactRadioButton);

        layouts = new ArrayList<>();
        layouts.add(new BindedLayout(standardLayoutRB, MyApplication.Layout.STANDARD));
        layouts.add(new BindedLayout(compactLayoutRB, MyApplication.Layout.COMPACT));
        standardLayoutRB.setOnClickListener(this::onRadioButtonClick);
        compactLayoutRB.setOnClickListener(this::onRadioButtonClick);

        standardLayoutRB.setChecked(true);

        findViewById(R.id.layoutPickerNextButton).setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), LauncherActivity.class);
            startActivity(intent);
//                finish();
        });
    }

    private void onRadioButtonClick(View v) {
        final int clickedRBId = v.getId();
        for (BindedLayout bl : layouts) {
            if (bl.radioButton.getId() != clickedRBId) {
                bl.radioButton.setChecked(false);
            } else {
                app.layout = bl.layout;
            }
        }
    }
}
