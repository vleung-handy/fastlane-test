package com.handy.portal.onboarding.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;

import com.handy.portal.R;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.onboarding.model.OnboardingDetails;
import com.handy.portal.onboarding.model.subflow.SubflowData;
import com.handy.portal.onboarding.model.subflow.SubflowType;
import com.handy.portal.onboarding.model.supplies.SuppliesOrderInfo;
import com.handy.portal.onboarding.ui.fragment.OnboardingStatusFragment;
import com.handy.portal.onboarding.ui.fragment.OnboardingSubflowFragment;
import com.handy.portal.onboarding.ui.fragment.PurchaseSuppliesFragment;
import com.handy.portal.onboarding.ui.fragment.ScheduleConfirmationFragment;
import com.handy.portal.onboarding.ui.fragment.SchedulePreferencesFragment;
import com.handy.portal.ui.activity.BaseActivity;

import java.util.ArrayList;

public class OnboardingSubflowActivity extends BaseActivity
{
    private OnboardingDetails mOnboardingDetails;
    private SubflowType mSubflowType;

    public OnboardingDetails getOnboardingDetails()
    {
        return mOnboardingDetails;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mOnboardingDetails = (OnboardingDetails) getIntent()
                .getSerializableExtra(BundleKeys.ONBOARDING_DETAILS);
        mSubflowType = (SubflowType) getIntent().getSerializableExtra(BundleKeys.SUBFLOW_TYPE);
        setContentView(R.layout.activity_onboarding_subflow);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        startSubflow();
    }

    @SuppressWarnings("unchecked")
    private void startSubflow()
    {
        OnboardingSubflowFragment fragment = null;
        switch (mSubflowType)
        {
            case STATUS:
                fragment = OnboardingStatusFragment.newInstance();
                break;
            case CLAIM:
                fragment = SchedulePreferencesFragment.newInstance();
                break;
            case SUPPLIES:
                fragment = PurchaseSuppliesFragment.newInstance();
                break;
            case CONFIRMATION:
                final ArrayList<Booking> pendingBookings =
                        (ArrayList<Booking>) getIntent().getSerializableExtra(BundleKeys.BOOKINGS);
                final SuppliesOrderInfo suppliesOrderInfo = (SuppliesOrderInfo) getIntent()
                        .getSerializableExtra(BundleKeys.SUPPLIES_ORDER_INFO);
                fragment = ScheduleConfirmationFragment.newInstance(pendingBookings,
                        suppliesOrderInfo);
                break;
            default:
                break;
        }
        if (fragment != null)
        {
            next(fragment, false);
        }
    }

    public void next(@NonNull final OnboardingSubflowFragment fragment,
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

        Bundle arguments = fragment.getArguments();
        if (arguments == null)
        {
            arguments = new Bundle();
        }
        final SubflowData subflowData =
                mOnboardingDetails.getSubflowDataByType(mSubflowType);
        arguments.putSerializable(BundleKeys.SUBFLOW_DATA, subflowData);
        fragment.setArguments(arguments);

        fragmentTransaction.setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
        );
        fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.commit();
    }

    public void terminate(@NonNull Intent data)
    {
        if (!isFinishing())
        {
            setResult(Activity.RESULT_OK, data);
            finish();
        }
    }

    @Override
    public void onBackPressed()
    {
        if (mSubflowType == SubflowType.STATUS)
        {
            final Intent data = new Intent();
            data.putExtra(BundleKeys.FORCE_FINISH, true);
            cancel(data);
        }
        else
        {
            super.onBackPressed();
        }
    }

    public void cancel(@NonNull final Intent data)
    {
        if (!isFinishing())
        {
            setResult(Activity.RESULT_CANCELED, data);
            finish();
        }
    }
}
