package com.handy.portal.onboarding.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.google.common.collect.Lists;
import com.handy.portal.R;
import com.handy.portal.RobolectricGradleTestWrapper;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.core.constant.BundleKeys;
import com.handy.portal.onboarding.model.OnboardingDetails;
import com.handy.portal.onboarding.model.subflow.SubflowType;
import com.handy.portal.onboarding.model.supplies.SuppliesOrderInfo;
import com.handy.portal.onboarding.ui.fragment.NewPurchaseSuppliesFragment;
import com.handy.portal.onboarding.ui.fragment.OnboardingStatusFragment;
import com.handy.portal.onboarding.ui.fragment.PurchaseSuppliesFragment;
import com.handy.portal.onboarding.ui.fragment.ScheduleConfirmationFragment;
import com.handy.portal.onboarding.ui.fragment.SchedulePreferencesFragment;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.shadows.ShadowApplication;

import java.util.ArrayList;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class OnboardingSubflowActivityTest extends RobolectricGradleTestWrapper {
    private ActivityController<OnboardingSubflowActivity> mActivityController;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private OnboardingDetails mOnboardingDetails;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Intent mIntent;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        when(mIntent.getSerializableExtra(BundleKeys.ONBOARDING_DETAILS))
                .thenReturn(mOnboardingDetails);
        when(mIntent.getSerializableExtra(BundleKeys.SUBFLOW_TYPE)).thenReturn(SubflowType.STATUS);
        mActivityController = Robolectric.buildActivity(OnboardingSubflowActivity.class, mIntent);
    }

    @Test
    public void shouldLaunchStatusSubflowFragment() throws Exception {
        mActivityController.create().resume();

        final Fragment subflowFragment =
                mActivityController.get().getSupportFragmentManager().getFragments().get(0);
        assertNotNull(subflowFragment);
        assertThat(subflowFragment, instanceOf(OnboardingStatusFragment.class));
    }

    @Test
    public void shouldLaunchClaimSubflowFragment() throws Exception {
        when(mIntent.getSerializableExtra(BundleKeys.SUBFLOW_TYPE)).thenReturn(SubflowType.CLAIM);

        mActivityController.create().resume();

        final Fragment subflowFragment =
                mActivityController.get().getSupportFragmentManager().getFragments().get(0);
        assertNotNull(subflowFragment);
        assertThat(subflowFragment, instanceOf(SchedulePreferencesFragment.class));
    }

    @Test
    public void shouldLaunchSuppliesSubflowFragment() throws Exception {
        when(mIntent.getSerializableExtra(BundleKeys.SUBFLOW_TYPE))
                .thenReturn(SubflowType.SUPPLIES);

        mActivityController.create().resume();

        final Fragment subflowFragment =
                mActivityController.get().getSupportFragmentManager().getFragments().get(0);
        assertNotNull(subflowFragment);
        assertThat(subflowFragment, instanceOf(PurchaseSuppliesFragment.class));
    }

    @Test
    public void shouldLaunchNewSuppliesSubflowFragment() throws Exception {
        when(mIntent.getSerializableExtra(BundleKeys.SUBFLOW_TYPE))
                .thenReturn(SubflowType.NEW_SUPPLIES);

        mActivityController.create().resume();

        final Fragment subflowFragment =
                mActivityController.get().getSupportFragmentManager().getFragments().get(0);
        assertNotNull(subflowFragment);
        assertThat(subflowFragment, instanceOf(NewPurchaseSuppliesFragment.class));
    }

    @Test
    public void shouldLaunchConfirmationSubflowFragment() throws Exception {
        when(mIntent.getSerializableExtra(BundleKeys.SUBFLOW_TYPE))
                .thenReturn(SubflowType.CONFIRMATION);
        final ArrayList<Booking> bookings = Lists.newArrayList();
        when(mIntent.getSerializableExtra(BundleKeys.BOOKINGS)).thenReturn(bookings);
        final SuppliesOrderInfo suppliesOrderInfo = new SuppliesOrderInfo();
        when(mIntent.getSerializableExtra(BundleKeys.SUPPLIES_ORDER_INFO)).thenReturn(
                suppliesOrderInfo);

        mActivityController.create().resume();

        final Fragment subflowFragment =
                mActivityController.get().getSupportFragmentManager().getFragments().get(0);
        assertNotNull(subflowFragment);
        assertThat(subflowFragment, instanceOf(ScheduleConfirmationFragment.class));
        assertThat((ArrayList<Booking>) mIntent.getSerializableExtra(BundleKeys.BOOKINGS),
                equalTo(bookings));
        assertThat((SuppliesOrderInfo) mIntent.getSerializableExtra(BundleKeys.SUPPLIES_ORDER_INFO),
                equalTo(suppliesOrderInfo));
    }

    @Test
    public void shouldAddToFragmentBackStackOnNext() throws Exception {
        mActivityController.create().resume();

        final OnboardingSubflowActivity activity = mActivityController.get();

        assertThat(activity.getSupportFragmentManager().getFragments().size(), equalTo(1));
        assertThat(activity.getSupportFragmentManager().getBackStackEntryCount(), equalTo(0));

        activity.next(NewPurchaseSuppliesFragment.newInstance(), true);

        assertThat(activity.getSupportFragmentManager().getFragments().size(), equalTo(2));
        assertThat(activity.getSupportFragmentManager().getBackStackEntryCount(), equalTo(1));
    }

    @Test
    @Ignore
    public void shouldPopFragmentBackStackOnBackPressed() throws Exception {
        when(mIntent.getSerializableExtra(BundleKeys.SUBFLOW_TYPE))
                .thenReturn(SubflowType.SUPPLIES);
        mActivityController.create().resume();

        final OnboardingSubflowActivity activity = mActivityController.get();
        final NewPurchaseSuppliesFragment fragment = NewPurchaseSuppliesFragment.newInstance();
        activity.next(fragment, true);

        assertThat(activity.getSupportFragmentManager().getFragments().size(), equalTo(2));
        assertThat(activity.getSupportFragmentManager().getBackStackEntryCount(), equalTo(1));

        activity.onBackPressed(); // TODO: Find out why this causes a NPE.

        assertThat(activity.getSupportFragmentManager().getFragments().size(), equalTo(1));
        assertThat(activity.getSupportFragmentManager().getBackStackEntryCount(), equalTo(0));
    }

    @Test
    public void shouldFinishStatusSubflowOnBackPressed() throws Exception {
        mActivityController.create().resume();

        mActivityController.get().onBackPressed();

        assertTrue(mActivityController.get().isFinishing());
    }

    @Test
    public void shouldFinishSubflowOnTerminate() throws Exception {
        mActivityController.create().resume();

        mActivityController.get().terminate(new Intent());

        assertTrue(mActivityController.get().isFinishing());
    }

    @Test
    public void shouldCancelAfterReachingTtl() throws Exception {
        final int ttlMillis = ShadowApplication.getInstance().getApplicationContext().getResources()
                .getInteger(R.integer.onboarding_ttl_mins) * 60 * 1000;
        final Bundle state = new Bundle();
        state.putLong(OnboardingSubflowActivity.EXTRA_LAUNCHED_TIME_MILLIS,
                System.currentTimeMillis() - ttlMillis);
        mActivityController.create(state).resume();

        assertTrue(mActivityController.get().isFinishing());
    }
}
