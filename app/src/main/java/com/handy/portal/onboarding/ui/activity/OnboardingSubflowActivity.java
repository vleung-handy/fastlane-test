package com.handy.portal.onboarding.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.onboarding.model.OnboardingDetails;
import com.handy.portal.onboarding.model.subflow.SubflowData;
import com.handy.portal.onboarding.model.subflow.SubflowType;
import com.handy.portal.onboarding.model.supplies.SuppliesOrderInfo;
import com.handy.portal.onboarding.ui.fragment.IDVerificationFragment;
import com.handy.portal.onboarding.ui.fragment.OnboardingStatusFragment;
import com.handy.portal.onboarding.ui.fragment.OnboardingSubflowFragment;
import com.handy.portal.onboarding.ui.fragment.PurchaseSuppliesFragment;
import com.handy.portal.onboarding.ui.fragment.ScheduleConfirmationFragment;
import com.handy.portal.onboarding.ui.fragment.SchedulePreferencesFragment;
import com.handy.portal.ui.activity.BaseActivity;

import java.util.ArrayList;

public class OnboardingSubflowActivity extends BaseActivity
{
    private static final String EXTRA_INITIAL_LOAD = "initial_load";
    private static final String EXTRA_LAUNCHED_TIME_MILLIS = "extra_launched_time_millis";
    private OnboardingDetails mOnboardingDetails;
    private SubflowType mSubflowType;
    private long mLaunchedTimeMillis;
    private boolean mIsSingleStepMode;
    private int mPercentComplete;
    private int mPercentRange;
    private int mSubflowStepCount;

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
        mIsSingleStepMode = getIntent().getBooleanExtra(BundleKeys.IS_SINGLE_STEP_MODE, false);
        mPercentComplete = getIntent().getIntExtra(BundleKeys.PERCENT_COMPLETE, 0);
        mPercentRange = getIntent().getIntExtra(BundleKeys.PERCENT_RANGE, 0);
        setContentView(R.layout.activity_onboarding_subflow);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        initLaunchedTimeMillis(savedInstanceState);
        if (savedInstanceState == null || savedInstanceState.getBoolean(EXTRA_INITIAL_LOAD, true))
        {
            startSubflow();
        }
    }

    private void initLaunchedTimeMillis(final Bundle savedInstanceState)
    {
        if (savedInstanceState == null
                || savedInstanceState.getInt(EXTRA_LAUNCHED_TIME_MILLIS, 0) == 0)
        {
            mLaunchedTimeMillis = System.currentTimeMillis();
        }
        else
        {
            mLaunchedTimeMillis = savedInstanceState.getInt(EXTRA_LAUNCHED_TIME_MILLIS);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        if (outState == null)
        {
            outState = new Bundle();
        }
        outState.putBoolean(EXTRA_INITIAL_LOAD, false);
        outState.putLong(EXTRA_LAUNCHED_TIME_MILLIS, mLaunchedTimeMillis);
        try
        {
            super.onSaveInstanceState(outState);
        }
        catch (IllegalArgumentException e)
        {
            // Non fatal
            Crashlytics.logException(e);
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        final int onboardingTtlMillis =
                getResources().getInteger(R.integer.onboarding_ttl_mins) * 60 * 1000;
        if (System.currentTimeMillis() - mLaunchedTimeMillis >= onboardingTtlMillis)
        {
            cancel(new Intent());
        }
    }

    @SuppressWarnings("unchecked")
    private void startSubflow()
    {
        OnboardingSubflowFragment fragment = null;
        switch (mSubflowType)
        {
            case STATUS:
                fragment = OnboardingStatusFragment.newInstance();
                Bundle arguments = getFragmentArguments(fragment);
                arguments.putBoolean(BundleKeys.DISALLOW_EXIT, true);
                fragment.setArguments(arguments);
                break;
            case ID_VERIFICATION:
                fragment = IDVerificationFragment.newInstance();
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
            Bundle arguments = getFragmentArguments(fragment);
            arguments.putBoolean(BundleKeys.ALLOW_BACK_NAVIGATION, true);
            fragment.setArguments(arguments);
            fragmentTransaction.addToBackStack(null);
        }

        Bundle arguments = getFragmentArguments(fragment);
        final SubflowData subflowData =
                mOnboardingDetails.getSubflowDataByType(mSubflowType);
        arguments.putSerializable(BundleKeys.SUBFLOW_DATA, subflowData);
        arguments.putBoolean(BundleKeys.IS_SINGLE_STEP_MODE, mIsSingleStepMode);
        arguments.putInt(BundleKeys.PERCENT_COMPLETE, calculatePercentComplete());
        fragment.setArguments(arguments);

        fragmentTransaction.setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
        );
        fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.commit();
        mSubflowStepCount++;
    }

    public void terminate(@NonNull Intent data)
    {
        if (!isFinishing())
        {
            setResult(Activity.RESULT_OK, data);
            finish();
        }
    }

    @NonNull
    private Bundle getFragmentArguments(final OnboardingSubflowFragment fragment)
    {
        Bundle arguments = fragment.getArguments();
        if (arguments == null)
        {
            arguments = new Bundle();
        }
        return arguments;
    }

    @Override
    public void onBackPressed()
    {
        mSubflowStepCount--;
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

    // This will use number of screens within the subflow to calculate percent complete.
    public int calculatePercentComplete()
    {
        final int miniPercentJump = mPercentRange / (mSubflowType.getNumberOfSteps() + 1); // + 1 to exaggerate transition between subflows
        return mPercentComplete + (mSubflowStepCount * miniPercentJump);
    }

}
