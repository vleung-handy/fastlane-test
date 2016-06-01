package com.handy.portal.bookings.ui.fragment.dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.handy.portal.R;
import com.handy.portal.bookings.BookingEvent;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.bookings.model.BookingsWrapper;
import com.handy.portal.bookings.ui.element.ProRequestedJobsExpandableListView;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.constant.TransitionStyle;
import com.handy.portal.event.NavigationEvent;
import com.handy.portal.ui.fragment.dialog.SlideUpDialogFragment;
import com.handy.portal.ui.widget.SafeSwipeRefreshLayout;
import com.handy.portal.util.DateTimeUtils;
import com.handy.portal.util.Utils;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;

public class ProRequestedJobsDialogFragment extends SlideUpDialogFragment //TODO refactor
{
    @Bind(R.id.fragment_dialog_pro_requested_jobs_list_view)
    ProRequestedJobsExpandableListView mProRequestedJobsExpandableListView;
    @Bind(R.id.pro_requested_bookings_empty)
    SafeSwipeRefreshLayout mJobsEmptyView;
    @Bind(R.id.fragment_dialog_pro_requested_jobs_list_swipe_refresh_layout)
    SafeSwipeRefreshLayout mJobListSwipeRefreshLayout;
    @Bind(R.id.loading_overlay)
    RelativeLayout mLoadingOverlay;
    @Bind(R.id.fetch_error_view)
    LinearLayout mFetchErrorView;

    public static final String FRAGMENT_TAG = ProRequestedJobsDialogFragment.class.getName();

    @Inject
    protected Bus mBus;

    private View mFragmentView; //this saves the exact view state including the scroll position

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Utils.inject(getActivity(), this);
    }

    public static ProRequestedJobsDialogFragment newInstance()
    {
        return new ProRequestedJobsDialogFragment();
    }
    /**
     *
     * @param jobList sorted by date
     */
    private void updateJobListView(@NonNull List<BookingsWrapper> jobList)
    {
        //todo null checks
        List<BookingsWrapper> filteredJobList = new ArrayList<>(jobList.size());
        for(BookingsWrapper bookingsWrapper : jobList)
        {
            if(bookingsWrapper.getBookings().size() > 0)
            {
                filteredJobList.add(bookingsWrapper);
            }
        }

        mProRequestedJobsExpandableListView.setData(filteredJobList);
        if(filteredJobList.isEmpty())
        {
            showContentViewAndHideOthers(mJobsEmptyView); //todo refactor
        }
        else
        {
            showContentViewAndHideOthers(mJobListSwipeRefreshLayout); //todo refactor
            mProRequestedJobsExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(final ExpandableListView parent, final View v, final int groupPosition, final int childPosition, final long id)
                {
                    ExpandableListAdapter expandableListAdapter = parent.getExpandableListAdapter();
                    Booking booking = (Booking) expandableListAdapter.getChild(groupPosition, childPosition);
                    navigateToJobDetails(booking);
                    return true;
                }
            });
        }
    }

    private void navigateToJobDetails(@NonNull Booking booking)
    {
        Bundle arguments = new Bundle();
        arguments.putString(BundleKeys.BOOKING_ID, booking.getId());
        arguments.putString(BundleKeys.BOOKING_TYPE, booking.getType().toString());
        arguments.putLong(BundleKeys.BOOKING_DATE, booking.getStartDate().getTime());
//        arguments.putString(BundleKeys.BOOKING_SOURCE, getBookingSourceName());
//        arguments.putSerializable(BundleKeys.TAB, getTab());
        mBus.post(new NavigationEvent.NavigateToTab(MainViewTab.JOB_DETAILS, arguments,
                TransitionStyle.JOB_LIST_TO_DETAILS, true));
    }

    @NonNull
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
    {
        //TODO this saves the exact view state, including scroll position. need to research and test if this is safe
        if(mFragmentView == null)
        {
            mFragmentView = super.onCreateView(inflater, container, savedInstanceState);
        }
        else
        {
            //the view can't already have a parent, so remove it
            ViewGroup view = (ViewGroup) mFragmentView.getParent();
            view.removeView(mFragmentView);
        }
        return mFragmentView;
    }

    @Override
    protected View inflateContentView(final LayoutInflater inflater, final ViewGroup container)
    {
        return inflater.inflate(R.layout.fragment_dialog_pro_requested_jobs, container, false);
    }

    /**
     * hides all the content views in this fragment
     * except the given content view
     * @param contentView
     */
    private void showContentViewAndHideOthers(@NonNull View contentView)
    {
        hideAllContentViews();
        contentView.setVisibility(View.VISIBLE);
    }

    private void hideAllContentViews()
    {
        mJobsEmptyView.setVisibility(View.GONE);
        mJobListSwipeRefreshLayout.setVisibility(View.GONE);
        mFetchErrorView.setVisibility(View.GONE);
        mLoadingOverlay.setVisibility(View.GONE);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Window window = getDialog().getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        mBus.register(this);

        //TODO clean this up
        mJobListSwipeRefreshLayout.setRefreshing(false);
        if(mProRequestedJobsExpandableListView.getExpandableListAdapter() == null)
        {
            showContentViewAndHideOthers(mLoadingOverlay);
            requestProRequestedJobs();
        }
    }

    private void requestProRequestedJobs()
    {
        List<Date> datesForBookings = getDatesForBookings();
        mBus.post(new BookingEvent.RequestProRequestedJobs(datesForBookings, true));

    }

    @Override
    public void onPause()
    {
        mBus.unregister(this);
        super.onPause();
    }

    @Subscribe
    public void onReceiveProRequestedJobsSuccess(BookingEvent.ReceiveProRequestedJobsSuccess event)
    {
        mJobListSwipeRefreshLayout.setRefreshing(false);
        updateJobListView(event.getProRequestedJobs());
    }

    @Subscribe
    public void onReceiveProRequestedJobsError(BookingEvent.ReceiveProRequestedError event)
    {
        mJobListSwipeRefreshLayout.setRefreshing(false);
        showContentViewAndHideOthers(mFetchErrorView);
    }


    //FIXME: get this from a config param from the server instead
    protected int numberOfDaysToDisplay()
    {
        return 28;
    }

    /**
     * FIXME copied out of bookings fragment, can we consolidate
     * @return
     */
    private List<Date> getDatesForBookings()
    {
        List<Date> dates = new ArrayList<>();
        for (int i = 0; i < numberOfDaysToDisplay(); i++)
        {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DATE, i);
            final Date day = DateTimeUtils.getDateWithoutTime(calendar.getTime());

            dates.add(day);
        }
        return dates;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        mJobListSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh()
            {
                mJobListSwipeRefreshLayout.setRefreshing(true);
                requestProRequestedJobs();
            }
        });
        mJobListSwipeRefreshLayout.setColorSchemeResources(R.color.handy_blue);
    }

    @OnClick(R.id.fragment_dialog_requested_jobs_dismiss_button)
    public void onDismissButtonClicked()
    {
        dismiss();
    }
}
