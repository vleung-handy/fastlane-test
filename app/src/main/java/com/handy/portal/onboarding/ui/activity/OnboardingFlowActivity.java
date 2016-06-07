package com.handy.portal.onboarding.ui.activity;

import android.content.Intent;
import android.os.Bundle;

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
import java.util.List;

public class OnboardingFlowActivity extends BaseActivity implements SubflowLauncher
{
    private OnboardingDetails mOnboardingDetails;
    private ArrayList<Booking> mPendingBookings;
    private SuppliesOrderInfo mSuppliesOrderInfo;
    private Flow mOnboardingFlow;
    private ArrayList<OnboardingSubflowDetails> mIncompleteSubflows;
    private SubflowType mLastLaunchedSubflowType;

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
        mIncompleteSubflows = mOnboardingDetails.getSubflowsByStatus(SubflowStatus.INCOMPLETE);
        restoreOnboardingFlow(savedInstanceState);
    }

    @SuppressWarnings("unchecked")
    private void restoreOnboardingFlow(final Bundle savedInstanceState)
    {
        if (savedInstanceState != null)
        {
            final ArrayList<OnboardingSubflowDetails> incompleteSubflows =
                    (ArrayList<OnboardingSubflowDetails>) savedInstanceState
                            .getSerializable(BundleKeys.SUBFLOWS);
            final SubflowType lastLaunchedSubflowType = (SubflowType) savedInstanceState
                    .getSerializable(BundleKeys.SUBFLOW_TYPE);
            if (incompleteSubflows != null && lastLaunchedSubflowType != null)
            {
                removeLaunchedSubflows(incompleteSubflows, lastLaunchedSubflowType);
                if (incompleteSubflows.isEmpty())
                {
                    finishOnboardingFlow(true);
                }
                else
                {
                    mIncompleteSubflows = incompleteSubflows;
                    mLastLaunchedSubflowType = lastLaunchedSubflowType;
                    final Intent data = new Intent();
                    data.putExtras(savedInstanceState);
                    savePendingBookingsIfAvailable(data);
                    saveSuppliesOrderInfoIfAvailable(data);
                    initOnboardingFlow();
                }
            }
        }
    }

    private void removeLaunchedSubflows(final List<OnboardingSubflowDetails> incompleteSubflows,
                                        final SubflowType lastLaunchedSubflowType)
    {
        if (!incompleteSubflows.isEmpty())
        {
            OnboardingSubflowDetails removedSubflow;
            do
            {
                removedSubflow = incompleteSubflows.remove(0);
            }
            while (!incompleteSubflows.isEmpty()
                    && removedSubflow.getType() != lastLaunchedSubflowType);
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCode.ONBOARDING_SUBFLOW)
        {
            switch (resultCode)
            {
                case RESULT_OK:
                    savePendingBookingsIfAvailable(data);
                    saveSuppliesOrderInfoIfAvailable(data);
                    mOnboardingFlow.goForward();
                    if (mOnboardingFlow.isComplete())
                    {
                        finishOnboardingFlow(true);
                    }
                    break;
                case RESULT_CANCELED:
                    boolean shouldLaunchSplash = true;
                    if (data != null)
                    {
                        shouldLaunchSplash = !data.getBooleanExtra(BundleKeys.FORCE_FINISH, false);
                    }
                    finishOnboardingFlow(shouldLaunchSplash);
                    break;
                default:
                    break;
            }
        }
    }

    private void finishOnboardingFlow(final boolean shouldLaunchSplash)
    {
        if (shouldLaunchSplash)
        {
            startActivity(new Intent(this, SplashActivity.class));
        }
        finish();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (mOnboardingFlow == null)
        {
            initOnboardingFlow();
            mOnboardingFlow.start();
        }
    }

    private void initOnboardingFlow()
    {
        mOnboardingFlow = new Flow();
        for (final OnboardingSubflowDetails subflow : mIncompleteSubflows)
        {
            if (subflow.getType() != null)
            {
                mOnboardingFlow.addStep(new OnboardingFlowStep(this, subflow.getType()));
            }
        }
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
        mLastLaunchedSubflowType = subflowType;
        startActivityForResult(intent, RequestCode.ONBOARDING_SUBFLOW);
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        if (outState == null)
        {
            outState = new Bundle();
        }
        outState.putSerializable(BundleKeys.SUBFLOWS, mIncompleteSubflows);
        outState.putSerializable(BundleKeys.SUBFLOW_TYPE, mLastLaunchedSubflowType);
        outState.putSerializable(BundleKeys.BOOKINGS, mPendingBookings);
        outState.putSerializable(BundleKeys.SUPPLIES_ORDER_INFO, mSuppliesOrderInfo);
        super.onSaveInstanceState(outState);
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
