package com.handy.portal.onboarding.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.handy.portal.RobolectricGradleTestWrapper;
import com.handy.portal.TestUtils;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.RequestCode;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.model.Designation;
import com.handy.portal.onboarding.model.OnboardingDetails;
import com.handy.portal.onboarding.model.subflow.SubflowData;
import com.handy.portal.onboarding.model.subflow.SubflowType;
import com.handy.portal.onboarding.model.supplies.SuppliesOrderInfo;
import com.handy.portal.onboarding.ui.activity.OnboardingSubflowActivity;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.robolectric.Robolectric;
import org.robolectric.util.ActivityController;

import java.util.List;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PurchaseSuppliesFragmentTest extends RobolectricGradleTestWrapper
{
    private PurchaseSuppliesFragment mFragment;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private OnboardingDetails mOnboardingDetails;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Intent mIntent;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private SubflowData mSubflowData;
    @Captor
    private ArgumentCaptor<Intent> mIntentCaptor;
    private ActivityController<OnboardingSubflowActivity> mActivityController;

    @Before
    public void setUp() throws Exception
    {
        initMocks(this);
        when(mOnboardingDetails.getSubflowDataByType(SubflowType.SUPPLIES))
                .thenReturn(mSubflowData);
        when(mIntent.getSerializableExtra(BundleKeys.ONBOARDING_DETAILS))
                .thenReturn(mOnboardingDetails);
        when(mIntent.getSerializableExtra(BundleKeys.SUBFLOW_TYPE))
                .thenReturn(SubflowType.SUPPLIES);
    }

    private void startFragment()
    {
        mActivityController = Robolectric.buildActivity(OnboardingSubflowActivity.class, mIntent);
        mActivityController.create().resume().visible();
        mFragment = spy((PurchaseSuppliesFragment) mActivityController.get()
                .getSupportFragmentManager().getFragments().get(0));
    }

    @Test
    public void shouldGoToSuppliesConfirmationIfOptingIn() throws Exception
    {
        startFragment();

        mFragment.mGroupPrimaryButton.performClick();

        final List<Fragment> fragments =
                mActivityController.get().getSupportFragmentManager().getFragments();
        assertThat(fragments.size(), equalTo(2));
        assertThat(fragments.get(fragments.size() - 1),
                instanceOf(PurchaseSuppliesConfirmationFragment.class));
    }

    @Test
    public void shouldLaunchConfirmationDialogIfOptingOut() throws Exception
    {
        startFragment();

        mFragment.mGroupSecondaryButton.performClick();

        assertNotNull(mFragment.getFragmentManager()
                .findFragmentByTag(DeclineSuppliesDialogFragment.TAG));
    }

    @Test
    public void shouldTerminateWithProperDataAfterConfirmingOptOut() throws Exception
    {
        startFragment();
        mFragment.onActivityResult(RequestCode.DECLINE_SUPPLIES, Activity.RESULT_OK, null);

        verify(mFragment).terminate(mIntentCaptor.capture());
        final SuppliesOrderInfo suppliesOrderInfo = (SuppliesOrderInfo) mIntentCaptor.getValue()
                .getSerializableExtra(BundleKeys.SUPPLIES_ORDER_INFO);
        assertNotNull(suppliesOrderInfo);
        assertThat(suppliesOrderInfo.getDesignation(), equalTo(Designation.NO));
    }

    @Test
    public void shouldNotifyServerAfterConfirmingOptOut() throws Exception
    {
        startFragment();
        mFragment.onActivityResult(RequestCode.DECLINE_SUPPLIES, Activity.RESULT_OK, null);

        final HandyEvent.RequestOnboardingSupplies event =
                TestUtils.getFirstMatchingBusEvent(mFragment.getBus(),
                        HandyEvent.RequestOnboardingSupplies.class);
        assertNotNull(event);
        assertFalse(event.getOptIn());
    }
}
