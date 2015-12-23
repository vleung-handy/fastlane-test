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

import com.handy.portal.R;
import com.handy.portal.ui.activity.OnboardingActivity;
import com.handy.portal.ui.view.TooltipView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OnboardingFragment extends Fragment
{
    @Bind(R.id.body)
    ImageView body;
    @Bind(R.id.footer)
    ImageView footer;
    @Bind(R.id.tooltip_top)
    ViewGroup topTooltipContainer;
    @Bind(R.id.tooltip_bottom)
    ViewGroup bottomTooltipContainer;

    private int tooltipTextId;
    private int tooltipSubtextId;
    private TooltipPlacement tooltipPlacement;
    private int bodyDrawableId;
    private int footerDrawableId;

    public static OnboardingFragment newInstance(int bodyDrawableId, int footerDrawableId)
    {
        OnboardingFragment onboardingFragment = new OnboardingFragment();
        onboardingFragment.bodyDrawableId = bodyDrawableId;
        onboardingFragment.footerDrawableId = footerDrawableId;
        return onboardingFragment;
    }

    public enum TooltipPlacement
    {
        TOP, BOTTOM
    }

    public OnboardingFragment withTooltip(int tooltipTextId, int tooltipSubtextId, TooltipPlacement tooltipPlacement)
    {
        this.tooltipTextId = tooltipTextId;
        this.tooltipSubtextId = tooltipSubtextId;
        this.tooltipPlacement = tooltipPlacement;
        return this;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_onboarding, container, false);

        ButterKnife.bind(this, view);
        body.setImageResource(bodyDrawableId);
        footer.setImageResource(footerDrawableId);

        if (tooltipPlacement == TooltipPlacement.TOP)
        {
            initTooltip(topTooltipContainer);
        }
        if (tooltipPlacement == TooltipPlacement.BOTTOM)
        {
            initTooltip(bottomTooltipContainer);
        }

        return view;
    }

    @OnClick(R.id.body)
    public void showNextStep()
    {
        ((OnboardingActivity) getActivity()).showNextStep();
    }

    private void initTooltip(ViewGroup tooltipContainer)
    {
        TooltipView tooltip = (TooltipView) tooltipContainer.findViewById(R.id.tooltip);

        tooltip.setContent(Html.fromHtml(getString(tooltipTextId)), getString(tooltipSubtextId));

        tooltipContainer.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade_and_grow_in));
        tooltipContainer.setVisibility(View.VISIBLE);
    }
}
