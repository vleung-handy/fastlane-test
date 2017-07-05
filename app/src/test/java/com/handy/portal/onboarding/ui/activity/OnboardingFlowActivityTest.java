package com.handy.portal.onboarding.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.common.collect.Lists;
import com.handy.portal.RobolectricGradleTestWrapper;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.core.constant.BundleKeys;
import com.handy.portal.core.ui.activity.SplashActivity;
import com.handy.portal.onboarding.model.OnboardingDetails;
import com.handy.portal.onboarding.model.subflow.OnboardingSubflowDetails;
import com.handy.portal.onboarding.model.subflow.SubflowStatus;
import com.handy.portal.onboarding.model.subflow.SubflowType;
import com.handy.portal.onboarding.model.supplies.SuppliesOrderInfo;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.robolectric.Shadows.shadowOf;

public class OnboardingFlowActivityTest extends RobolectricGradleTestWrapper {
    @Mock
    private OnboardingDetails mOnboardingDetails;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private OnboardingSubflowDetails mStatusSubflowDetails;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private OnboardingSubflowDetails mClaimSubflowDetails;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private OnboardingSubflowDetails mSuppliesSubflowDetails;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Intent mIntent;
    private ArrayList<OnboardingSubflowDetails> mOneSubflowStep;
    private ArrayList<OnboardingSubflowDetails> mTwoSubflowSteps;
    private ArrayList<OnboardingSubflowDetails> mThreeSubflowSteps;

    private ActivityController<OnboardingFlowActivity> mActivityController;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        when(mStatusSubflowDetails.getStatus()).thenReturn(SubflowStatus.INCOMPLETE);
        when(mStatusSubflowDetails.getType()).thenReturn(SubflowType.STATUS);
        when(mClaimSubflowDetails.getStatus()).thenReturn(SubflowStatus.INCOMPLETE);
        when(mClaimSubflowDetails.getType()).thenReturn(SubflowType.CLAIM);
        mOneSubflowStep = Lists.newArrayList(mStatusSubflowDetails);
        mTwoSubflowSteps = Lists.newArrayList(mStatusSubflowDetails, mClaimSubflowDetails);
        mThreeSubflowSteps = Lists.newArrayList(mStatusSubflowDetails, mClaimSubflowDetails,
                mSuppliesSubflowDetails);
        when(mOnboardingDetails.getSubflowsByStatus(SubflowStatus.INCOMPLETE))
                .thenReturn(mOneSubflowStep);
        when(mIntent.getSerializableExtra(BundleKeys.ONBOARDING_DETAILS))
                .thenReturn(mOnboardingDetails);
        mActivityController = Robolectric.buildActivity(OnboardingFlowActivity.class, mIntent);
    }

    @Test
    public void shouldLaunchIncompleteStatusSubflow() throws Exception {
        mActivityController.create().resume();

        final Intent nextStartedActivity =
                shadowOf(mActivityController.get()).getNextStartedActivity();
        assertNotNull(nextStartedActivity);
        assertThat(nextStartedActivity.getComponent().getClassName(),
                equalTo(OnboardingSubflowActivity.class.getName()));
        assertThat((SubflowType) nextStartedActivity.getSerializableExtra(BundleKeys.SUBFLOW_TYPE),
                equalTo(SubflowType.STATUS));
    }

    @Test
    public void shouldLaunchAsSingleStepMode() throws Exception {
        mActivityController.create().resume();

        final Intent nextStartedActivity =
                shadowOf(mActivityController.get()).getNextStartedActivity();
        assertNotNull(nextStartedActivity);
        assertTrue(nextStartedActivity.getBooleanExtra(BundleKeys.IS_SINGLE_STEP_MODE, false));
    }

    @Test
    public void shouldNotLaunchAsSingleStepMode() throws Exception {
        when(mOnboardingDetails.getSubflowsByStatus(SubflowStatus.INCOMPLETE))
                .thenReturn(mTwoSubflowSteps);

        mActivityController.create().resume();

        final Intent nextStartedActivity =
                shadowOf(mActivityController.get()).getNextStartedActivity();
        assertNotNull(nextStartedActivity);
        assertFalse(nextStartedActivity.getBooleanExtra(BundleKeys.IS_SINGLE_STEP_MODE, true));
    }

    @Test
    public void shouldLaunchNextSubflow() throws Exception {
        when(mOnboardingDetails.getSubflowsByStatus(SubflowStatus.INCOMPLETE))
                .thenReturn(mTwoSubflowSteps);

        mActivityController.create().resume();

        Intent nextStartedActivity = shadowOf(mActivityController.get()).getNextStartedActivity();
        shadowOf(mActivityController.get()).receiveResult(nextStartedActivity, Activity.RESULT_OK,
                new Intent());

        nextStartedActivity = shadowOf(mActivityController.get()).getNextStartedActivity();
        assertNotNull(nextStartedActivity);
        assertThat(nextStartedActivity.getComponent().getClassName(),
                equalTo(OnboardingSubflowActivity.class.getName()));
        assertThat((SubflowType) nextStartedActivity.getSerializableExtra(BundleKeys.SUBFLOW_TYPE),
                equalTo(SubflowType.CLAIM));
    }

    @Test
    public void shouldFinishOnboardingFlowByLaunchingSplash() throws Exception {
        mActivityController.create().resume();

        Intent nextStartedActivity = shadowOf(mActivityController.get()).getNextStartedActivity();
        shadowOf(mActivityController.get()).receiveResult(nextStartedActivity, Activity.RESULT_OK,
                new Intent());

        nextStartedActivity = shadowOf(mActivityController.get()).getNextStartedActivity();
        assertNotNull(nextStartedActivity);
        assertThat(nextStartedActivity.getComponent().getClassName(),
                equalTo(SplashActivity.class.getName()));
    }

    @Test
    public void shouldFinishAndLaunchSplashOnCancelOnboardingFlow() throws Exception {
        mActivityController.create().resume();

        Intent nextStartedActivity =
                shadowOf(mActivityController.get()).getNextStartedActivity();
        shadowOf(mActivityController.get()).receiveResult(nextStartedActivity,
                Activity.RESULT_CANCELED, new Intent());

        assertTrue(shadowOf(mActivityController.get()).isFinishing());
        nextStartedActivity = shadowOf(mActivityController.get()).getNextStartedActivity();
        assertNotNull(nextStartedActivity);
        assertThat(nextStartedActivity.getComponent().getClassName(),
                equalTo(SplashActivity.class.getName()));
    }

    @Test
    public void shouldFinishAndNotLaunchSplashOnForceCancelOnboardingFlow() throws Exception {
        when(mOnboardingDetails.getSubflowsByStatus(SubflowStatus.INCOMPLETE))
                .thenReturn(mTwoSubflowSteps);

        mActivityController.create().resume();

        Intent nextStartedActivity = shadowOf(mActivityController.get()).getNextStartedActivity();
        final Intent resultIntent = new Intent();
        resultIntent.putExtra(BundleKeys.FORCE_FINISH, true);
        shadowOf(mActivityController.get()).receiveResult(nextStartedActivity,
                Activity.RESULT_CANCELED, resultIntent);

        assertTrue(shadowOf(mActivityController.get()).isFinishing());
        nextStartedActivity = shadowOf(mActivityController.get()).getNextStartedActivity();
        assertNull(nextStartedActivity);
    }

    @Test
    public void shouldSaveAndForwardSuppliesAndClaimsInfo() throws Exception {
        when(mOnboardingDetails.getSubflowsByStatus(SubflowStatus.INCOMPLETE))
                .thenReturn(mTwoSubflowSteps);

        mActivityController.create().resume();

        Intent nextStartedActivity = shadowOf(mActivityController.get()).getNextStartedActivity();
        final Intent resultIntent = new Intent();
        final SuppliesOrderInfo mockSuppliesOrderInfo =
                mock(SuppliesOrderInfo.class, Answers.RETURNS_DEEP_STUBS.get());
        final Booking mockBooking = mock(Booking.class, Answers.RETURNS_DEEP_STUBS.get());
        resultIntent.putExtra(BundleKeys.BOOKINGS, Lists.newArrayList(mockBooking));
        resultIntent.putExtra(BundleKeys.SUPPLIES_ORDER_INFO, mockSuppliesOrderInfo);
        shadowOf(mActivityController.get()).receiveResult(nextStartedActivity, Activity.RESULT_OK,
                resultIntent);

        nextStartedActivity = shadowOf(mActivityController.get()).getNextStartedActivity();
        assertNotNull(nextStartedActivity);
        assertThat(((ArrayList<Booking>) nextStartedActivity
                .getSerializableExtra(BundleKeys.BOOKINGS)).get(0), equalTo(mockBooking));
        assertThat((SuppliesOrderInfo) nextStartedActivity
                        .getSerializableExtra(BundleKeys.SUPPLIES_ORDER_INFO),
                equalTo(mockSuppliesOrderInfo));
    }

    @Test
    public void shouldRetainState() throws Exception {
        when(mOnboardingDetails.getSubflowsByStatus(SubflowStatus.INCOMPLETE))
                .thenReturn(mThreeSubflowSteps);

        mActivityController.create().resume();

        Intent nextStartedActivity = shadowOf(mActivityController.get()).getNextStartedActivity();
        final Intent resultIntent = new Intent();
        final SuppliesOrderInfo mockSuppliesOrderInfo =
                mock(SuppliesOrderInfo.class, Answers.RETURNS_DEEP_STUBS.get());
        final Booking mockBooking = mock(Booking.class, Answers.RETURNS_DEEP_STUBS.get());
        resultIntent.putExtra(BundleKeys.BOOKINGS, Lists.newArrayList(mockBooking));
        resultIntent.putExtra(BundleKeys.SUPPLIES_ORDER_INFO, mockSuppliesOrderInfo);
        shadowOf(mActivityController.get()).receiveResult(nextStartedActivity, Activity.RESULT_OK,
                resultIntent);

        final Bundle outState = new Bundle();
        mActivityController.pause().saveInstanceState(outState).stop().destroy();
        mActivityController = Robolectric.buildActivity(OnboardingFlowActivity.class, mIntent)
                .create(outState);

        nextStartedActivity = shadowOf(mActivityController.get()).getNextStartedActivity();
        assertNotNull(nextStartedActivity);
        assertThat(nextStartedActivity.getComponent().getClassName(),
                equalTo(OnboardingSubflowActivity.class.getName()));
        assertThat((SubflowType) nextStartedActivity.getSerializableExtra(BundleKeys.SUBFLOW_TYPE),
                equalTo(SubflowType.CLAIM));
        assertThat(((ArrayList<Booking>) nextStartedActivity
                .getSerializableExtra(BundleKeys.BOOKINGS)).get(0), equalTo(mockBooking));
        assertThat((SuppliesOrderInfo) nextStartedActivity
                        .getSerializableExtra(BundleKeys.SUPPLIES_ORDER_INFO),
                equalTo(mockSuppliesOrderInfo));
    }
}
