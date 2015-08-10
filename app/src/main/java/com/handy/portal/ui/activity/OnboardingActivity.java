package com.handy.portal.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.handy.portal.R;
import com.handy.portal.manager.ProviderManager;
import com.handy.portal.ui.fragment.InitialOnboardingFragment;
import com.handy.portal.ui.fragment.OnboardingFragment;
import com.handy.portal.ui.fragment.TerminalOnboardingFragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.handy.portal.ui.fragment.OnboardingFragment.TooltipPlacement.BOTTOM;
import static com.handy.portal.ui.fragment.OnboardingFragment.TooltipPlacement.TOP;

public class OnboardingActivity extends BaseActivity
{
    @Inject
    ProviderManager providerManager;

    private List<Fragment> steps;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        ButterKnife.inject(this);

        initSteps();
        nextStep(null);
    }

    private void initSteps()
    {
        steps = new ArrayList<>();
        steps.add(InitialOnboardingFragment.newInstance(isUk() ? R.drawable.onboarding_0_uk : R.drawable.onboarding_0, providerManager.getCachedActiveProvider().getFirstName()));
        steps.add(OnboardingFragment.newInstance(isUk() ? R.drawable.onboarding_1_uk : R.drawable.onboarding_1, R.drawable.onboarding_menu_jobs).withTooltip(R.string.step_1, R.string.step_1_of_7, TOP));
        steps.add(OnboardingFragment.newInstance(isUk() ? R.drawable.onboarding_2_uk : R.drawable.onboarding_2, R.drawable.onboarding_menu_jobs).withTooltip(R.string.step_2, R.string.step_2_of_7, TOP));
        steps.add(OnboardingFragment.newInstance(isUk() ? R.drawable.onboarding_3_uk : R.drawable.onboarding_3, R.drawable.onboarding_menu_schedule).withTooltip(R.string.step_3, R.string.step_3_of_7, BOTTOM));
        steps.add(OnboardingFragment.newInstance(isUk() ? R.drawable.onboarding_4_uk : R.drawable.onboarding_4, R.drawable.onboarding_menu_schedule).withTooltip(R.string.step_4, R.string.step_4_of_7, TOP));
        steps.add(OnboardingFragment.newInstance(isUk() ? R.drawable.onboarding_5_uk : R.drawable.onboarding_5, R.drawable.onboarding_menu_schedule).withTooltip(R.string.step_5, R.string.step_5_of_7, TOP));
        steps.add(OnboardingFragment.newInstance(isUk() ? R.drawable.onboarding_6_uk : R.drawable.onboarding_6, R.drawable.onboarding_menu_schedule).withTooltip(R.string.step_6, R.string.step_6_of_7, TOP));
        steps.add(TerminalOnboardingFragment.newInstance(isUk() ? R.drawable.onboarding_7_uk : R.drawable.onboarding_7));
    }

    private boolean isUk()
    {
        return "GB".equals(providerManager.getCachedActiveProvider().getCountry());
    }

    public void nextStep(View view)
    {
        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
        if (backStackEntryCount < steps.size())
        {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (backStackEntryCount > 0)
            {
                transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
            }
            transaction.replace(R.id.fragment_container, steps.get(backStackEntryCount)).addToBackStack(null);
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
