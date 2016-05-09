package com.handy.portal.preactivation;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;

import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.model.onboarding.OnboardingSuppliesInfo;
import com.handy.portal.ui.activity.BaseActivity;
import com.handy.portal.ui.activity.SplashActivity;

public class PreActivationFlowActivity extends BaseActivity
{
    private OnboardingSuppliesInfo mOnboardingSuppliesInfo;

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mOnboardingSuppliesInfo = (OnboardingSuppliesInfo) getIntent()
                .getSerializableExtra(BundleKeys.ONBOARDING_SUPPLIES);
        setContentView(R.layout.activity_pre_activation_flow);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        goToFirstStep();
    }

    public void next(@Nullable final PreActivationFlowFragment fragment,
                     final boolean allowBackNavigation)
    {
        if (fragment != null)
        {
            final FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            if (allowBackNavigation)
            {
                fragmentTransaction.addToBackStack(null);
            }
            fragmentTransaction.setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
            );
            fragmentTransaction.replace(R.id.main_container, fragment);
            fragmentTransaction.commit();
        }
        else
        {
            final Intent intent = new Intent(this, SplashActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    private void goToFirstStep()
    {
        next(PurchaseSuppliesFragment.newInstance(mOnboardingSuppliesInfo), false);
    }
}
