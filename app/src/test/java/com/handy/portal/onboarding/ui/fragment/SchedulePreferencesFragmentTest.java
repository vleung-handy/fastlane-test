package com.handy.portal.onboarding.ui.fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.common.collect.Lists;
import com.handy.portal.RobolectricGradleTestWrapper;
import com.handy.portal.TestUtils;
import com.handy.portal.bookings.model.BookingsWrapper;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.onboarding.model.OnboardingDetails;
import com.handy.portal.onboarding.model.claim.StartDateRange;
import com.handy.portal.onboarding.model.claim.Zipcluster;
import com.handy.portal.onboarding.model.subflow.SubflowData;
import com.handy.portal.onboarding.model.subflow.SubflowType;
import com.handy.portal.onboarding.ui.activity.OnboardingSubflowActivity;
import com.handy.portal.ui.adapter.CheckBoxListAdapter;
import com.handy.portal.ui.widget.HandyCheckBox;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.robolectric.Robolectric;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowDatePickerDialog;
import org.robolectric.shadows.ShadowListView;
import org.robolectric.util.ActivityController;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.robolectric.Shadows.shadowOf;

public class SchedulePreferencesFragmentTest extends RobolectricGradleTestWrapper
{
    private SchedulePreferencesFragment mFragment;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private OnboardingDetails mOnboardingDetails;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Intent mIntent;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private SubflowData mSubflowData;
    private ActivityController<OnboardingSubflowActivity> mActivityController;

    @Before
    public void setUp() throws Exception
    {
        initMocks(this);
        when(mOnboardingDetails.getSubflowDataByType(SubflowType.CLAIM)).thenReturn(mSubflowData);
        when(mIntent.getSerializableExtra(BundleKeys.ONBOARDING_DETAILS))
                .thenReturn(mOnboardingDetails);
        when(mIntent.getSerializableExtra(BundleKeys.SUBFLOW_TYPE)).thenReturn(SubflowType.CLAIM);
    }

    private void startFragment()
    {
        mActivityController = Robolectric.buildActivity(OnboardingSubflowActivity.class, mIntent);
        mActivityController.create().resume().visible();
        mFragment = spy((SchedulePreferencesFragment) mActivityController.get()
                .getSupportFragmentManager().getFragments().get(0));
    }

    @Test
    public void shouldNotShowLocationsFieldIfThereAreNoLocations() throws Exception
    {
        when(mSubflowData.getZipclusters()).thenReturn(Lists.<Zipcluster>newArrayList());

        startFragment();

        assertThat(mFragment.mLocationField.getVisibility(), equalTo(View.GONE));
    }

    @Test
    public void testLocationsDialogInteraction() throws Exception
    {
        final Zipcluster zipcluster1 = mock(Zipcluster.class);
        when(zipcluster1.getId()).thenReturn("1");
        when(zipcluster1.getName()).thenReturn("Manhattan");
        final Zipcluster zipcluster2 = mock(Zipcluster.class);
        when(zipcluster2.getId()).thenReturn("2");
        when(zipcluster2.getName()).thenReturn("Brooklyn");
        when(mSubflowData.getZipclusters())
                .thenReturn(Lists.newArrayList(zipcluster1, zipcluster2));

        startFragment();

        // initial state
        assertThat(mFragment.mLocationField.getVisibility(), equalTo(View.VISIBLE));
        mFragment.mLocationField.performClick();

        final AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();
        final ShadowAlertDialog shadowDialog = shadowOf(dialog);
        final ListView listView = (ListView) shadowDialog.getView();
        assertNotNull(listView);
        final ShadowListView shadowListView = shadowOf(listView);
        shadowListView.populateItems();
        final ListAdapter adapter = listView.getAdapter();
        assertNotNull(adapter);

        // test display
        assertThat(listView.getChildCount(), equalTo(2));
        final CheckBoxListAdapter.CheckBoxListItem item1 =
                (CheckBoxListAdapter.CheckBoxListItem) adapter.getItem(0);
        assertThat(item1.getId(), equalTo("1"));
        assertThat(item1.getLabel(), equalTo("Manhattan"));
        final CheckBoxListAdapter.CheckBoxListItem item2 =
                (CheckBoxListAdapter.CheckBoxListItem) adapter.getItem(1);
        assertThat(item2.getId(), equalTo("2"));
        assertThat(item2.getLabel(), equalTo("Brooklyn"));

        // test selection
        assertFalse(item1.isChecked());
        ((HandyCheckBox) listView.getChildAt(0)).setChecked(true);
        assertTrue(item1.isChecked());
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();

        // terminal state
        assertThat(mFragment.mLocationField.getValue().getText().toString(),
                equalTo("1 location selected"));
    }

    @Test
    public void testDateDialogInteraction() throws Exception
    {
        StartDateRange dateRange = mock(StartDateRange.class);
        final Calendar fiveDaysFromNow = Calendar.getInstance();
        fiveDaysFromNow.add(Calendar.DATE, 5);
        when(dateRange.getStartDate()).thenReturn(new Date());
        when(dateRange.getEndDate()).thenReturn(fiveDaysFromNow.getTime());
        when(mSubflowData.getStartDateRange()).thenReturn(dateRange);

        startFragment();
        mFragment.mDateField.performClick();

        final DatePickerDialog dialog =
                (DatePickerDialog) ShadowDatePickerDialog.getLatestAlertDialog();
        final Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DATE, 1);
        dialog.updateDate(tomorrow.get(Calendar.YEAR), tomorrow.get(Calendar.MONTH),
                tomorrow.get(Calendar.DAY_OF_MONTH));
        dialog.getButton(DatePickerDialog.BUTTON_POSITIVE).performClick();

        assertThat(mFragment.mDateField.getValue().getText().toString(),
                equalTo(DateTimeUtils.formatDayOfWeekMonthDate(tomorrow.getTime())));
    }

    @Test
    public void shouldRequestForJobs() throws Exception
    {
        final Zipcluster zipcluster1 = mock(Zipcluster.class);
        when(zipcluster1.getId()).thenReturn("1");
        final Zipcluster zipcluster2 = mock(Zipcluster.class);
        when(zipcluster2.getId()).thenReturn("2");
        when(mSubflowData.getZipclusters())
                .thenReturn(Lists.newArrayList(zipcluster1, zipcluster2));

        // A little hacky, but the following line sets up the date portion of the test. Plus, it
        // calls startFragment().
        testDateDialogInteraction();

        // The following lines set up the location portion of the test.
        mFragment.mLocationField.performClick();
        final AlertDialog locationsDialog = ShadowAlertDialog.getLatestAlertDialog();
        final ListView listView = (ListView) shadowOf(locationsDialog).getView();
        shadowOf(listView).populateItems();
        ((CheckBoxListAdapter.CheckBoxListItem) listView.getAdapter().getItem(0)).setChecked(true);
        locationsDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).performClick();

        mFragment.mSingleActionButton.performClick();

        final HandyEvent.RequestOnboardingJobs event =
                TestUtils.getFirstMatchingBusEvent(mFragment.getBus(),
                        HandyEvent.RequestOnboardingJobs.class);
        assertNotNull(event);
        final Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DATE, 1);
        assertNotNull(event.getStartDate());
        assertThat(event.getPreferredZipclusterIds().size(), equalTo(1));
        assertThat(event.getPreferredZipclusterIds().get(0), equalTo("1"));
    }

    @Test
    public void shouldGoToScheduleBuilderAfterReceivingClaimableJobs() throws Exception
    {
        startFragment();
        final HandyEvent.ReceiveOnboardingJobsSuccess event =
                mock(HandyEvent.ReceiveOnboardingJobsSuccess.class,
                        Answers.RETURNS_DEEP_STUBS.get());
        when(event.getBookingsListWrapper().hasBookings()).thenReturn(true);
        when(event.getBookingsListWrapper().getBookingsWrappers()).thenReturn(
                Lists.newArrayList(mock(BookingsWrapper.class, Answers.RETURNS_DEEP_STUBS.get())));
        mFragment.onReceiveOnboardingJobsSuccess(event);

        final List<Fragment> fragments =
                mActivityController.get().getSupportFragmentManager().getFragments();
        assertThat(fragments.size(), equalTo(2));
        assertThat(fragments.get(fragments.size() - 1), instanceOf(ScheduleBuilderFragment.class));
    }
}
