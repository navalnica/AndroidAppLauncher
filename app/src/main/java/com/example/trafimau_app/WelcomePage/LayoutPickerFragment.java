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

public class LayoutPickerFragment extends Fragment {

    private OnContinueButtonClickListener continueButtonClickListener;
    private MyApplication app;
    private List<BindedLayout> layouts;

    final class BindedLayout {
        final View layoutBlock;
        final RadioButton radioButton;
        final MyApplication.Layout layout;

        BindedLayout(View layoutBlock, RadioButton radioButton, MyApplication.Layout layout) {
            this.layoutBlock = layoutBlock;
            this.radioButton = radioButton;
            this.layout = layout;
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

        View rootView = inflater.inflate(R.layout.fragment_welcome_layout_picker, container, false);

        final RadioButton standardLayoutRB = rootView.findViewById(R.id.layoutPickerStandardRadioButton);
        final View standardLayoutBlock = rootView.findViewById(R.id.layoutPickerStandardBlock);
        final RadioButton compactLayoutRB = rootView.findViewById(R.id.layoutPickerCompactRadioButton);
        final View compactLayoutBlock = rootView.findViewById(R.id.layoutPickerCompactBlock);

        layouts = new ArrayList<>();
        layouts.add(new BindedLayout(standardLayoutBlock, standardLayoutRB, MyApplication.Layout.STANDARD));
        layouts.add(new BindedLayout(compactLayoutBlock, compactLayoutRB, MyApplication.Layout.COMPACT));
        standardLayoutBlock.setOnClickListener(this::onRadioButtonBlockClick);
        compactLayoutBlock.setOnClickListener(this::onRadioButtonBlockClick);

        standardLayoutRB.setChecked(true);

        rootView.findViewById(R.id.layoutPickerContinueButton).setOnClickListener(
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
        for (BindedLayout bl : layouts) {
            if (bl.layoutBlock.getId() != clickedBlockId) {
                bl.radioButton.setChecked(false);
            } else {
                bl.radioButton.setChecked(true);
                app.layout = bl.layout;
            }
        }
    }
}
