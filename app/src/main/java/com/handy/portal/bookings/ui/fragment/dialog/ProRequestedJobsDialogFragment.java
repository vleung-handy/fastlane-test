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
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;
import com.handy.portal.bookings.BookingEvent;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.bookings.model.BookingsWrapper;
import com.handy.portal.bookings.ui.element.ProRequestedJobsExpandableListView;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.constant.TransitionStyle;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.NavigationEvent;
import com.handy.portal.library.ui.fragment.dialog.SlideUpDialogFragment;
import com.handy.portal.library.ui.widget.SafeSwipeRefreshLayout;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.library.util.Utils;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;

public class ProRequestedJobsDialogFragment extends SlideUpDialogFragment
{
    public static final String FRAGMENT_TAG = ProRequestedJobsDialogFragment.class.getName();

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
    @Bind(R.id.fetch_error_text)
    TextView mFetchErrorText;

    @Inject
    protected Bus mBus;

    private View mFragmentView; //this saves the exact view state including the scroll position

    private ExpandableListView.OnChildClickListener onProRequestedJobsListChildClickListener = new ExpandableListView.OnChildClickListener() {
        @Override
        public boolean onChildClick(final ExpandableListView parent, final View v, final int groupPosition, final int childPosition, final long id)
        {
            ExpandableListAdapter expandableListAdapter = parent.getExpandableListAdapter();
            Booking booking = (Booking) expandableListAdapter.getChild(groupPosition, childPosition);
            if(booking != null)
            {
                navigateToJobDetails(booking);
            }
            return true;
        }
    };

    private SwipeRefreshLayout.OnRefreshListener onProRequestedJobsListRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh()
        {
            mJobListSwipeRefreshLayout.setRefreshing(true);
            requestProRequestedJobs(false);
        }
    };

    public static ProRequestedJobsDialogFragment newInstance()
    {
        return new ProRequestedJobsDialogFragment();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Utils.inject(getActivity(), this);
    }

    /**
     * updates and shows the job list view based on the given jobs list
     *
     * @param jobList sorted by date
     */
    private void updateJobListView(@NonNull List<BookingsWrapper> jobList)
    {
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
            showContentViewAndHideOthers(mJobsEmptyView);
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
        arguments.putString(BundleKeys.BOOKING_ID, booking.getId());
        arguments.putString(BundleKeys.BOOKING_TYPE, booking.getType().toString());
        arguments.putLong(BundleKeys.BOOKING_DATE, booking.getStartDate().getTime());
        mBus.post(new NavigationEvent.NavigateToTab(MainViewTab.JOB_DETAILS, arguments,
                TransitionStyle.JOB_LIST_TO_DETAILS, true));
    }

    @Override
    protected View inflateContentView(final LayoutInflater inflater, final ViewGroup container)
    {
        return inflater.inflate(R.layout.fragment_dialog_pro_requested_jobs, container, false);
    }

    @NonNull
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
    {
        //this saves the exact view state, including scroll position
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
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        //initialize swipe refresh layout
        mJobListSwipeRefreshLayout.setOnRefreshListener(onProRequestedJobsListRefreshListener);
        mJobListSwipeRefreshLayout.setColorSchemeResources(R.color.handy_blue);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Window window = getDialog().getWindow();

        //force it to be full screen
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        mBus.register(this);

        mJobListSwipeRefreshLayout.setRefreshing(false);
        if(!mProRequestedJobsExpandableListView.hasValidData())
        {
            showContentViewAndHideOthers(mLoadingOverlay);
            requestProRequestedJobs(true);
        }
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

    /**
     * hides all the content views in this fragment
     */
    private void hideAllContentViews()
    {
        mJobsEmptyView.setVisibility(View.GONE);
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
        mBus.post(new BookingEvent.RequestProRequestedJobs(datesForBookings, useCachedIfPresent));
    }

    private List<Date> getDatesForBookings()
    {
        return DateTimeUtils.getDateWithoutTimeList(new Date(), getNumDaysToDisplay());
    }

    //FIXME: get this as a config param from the server instead
    protected int getNumDaysToDisplay()
    {
        return 28;
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
        List<BookingsWrapper> proRequestedJobsList = event.getProRequestedJobs();
        if(proRequestedJobsList == null)
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

    private void onError(DataManager.DataManagerError error)
    {
        mJobListSwipeRefreshLayout.setRefreshing(false);

        //show the try again error screen
        if(error != null && error.getType() == DataManager.DataManagerError.Type.NETWORK)
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

    @OnClick(R.id.fragment_dialog_requested_jobs_dismiss_button)
    public void onDismissButtonClicked()
    {
        dismiss();
    }
}
