package com.example.trafimau_app.WelcomePage;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.trafimau_app.R;

public class GreetingFragment extends Fragment {

    private OnContinueButtonClickListener continueButtonClickListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_welcome_greeting, container, false);
        rootView.findViewById(R.id.welcomeStartContinueButton).setOnClickListener(
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
                    OnContinueButtonClickListener.getErrorMessage(context.toString()));
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        continueButtonClickListener = null;
    }
}
