package com.handy.portal.clients.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;
import com.handy.portal.availability.manager.AvailabilityManager;
import com.handy.portal.bookings.BookingEvent;
import com.handy.portal.bookings.manager.BookingManager;
import com.handy.portal.bookings.manager.BookingManager.DismissalReason;
import com.handy.portal.bookings.model.AuxiliaryInfo;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.bookings.model.BookingsWrapper;
import com.handy.portal.bookings.util.BookingListUtils;
import com.handy.portal.bookings.util.ClaimUtils;
import com.handy.portal.clients.ui.adapter.RequestedJobsRecyclerViewAdapter;
import com.handy.portal.clients.ui.fragment.dialog.RequestDismissalReasonsDialogFragment;
import com.handy.portal.clients.ui.fragment.dialog.RescheduleDialogFragment;
import com.handy.portal.core.constant.BundleKeys;
import com.handy.portal.core.constant.MainViewPage;
import com.handy.portal.core.constant.RequestCode;
import com.handy.portal.core.constant.TransitionStyle;
import com.handy.portal.core.event.HandyEvent;
import com.handy.portal.core.manager.PageNavigationManager;
import com.handy.portal.data.DataManager;
import com.handy.portal.library.ui.fragment.InjectedFragment;
import com.handy.portal.library.ui.widget.SafeSwipeRefreshLayout;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.library.util.FragmentUtils;
import com.handy.portal.logger.handylogger.model.EventContext;
import com.handy.portal.logger.handylogger.model.EventType;
import com.handy.portal.logger.handylogger.model.JobsLog;
import com.handy.portal.logger.handylogger.model.RequestedJobsLog;
import com.handy.portal.logger.handylogger.model.SendAvailabilityLog;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProRequestedJobsFragment extends InjectedFragment {
    @Inject
    BookingManager mBookingManager;
    @Inject
    AvailabilityManager mAvailabilityManager;
    @Inject
    PageNavigationManager mNavigationManager;

    @BindView(R.id.fragment_pro_requested_jobs_recycler_view)
    RecyclerView mRequestedJobsRecyclerView;
    @BindView(R.id.pro_requested_bookings_empty)
    SafeSwipeRefreshLayout mEmptyJobsSwipeRefreshLayout;
    @BindView(R.id.fragment_pro_requested_jobs_list_swipe_refresh_layout)
    SafeSwipeRefreshLayout mJobListSwipeRefreshLayout;
    @BindView(R.id.fetch_error_view)
    LinearLayout mFetchErrorView;
    @BindView(R.id.fetch_error_text)
    TextView mFetchErrorText;

    private View mFragmentView; //this saves the exact view state including the scroll position
    private RequestedJobsRecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private int mUnreadJobsCount;
    private SwipeRefreshLayout.OnRefreshListener onProRequestedJobsListRefreshListener;
    private RequestedJobsRecyclerViewAdapter.JobViewHolder.Listener mJobViewHolderListener;

    {
        onProRequestedJobsListRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestProRequestedJobs(false);
            }
        };
        mJobViewHolderListener = new RequestedJobsRecyclerViewAdapter.JobViewHolder.Listener() {
            @Override
            public void onSelect(final Booking booking) {
                navigateToJobDetails(booking);
            }

            @Override
            public void onClaim(final Booking booking) {
                boolean confirmClaimDialogShown =
                        ClaimUtils.showConfirmBookingClaimDialogIfNecessary(
                                booking, ProRequestedJobsFragment.this, getFragmentManager()
                        );
                if (!confirmClaimDialogShown) {
                    requestClaimJob(booking);
                }
            }

            @Override
            public void onDismiss(final Booking booking) {
                if (booking.getRequestAttributes() != null
                        && booking.getRequestAttributes().hasCustomer()) {
                    // Display dialog for selecting a request dismissal reason
                    final RequestDismissalReasonsDialogFragment dialogFragment =
                            RequestDismissalReasonsDialogFragment.newInstance(booking);
                    dialogFragment.setTargetFragment(
                            ProRequestedJobsFragment.this, RequestCode.CONFIRM_DISMISS
                    );
                    FragmentUtils.safeLaunchDialogFragment(
                            dialogFragment, ProRequestedJobsFragment.this, null
                    );
                    bus.post(new RequestedJobsLog.DismissJobShown(
                            EventContext.REQUESTED_JOBS, booking
                    ));
                }
                else {
                    dismissJob(booking);
                }
            }

            @Override
            public void onReschedule(final Booking booking) {
                FragmentUtils.safeLaunchDialogFragment(
                        RescheduleDialogFragment.newInstance(booking),
                        ProRequestedJobsFragment.this,
                        null
                );
                bus.post(new SendAvailabilityLog.SendAvailabilitySelected(
                        EventContext.REQUESTED_JOBS, booking)
                );
            }
        };
    }

    public static ProRequestedJobsFragment newInstance() {
        return new ProRequestedJobsFragment();
    }

    /**
     * updates and shows the job list view based on the given jobs list
     *
     * @param jobList sorted by date
     */
    private void updateJobListView(@NonNull List<BookingsWrapper> jobList) {
        mUnreadJobsCount = 0;
        List<BookingsWrapper> filteredJobList = new ArrayList<>(jobList.size());
        for (BookingsWrapper bookingsWrapper : jobList) {
            final int unreadJobsCountForDate = bookingsWrapper.getUndismissedBookings().size();
            if (unreadJobsCountForDate > 0) {
                mUnreadJobsCount += unreadJobsCountForDate;
                filteredJobList.add(bookingsWrapper);
            }
        }
        bus.post(new BookingEvent.ReceiveProRequestedJobsCountSuccess(mUnreadJobsCount));
        mAdapter = new RequestedJobsRecyclerViewAdapter(getActivity(), filteredJobList, mJobViewHolderListener);
        mRequestedJobsRecyclerView.setAdapter(mAdapter);
        if (filteredJobList.isEmpty()) {
            showContentViewAndHideOthers(mEmptyJobsSwipeRefreshLayout);
        }
        else {
            showContentViewAndHideOthers(mJobListSwipeRefreshLayout);
        }
        bus.post(
                new RequestedJobsLog.RequestsShown(
                        EventContext.REQUESTED_JOBS,
                        mUnreadJobsCount,
                        BookingListUtils.getOverallCountPerAuxType(filteredJobList, AuxiliaryInfo.Type.REFERRAL),
                        BookingListUtils.getOverallCountPerAuxType(filteredJobList, AuxiliaryInfo.Type.FAVORITE),
                        null
                )
        );
    }

    private void navigateToJobDetails(@NonNull Booking booking) {
        Bundle arguments = new Bundle();
        arguments.putSerializable(BundleKeys.BOOKING, booking);
        arguments.putString(BundleKeys.BOOKING_ID, booking.getId());
        arguments.putString(BundleKeys.BOOKING_TYPE, booking.getType().toString());
        arguments.putLong(BundleKeys.BOOKING_DATE, booking.getStartDate().getTime());
        arguments.putString(BundleKeys.EVENT_CONTEXT, EventContext.REQUESTED_JOBS);
        bus.post(new RequestedJobsLog.Clicked(booking));
        mNavigationManager.navigateToPage(getActivity().getSupportFragmentManager(),
                MainViewPage.JOB_DETAILS, arguments, TransitionStyle.JOB_LIST_TO_DETAILS, true);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bus.register(this);
    }

    @NonNull
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        //this saves the exact view state, including scroll position
        if (mFragmentView == null) {
            mFragmentView = inflater.inflate(R.layout.fragment_pro_requested_jobs_inbox, container, false);
        }
        return mFragmentView;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        //initialize swipe refresh layout
        mJobListSwipeRefreshLayout.setOnRefreshListener(onProRequestedJobsListRefreshListener);
        mEmptyJobsSwipeRefreshLayout.setOnRefreshListener(onProRequestedJobsListRefreshListener);
        mJobListSwipeRefreshLayout.setColorSchemeResources(R.color.handy_blue);
        mEmptyJobsSwipeRefreshLayout.setColorSchemeResources(R.color.handy_blue);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRequestedJobsRecyclerView.setLayoutManager(mLayoutManager);
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        mJobListSwipeRefreshLayout.setRefreshing(false);
        if (mAdapter == null) {
            showContentViewAndHideOthers(mJobListSwipeRefreshLayout);
            mJobListSwipeRefreshLayout.setRefreshing(true);
            requestProRequestedJobs(true);
        }
        if (!mAvailabilityManager.isReady()) {
            // We need this in case the pro decides to reschedule.
            // See usage of AvailabilityManager in RescheduleDialogFragment.
            mAvailabilityManager.getAvailability(false, null);
        }
    }

    @Override
    public void onDestroy() {
        bus.unregister(this);
        super.onDestroy();
    }

    /**
     * hides all the content views in this fragment
     * except the given content view
     */
    private void showContentViewAndHideOthers(@NonNull View contentView) {
        hideAllContentViews();
        contentView.setVisibility(View.VISIBLE);
    }

    /**
     * hides all the content views in this fragment
     */
    private void hideAllContentViews() {
        mEmptyJobsSwipeRefreshLayout.setVisibility(View.GONE);
        mJobListSwipeRefreshLayout.setVisibility(View.GONE);
        mFetchErrorView.setVisibility(View.GONE);
    }

    /**
     * requests jobs for which this pro was requested by customers for
     */
    private void requestProRequestedJobs(boolean useCachedIfPresent) {
        setRefreshingIndicator(true);
        List<Date> datesForBookings = getDatesForBookings();
        mBookingManager.requestProRequestedJobs(datesForBookings, useCachedIfPresent);
    }

    private List<Date> getDatesForBookings() {
        if (configManager.getConfigurationResponse() != null) {
            int numDaysForRequestedJobs = configManager.getConfigurationResponse()
                    .getNumberOfDaysForRequestedJobs();
            if (numDaysForRequestedJobs != 0) {
                return DateTimeUtils.getDateWithoutTimeList(new Date(),
                        numDaysForRequestedJobs);
            }
        }
        return DateTimeUtils.getDateWithoutTimeList(new Date(), BookingManager.REQUESTED_JOBS_NUM_DAYS_IN_ADVANCE);
    }

    @Subscribe
    public void onReceiveProRequestedJobsSuccess(BookingEvent.ReceiveProRequestedJobsSuccess event) {
        setRefreshingIndicator(false);
        List<BookingsWrapper> proRequestedJobsList = event.getProRequestedJobs();
        if (proRequestedJobsList == null) {
            Crashlytics.logException(new Exception("pro requested jobs list is null"));
            onError(new DataManager.DataManagerError(DataManager.DataManagerError.Type.SERVER));
        }
        else {
            updateJobListView(event.getProRequestedJobs());
        }
    }

    @Subscribe
    public void onReceiveProRequestedJobsError(BookingEvent.ReceiveProRequestedJobsError event) {
        onError(event.error);
    }

    private void requestClaimJob(final Booking booking) {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        bus.post(new JobsLog(EventType.CLAIM_SUBMITTED,
                EventContext.REQUESTED_JOBS, booking));
        mBookingManager.requestClaimJob(booking, null);
    }

    @Subscribe
    public void onReceiveClaimJobSuccess(final HandyEvent.ReceiveClaimJobSuccess event) {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        final Booking booking = event.originalBooking;
        bus.post(new JobsLog(EventType.CLAIM_SUCCESS,
                EventContext.REQUESTED_JOBS, booking));
        bus.post(new BookingEvent.ReceiveProRequestedJobsCountSuccess(--mUnreadJobsCount));
        mAdapter.remove(booking);
        Snackbar.make(mJobListSwipeRefreshLayout, R.string.job_claim_success,
                Snackbar.LENGTH_LONG).show();
        if (mAdapter.getItemCount() == 0) {
            showContentViewAndHideOthers(mEmptyJobsSwipeRefreshLayout);
        }
    }

    @Subscribe
    public void onReceiveClaimJobError(final HandyEvent.ReceiveClaimJobError event) {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        String errorMessage = event.error.getMessage();
        if (TextUtils.isEmpty(errorMessage)) {
            errorMessage = getString(R.string.job_claim_error);
        }
        bus.post(new JobsLog(EventType.CLAIM_ERROR,
                EventContext.REQUESTED_JOBS, event.getBooking()));
        Snackbar.make(mJobListSwipeRefreshLayout, errorMessage, Snackbar.LENGTH_LONG).show();
    }

    @Subscribe
    public void onAvailableHoursSent(final HandyEvent.AvailableHoursSent event) {
        mAdapter.remove(event.getBooking());
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            final Booking booking = (Booking) data.getSerializableExtra(BundleKeys.BOOKING);
            switch (requestCode) {
                case RequestCode.CONFIRM_DISMISS:
                    final String dismissalReason =
                            data.getStringExtra(BundleKeys.DISMISSAL_REASON);
                    dismissJob(booking, dismissalReason);
                    break;
                case RequestCode.CONFIRM_SWAP:
                case RequestCode.CONFIRM_REQUEST:
                    requestClaimJob(booking);
                    break;
            }
        }
    }

    private void dismissJob(final Booking booking) {
        dismissJob(booking, BookingManager.DISMISSAL_REASON_UNSPECIFIED);
    }

    private void dismissJob(@NonNull final Booking booking,
                            @NonNull @DismissalReason final String dismissalReason) {
        bus.post(new RequestedJobsLog.DismissJobSubmitted(
                EventContext.REQUESTED_JOBS, booking, dismissalReason));

        final Booking.RequestAttributes requestAttributes = booking.getRequestAttributes();
        String customerId = null;
        if (requestAttributes != null && requestAttributes.hasCustomer()) {
            customerId = requestAttributes.getCustomerId();
        }

        mBookingManager.requestDismissJob(booking, customerId, dismissalReason);
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
    }

    @Subscribe
    public void onReceiveDismissJobSuccess(final HandyEvent.ReceiveDismissJobSuccess event) {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        final Booking booking = event.getBooking();
        bus.post(new RequestedJobsLog.DismissJobSuccess(
                EventContext.REQUESTED_JOBS, booking));
        bus.post(new BookingEvent.ReceiveProRequestedJobsCountSuccess(--mUnreadJobsCount));
        mAdapter.remove(booking);
        if (BookingManager.DISMISSAL_REASON_BLOCK_CUSTOMER.equals(event.getDismissalReason())) {
            removeBookingsForCustomer(booking.getRequestAttributes().getCustomerId());
        }
        Snackbar.make(mJobListSwipeRefreshLayout, R.string.request_dismissal_success_message,
                Snackbar.LENGTH_LONG).show();
        if (mAdapter.getItemCount() == 0) {
            showContentViewAndHideOthers(mEmptyJobsSwipeRefreshLayout);
        }
    }

    private void removeBookingsForCustomer(final String customerId) {
        final List<Booking> bookingsToRemove = new ArrayList<>();
        for (Object item : mAdapter.getItems()) {
            if (item instanceof Booking) {
                final Booking booking = (Booking) item;
                if (booking.getRequestAttributes() != null
                        && booking.getRequestAttributes().hasCustomer()
                        && booking.getRequestAttributes().getCustomerId().equals(customerId)) {
                    bookingsToRemove.add(booking);
                }
            }
        }
        for (Booking booking : bookingsToRemove) {
            mAdapter.remove(booking);
        }
    }

    @Subscribe
    public void onReceiveDismissJobError(final HandyEvent.ReceiveDismissJobError event) {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        String errorMessage = event.error.getMessage();
        if (TextUtils.isEmpty(errorMessage)) {
            errorMessage = getString(R.string.request_dismissal_error);
        }
        bus.post(new RequestedJobsLog.DismissJobError(
                EventContext.REQUESTED_JOBS, event.getBooking(), errorMessage));
        Snackbar.make(mJobListSwipeRefreshLayout, errorMessage, Snackbar.LENGTH_LONG).show();
    }

    /**
     * need this because the empty view is also a swipe refresh layout
     * and on error we don't know which refresh layout
     * triggered the request
     */
    private void setRefreshingIndicator(boolean isRefreshing) {
        mEmptyJobsSwipeRefreshLayout.setRefreshing(isRefreshing);
        mJobListSwipeRefreshLayout.setRefreshing(isRefreshing);
    }

    private void onError(DataManager.DataManagerError error) {
        setRefreshingIndicator(false);

        //show the try again error screen
        if (error != null && error.getType() == DataManager.DataManagerError.Type.NETWORK) {
            mFetchErrorText.setText(R.string.error_fetching_connectivity_issue);
        }
        else {
            mFetchErrorText.setText(R.string.an_error_has_occurred);
        }
        showContentViewAndHideOthers(mFetchErrorView);
    }

    @OnClick(R.id.try_again_button)
    public void onFetchErrorViewTryAgainButtonClicked() {
        showContentViewAndHideOthers(mJobListSwipeRefreshLayout);
        requestProRequestedJobs(false);
    }
}
