package com.handy.portal.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.ui.activity.OnboardingActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TerminalOnboardingFragment extends Fragment
{
    @Bind(R.id.body)
    ImageView body;
    @Bind(R.id.job_complete_image)
    ImageView jobCompleteImage;
    @Bind(R.id.bottom_tooltip)
    TextView tooltip;

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

        ButterKnife.bind(this, view);
        body.setImageResource(bodyDrawableId);
        jobCompleteImage.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade_and_grow_in));
        jobCompleteImage.setVisibility(View.VISIBLE);

        tooltip.setText(Html.fromHtml(getString(R.string.step_final)));

        return view;
    }

    @OnClick(R.id.start_claiming_button)
    public void showNextStep()
    {
        ((OnboardingActivity) getActivity()).showNextStep();
    }
}
