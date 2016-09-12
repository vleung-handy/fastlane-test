package com.handy.portal.onboarding.ui.fragment;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.handy.portal.R;
import com.handy.portal.RobolectricGradleTestWrapper;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.event.ProfileEvent;
import com.handy.portal.model.Designation;
import com.handy.portal.model.ProviderPersonalInfo;
import com.handy.portal.model.ProviderProfile;
import com.handy.portal.onboarding.model.OnboardingDetails;
import com.handy.portal.onboarding.model.status.LearningLink;
import com.handy.portal.onboarding.model.status.LearningLinkDetails;
import com.handy.portal.onboarding.model.subflow.SubflowData;
import com.handy.portal.onboarding.model.subflow.SubflowType;
import com.handy.portal.onboarding.ui.activity.OnboardingSubflowActivity;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.robolectric.Robolectric;
import org.robolectric.util.ActivityController;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class OnboardingStatusFragmentTest extends RobolectricGradleTestWrapper
{
    private OnboardingStatusFragment mFragment;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private OnboardingDetails mOnboardingDetails;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Intent mIntent;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private SubflowData mStatusData;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ProviderProfile mProviderProfile;

    @Before
    public void setUp() throws Exception
    {
        initMocks(this);
        when(mOnboardingDetails.getSubflowDataByType(SubflowType.STATUS))
                .thenReturn(mStatusData);
        when(mIntent.getSerializableExtra(BundleKeys.ONBOARDING_DETAILS))
                .thenReturn(mOnboardingDetails);
        when(mIntent.getSerializableExtra(BundleKeys.SUBFLOW_TYPE)).thenReturn(SubflowType.STATUS);
    }

    private void startFragment()
    {
        final ActivityController<OnboardingSubflowActivity> activityController =
                Robolectric.buildActivity(OnboardingSubflowActivity.class, mIntent);
        activityController.create().resume().visible();
        mFragment = spy((OnboardingStatusFragment) activityController.get()
                .getSupportFragmentManager().getFragments().get(0));
    }

    @Test
    public void shouldTerminateOnPrimaryButtonClicked() throws Exception
    {
        when(mStatusData.getButton().getTitle()).thenReturn("Button");

        startFragment();

        assertThat(mFragment.mSingleActionButton.getVisibility(), equalTo(View.VISIBLE));

        mFragment.mSingleActionButton.performClick();

        assertTrue(mFragment.getActivity().isFinishing());
    }

    @Test
    public void shouldNotShowPrimaryButtonIfButtonIsNull() throws Exception
    {
        when(mStatusData.getButton()).thenReturn(null);

        startFragment();

        assertThat(mFragment.mSingleActionButton.getVisibility(), equalTo(View.GONE));
    }

    @Test
    public void shouldNotShowJobsSectionIfThereAreNoClaims() throws Exception
    {
        when(mStatusData.getClaims()).thenReturn(Lists.<Booking>newArrayList());

        startFragment();
        mFragment.onReceiveProviderProfileSuccess(
                new ProfileEvent.ReceiveProviderProfileSuccess(mProviderProfile));

        assertThat(mFragment.mJobsCollapsible.getVisibility(), equalTo(View.GONE));
    }

    @Test
    public void shouldShowJobsSectionIfThereAreClaims() throws Exception
    {
        final Booking booking1 = mock(Booking.class, Answers.RETURNS_DEEP_STUBS.get());
        when(booking1.isProxy()).thenReturn(true);
        when(booking1.getLocationName()).thenReturn("Manhattan");
        final Booking booking2 = mock(Booking.class, Answers.RETURNS_DEEP_STUBS.get());
        when(booking2.isProxy()).thenReturn(true);
        when(booking2.getLocationName()).thenReturn("Brooklyn");
        when(mStatusData.getClaims()).thenReturn(Lists.newArrayList(booking1, booking2));

        startFragment();
        mFragment.onReceiveProviderProfileSuccess(
                new ProfileEvent.ReceiveProviderProfileSuccess(mProviderProfile));

        assertThat(mFragment.mJobsCollapsible.getVisibility(), equalTo(View.VISIBLE));
        final ViewGroup jobsContainer = mFragment.mJobsCollapsible.getContentViewContainer();
        assertThat(jobsContainer.getChildCount(), equalTo(2));
        assertThat(((TextView) jobsContainer.getChildAt(0)
                        .findViewById(R.id.booking_entry_area_text)).getText().toString(),
                equalTo(booking1.getLocationName()));
        assertThat(((TextView) jobsContainer.getChildAt(1)
                        .findViewById(R.id.booking_entry_area_text)).getText().toString(),
                equalTo(booking2.getLocationName()));
    }

    @Test
    public void shouldNotShowSuppliesInfoIfDesignationIsUndecided() throws Exception
    {
        when(mStatusData.getSuppliesInfo().getDesignation()).thenReturn(Designation.UNDECIDED);

        startFragment();
        mFragment.onReceiveProviderProfileSuccess(
                new ProfileEvent.ReceiveProviderProfileSuccess(mProviderProfile));

        assertThat(mFragment.mSuppliesCollapsible.getVisibility(), equalTo(View.GONE));
    }

    @Test
    public void shouldShowSuppliesInfoIfDesignationIsYes() throws Exception
    {
        when(mStatusData.getSuppliesInfo().getDesignation()).thenReturn(Designation.YES);
        when(mStatusData.getSuppliesInfo().getCost()).thenReturn("$50");
        final ProviderPersonalInfo providerPersonalInfo = mProviderProfile.getProviderPersonalInfo();
        when(providerPersonalInfo.getCardLast4()).thenReturn("5566");
        when(providerPersonalInfo.getAddress().getShippingAddress()).thenReturn("123 Handy St");
        when(providerPersonalInfo.getFullName()).thenReturn("John Doe");

        startFragment();
        mFragment.onReceiveProviderProfileSuccess(
                new ProfileEvent.ReceiveProviderProfileSuccess(mProviderProfile));

        assertThat(mFragment.mSuppliesCollapsible.getVisibility(), equalTo(View.VISIBLE));
        assertThat(((TextView) mFragment.mShippingView.findViewById(R.id.description))
                .getText().toString(), equalTo("John Doe\n123 Handy St"));
        assertThat(((TextView) mFragment.mOrderTotalView.findViewById(R.id.label))
                .getText().toString(), equalTo("Order Total"));
        assertThat(((TextView) mFragment.mOrderTotalView.findViewById(R.id.value))
                .getText().toString(), equalTo("$50"));
        assertThat(((TextView) mFragment.mPaymentView.findViewById(R.id.value))
                .getText().toString(), equalTo("Card ending in 5566"));
    }

    @Test
    public void shouldShowSuppliesInfoWithoutPaymentIfDesignationIsYesButThereIsNoPaymentMethod() throws Exception
    {
        when(mStatusData.getSuppliesInfo().getDesignation()).thenReturn(Designation.YES);
        when(mStatusData.getSuppliesInfo().getCost()).thenReturn("$50");
        final ProviderPersonalInfo providerPersonalInfo = mProviderProfile.getProviderPersonalInfo();
        when(providerPersonalInfo.getCardLast4()).thenReturn(null);
        when(providerPersonalInfo.getAddress().getShippingAddress()).thenReturn("123 Handy St");
        when(providerPersonalInfo.getFullName()).thenReturn("John Doe");

        startFragment();
        mFragment.onReceiveProviderProfileSuccess(
                new ProfileEvent.ReceiveProviderProfileSuccess(mProviderProfile));

        assertThat(mFragment.mSuppliesCollapsible.getVisibility(), equalTo(View.VISIBLE));
        assertThat(((TextView) mFragment.mShippingView.findViewById(R.id.description))
                .getText().toString(), equalTo("John Doe\n123 Handy St"));
        assertThat(((TextView) mFragment.mOrderTotalView.findViewById(R.id.label))
                .getText().toString(), equalTo("Supplies Fee"));
        assertThat(((TextView) mFragment.mOrderTotalView.findViewById(R.id.value))
                .getText().toString(), equalTo("$50"));
        assertThat(mFragment.mPaymentView.getVisibility(), equalTo(View.GONE));
    }

    @Test
    public void shouldShowNotRequestedInSuppliesInfoIfDesignationIsNo() throws Exception
    {
        when(mStatusData.getSuppliesInfo().getDesignation()).thenReturn(Designation.NO);

        startFragment();
        mFragment.onReceiveProviderProfileSuccess(
                new ProfileEvent.ReceiveProviderProfileSuccess(mProviderProfile));

        assertThat(mFragment.mSuppliesCollapsible.getVisibility(), equalTo(View.VISIBLE));
        assertThat(((TextView) mFragment.mSuppliesCollapsible.findViewById(R.id.subtitle))
                .getText().toString(), equalTo("Not requested"));
    }

    @Test
    public void shouldShowLearningLinks() throws Exception
    {
        final LearningLink learningLink1 = mock(LearningLink.class);
        when(learningLink1.getTitle()).thenReturn("Learning Link 1");
        final LearningLink learningLink2 = mock(LearningLink.class);
        when(learningLink2.getTitle()).thenReturn("Learning Link 2");
        final LearningLinkDetails learningLinkDetails = mock(LearningLinkDetails.class);
        when(learningLinkDetails.getLearningLinks())
                .thenReturn(Lists.newArrayList(learningLink1, learningLink2));
        when(mStatusData.getLearningLinkDetails()).thenReturn(learningLinkDetails);

        startFragment();
        mFragment.onReceiveProviderProfileSuccess(
                new ProfileEvent.ReceiveProviderProfileSuccess(mProviderProfile));

        assertThat(mFragment.mLinksContainer.getChildCount(), equalTo(2));
        assertThat(((TextView) mFragment.mLinksContainer.getChildAt(0)).getText().toString(),
                equalTo("Learning Link 1"));
        assertThat(((TextView) mFragment.mLinksContainer.getChildAt(1)).getText().toString(),
                equalTo("Learning Link 2"));
    }
}
