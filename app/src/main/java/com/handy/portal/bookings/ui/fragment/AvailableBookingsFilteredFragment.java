package com.handy.portal.bookings.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.handy.portal.R;
import com.handy.portal.bookings.model.ScheduledBookingFindJob;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewPage;
import com.handy.portal.event.HandyEvent;

import java.util.Date;
import java.util.List;

/**
 * This fragment is currently display from tapping on available times in the Scheduled Bookings section
 * NOTE: We can figure out how to modify this if we want to reuse this for the Filter location phase 2
 * NOTE: We don't use a date bar currently, but could be modified to toggle between the 2
 * Created by sng on 9/29/16.
 */

public class AvailableBookingsFilteredFragment extends AvailableBookingsFragment {

    private ScheduledBookingFindJob mScheduledBookingFindJob;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        //Optional param, needs to be validated
        if (getArguments() != null) {
            mScheduledBookingFindJob = (ScheduledBookingFindJob) getArguments().getSerializable(BundleKeys.FIND_JOB);
        }
        //Disble the ScrollView
        mAvailableJobsDatesScrollView.setVisibility(View.GONE);
        return view;
    }

    @Override
    protected LinearLayout getDatesLayout() {
        return null;
    }

    @Override
    protected HandyEvent getRequestEvent(List<Date> dates, boolean useCachedIfPresent) {
        return new HandyEvent.RequestAvailableBookingsFiltered(mScheduledBookingFindJob);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setActionBar(R.string.available_jobs, true);
    }

    @Override
    protected void requestAllBookings() {
        //Do nothing
    }

    /**
     * This is needed to be overridden to request bookings for the selected day. Parent class does it y toggling the
     * Date bar, but we don't have one here
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
}
