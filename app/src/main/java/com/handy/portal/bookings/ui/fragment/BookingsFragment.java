package com.handy.portal.bookings.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.common.collect.Lists;
import com.handy.portal.R;
import com.handy.portal.bookings.BookingEvent;
import com.handy.portal.bookings.manager.BookingManager;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.bookings.model.BookingsWrapper;
import com.handy.portal.bookings.ui.element.BookingElementView;
import com.handy.portal.bookings.ui.element.BookingListView;
import com.handy.portal.core.constant.BundleKeys;
import com.handy.portal.core.constant.MainViewPage;
import com.handy.portal.core.constant.TransitionStyle;
import com.handy.portal.core.event.HandyEvent;
import com.handy.portal.core.event.ProviderSettingsEvent;
import com.handy.portal.core.manager.ConfigManager;
import com.handy.portal.core.manager.PageNavigationManager;
import com.handy.portal.core.manager.PrefsManager;
import com.handy.portal.core.model.ProviderSettings;
import com.handy.portal.core.ui.activity.MainActivity;
import com.handy.portal.core.ui.element.DateButtonView;
import com.handy.portal.core.ui.fragment.ActionBarFragment;
import com.handy.portal.data.DataManager;
import com.handy.portal.deeplink.DeeplinkUtils;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.library.util.UIUtils;
import com.handy.portal.logger.handylogger.model.AvailableJobsLog;
import com.handy.portal.logger.handylogger.model.EventContext;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@Deprecated
public abstract class BookingsFragment<T extends HandyEvent.ReceiveBookingsSuccess> extends ActionBarFragment {
    private static int SNACK_BAR_DURATION = 500;

    @Inject
    ConfigManager mConfigManager;
    @Inject
    PrefsManager mPrefsManager;
    @Inject
    BookingManager mBookingManager;
    @Inject
    PageNavigationManager mNavigationManager;

    @BindView(R.id.fetch_error_view)
    View mFetchErrorView;
    @BindView(R.id.fetch_error_text)
    TextView mErrorText;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout mRefreshLayout;
    @BindView(R.id.bookings_content)
    LinearLayout mBookingsContent;

    protected String mMessage;

    @DrawableRes
    protected int mMessageIconRes = Integer.MIN_VALUE;
    private Runnable mRefreshRunnable = new Runnable() {
        @Override
        public void run() {
            setRefreshing(true);
        }
    };

    protected abstract int getFragmentResourceId();

    protected abstract BookingListView getBookingListView();

    protected abstract SwipeRefreshLayout getNoBookingsSwipeRefreshLayout();

    protected abstract LinearLayout getDatesLayout();

    @NonNull
    protected abstract String getTrackingType();

    protected abstract void requestBookings(List<Date> dates, boolean useCachedIfPresent);

    protected abstract boolean shouldShowRequestedIndicator(List<Booking> bookingsForDay);

    protected abstract boolean shouldShowClaimedIndicator(List<Booking> bookingsForDay);

    protected abstract int numberOfDaysToDisplay();

    protected abstract void beforeRequestBookings();

    protected void afterDisplayBookings(List<Booking> bookingsForDay, Date dateOfBookings) {
        if (mMessage != null) {
            final Snackbar snackbar = Snackbar.make(
                    mBookingsContent,
                    mMessage, Snackbar.LENGTH_LONG
            );

            snackbar.getView().setBackgroundColor(ContextCompat.getColor(getContext(), R.color.snack_bar_gray));

            //set the snack bar image
            if (mMessageIconRes > Integer.MIN_VALUE) {
                int padding = getResources().getDimensionPixelOffset(R.dimen.default_padding);
                UIUtils.setSnackbarImage(snackbar, mMessageIconRes, padding);
            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    snackbar.show();
                }
            }, SNACK_BAR_DURATION);

            if (mMessage.equals(getString(R.string.job_no_longer_available))) {
                final Bundle extras = getArguments().getBundle(BundleKeys.EXTRAS);
                bus.post(
                        new AvailableJobsLog.UnavailableJobNoticeShown(extras));
            }
            mMessage = null; // this is a one-off
        }
    }

    protected abstract Class<? extends BookingElementView> getBookingElementViewClass();

    protected abstract String getBookingSourceName();

    //Event listeners
    public abstract void onBookingsRetrieved(T event);

    //should use date without time for these entries, see Utils.getDateWithoutTime
    protected Map<Date, DateButtonView> mDateDateButtonViewMap;
    protected Date mSelectedDay;
    protected List<Booking> mBookingsForSelectedDay;
    protected ProviderSettings mProviderSettings;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMessage = getArguments().getString(BundleKeys.MESSAGE);
        mMessageIconRes = getArguments().getInt(BundleKeys.MESSAGE_ICON);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(getFragmentResourceId(), null);
        ButterKnife.bind(this, view);

        //Optional param, needs to be validated
        initializeSelectedDate();

        final SwipeRefreshLayout.OnRefreshListener refreshListener =
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        requestBookingsForSelectedDay(false, false);
                    }
                };
        final SwipeRefreshLayout noBookingsSwipeRefreshLayout = getNoBookingsSwipeRefreshLayout();
        mRefreshLayout.setOnRefreshListener(refreshListener);
        noBookingsSwipeRefreshLayout.setOnRefreshListener(refreshListener);
        mRefreshLayout.setColorSchemeResources(R.color.handy_blue);
        noBookingsSwipeRefreshLayout.setColorSchemeResources(R.color.handy_blue);

        return view;
    }

    /**
     * Arguments can be passed in to aid which day we want to be "selected".
     */
    private void initializeSelectedDate() {
        if (getArguments() != null) {
            long targetDateTime = getArguments().getLong(BundleKeys.DATE_EPOCH_TIME);
            if (targetDateTime > 0) {
                mSelectedDay = DateTimeUtils.getDateWithoutTime(new Date(targetDateTime));
            }

            //the BundleKeys.DATE_EPOCH_TIME will never collide with DeeplinkUtils.DEEP_LINK_PARAM_DAY
            //so we don't have to worry about things getting overriden.
            String daysToAdd = getArguments().getString(DeeplinkUtils.DEEP_LINK_PARAM_DAY, null);
            if (daysToAdd != null) {
                try {
                    int dta = Integer.parseInt(daysToAdd);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date());
                    calendar.add(Calendar.DATE, dta);
                    mSelectedDay = DateTimeUtils.getDateWithoutTime(calendar.getTime());
                }
                catch (Exception e) {
                    //anything that goes wrong here, we just ignore and move on.
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!MainActivity.clearingBackStack) {
            bus.post(new ProviderSettingsEvent.RequestProviderSettings());

            initDateButtons();

            if (mSelectedDay == null || (mDateDateButtonViewMap != null
                    && !mDateDateButtonViewMap.containsKey(mSelectedDay))) {
                mSelectedDay = DateTimeUtils.getDateWithoutTime(new Date());
            }

            if (mDateDateButtonViewMap != null && mDateDateButtonViewMap.containsKey(mSelectedDay)) {
                mDateDateButtonViewMap.get(mSelectedDay).setChecked(true);
            }

            requestAllBookings();
        }
    }

    @OnClick(R.id.try_again_button)
    public void doRequestBookingsAgain() {
        requestBookingsForSelectedDay(true, true);
    }

    private void requestAllBookings() {
        requestBookingsForSelectedDay(true, true);
        requestBookingsForOtherDays(mSelectedDay);
    }

    private void requestBookingsForSelectedDay(boolean refreshing, boolean useCachedIfPresent) {
        if (mSelectedDay != null) {
            requestBookings(Lists.newArrayList(mSelectedDay), refreshing, useCachedIfPresent);
        }
    }

    private void requestBookingsForOtherDays(Date dayToExclude) {
        List<Date> dates = Lists.newArrayList();
        for (int i = 0; i < numberOfDaysToDisplay(); i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DATE, i);
            Date day = DateTimeUtils.getDateWithoutTime(calendar.getTime());

            if (!day.equals(dayToExclude)) {
                dates.add(day);
            }
        }
        requestBookings(dates, false, true);
    }

    private void requestBookings(List<Date> dates, boolean refreshing, boolean useCachedIfPresent) {
        Crashlytics.log("Requesting bookings for the following dates" + dates.toString());
        if (mFetchErrorView == null) {
            Crashlytics.logException(
                    new NullPointerException("All views are null due to ButterKnife unbind."));
            return;
        }
        mFetchErrorView.setVisibility(View.GONE);
        if (refreshing) {
            getBookingListView().setAdapter(null);
            getNoBookingsSwipeRefreshLayout().setVisibility(View.GONE);
            // this delay will prevent the refreshing icon to flicker when loading cached data
            getBookingListView().postDelayed(mRefreshRunnable, 200);
        }
        requestBookings(dates, useCachedIfPresent);
    }

    protected void handleBookingsRetrieved(T event) {
        BookingsWrapper bookingsWrapper = event.bookingsWrapper;
        if (bookingsWrapper == null || event.day == null) {
            Crashlytics.logException(new Exception("on receive bookings success bookings wrapper or day is null"));
            return;
        }

        getBookingListView().removeCallbacks(mRefreshRunnable);
        List<Booking> bookings = event.bookingsWrapper.getBookings();
        Collections.sort(bookings);

        for (Booking b : bookings) {
            if (b.getZipClusterId() != null) {
                bus.post(new BookingEvent.RequestZipClusterPolygons(b.getZipClusterId()));
            }
        }

        if (mDateDateButtonViewMap != null) {
            DateButtonView dateButtonView = mDateDateButtonViewMap.get(event.day);
            if (dateButtonView != null) {
                dateButtonView.showRequestedIndicator(shouldShowRequestedIndicator(bookings));
                dateButtonView.showClaimedIndicator(shouldShowClaimedIndicator(bookings));
            }
            else {
                Crashlytics.logException(new RuntimeException("Date button for " + event.day + " not found"));
            }
        }

        if (mSelectedDay != null && mSelectedDay.equals(event.day)) {
            setRefreshing(false);
            displayBookings(bookingsWrapper, mSelectedDay);
        }
    }

    protected void handleBookingsRetrievalError(HandyEvent.ReceiveBookingsError event, int errorStateStringId) {
        if (event.days.contains(mSelectedDay)) {
            setRefreshing(false);
            if (event.error != null && event.error.getType() == DataManager.DataManagerError.Type.NETWORK) {
                mErrorText.setText(R.string.error_fetching_connectivity_issue);
            }
            else {
                mErrorText.setText(errorStateStringId);
            }
            mFetchErrorView.setVisibility(View.VISIBLE);
        }
    }

    protected void initDateButtons() {
        LinearLayout datesLayout = getDatesLayout();
        datesLayout.removeAllViews();

        mDateDateButtonViewMap = new HashMap<>(numberOfDaysToDisplay());

        Context context = getActivity();

        for (int i = 0; i < numberOfDaysToDisplay(); i++) {
            LayoutInflater.from(context).inflate(R.layout.element_date_button, datesLayout);
            final DateButtonView dateButtonView = (DateButtonView) datesLayout.getChildAt(datesLayout.getChildCount() - 1);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DATE, i);
            final Date day = DateTimeUtils.getDateWithoutTime(calendar.getTime());

            dateButtonView.init(day);
            dateButtonView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    onDateClicked(day);
                }
            });

            mDateDateButtonViewMap.put(day, dateButtonView);
        }
    }

    protected void onDateClicked(final Date day) {
        selectDay(day);
        beforeRequestBookings();
        requestBookings(Lists.newArrayList(day), true, true);
    }

    private void selectDay(Date day) {
        if (mDateDateButtonViewMap != null) {
            DateButtonView selectedDateButtonView = mDateDateButtonViewMap.get(mSelectedDay);
            if (selectedDateButtonView != null) {
                selectedDateButtonView.setChecked(false);
            }
            mDateDateButtonViewMap.get(day).setChecked(true);
        }
        mSelectedDay = day;
    }

    /**
     * updates the bookings view with the given list of bookings for the given date
     */
    protected void displayBookings(@NonNull BookingsWrapper bookingsWrapper, @NonNull Date dateOfBookings) {
        List<Booking> bookings = bookingsWrapper.getBookings();
        mBookingsForSelectedDay = bookings;
        getBookingListView().populateList(bookings, getBookingElementViewClass());
        initListClickListener();
        getNoBookingsSwipeRefreshLayout().setVisibility(bookings.size() > 0 ? View.GONE : View.VISIBLE);
        afterDisplayBookings(bookings, dateOfBookings);
    }

    private void initListClickListener() {
        getBookingListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                Booking booking = (Booking) adapter.getItemAtPosition(position);
                if (booking != null) {
                    int oneBasedIndex = position + 1;
                    if (getTrackingType().equalsIgnoreCase(getString(R.string.available_job))) {
                        bus.post(new AvailableJobsLog.Clicked(booking, oneBasedIndex));
                    }
                    showBookingDetails(booking);
                }
            }
        });
    }

    private void showBookingDetails(Booking booking) {
        Bundle arguments = new Bundle();
        arguments.putString(BundleKeys.BOOKING_ID, booking.getId());
        arguments.putString(BundleKeys.BOOKING_TYPE, booking.getType().toString());
        arguments.putLong(BundleKeys.BOOKING_DATE, booking.getStartDate().getTime());
        arguments.putString(BundleKeys.BOOKING_SOURCE, getBookingSourceName());
        arguments.putString(BundleKeys.EVENT_CONTEXT, EventContext.AVAILABLE_JOBS);
        arguments.putSerializable(BundleKeys.PAGE, getAppPage());
        mNavigationManager.navigateToPage(getActivity().getSupportFragmentManager(),
                MainViewPage.JOB_DETAILS, arguments, TransitionStyle.JOB_LIST_TO_DETAILS, true);
    }

    private void setRefreshing(final boolean refreshing) {
        mRefreshLayout.setRefreshing(refreshing);
        getNoBookingsSwipeRefreshLayout().setRefreshing(refreshing);
    }
}
