package com.handy.portal.ui.fragment.bookings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.NavigationEvent;
import com.handy.portal.logger.mixpanel.Mixpanel;
import com.handy.portal.model.Booking;
import com.handy.portal.model.Booking.BookingType;
import com.handy.portal.ui.element.AvailableBookingElementView;
import com.handy.portal.ui.element.BookingElementMediator;
import com.handy.portal.ui.element.BookingElementView;
import com.handy.portal.ui.element.ScheduledBookingElementView;
import com.handy.portal.ui.fragment.ActionBarFragment;
import com.handy.portal.ui.fragment.MainActivityFragment;
import com.squareup.otto.Subscribe;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ComplementaryBookingsFragment extends ActionBarFragment
{
    @Bind(R.id.loading_overlay)
    View loadingOverlay;
    @Bind(R.id.complementary_bookings_empty)
    View noBookingsView;
    @Bind(R.id.earlier_bookings)
    ViewGroup earlierBookingsContainer;
    @Bind(R.id.later_bookings)
    ViewGroup laterBookingsContainer;
    @Bind(R.id.claimed_bookings)
    ViewGroup claimedBookingsContainer;
    @Bind(R.id.fetch_error_view)
    View errorView;
    @Bind(R.id.fetch_error_text)
    TextView errorText;

    @Inject
    Mixpanel mixpanel;

    private Booking claimedBooking;
    private List<Booking> complementaryBookings;
    private String bookingId;
    private BookingType bookingType;
    private Date bookingDate;

    private static final String SOURCE_COMPLEMENTARY_JOBS_LIST = "matching_jobs_list";

    @Override
    protected MainViewTab getTab()
    {
        return MainViewTab.SCHEDULED_JOBS;
    }

    @Override
    public void onCreate(Bundle savedInstance)
    {
        super.onCreate(savedInstance);
        setOptionsMenuEnabled(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.menu_x_back, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.action_exit:
                onBackButtonPressed();
                return true;
            default:
                return false;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_complementary_bookings, container, false);
        ButterKnife.bind(this, view);

        if (validateRequiredArguments())
        {
            bookingId = getArguments().getString(BundleKeys.BOOKING_ID);
            bookingType = BookingType.valueOf(getArguments().getString(BundleKeys.BOOKING_TYPE));
            bookingDate = new Date(getArguments().getLong(BundleKeys.BOOKING_DATE));
        }
        else
        {
            showToast(R.string.error_fetching_matching_jobs);
            bus.post(new NavigationEvent.NavigateToTab(MainViewTab.SCHEDULED_JOBS));
        }

        loadingOverlay.setVisibility(View.VISIBLE);

        return view;
    }

    @Override
    protected List<String> requiredArguments()
    {
        return Lists.newArrayList(BundleKeys.BOOKING_ID, BundleKeys.BOOKING_TYPE, BundleKeys.BOOKING_DATE);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setActionBar(R.string.matching_jobs, false);
        if (!MainActivityFragment.clearingBackStack)
        {
            requestComplementaryBookings();
        }
    }

    @OnClick(R.id.all_jobs_button)
    public void onGoToAllJobsClicked()
    {
        Bundle arguments = new Bundle();
        arguments.putLong(BundleKeys.DATE_EPOCH_TIME, claimedBooking.getStartDate().getTime());
        bus.post(new NavigationEvent.NavigateToTab(MainViewTab.AVAILABLE_JOBS, arguments));
    }

    @OnClick(R.id.try_again_button)
    public void onTryAgainClicked()
    {
        requestComplementaryBookings();
    }

    private void requestComplementaryBookings()
    {
        errorView.setVisibility(View.GONE);
        loadingOverlay.setVisibility(View.VISIBLE);
        bus.post(new HandyEvent.RequestBookingDetails(bookingId, bookingType, bookingDate));
        bus.post(new HandyEvent.RequestComplementaryBookings(bookingId, bookingType, bookingDate));
    }


    @Subscribe
    public void onReceiveBookingDetailsSuccess(HandyEvent.ReceiveBookingDetailsSuccess event)
    {
        claimedBooking = event.booking;
        if (isDataReady())
        {
            displayResults();
        }
    }

    @Subscribe
    public void onReceiveComplementaryBookingsSuccess(HandyEvent.ReceiveComplementaryBookingsSuccess event)
    {
        complementaryBookings = event.bookings;
        if (isDataReady())
        {
            displayResults();
        }
    }

    private boolean isDataReady()
    {
        return complementaryBookings != null && claimedBooking != null;
    }


    private void displayResults()
    {
        loadingOverlay.setVisibility(View.GONE);
        if (complementaryBookings.isEmpty())
        {
            mixpanel.track("no complementary jobs found");
            setActionBarTitle(R.string.no_matching_jobs);
            noBookingsView.setVisibility(View.VISIBLE);
        }
        else
        {
            mixpanel.track("complementary jobs found");
            setActionBarTitle(complementaryBookings.size() == 1 ? getString(R.string.one_matching_job) : getString(R.string.n_matching_jobs, complementaryBookings.size()));
            displayBookings(Lists.newArrayList(complementaryBookings));
        }
    }

    @Subscribe
    public void onReceiveBookingDetailsError(HandyEvent.ReceiveBookingDetailsError event)
    {
        displayErrorView(event.error);
    }

    @Subscribe
    public void onReceiveComplementaryBookingsError(HandyEvent.ReceiveComplementaryBookingsError event)
    {
        displayErrorView(event.error);
    }

    private void displayErrorView(DataManager.DataManagerError error)
    {
        loadingOverlay.setVisibility(View.GONE);
        errorView.setVisibility(View.VISIBLE);
        if (error.getType() == DataManager.DataManagerError.Type.NETWORK)
        {
            errorText.setText(R.string.error_fetching_connectivity_issue);
        }
        else
        {
            errorText.setText(R.string.error_fetching_connectivity_issue);
        }
    }

    private void displayBookings(List<Booking> bookings)
    {
        claimedBookingsContainer.removeAllViews();
        earlierBookingsContainer.removeAllViews();
        laterBookingsContainer.removeAllViews();

        View claimedBookingEntryView = createBookingEntryView(claimedBooking, claimedBookingsContainer, ScheduledBookingElementView.class);
        claimedBookingsContainer.addView(claimedBookingEntryView);
        claimedBookingEntryView.setOnClickListener(new ShowBookingDetailsClickListener(claimedBooking));

        Collections.sort(bookings);
        for (Booking booking : bookings)
        {
            ViewGroup container = booking.getStartDate().before(claimedBooking.getStartDate()) ? earlierBookingsContainer : laterBookingsContainer;
            View bookingEntryView = createBookingEntryView(booking, container, AvailableBookingElementView.class);
            container.addView(bookingEntryView);

            bookingEntryView.setOnClickListener(new ShowBookingDetailsClickListener(booking));
        }

        setVisibilityByChildCount(claimedBookingsContainer, earlierBookingsContainer, laterBookingsContainer);
    }

    private void setVisibilityByChildCount(ViewGroup... viewGroups)
    {
        for (ViewGroup viewGroup : viewGroups)
        {
            if (viewGroup.getChildCount() > 0)
            {
                viewGroup.setVisibility(View.VISIBLE);
            }
            else
            {
                viewGroup.setVisibility(View.GONE);
            }

        }
    }

    private View createBookingEntryView(Booking booking, ViewGroup container, Class<? extends BookingElementView> viewClass)
    {
        BookingElementMediator mediator = new BookingElementMediator(getActivity(), booking, null, container, viewClass);
        return mediator.getAssociatedView();
    }

    private class ShowBookingDetailsClickListener implements View.OnClickListener
    {
        private final Booking booking;

        ShowBookingDetailsClickListener(Booking booking)
        {
            this.booking = booking;
        }

        @Override
        public void onClick(View view)
        {
            if (!booking.getId().equals(claimedBooking.getId()))
            {
                mixpanel.track("complementary job clicked");
            }
            Bundle arguments = new Bundle();
            arguments.putString(BundleKeys.BOOKING_ID, booking.getId());
            arguments.putString(BundleKeys.BOOKING_TYPE, booking.getType().toString());
            arguments.putString(BundleKeys.BOOKING_SOURCE, SOURCE_COMPLEMENTARY_JOBS_LIST);
            bus.post(new NavigationEvent.NavigateToTab(MainViewTab.JOB_DETAILS, arguments, true));
        }
    }
}
