package com.handy.portal.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.handy.portal.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class TerminalOnboardingFragment extends Fragment
{
    @InjectView(R.id.body)
    ImageView body;
    @InjectView(R.id.job_complete_image)
    ImageView jobCompleteImage;

    private int bodyDrawableId;

    public static TerminalOnboardingFragment newInstance(int bodyDrawableId)
    {
        TerminalOnboardingFragment terminalOnboardingFragment = new TerminalOnboardingFragment();
        terminalOnboardingFragment.bodyDrawableId = bodyDrawableId;
        return terminalOnboardingFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_onboarding_terminal, container, false);

        ButterKnife.inject(this, view);
        body.setImageResource(bodyDrawableId);
        jobCompleteImage.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade_and_grow_in));
        jobCompleteImage.setVisibility(View.VISIBLE);

        return view;
    }
}
