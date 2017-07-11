package com.handy.portal.onboarding.ui.fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ListView;

import com.handy.portal.R;
import com.handy.portal.bookings.manager.BookingManager;
import com.handy.portal.bookings.model.BookingsListWrapper;
import com.handy.portal.core.event.HandyEvent;
import com.handy.portal.core.ui.adapter.CheckBoxListAdapter;
import com.handy.portal.library.ui.view.StaticFieldTableRow;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.library.util.UIUtils;
import com.handy.portal.logger.handylogger.model.NativeOnboardingLog;
import com.handy.portal.onboarding.model.claim.StartDateRange;
import com.handy.portal.onboarding.model.claim.Zipcluster;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class SchedulePreferencesFragment extends OnboardingSubflowUIFragment {
    @Inject
    BookingManager mBookingManager;

    @BindView(R.id.date_field)
    StaticFieldTableRow mDateField;
    @BindView(R.id.location_field)
    StaticFieldTableRow mLocationField;
    @BindView(R.id.schedule_preferences_notice)
    View mSchedulePreferencesNotice;

    private Date mSelectedStartDate;
    private ArrayList<String> mSelectedZipclusterIds;
    private CheckBoxListAdapter.CheckBoxListItem[] mLocationViewModels;

    @OnClick(R.id.date_field)
    public void onDateFieldClicked() {
        final StartDateRange startDateRange = mSubflowData.getStartDateRange();
        final Date startDate = startDateRange.getStartDate();
        final Date endDate = startDateRange.getEndDate();

        final Calendar dayAfterStartDate = Calendar.getInstance();
        dayAfterStartDate.setTime(startDate);
        dayAfterStartDate.add(Calendar.DATE, 1);
        final DatePickerDialog datePickerDialog =
                new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(
                            final DatePicker view,
                            final int year,
                            final int monthOfYear,
                            final int dayOfMonth) {
                        final Calendar c = Calendar.getInstance();
                        c.set(year, monthOfYear, dayOfMonth);
                        updateSelectedStartedDate(c.getTime());
                    }
                }, dayAfterStartDate.get(Calendar.YEAR), dayAfterStartDate.get(Calendar.MONTH),
                        dayAfterStartDate.get(Calendar.DAY_OF_MONTH));

        final DatePicker datePicker = datePickerDialog.getDatePicker();
        datePicker.setMinDate(startDate.getTime());
        datePicker.setMaxDate(endDate.getTime());
        datePickerDialog.show();
    }

    @OnClick(R.id.location_field)
    public void onLocationFieldClicked() {
        final CheckBoxListAdapter adapter = new CheckBoxListAdapter(getActivity(),
                Arrays.copyOf(mLocationViewModels, mLocationViewModels.length));
        final ListView listView = new ListView(getActivity());
        listView.setDivider(null);
        listView.setAdapter(adapter);

        final AlertDialog.Builder dialogBuilder = UIUtils.createDialogBuilderWithTitle(getActivity(),
                R.string.choose_locations);
        dialogBuilder.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        updateSelectedLocations(adapter.getItems());
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .setView(listView)
                .create()
                .show();
    }

    public static SchedulePreferencesFragment newInstance() {
        return new SchedulePreferencesFragment();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSelectedZipclusterIds = new ArrayList<>();
        initLocationViewModels();
    }

    private void initLocationViewModels() {
        final ArrayList<Zipcluster> zipclusters = mSubflowData.getZipclusters();
        mLocationViewModels = new CheckBoxListAdapter.CheckBoxListItem[zipclusters.size()];
        for (int i = 0; i < mLocationViewModels.length; i++) {
            final Zipcluster zipcluster = zipclusters.get(i);
            mLocationViewModels[i] =
                    new CheckBoxListAdapter.CheckBoxListItem(zipcluster.getName(),
                            zipcluster.getId(), false);
        }
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDateField.setLabel(R.string.date);
        mLocationField.setLabel(R.string.locations);
        displaySelectedStartDate();
        if (shouldDisplayLocationField()) {
            displaySelectedLocations();
        }
        else {
            mLocationField.setVisibility(View.GONE);
            mSchedulePreferencesNotice.setVisibility(View.GONE);
        }
        bus.post(new NativeOnboardingLog(
                NativeOnboardingLog.Types.JOB_SEARCH_SHOWN));
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Override
    public void onPause() {
        bus.unregister(this);
        super.onPause();
    }

    @Override
    protected int getButtonType() {
        return ButtonTypes.SINGLE_FIXED;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.view_schedule_preferences;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.claim_jobs);
    }

    @Nullable
    @Override
    protected String getHeaderText() {
        return getString(R.string.set_your_preferences);
    }

    @Nullable
    @Override
    protected String getSubHeaderText() {
        return getString(R.string.start_as_early_as_next_week);
    }

    @Override
    protected void onPrimaryButtonClicked() {
        if (!validate()) {
            return;
        }
        showLoadingOverlay();
        mBookingManager.requestOnboardingJobs(mSelectedStartDate, mSelectedZipclusterIds);
    }

    @Subscribe
    public void onReceiveOnboardingJobsSuccess(HandyEvent.ReceiveOnboardingJobsSuccess event) {
        hideLoadingOverlay();
        final BookingsListWrapper bookingsListWrapper = event.getBookingsListWrapper();
        if (bookingsListWrapper.hasBookings()) {
            next(ScheduleBuilderFragment.newInstance(bookingsListWrapper.getBookingsWrappers(),
                    bookingsListWrapper.getMessage()));
        }
        else {
            showError(getString(R.string.no_jobs_matching_preferences), true);
            bus.post(new NativeOnboardingLog(
                    NativeOnboardingLog.Types.NO_JOBS_LOADED));
        }
    }

    @Subscribe
    public void onReceiveOnboardingJobsError(HandyEvent.ReceiveOnboardingJobsError event) {
        hideLoadingOverlay();
        showError(event.error.getMessage(), true);
    }

    private boolean validate() {
        boolean allFieldsValid = true;
        if (mSelectedStartDate == null) {
            mDateField.setErrorState(true);
            allFieldsValid = false;
        }
        if (shouldDisplayLocationField() && mSelectedZipclusterIds.isEmpty()) {
            mLocationField.setErrorState(true);
            allFieldsValid = false;
        }
        return allFieldsValid;
    }

    public void updateSelectedStartedDate(final Date date) {
        mSelectedStartDate = date;
        bus.post(
                new NativeOnboardingLog.StartDateSelected(mSelectedStartDate));
        displaySelectedStartDate();
    }

    private void displaySelectedStartDate() {
        if (mSelectedStartDate != null) {
            mDateField.setValue(DateTimeUtils.formatDayOfWeekMonthDate(mSelectedStartDate));
        }
        else {
            mDateField.setValue(null).setHint(R.string.choose_date);
        }
    }

    public void updateSelectedLocations(final CheckBoxListAdapter.CheckBoxListItem[] items) {
        mSelectedZipclusterIds.clear();
        mLocationViewModels = items;
        for (CheckBoxListAdapter.CheckBoxListItem item : mLocationViewModels) {
            if (item.isChecked()) {
                mSelectedZipclusterIds.add(item.getId());
            }
        }
        bus.post(
                new NativeOnboardingLog.LocationsSelected(
                        new ArrayList<>(mSelectedZipclusterIds)));
        displaySelectedLocations();
    }

    private void displaySelectedLocations() {
        final int count = mSelectedZipclusterIds.size();
        if (count > 0) {
            mLocationField.setValue(getResources().getQuantityString(
                    R.plurals.locations_selected_count_formatted, count, count));
        }
        else {
            mLocationField.setValue(null).setHint(R.string.choose_locations);
        }
    }

    private boolean shouldDisplayLocationField() {
        return mLocationViewModels != null && mLocationViewModels.length > 0;
    }
}
