package com.handy.portal.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.handy.portal.R;
import com.handy.portal.ui.fragment.InitialOnboardingFragment;
import com.handy.portal.ui.fragment.OnboardingFragment;
import com.handy.portal.ui.fragment.TerminalOnboardingFragment;

import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.handy.portal.ui.fragment.OnboardingFragment.TooltipPlacement.*;

public class OnboardingActivity extends FragmentActivity
{
    private static final Fragment[] STEPS = {
            InitialOnboardingFragment.newInstance(R.drawable.onboarding_0, "Carl"),
            OnboardingFragment.newInstance(R.drawable.onboarding_1, R.drawable.onboarding_menu_jobs).withTooltip(R.string.step_1, TOP),
            OnboardingFragment.newInstance(R.drawable.onboarding_2, R.drawable.onboarding_menu_jobs).withTooltip(R.string.step_2, TOP),
            OnboardingFragment.newInstance(R.drawable.onboarding_3, R.drawable.onboarding_menu_schedule).withTooltip(R.string.step_3, BOTTOM),
            OnboardingFragment.newInstance(R.drawable.onboarding_4, R.drawable.onboarding_menu_schedule).withTooltip(R.string.step_4, TOP),
            OnboardingFragment.newInstance(R.drawable.onboarding_5, R.drawable.onboarding_menu_schedule).withTooltip(R.string.step_5, TOP),
            OnboardingFragment.newInstance(R.drawable.onboarding_6, R.drawable.onboarding_menu_schedule).withTooltip(R.string.step_6, TOP),
            TerminalOnboardingFragment.newInstance(R.drawable.onboarding_7),
    };

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
            transaction.replace(R.id.fragment_container, STEPS[backStackEntryCount]).addToBackStack(null);
            transaction.commit();
        }
        else
        {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
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
