package com.handy.portal.onboarding.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.handy.portal.R;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.RequestCode;
import com.handy.portal.flow.Flow;
import com.handy.portal.onboarding.OnboardingFlowStep;
import com.handy.portal.onboarding.SubflowLauncher;
import com.handy.portal.onboarding.model.OnboardingDetails;
import com.handy.portal.onboarding.model.subflow.OnboardingSubflowDetails;
import com.handy.portal.onboarding.model.subflow.SubflowStatus;
import com.handy.portal.onboarding.model.subflow.SubflowType;
import com.handy.portal.onboarding.model.supplies.SuppliesOrderInfo;
import com.handy.portal.ui.activity.BaseActivity;
import com.handy.portal.ui.activity.SplashActivity;

import java.util.ArrayList;

public class OnboardingFlowActivity extends BaseActivity implements SubflowLauncher
{
    private OnboardingDetails mOnboardingDetails;
    private ArrayList<Booking> mPendingBookings;
    private SuppliesOrderInfo mSuppliesOrderInfo;
    private Flow mOnboardingFlow;

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
        setContentView(R.layout.activity_onboarding_flow);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        startOnboardingFlow();
    }

    private void startOnboardingFlow()
    {
        mOnboardingFlow = new Flow();
        final ArrayList<OnboardingSubflowDetails> incompleteSubflows =
                mOnboardingDetails.getSubflowsByStatus(SubflowStatus.INCOMPLETE);
        for (final OnboardingSubflowDetails subflow : incompleteSubflows)
        {
            if (subflow.getType() != null)
            {
                mOnboardingFlow.addStep(new OnboardingFlowStep(this, subflow.getType()));
            }
        }
        mOnboardingFlow.start();
    }

    @Override
    public void launchSubflow(final SubflowType subflowType)
    {
        final Intent intent = new Intent(this, OnboardingSubflowActivity.class);
        intent.putExtra(BundleKeys.ONBOARDING_DETAILS, mOnboardingDetails);
        intent.putExtra(BundleKeys.SUBFLOW_TYPE, subflowType);
        if (mPendingBookings != null)
        {
            intent.putExtra(BundleKeys.BOOKINGS, mPendingBookings);
        }
        if (mSuppliesOrderInfo != null)
        {
            intent.putExtra(BundleKeys.SUPPLIES_ORDER_INFO, mSuppliesOrderInfo);
        }
        startActivityForResult(intent, RequestCode.ONBOARDING_SUBFLOW);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCode.ONBOARDING_SUBFLOW)
        {
            switch (resultCode)
            {
                case Activity.RESULT_OK:
                    savePendingBookingsIfAvailable(data);
                    saveSuppliesOrderInfoIfAvailable(data);
                    mOnboardingFlow.goForward();
                    break;
                case Activity.RESULT_CANCELED:
                    startActivity(new Intent(this, SplashActivity.class));
                    finish();
                    break;
                default:
                    break;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void savePendingBookingsIfAvailable(final Intent data)
    {
        final ArrayList<Booking> pendingBookings =
                (ArrayList<Booking>) data.getSerializableExtra(BundleKeys.BOOKINGS);
        if (pendingBookings != null)
        {
            mPendingBookings = pendingBookings;
        }
    }

    private void saveSuppliesOrderInfoIfAvailable(final Intent data)
    {
        final SuppliesOrderInfo suppliesOrderInfo =
                (SuppliesOrderInfo) data.getSerializableExtra(BundleKeys.SUPPLIES_ORDER_INFO);
        if (suppliesOrderInfo != null)
        {
            mSuppliesOrderInfo = suppliesOrderInfo;
        }
    }
}
