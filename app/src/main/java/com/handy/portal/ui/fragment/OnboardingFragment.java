package com.handy.portal.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.handy.portal.R;
import com.handy.portal.ui.activity.OnboardingActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class OnboardingFragment extends Fragment
{
    @InjectView(R.id.body)
    ImageView body;
    @InjectView(R.id.footer)
    ImageView footer;

    private OnboardingActivity.Step step;
    private OnNextStepListener onNextStepListener;

    public static OnboardingFragment newInstance(OnboardingActivity.Step step, OnNextStepListener onNextStepListener)
    {
        OnboardingFragment onboardingFragment = new OnboardingFragment();
        onboardingFragment.onNextStepListener = onNextStepListener;
        onboardingFragment.step = step;
        return onboardingFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_onboarding, container, false);

        ButterKnife.inject(this, view);
        body.setImageResource(step.getBodyDrawableId());
        body.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                onNextStepListener.nextStep();
            }
        });
        footer.setImageResource(step.getFooterDrawableId());

        return view;
    }

    public interface OnNextStepListener
    {
        void nextStep();
    }
}
