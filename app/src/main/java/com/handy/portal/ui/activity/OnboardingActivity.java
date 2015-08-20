package com.handy.portal.ui.activity;

import android.content.Intent;
import android.content.res.Configuration;
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
import java.util.Locale;

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
        showNextStep();
    }

    private void initSteps()
    {
        Provider provider = providerManager.getCachedActiveProvider();
        if (provider.isUk())
        {
            Configuration configuration = new Configuration();
            configuration.locale = Locale.UK;
            getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
        }

        steps = new ArrayList<>();
        steps.add(InitialOnboardingFragment.newInstance(R.drawable.onboarding_0, provider.getFirstName()));
        steps.add(OnboardingFragment.newInstance(R.drawable.onboarding_1, R.drawable.onboarding_menu_jobs).withTooltip(R.string.step_1, R.string.step_1_of_9, TOP));
        steps.add(OnboardingFragment.newInstance(R.drawable.onboarding_2, R.drawable.onboarding_menu_jobs).withTooltip(R.string.step_2, R.string.step_2_of_9, TOP));
        steps.add(OnboardingFragment.newInstance(R.drawable.onboarding_3, R.drawable.onboarding_menu_schedule).withTooltip(R.string.step_3, R.string.step_3_of_9, BOTTOM));
        steps.add(OnboardingFragment.newInstance(R.drawable.onboarding_4, R.drawable.onboarding_menu_schedule).withTooltip(R.string.step_4, R.string.step_4_of_9, TOP));
        steps.add(OnboardingFragment.newInstance(R.drawable.onboarding_5, R.drawable.onboarding_menu_schedule).withTooltip(R.string.step_5, R.string.step_5_of_9, TOP));
        steps.add(OnboardingFragment.newInstance(R.drawable.onboarding_6, R.drawable.onboarding_menu_schedule).withTooltip(R.string.step_6, R.string.step_6_of_9, TOP));
        steps.add(OnboardingFragment.newInstance(R.drawable.onboarding_7, R.drawable.onboarding_help_menu).withTooltip(R.string.step_7, R.string.step_7_of_9, TOP));
        steps.add(OnboardingFragment.newInstance(R.drawable.onboarding_8, R.drawable.onboarding_menu_schedule).withTooltip(R.string.step_8, R.string.step_8_of_9, TOP));
        steps.add(TerminalOnboardingFragment.newInstance(R.drawable.onboarding_final));
    }

    public void showNextStep()
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
            prefsManager.setBoolean(PrefsKey.ONBOARDING_NEEDED, false);

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
