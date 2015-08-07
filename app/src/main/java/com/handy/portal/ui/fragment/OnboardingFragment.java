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

import butterknife.ButterKnife;
import butterknife.InjectView;

public class OnboardingFragment extends Fragment
{
    @InjectView(R.id.body)
    ImageView body;
    @InjectView(R.id.footer)
    ImageView footer;
    @InjectView(R.id.tooltip_top)
    TextView topTooltip;
    @InjectView(R.id.tooltip_bottom)
    TextView bottomTooltip;

    private OnboardingActivity.Step step;

    public static OnboardingFragment newInstance(OnboardingActivity.Step step)
    {
        OnboardingFragment onboardingFragment = new OnboardingFragment();
        onboardingFragment.step = step;
        return onboardingFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup fragmentViewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_onboarding, container, false);

        ButterKnife.inject(this, fragmentViewGroup);
        body.setImageResource(step.getBodyDrawableId());
        footer.setImageResource(step.getFooterDrawableId());

        initTooltip(topTooltip, step.getTopTooltipStringId());
        initTooltip(bottomTooltip, step.getBottomTooltipStringId());

        int overlayLayout = step.getOverlayLayout();
        if (overlayLayout != -1)
        {
            inflater.inflate(overlayLayout, fragmentViewGroup, true);
        }

        return fragmentViewGroup;
    }

    private void initTooltip(TextView tooltip, int tooltipStringId)
    {
        if (tooltipStringId != -1)
        {
            tooltip.setText(Html.fromHtml(getString(tooltipStringId)));
            tooltip.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade_and_grow_in));
            tooltip.setVisibility(View.VISIBLE);
        }
    }
}
