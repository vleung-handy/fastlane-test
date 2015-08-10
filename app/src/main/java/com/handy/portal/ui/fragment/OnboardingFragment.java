package com.handy.portal.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
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

    private int tooltipStringId;
    private TooltipPlacement tooltipPlacement;
    private int bodyDrawableId;
    private int footerDrawableId;

    public static OnboardingFragment newInstance(int bodyDrawableId, int footerDrawableId)
    {
        OnboardingFragment onboardingFragment = new OnboardingFragment();
        onboardingFragment.bodyDrawableId = bodyDrawableId;
        onboardingFragment.footerDrawableId = footerDrawableId;
        onboardingFragment.tooltipStringId = -1;
        return onboardingFragment;
    }

    public enum TooltipPlacement
    {
        TOP, BOTTOM
    }

    public OnboardingFragment withTooltip(int tooltipStringId, @NonNull TooltipPlacement tooltipPlacement)
    {
        this.tooltipStringId = tooltipStringId;
        this.tooltipPlacement = tooltipPlacement;
        return this;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_onboarding, container, false);

        ButterKnife.inject(this, view);
        body.setImageResource(bodyDrawableId);
        footer.setImageResource(footerDrawableId);

        if (tooltipPlacement == TooltipPlacement.TOP)
        {
            initTooltip(topTooltip, tooltipStringId);
        }
        if (tooltipPlacement == TooltipPlacement.BOTTOM)
        {
            initTooltip(bottomTooltip, tooltipStringId);
        }

        return view;
    }

    private void initTooltip(TextView tooltip, int tooltipStringId)
    {
        tooltip.setText(Html.fromHtml(getString(tooltipStringId)));
        tooltip.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade_and_grow_in));
        tooltip.setVisibility(View.VISIBLE);
    }
}
