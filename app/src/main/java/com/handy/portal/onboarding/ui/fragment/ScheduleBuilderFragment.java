package com.handy.portal.onboarding.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.handy.portal.R;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.bookings.model.BookingsWrapper;
import com.handy.portal.bookings.ui.element.OnboardingAvailableBookingElementView;
import com.handy.portal.core.constant.BundleKeys;
import com.handy.portal.library.util.TextUtils;
import com.handy.portal.logger.handylogger.model.NativeOnboardingLog;
import com.handy.portal.onboarding.ui.view.SelectableJobsViewGroup;
import com.handy.portal.onboarding.viewmodel.BookingViewModel;
import com.handy.portal.onboarding.viewmodel.BookingsWrapperViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class ScheduleBuilderFragment extends OnboardingSubflowUIFragment
        implements SelectableJobsViewGroup.OnJobCheckedChangedListener {
    @BindView(R.id.jobs_container)
    ViewGroup mJobsContainer;

    private List<BookingsWrapper> mBookingsWrappers;
    private List<BookingsWrapperViewModel> mBookingsWrapperViewModels;
    private String mMessage;

    public static ScheduleBuilderFragment newInstance(
            final ArrayList<BookingsWrapper> bookingsWrappers, final String message) {
        final ScheduleBuilderFragment fragment = new ScheduleBuilderFragment();
        final Bundle arguments = new Bundle();
        arguments.putSerializable(BundleKeys.BOOKINGS_WRAPPERS, bookingsWrappers);
        arguments.putString(BundleKeys.MESSAGE, message);
        fragment.setArguments(arguments);
        return fragment;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBookingsWrappers = (List<BookingsWrapper>) getArguments()
                .getSerializable(BundleKeys.BOOKINGS_WRAPPERS);
        mMessage = getArguments().getString(BundleKeys.MESSAGE);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initBookingsWrapperViewModels();
        displayBookings();
        updateButton();
        bus.post(
                new NativeOnboardingLog.ScheduleJobsShown(countBookings()));
    }

    private void initBookingsWrapperViewModels() {
        mBookingsWrapperViewModels = new ArrayList<>();
        for (final BookingsWrapper bookingsWrapper : mBookingsWrappers) {
            final List<Booking> bookings = bookingsWrapper.getBookings();
            if (bookings != null && !bookings.isEmpty()) {
                mBookingsWrapperViewModels.add(new BookingsWrapperViewModel(bookingsWrapper));
            }
        }
    }

    private void displayBookings() {
        mJobsContainer.removeAllViews();
        for (final BookingsWrapperViewModel viewModel : mBookingsWrapperViewModels) {
            final SelectableJobsViewGroup jobsViewGroup = new SelectableJobsViewGroup(getContext());
            jobsViewGroup.setOnJobCheckedChangedListener(this);
            jobsViewGroup.bind(viewModel, OnboardingAvailableBookingElementView.class);
            mJobsContainer.addView(jobsViewGroup);
        }
    }

    private int countBookings() {
        int bookingsCount = 0;
        for (final BookingsWrapper bookingsWrapper : mBookingsWrappers) {
            final List<Booking> bookings = bookingsWrapper.getBookings();
            if (bookings != null) {
                bookingsCount += bookings.size();
            }
        }
        return bookingsCount;
    }

    @Override
    public void onJobCheckedChanged() {
        updateButton();
    }

    public void updateButton() {
        if (getSelectedBookings().isEmpty()) {
            disableButtons();
        }
        else {
            enableButtons();
        }
    }

    public ArrayList<Booking> getSelectedBookings() {
        final ArrayList<Booking> bookings = new ArrayList<>();
        for (final BookingsWrapperViewModel viewModel : mBookingsWrapperViewModels) {
            if (viewModel != null) {
                for (BookingViewModel bookingView : viewModel.getBookingViewModels()) {
                    if (bookingView.isSelected()) {
                        bookings.add(bookingView.getBooking());
                    }
                }
            }
        }
        return bookings;
    }

    @Override
    protected int getButtonType() {
        return ButtonTypes.SINGLE_FIXED;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.view_schedule_builder;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.claim_jobs);
    }

    @Nullable
    @Override
    protected String getHeaderText() {
        return getString(R.string.create_your_first_schedule);
    }

    @Nullable
    @Override
    protected String getSubHeaderText() {
        return TextUtils.isNullOrEmpty(mMessage) ?
                getString(R.string.we_found_some_jobs) : mMessage;
    }

    @Override
    protected void onPrimaryButtonClicked() {
        final ArrayList<Booking> selectedBookings = getSelectedBookings();
        if (!selectedBookings.isEmpty()) {
            bus.post(
                    new NativeOnboardingLog.ScheduleJobsSubmitted(selectedBookings.size()));
            final Intent data = new Intent();
            data.putExtra(BundleKeys.BOOKINGS, selectedBookings);
            terminate(data);
        }
    }
}
