package com.handy.portal.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.common.collect.Lists;
import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.constant.PrefsKey;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.BookingEvent;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.LogEvent;
import com.handy.portal.event.ProviderSettingsEvent;
import com.handy.portal.manager.ConfigManager;
import com.handy.portal.manager.PrefsManager;
import com.handy.portal.model.Booking;
import com.handy.portal.model.ConfigurationResponse;
import com.handy.portal.model.ProviderSettings;
import com.handy.portal.model.logs.EventLogFactory;
import com.handy.portal.ui.element.BookingElementView;
import com.handy.portal.ui.element.BookingListView;
import com.handy.portal.ui.element.DateButtonView;
import com.handy.portal.util.DateTimeUtils;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public abstract class BookingsFragment<T extends HandyEvent.ReceiveBookingsSuccess> extends ActionBarFragment
{
    @Inject
    protected EventLogFactory mEventLogFactory;
    @Inject
    ConfigManager mConfigManager;
    @Inject
    PrefsManager mPrefsManager;
    @Bind(R.id.fetch_error_view)
    View mFetchErrorView;
    @Bind(R.id.fetch_error_text)
    TextView mErrorText;
    @Bind(R.id.refresh_layout)
    SwipeRefreshLayout mRefreshLayout;
    @Bind(R.id.toggle_available_job_notification)
    SwitchCompat mToggleAvailableJobNotification;
    @Bind(R.id.bookings_content)
    LinearLayout mBookingsContent;

    protected abstract int getFragmentResourceId();

    protected abstract BookingListView getBookingListView();

    protected abstract ViewGroup getNoBookingsView();

    protected abstract LinearLayout getDatesLayout();

    @NonNull
    protected abstract String getTrackingType();

    protected abstract HandyEvent getRequestEvent(List<Date> dates, boolean useCachedIfPresent);

    protected abstract boolean shouldShowRequestedIndicator(List<Booking> bookingsForDay);

    protected abstract boolean shouldShowClaimedIndicator(List<Booking> bookingsForDay);

    protected abstract int numberOfDaysToDisplay();

    protected abstract void beforeRequestBookings();

    protected abstract void afterDisplayBookings(List<Booking> bookingsForDay, Date dateOfBookings);

    protected abstract Class<? extends BookingElementView> getBookingElementViewClass();

    //Event listeners
    public abstract void onBookingsRetrieved(T event);

    //should use date without time for these entries, see Utils.getDateWithoutTime
    private Map<Date, DateButtonView> mDateDateButtonViewMap;
    protected Date mSelectedDay;
    protected List<Booking> mBookingsForSelectedDay;
    protected ProviderSettings mProviderSettings;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(getFragmentResourceId(), null);
        ButterKnife.bind(this, view);

        //Optional param, needs to be validated
        if (getArguments() != null && getArguments().containsKey(BundleKeys.DATE_EPOCH_TIME))
        {
            long targetDateTime = getArguments().getLong(BundleKeys.DATE_EPOCH_TIME);
            if (targetDateTime > 0)
            {
                mSelectedDay = DateTimeUtils.getDateWithoutTime(new Date(getArguments().getLong(BundleKeys.DATE_EPOCH_TIME)));
            }
        }

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                requestBookingsForSelectedDay(false);
            }
        });
        mRefreshLayout.setColorSchemeResources(R.color.handy_blue);

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (!MainActivityFragment.clearingBackStack)
        {
            bus.post(new HandyEvent.RequestProviderInfo());
            bus.post(new ProviderSettingsEvent.RequestProviderSettings());

            initDateButtons();

            if (mSelectedDay == null || !mDateDateButtonViewMap.containsKey(mSelectedDay))
            {
                mSelectedDay = DateTimeUtils.getDateWithoutTime(new Date());
            }

            if (mDateDateButtonViewMap.containsKey(mSelectedDay))
            {
                mDateDateButtonViewMap.get(mSelectedDay).setChecked(true);
            }

            if (shouldShowAvailableBookingsToggle())
            {
                mToggleAvailableJobNotification.setVisibility(View.VISIBLE);
            }

            setLateDispatchOptInToggleListener();
            requestAllBookings();
        }
    }

    @OnClick(R.id.try_again_button)
    public void doRequestBookingsAgain()
    {
        requestBookingsForSelectedDay(true);
    }

    private void requestAllBookings()
    {
        requestBookingsForSelectedDay(true);

        requestBookingsForOtherDays(mSelectedDay);
    }

    public void onReceiveProviderSettingsSuccess(ProviderSettingsEvent.ReceiveProviderSettingsSuccess event)
    {
        mProviderSettings = event.getProviderSettings().clone();
        mToggleAvailableJobNotification.setChecked(mProviderSettings.hasOptedInToLateDispatchNotifications());
        if (shouldShowAvailableBookingsToggle())
        {
            mToggleAvailableJobNotification.setVisibility(View.VISIBLE);
        }
    }

    public void onReceiveProviderSettingsError(ProviderSettingsEvent.ReceiveProviderSettingsError event) {}

    public void onReceiveProviderSettingsUpdateSuccess(ProviderSettingsEvent.ReceiveProviderSettingsUpdateSuccess event)
    {
        mProviderSettings = event.getProviderSettings().clone();
        if (!mPrefsManager.getBoolean(PrefsKey.SAME_DAY_LATE_DISPATCH_AVAILABLE_JOB_NOTIFICATION_EXPLAINED, false) &&
                mProviderSettings.hasOptedInToLateDispatchNotifications())
        {
            mPrefsManager.setBoolean(PrefsKey.SAME_DAY_LATE_DISPATCH_AVAILABLE_JOB_NOTIFICATION_EXPLAINED, true);
            Snackbar snackbar = Snackbar
                    .make(mBookingsContent, R.string.notify_available_jobs_update_intro_success, Snackbar.LENGTH_LONG);

            snackbar.show();
        }
    }

    public void onReceiveProviderSettingsUpdateError(ProviderSettingsEvent.ReceiveProviderSettingsUpdateError event)
    {
        if (mProviderSettings != null)
        {
            boolean optedIn = !mProviderSettings.hasOptedInToLateDispatchNotifications();
            mProviderSettings.setLateDispatchOptIn(optedIn);
            mToggleAvailableJobNotification.setChecked(optedIn);
        }
        else
        {
            bus.post(new ProviderSettingsEvent.RequestProviderSettings());
        }

        Snackbar snackbar = Snackbar
                .make(mBookingsContent, R.string.notify_available_jobs_update_error, Snackbar.LENGTH_LONG);

        snackbar.show();
    }

    private void requestBookingsForSelectedDay(boolean showOverlay)
    {
        requestBookings(Lists.newArrayList(mSelectedDay), showOverlay, false);
    }

    private void requestBookingsForOtherDays(Date dayToExclude)
    {
        List<Date> dates = Lists.newArrayList();
        for (int i = 0; i < numberOfDaysToDisplay(); i++)
        {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DATE, i);
            Date day = DateTimeUtils.getDateWithoutTime(calendar.getTime());

            if (!day.equals(dayToExclude))
            {
                dates.add(day);
            }
        }
        requestBookings(dates, false, true);
    }

    private void requestBookings(List<Date> dates, boolean showOverlay, boolean useCachedIfPresent)
    {
        Crashlytics.log("Requesting bookings for the following dates" + dates.toString());
        if (mFetchErrorView == null)
        {
            Crashlytics.logException(
                    new NullPointerException("All views are null due to ButterKnife unbind."));
            return;
        }
        mFetchErrorView.setVisibility(View.GONE);
        if (showOverlay)
        {
            bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        }
        bus.post(getRequestEvent(dates, useCachedIfPresent));
    }

    protected void handleBookingsRetrieved(HandyEvent.ReceiveBookingsSuccess event)
    {
        mRefreshLayout.setRefreshing(false);
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));

        List<Booking> bookings = event.bookings;
        Collections.sort(bookings);

        for (Booking b : bookings)
        {
            if (b.getZipClusterId() != null)
            {
                bus.post(new BookingEvent.RequestZipClusterPolygons(b.getZipClusterId()));
            }
        }

        DateButtonView dateButtonView = mDateDateButtonViewMap.get(event.day);
        if (dateButtonView != null)
        {
            dateButtonView.showRequestedIndicator(shouldShowRequestedIndicator(bookings));
            dateButtonView.showClaimedIndicator(shouldShowClaimedIndicator(bookings));
        }
        else
        {
            Crashlytics.logException(new RuntimeException("Date button for " + event.day + " not found"));
        }

        if (mSelectedDay.equals(event.day))
        {
            displayBookings(bookings, mSelectedDay);
        }
    }

    protected void handleBookingsRetrievalError(HandyEvent.ReceiveBookingsError event, int errorStateStringId)
    {
        mRefreshLayout.setRefreshing(false);
        if (event.days.contains(mSelectedDay))
        {
            bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
            if (event.error.getType() == DataManager.DataManagerError.Type.NETWORK)
            {
                mErrorText.setText(R.string.error_fetching_connectivity_issue);
            }
            else
            {
                mErrorText.setText(errorStateStringId);
            }
            mFetchErrorView.setVisibility(View.VISIBLE);
        }
    }

    private void initDateButtons()
    {
        LinearLayout datesLayout = getDatesLayout();
        datesLayout.removeAllViews();

        mDateDateButtonViewMap = new HashMap<>(numberOfDaysToDisplay());

        Context context = getActivity();

        for (int i = 0; i < numberOfDaysToDisplay(); i++)
        {
            LayoutInflater.from(context).inflate(R.layout.element_date_button, datesLayout);
            final DateButtonView dateButtonView = (DateButtonView) datesLayout.getChildAt(datesLayout.getChildCount() - 1);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DATE, i);
            final Date day = DateTimeUtils.getDateWithoutTime(calendar.getTime());

            dateButtonView.init(day);
            dateButtonView.setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View v)
                {
                    bus.post(new HandyEvent.DateClicked(getTrackingType(), day));
                    selectDay(day);
                    beforeRequestBookings();
                    requestBookings(Lists.newArrayList(day), true, true);
                }
            });

            mDateDateButtonViewMap.put(day, dateButtonView);
        }
    }

    private void selectDay(Date day)
    {
        DateButtonView selectedDateButtonView = mDateDateButtonViewMap.get(mSelectedDay);
        if (selectedDateButtonView != null)
        {
            selectedDateButtonView.setChecked(false);
        }
        mDateDateButtonViewMap.get(day).setChecked(true);
        mSelectedDay = day;

        if (shouldShowAvailableBookingsToggle())
        {
            mToggleAvailableJobNotification.setVisibility(View.VISIBLE);
        }
        else
        {
            mToggleAvailableJobNotification.setVisibility(View.GONE);
        }
    }

    private void displayBookings(List<Booking> bookings, Date dateOfBookings)
    {
        mBookingsForSelectedDay = bookings;
        getBookingListView().populateList(bookings, getBookingElementViewClass());
        initListClickListener();
        getNoBookingsView().setVisibility(bookings.size() > 0 ? View.GONE : View.VISIBLE);
        afterDisplayBookings(bookings, dateOfBookings);
    }

    private void initListClickListener()
    {
        getBookingListView().setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id)
            {
                Booking booking = (Booking) adapter.getItemAtPosition(position);
                if (booking != null)
                {
                    int oneBasedIndex = position + 1;
                    if (getTrackingType().equalsIgnoreCase(getString(R.string.available_job)))
                    {
                        bus.post(new LogEvent.AddLogEvent(mEventLogFactory
                                .createAvailableJobClickedLog(booking, oneBasedIndex)));
                    }
                    else if (getTrackingType().equalsIgnoreCase(getString(R.string.scheduled_job)))
                    {
                        bus.post(new LogEvent.AddLogEvent(mEventLogFactory
                                .createScheduledJobClickedLog(booking, oneBasedIndex)));
                    }
                    bus.post(new HandyEvent.BookingSelected(getTrackingType(), booking.getId()));
                    showBookingDetails(booking);
                }
            }
        });
    }

    private void showBookingDetails(Booking booking)
    {
        Bundle arguments = new Bundle();
        arguments.putString(BundleKeys.BOOKING_ID, booking.getId());
        arguments.putString(BundleKeys.BOOKING_TYPE, booking.getType().toString());
        arguments.putLong(BundleKeys.BOOKING_DATE, booking.getStartDate().getTime());
        HandyEvent.NavigateToTab event = new HandyEvent.NavigateToTab(MainViewTab.DETAILS, arguments);
        bus.post(event);
    }

    private boolean shouldShowAvailableBookingsToggle()
    {
        return mSelectedDay != null &&
                DateTimeUtils.isToday(mSelectedDay) &&
                getConfigurationResponse() != null &&
                getConfigurationResponse().shouldShowLateDispatchOptIn() &&
                mProviderSettings != null;
    }

    private ConfigurationResponse getConfigurationResponse()
    {
        return mConfigManager.getConfigurationResponse();
    }

    private void setLateDispatchOptInToggleListener()
    {
        mToggleAvailableJobNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked)
            {
                if (mProviderSettings == null)
                {
                    bus.post(new ProviderSettingsEvent.RequestProviderSettings());
                }
                else if (mProviderSettings.hasOptedInToLateDispatchNotifications() != isChecked)
                {
                    mProviderSettings.setLateDispatchOptIn(isChecked);
                    bus.post(new ProviderSettingsEvent.RequestProviderSettingsUpdate(mProviderSettings));
                }
            }
        });
    }
}
