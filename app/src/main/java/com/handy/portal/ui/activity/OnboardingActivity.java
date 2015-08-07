package com.handy.portal.ui.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.handy.portal.R;
import com.handy.portal.ui.fragment.OnboardingFragment;

import butterknife.ButterKnife;

public class OnboardingActivity extends FragmentActivity implements OnboardingFragment.OnNextStepListener
{
    private static final Step[] STEPS = {
            new Step(R.drawable.onboarding_0, R.drawable.onboarding_menu_blur),
            new Step(R.drawable.onboarding_1, R.drawable.onboarding_menu_jobs),
            new Step(R.drawable.onboarding_2, R.drawable.onboarding_menu_jobs),
            new Step(R.drawable.onboarding_3, R.drawable.onboarding_menu_schedule),
            new Step(R.drawable.onboarding_4, R.drawable.onboarding_menu_schedule),
            new Step(R.drawable.onboarding_5, R.drawable.onboarding_menu_schedule),
            new Step(R.drawable.onboarding_6, R.drawable.onboarding_menu_schedule),
            new Step(R.drawable.onboarding_7, R.drawable.onboarding_menu_blur),
    };

    public static class Step
    {
        private int bodyDrawableId;
        private int footerDrawableId;

        public Step(int bodyDrawableId, int footerDrawableId)
        {
            this.bodyDrawableId = bodyDrawableId;
            this.footerDrawableId = footerDrawableId;
        }

        public int getBodyDrawableId()
        {
            return bodyDrawableId;
        }

        public int getFooterDrawableId()
        {
            return footerDrawableId;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        ButterKnife.inject(this);

        nextStep();
    }

    public void nextStep()
    {
        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
        if (backStackEntryCount < STEPS.length)
        {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, OnboardingFragment.newInstance(STEPS[backStackEntryCount], this))
                    .addToBackStack(null)
                    .commit();
        }
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
