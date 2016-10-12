package com.handy.portal.bookings.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;
import com.handy.portal.bookings.BookingEvent;
import com.handy.portal.bookings.manager.BookingManager;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.bookings.model.BookingsWrapper;
import com.handy.portal.bookings.ui.element.ProRequestedJobsExpandableListView;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewPage;
import com.handy.portal.constant.TransitionStyle;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.NavigationEvent;
import com.handy.portal.library.ui.widget.SafeSwipeRefreshLayout;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.RequestedJobsLog;
import com.handy.portal.model.ConfigurationResponse;
import com.handy.portal.ui.fragment.ActionBarFragment;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.handy.portal.bookings.ui.adapter.ProRequestedJobsExpandableListAdapter.Event;

public class ProRequestedJobsFragment extends ActionBarFragment
{
    @BindView(R.id.fragment_pro_requested_jobs_list_view)
    ProRequestedJobsExpandableListView mProRequestedJobsExpandableListView;
    @BindView(R.id.pro_requested_bookings_empty)
    SafeSwipeRefreshLayout mEmptyJobsSwipeRefreshLayout;
    @BindView(R.id.fragment_pro_requested_jobs_list_swipe_refresh_layout)
    SafeSwipeRefreshLayout mJobListSwipeRefreshLayout;
    @BindView(R.id.loading_overlay)
    RelativeLayout mLoadingOverlay;
    @BindView(R.id.fetch_error_view)
    LinearLayout mFetchErrorView;
    @BindView(R.id.fetch_error_text)
    TextView mFetchErrorText;

    private View mFragmentView; //this saves the exact view state including the scroll position

    @Override
    protected MainViewPage getAppPage()
    {
        return MainViewPage.REQUESTED_JOBS;
    }

    private ExpandableListView.OnChildClickListener onProRequestedJobsListChildClickListener = new ExpandableListView.OnChildClickListener()
    {
        @Override
        public boolean onChildClick(final ExpandableListView parent, final View v, final int groupPosition, final int childPosition, final long id)
        {
            ExpandableListAdapter expandableListAdapter = parent.getExpandableListAdapter();
            Booking booking = (Booking) expandableListAdapter.getChild(groupPosition, childPosition);
            if (booking != null)
            {
                navigateToJobDetails(booking);
            }
            return true;
        }
    };

    private SwipeRefreshLayout.OnRefreshListener onProRequestedJobsListRefreshListener = new SwipeRefreshLayout.OnRefreshListener()
    {
        @Override
        public void onRefresh()
        {
            setRefreshingIndicator(true);
            requestProRequestedJobs(false);
        }
    };

    /**
     * updates and shows the job list view based on the given jobs list
     *
     * @param jobList sorted by date
     */
    private void updateJobListView(@NonNull List<BookingsWrapper> jobList)
    {
        List<BookingsWrapper> filteredJobList = new ArrayList<>(jobList.size());
        for (BookingsWrapper bookingsWrapper : jobList)
        {
            if (bookingsWrapper.getBookings().size() > 0)
            {
                filteredJobList.add(bookingsWrapper);
            }
        }

        mProRequestedJobsExpandableListView.setData(filteredJobList);
        if (filteredJobList.isEmpty())
        {
            showContentViewAndHideOthers(mEmptyJobsSwipeRefreshLayout);
        }
        else
        {
            showContentViewAndHideOthers(mJobListSwipeRefreshLayout);
            mProRequestedJobsExpandableListView.setOnChildClickListener(onProRequestedJobsListChildClickListener);
        }
    }

    private void navigateToJobDetails(@NonNull Booking booking)
    {
        Bundle arguments = new Bundle();
        arguments.putSerializable(BundleKeys.BOOKING, booking);
        arguments.putString(BundleKeys.BOOKING_ID, booking.getId());
        arguments.putString(BundleKeys.BOOKING_TYPE, booking.getType().toString());
        arguments.putLong(BundleKeys.BOOKING_DATE, booking.getStartDate().getTime());
        bus.post(new LogEvent.AddLogEvent(new RequestedJobsLog.Clicked(booking)));
        bus.post(new NavigationEvent.NavigateToPage(MainViewPage.JOB_DETAILS, arguments,
                TransitionStyle.JOB_LIST_TO_DETAILS, true));
    }

    @NonNull
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
    {
        //this saves the exact view state, including scroll position
        if (mFragmentView == null)
        {
            mFragmentView = inflater.inflate(R.layout.fragment_pro_requested_jobs_inbox, container, false);
        }
        return mFragmentView;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        //initialize swipe refresh layout
        mJobListSwipeRefreshLayout.setOnRefreshListener(onProRequestedJobsListRefreshListener);
        mEmptyJobsSwipeRefreshLayout.setOnRefreshListener(onProRequestedJobsListRefreshListener);
        mJobListSwipeRefreshLayout.setColorSchemeResources(R.color.handy_blue);
        mEmptyJobsSwipeRefreshLayout.setColorSchemeResources(R.color.handy_blue);

        final ConfigurationResponse configuration = configManager.getConfigurationResponse();
        if (configuration != null
                && configuration.getRequestDismissal() != null
                && configuration.getRequestDismissal().isEnabled())
        {
            mProRequestedJobsExpandableListView.setDivider(null);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        bus.register(this);

        //this fragment doesn't use the universal overlay, so make sure it's hidden
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));

        setActionBar(R.string.your_requests, false);
        mJobListSwipeRefreshLayout.setRefreshing(false);
        if (!mProRequestedJobsExpandableListView.hasValidData())
        {
            showContentViewAndHideOthers(mLoadingOverlay);
            requestProRequestedJobs(true);
        }
    }

    @Override
    public void onPause()
    {
        bus.unregister(this);
        super.onPause();
    }

    /**
     * hides all the content views in this fragment
     * except the given content view
     */
    private void showContentViewAndHideOthers(@NonNull View contentView)
    {
        hideAllContentViews();
        contentView.setVisibility(View.VISIBLE);
    }

    /**
     * hides all the content views in this fragment
     */
    private void hideAllContentViews()
    {
        mEmptyJobsSwipeRefreshLayout.setVisibility(View.GONE);
        mJobListSwipeRefreshLayout.setVisibility(View.GONE);
        mFetchErrorView.setVisibility(View.GONE);
        mLoadingOverlay.setVisibility(View.GONE);
    }

    /**
     * requests jobs for which this pro was requested by customers for
     */
    private void requestProRequestedJobs(boolean useCachedIfPresent)
    {
        List<Date> datesForBookings = getDatesForBookings();
        bus.post(new BookingEvent.RequestProRequestedJobs(datesForBookings, useCachedIfPresent));
    }

    private List<Date> getDatesForBookings()
    {
        if (configManager.getConfigurationResponse() != null)
        {
            int numDaysForRequestedJobs = configManager.getConfigurationResponse()
                    .getNumberOfDaysForRequestedJobs();
            if (numDaysForRequestedJobs != 0)
            {
                return DateTimeUtils.getDateWithoutTimeList(new Date(),
                        numDaysForRequestedJobs);
            }
        }
        return DateTimeUtils.getDateWithoutTimeList(new Date(), BookingManager.REQUESTED_JOBS_NUM_DAYS_IN_ADVANCE);
    }

    @Subscribe
    public void onReceiveProRequestedJobsSuccess(BookingEvent.ReceiveProRequestedJobsSuccess event)
    {
        setRefreshingIndicator(false);
        List<BookingsWrapper> proRequestedJobsList = event.getProRequestedJobs();
        if (proRequestedJobsList == null)
        {
            Crashlytics.logException(new Exception("pro requested jobs list is null"));
            onError(new DataManager.DataManagerError(DataManager.DataManagerError.Type.SERVER));
        }
        else
        {
            updateJobListView(event.getProRequestedJobs());
        }
    }

    @Subscribe
    public void onReceiveProRequestedJobsError(BookingEvent.ReceiveProRequestedJobsError event)
    {
        onError(event.error);
    }

    @Subscribe
    public void onRequestedJobClaimClicked(final Event.RequestedJobClaimClicked event)
    {
        // FIXME: Claim job
    }

    @Subscribe
    public void onRequestedJobDismissClicked(final Event.RequestedJobDismissClicked event)
    {
        // FIXME: Dismiss job
    }

    /**
     * need this because the empty view is also a swipe refresh layout
     * and on error we don't know which refresh layout
     * triggered the request
     */
    private void setRefreshingIndicator(boolean isRefreshing)
    {
        mEmptyJobsSwipeRefreshLayout.setRefreshing(isRefreshing);
        mJobListSwipeRefreshLayout.setRefreshing(isRefreshing);
    }

    private void onError(DataManager.DataManagerError error)
    {
        setRefreshingIndicator(false);

        //show the try again error screen
        if (error != null && error.getType() == DataManager.DataManagerError.Type.NETWORK)
        {
            mFetchErrorText.setText(R.string.error_fetching_connectivity_issue);
        }
        else
        {
            mFetchErrorText.setText(R.string.an_error_has_occurred);
        }
        showContentViewAndHideOthers(mFetchErrorView);
    }

    @OnClick(R.id.try_again_button)
    public void onFetchErrorViewTryAgainButtonClicked()
    {
        showContentViewAndHideOthers(mLoadingOverlay);
        requestProRequestedJobs(false);
    }
}
