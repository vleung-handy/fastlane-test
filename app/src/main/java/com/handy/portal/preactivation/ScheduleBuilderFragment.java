package com.handy.portal.preactivation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.handy.portal.R;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.bookings.model.BookingsWrapper;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.NativeOnboardingLog;
import com.handy.portal.onboarding.viewmodel.BookingViewModel;
import com.handy.portal.onboarding.viewmodel.BookingsWrapperViewModel;
import com.handy.portal.onboarding.ui.view.OnboardingJobsViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

public class ScheduleBuilderFragment extends PreActivationFlowFragment
        implements OnboardingJobsViewGroup.OnJobCheckedChangedListener
{
    @Bind(R.id.jobs_container)
    ViewGroup mJobsContainer;

    private List<BookingsWrapper> mBookingsWrappers;
    private List<BookingsWrapperViewModel> mBookingsWrapperViewModels;

    public static ScheduleBuilderFragment newInstance(
            final ArrayList<BookingsWrapper> bookingsWrappers)
    {
        final ScheduleBuilderFragment fragment = new ScheduleBuilderFragment();
        final Bundle arguments = new Bundle();
        arguments.putSerializable(BundleKeys.BOOKINGS_WRAPPERS, bookingsWrappers);
        fragment.setArguments(arguments);
        return fragment;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mBookingsWrappers = (List<BookingsWrapper>) getArguments()
                .getSerializable(BundleKeys.BOOKINGS_WRAPPERS);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        disableButtons();
        initBookingsWrapperViewModels();
        displayBookings();

        updateButton();
    }

    private void initBookingsWrapperViewModels()
    {
        mBookingsWrapperViewModels = new ArrayList<>();
        for (final BookingsWrapper bookingsWrapper : mBookingsWrappers)
        {
            final List<Booking> bookings = bookingsWrapper.getBookings();
            if (bookings != null && !bookings.isEmpty())
            {
                mBookingsWrapperViewModels.add(new BookingsWrapperViewModel(bookingsWrapper));
            }
        }
    }

    private void displayBookings()
    {
        mJobsContainer.removeAllViews();
        for (final BookingsWrapperViewModel viewModel : mBookingsWrapperViewModels)
        {
            final OnboardingJobsViewGroup jobsViewGroup = new OnboardingJobsViewGroup(getContext());
            jobsViewGroup.setOnJobCheckedChangedListener(this);
            jobsViewGroup.bind(viewModel);
            mJobsContainer.addView(jobsViewGroup);
        }
    }

    @Override
    public void onJobCheckedChanged()
    {
        updateButton();
    }

    public void updateButton()
    {
        if (getSelectedBookings().isEmpty())
        {
            disableButtons();
        }
        else
        {
            enableButtons();
        }
    }

    public List<Booking> getSelectedBookings()
    {
        final ArrayList<Booking> bookings = new ArrayList<>();
        for (final BookingsWrapperViewModel viewModel : mBookingsWrapperViewModels)
        {
            if (viewModel != null)
            {
                for (BookingViewModel bookingView : viewModel.getBookingViewModels())
                {
                    if (bookingView.isSelected())
                    {
                        bookings.add(bookingView.getBooking());
                    }
                }
            }
        }
        return bookings;
    }

    @Override
    protected int getButtonType()
    {
        return ButtonTypes.SINGLE_FIXED;
    }

    @Override
    protected int getLayoutResId()
    {
        return R.layout.view_schedule_builder;
    }

    @Override
    protected String getTitle()
    {
        return getString(R.string.claim_your_first_jobs);
    }

    @Nullable
    @Override
    protected String getHeaderText()
    {
        return getString(R.string.onboard_getting_started_title);
    }

    @Nullable
    @Override
    protected String getSubHeaderText()
    {
        return null;
    }

    @Override
    protected void onPrimaryButtonClicked()
    {
        final List<Booking> selectedBookings = getSelectedBookings();
        if (!selectedBookings.isEmpty())
        {
            bus.post(new LogEvent.AddLogEvent(new NativeOnboardingLog.ClaimBatchSubmitted()));
            setPendingBookings(selectedBookings);
            // FIXME: Don't terminate here
            terminate();
        }
    }
}
