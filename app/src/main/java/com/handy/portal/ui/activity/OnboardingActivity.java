package com.handy.portal.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.handy.portal.R;
import com.handy.portal.ui.fragment.OnboardingFragment;

import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class OnboardingActivity extends FragmentActivity
{
    private static final Step[] STEPS = {
            new Step(R.drawable.onboarding_0, R.drawable.onboarding_menu_blur, -1, -1, R.layout.onboarding_overlay_initial),
            new Step(R.drawable.onboarding_1, R.drawable.onboarding_menu_jobs, R.string.step_1, -1, -1),
            new Step(R.drawable.onboarding_2, R.drawable.onboarding_menu_jobs, R.string.step_2, -1, -1),
            new Step(R.drawable.onboarding_3, R.drawable.onboarding_menu_schedule, -1, R.string.step_3, -1),
            new Step(R.drawable.onboarding_4, R.drawable.onboarding_menu_schedule, R.string.step_4, -1, -1),
            new Step(R.drawable.onboarding_5, R.drawable.onboarding_menu_schedule, R.string.step_5, -1, -1),
            new Step(R.drawable.onboarding_6, R.drawable.onboarding_menu_schedule, R.string.step_6, -1, -1),
            new Step(R.drawable.onboarding_7, R.drawable.onboarding_menu_blur, -1, -1, R.layout.onboarding_overlay_terminal),
    };

    public static class Step
    {
        private int bodyDrawableId;
        private int footerDrawableId;
        private final int topTooltipStringId;
        private final int bottomTooltipStringId;
        private int overlayLayout;

        public Step(int bodyDrawableId, int footerDrawableId, int topTooltipStringId, int bottomTooltipStringId, int overlayLayout)
        {
            this.bodyDrawableId = bodyDrawableId;
            this.footerDrawableId = footerDrawableId;
            this.topTooltipStringId = topTooltipStringId;
            this.bottomTooltipStringId = bottomTooltipStringId;
            this.overlayLayout = overlayLayout;
        }

        public int getBodyDrawableId()
        {
            return bodyDrawableId;
        }

        public int getFooterDrawableId()
        {
            return footerDrawableId;
        }

        public int getTopTooltipStringId()
        {
            return topTooltipStringId;
        }

        public int getBottomTooltipStringId()
        {
            return bottomTooltipStringId;
        }

        public int getOverlayLayout()
        {
            return overlayLayout;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        ButterKnife.inject(this);

        nextStep(null);
    }

    public void nextStep(View view)
    {
        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
        if (backStackEntryCount < STEPS.length)
        {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (backStackEntryCount > 0)
            {
                transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
            }
            transaction.replace(R.id.fragment_container, OnboardingFragment.newInstance(STEPS[backStackEntryCount])).addToBackStack(null);
            transaction.commit();
        }
        else
        {
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    @Override
    protected void attachBaseContext(Context newBase)
    {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onBackPressed()
    {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1)
        {
            super.onBackPressed();
        }
        else
        {
            moveTaskToBack(true);
        }
    }
}
