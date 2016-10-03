package com.handy.portal.bookings.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.bookings.model.BookingsWrapper;
import com.handy.portal.bookings.model.ScheduledBookingFindJob;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewPage;
import com.handy.portal.constant.TransitionStyle;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.NavigationEvent;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.ScheduledJobsLog;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * This fragment is currently display from tapping on available times in the Scheduled Bookings section
 * NOTE: We can figure out how to modify this if we want to reuse this for the Filter location phase 2
 * NOTE: We don't use a date bar currently, but could be modified to toggle between the 2
 * Created by sng on 9/29/16.
 */

public class AvailableBookingsFilteredFragment extends AvailableBookingsFragment {
    @BindView(R.id.bookings_filtered_header_date_time)
    TextView mDateTimeHeader;
    @BindView(R.id.bookings_filtered_header_near_jobs)
    TextView mNearJobHeader;

    @BindView(R.id.available_bookings_empty_text)
    TextView mEmptyResultsTextView;
    @BindView(R.id.available_jobs_find_all_button)
    View mFindAllJobsLayout;
    @BindView(R.id.available_bookings_see_all_jobs_button)
    Button mSeeAllJobsButton;

    private ScheduledBookingFindJob mScheduledBookingFindJob;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        //Optional param, needs to be validated
        if (getArguments() != null) {
            mScheduledBookingFindJob = (ScheduledBookingFindJob) getArguments().getSerializable(BundleKeys.FIND_JOB);
        }

        //Need to update the error message displayed on screen
        mEmptyResultsTextView.setText(R.string.no_available_filtered_jobs);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Update the header
        if (mScheduledBookingFindJob != null) {
            //Update the time
            Date startTime = mScheduledBookingFindJob.getAvailableStartTime();
            Date endTime = mScheduledBookingFindJob.getAvailableEndTime();

            mDateTimeHeader.setText(
                    getString(R.string.schedule_filter_matching_jobs_header_date,
                            DateTimeUtils.formatShortDateDayOfWeekMonthDay(startTime),
                            DateTimeUtils.formatDateTo12HourClock(startTime),
                            DateTimeUtils.formatDateTo12HourClock(endTime))
            );

            List<String> jobLocationNames = mScheduledBookingFindJob.getJobLocationNames();
            if (jobLocationNames != null) {
                if (jobLocationNames.size() == 1) {
                    mNearJobHeader.setText(getString(R.string.schedule_filter_matching_jobs_header_near_job1, jobLocationNames.get(0)));
                } else {
                    mNearJobHeader.setText(getString(R.string.schedule_filter_matching_jobs_header_near_job2, jobLocationNames.get(0), jobLocationNames.get(1)));
                }
            }
        }
    }

    @Override
    protected LinearLayout getDatesLayout() {
        return null;
    }

    @Override
    protected HandyEvent getRequestEvent(List<Date> dates, boolean useCachedIfPresent) {
        return new HandyEvent.RequestAvailableBookingsFiltered(mScheduledBookingFindJob);
    }

    //Override here just to get the count
    @Override
    protected void displayBookings(@NonNull BookingsWrapper bookingsWrapper, @NonNull Date dateOfBookings) {
        super.displayBookings(bookingsWrapper, dateOfBookings);
        setHeaderNumber(String.valueOf(bookingsWrapper.getBookings().size()));
        if (bookingsWrapper.getBookings().size() > 0) {
            mFindAllJobsLayout.setVisibility(View.VISIBLE);
            mSeeAllJobsButton.setVisibility(View.GONE);
        } else {
            mSeeAllJobsButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected int getFragmentResourceId() {
        return (R.layout.fragment_available_bookings_filtered);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Default to a - until we get the count
        setHeaderNumber("-");
    }

    private void setHeaderNumber(String value) {
        setActionBar(getString(R.string.schedule_filter_matching_jobs, value), true);
    }

    @Override
    protected void requestAllBookings() {
        //Do nothing. We don't do this on the filtered section
    }

    /**
     * This is needed to be overridden to request bookings for the selected day. Parent class does it y toggling the
     * Date bar, but we don't have one here
     *
     * @param day
     */
    @Override
    protected void selectDay(Date day) {
        super.selectDay(day);

        requestBookingsForSelectedDay(true, false);
    }

    /**
     * This is used for figuring out the tabs on the bottom of the nav
     *
     * @return
     */
    @Override
    protected MainViewPage getAppPage() {
        return MainViewPage.AVAILABLE_JOBS_FILTERED;
    }

    @OnClick(R.id.available_jobs_find_all_button)
    protected void clickFindJobsButton() {
        bus.post(new LogEvent.AddLogEvent((new ScheduledJobsLog.FindJobsSelected(mSelectedDay))));
        TransitionStyle transitionStyle = TransitionStyle.PAGE_TO_PAGE;
        long epochTime = mSelectedDay.getTime();
        //navigate back to available bookings for this day
        Bundle arguments = new Bundle();
        arguments.putLong(BundleKeys.DATE_EPOCH_TIME, epochTime);
        //Return to available jobs on that day
        bus.post(new NavigationEvent.NavigateToPage(MainViewPage.AVAILABLE_JOBS, arguments, transitionStyle));
    }

    @OnClick(R.id.available_bookings_see_all_jobs_button)
    protected void clickSeeAllJobsButton() {
        //TODO probably need to fix issue with the filtered fragment getting the booking list vs the available bookings fragment during tarnsition
        clickFindJobsButton();
    }
}
