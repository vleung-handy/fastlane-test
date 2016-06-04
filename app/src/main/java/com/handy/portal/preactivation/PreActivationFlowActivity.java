package com.handy.portal.preactivation;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;

import com.handy.portal.R;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.onboarding.model.OnboardingDetails;
import com.handy.portal.onboarding.model.subflow.SubflowType;
import com.handy.portal.ui.activity.BaseActivity;
import com.handy.portal.ui.activity.MainActivity;

import java.util.List;

public class PreActivationFlowActivity extends BaseActivity
{
    public OnboardingDetails mOnboardingDetails;
    private List<Booking> mPendingBookings;

    @Override
    protected boolean shouldTriggerSetup()
    {
        return true;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mOnboardingDetails = (OnboardingDetails) getIntent()
                .getSerializableExtra(BundleKeys.ONBOARDING_DETAILS);
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
            Bundle arguments = fragment.getArguments();
            if (arguments == null)
            {
                arguments = new Bundle();
            }
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
        next(OnboardingStatusFragment.newInstance(
                mOnboardingDetails.getSubflowByType(SubflowType.STATUS)), false);
    }

    @NonNull
    public List<Booking> getPendingBookings()
    {
        return mPendingBookings;
    }

    public void setPendingBookings(@NonNull final List<Booking> pendingBookings)
    {
        mPendingBookings = pendingBookings;
    }
}
