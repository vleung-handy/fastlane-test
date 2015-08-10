package com.handy.portal.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.ui.activity.OnboardingActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class InitialOnboardingFragment extends Fragment
{
    @InjectView(R.id.body)
    ImageView body;
    @InjectView(R.id.welcome_text)
    TextView welcomeText;

    private int bodyDrawableId;
    private String providerName;

    public static InitialOnboardingFragment newInstance(int bodyDrawableId, String providerName)
    {
        InitialOnboardingFragment initialOnboardingFragment = new InitialOnboardingFragment();
        initialOnboardingFragment.bodyDrawableId = bodyDrawableId;
        initialOnboardingFragment.providerName = providerName;
        return initialOnboardingFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_onboarding_initial, container, false);

        ButterKnife.inject(this, view);
        body.setImageResource(bodyDrawableId);
        welcomeText.setText(String.format(getString(R.string.welcome), providerName));

        return view;
    }

    @OnClick(R.id.get_started_button)
    public void nextStep()
    {
        ((OnboardingActivity) getActivity()).nextStep();
    }
}
