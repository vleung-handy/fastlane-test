package com.handy.portal.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.handy.portal.R;
import com.handy.portal.constant.PrefsKey;
import com.handy.portal.manager.PrefsManager;
import com.handy.portal.manager.ProviderManager;
import com.handy.portal.model.Provider;
import com.handy.portal.ui.fragment.InitialOnboardingFragment;
import com.handy.portal.ui.fragment.OnboardingFragment;
import com.handy.portal.ui.fragment.TerminalOnboardingFragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;

import static com.handy.portal.ui.fragment.OnboardingFragment.TooltipPlacement.BOTTOM;
import static com.handy.portal.ui.fragment.OnboardingFragment.TooltipPlacement.TOP;

public class OnboardingActivity extends BaseActivity
{
    @Inject
    ProviderManager providerManager;
    @Inject
    PrefsManager prefsManager;

    private List<Fragment> steps;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        ButterKnife.inject(this);

        initSteps();
        nextStep();
    }

    private void initSteps()
    {
        Provider provider = providerManager.getCachedActiveProvider();

        steps = new ArrayList<>();
        steps.add(InitialOnboardingFragment.newInstance(provider.isUk() ? R.drawable.onboarding_0_uk : R.drawable.onboarding_0, providerManager.getCachedActiveProvider().getFirstName()));
        steps.add(OnboardingFragment.newInstance(provider.isUk() ? R.drawable.onboarding_1_uk : R.drawable.onboarding_1, R.drawable.onboarding_menu_jobs).withTooltip(R.string.step_1, R.string.step_1_of_7, TOP));
        steps.add(OnboardingFragment.newInstance(provider.isUk() ? R.drawable.onboarding_2_uk : R.drawable.onboarding_2, R.drawable.onboarding_menu_jobs).withTooltip(R.string.step_2, R.string.step_2_of_7, TOP));
        steps.add(OnboardingFragment.newInstance(provider.isUk() ? R.drawable.onboarding_3_uk : R.drawable.onboarding_3, R.drawable.onboarding_menu_schedule).withTooltip(R.string.step_3, R.string.step_3_of_7, BOTTOM));
        steps.add(OnboardingFragment.newInstance(provider.isUk() ? R.drawable.onboarding_4_uk : R.drawable.onboarding_4, R.drawable.onboarding_menu_schedule).withTooltip(R.string.step_4, R.string.step_4_of_7, TOP));
        steps.add(OnboardingFragment.newInstance(provider.isUk() ? R.drawable.onboarding_5_uk : R.drawable.onboarding_5, R.drawable.onboarding_menu_schedule).withTooltip(R.string.step_5, R.string.step_5_of_7, TOP));
        steps.add(OnboardingFragment.newInstance(provider.isUk() ? R.drawable.onboarding_6_uk : R.drawable.onboarding_6, R.drawable.onboarding_menu_schedule).withTooltip(R.string.step_6, R.string.step_6_of_7, TOP));
        steps.add(TerminalOnboardingFragment.newInstance(provider.isUk() ? R.drawable.onboarding_7_uk : R.drawable.onboarding_7));
    }

    public void nextStep()
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
            prefsManager.setBoolean(PrefsKey.ONBOARDING_COMPLETED, true);

            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
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
