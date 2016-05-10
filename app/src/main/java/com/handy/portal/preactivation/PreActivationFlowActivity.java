package com.handy.portal.preactivation;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;

import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.model.onboarding.OnboardingSuppliesInfo;
import com.handy.portal.ui.activity.BaseActivity;
import com.handy.portal.ui.activity.MainActivity;

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

    public void next(@NonNull final PreActivationFlowFragment fragment,
                     final boolean allowBackNavigation)
    {
        final FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        if (allowBackNavigation)
        {
            final Bundle arguments = fragment.getArguments();
            arguments.putBoolean(BundleKeys.ALLOW_BACK_NAVIGATION, true);
            fragment.setArguments(arguments);
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

    public void terminate()
    {
        if (!isFinishing())
        {
            final Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }

    private void goToFirstStep()
    {
        next(PurchaseSuppliesFragment.newInstance(mOnboardingSuppliesInfo), false);
    }
}
