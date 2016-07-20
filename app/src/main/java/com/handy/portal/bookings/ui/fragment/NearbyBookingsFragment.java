package com.handy.portal.bookings.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.handy.portal.R;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.bookings.ui.fragment.dialog.ConfirmBookingActionDialogFragment;
import com.handy.portal.bookings.ui.fragment.dialog.ConfirmBookingClaimDialogFragment;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.Country;
import com.handy.portal.constant.MainViewPage;
import com.handy.portal.constant.RequestCode;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.NavigationEvent;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.library.util.MathUtils;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.AvailableJobsLog;
import com.handy.portal.logger.handylogger.model.NearbyJobsLog;
import com.handy.portal.manager.ProviderManager;
import com.handy.portal.model.Address;
import com.handy.portal.model.Provider;
import com.handy.portal.payments.model.PaymentInfo;
import com.handy.portal.ui.fragment.ActionBarFragment;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NearbyBookingsFragment extends ActionBarFragment
        implements NearbyBookingsMapFragment.MarkerClickedCallback
{
    private static final String SOURCE = "nearby_jobs_view";

    @Inject
    ProviderManager mProviderManager;

    @BindView(R.id.nearby_bookings_description)
    TextView mDescriptionText;
    @BindView(R.id.nearby_bookings_map)
    ViewGroup mMapContainer;
    @BindView(R.id.booking_info_timer)
    TextView mBookingTimerText;
    @BindView(R.id.booking_info_address)
    TextView mBookingAddressText;
    @BindView(R.id.booking_info_time)
    TextView mBookingTimeText;
    @BindView(R.id.booking_info_claim_button)
    Button mBookingClaimButton;
    @BindView(R.id.booking_info_distance)
    TextView mBookingDistanceText;

    private ArrayList<Booking> mBookings;
    private LatLng mCenter;
    private CountDownTimer mCounter;
    private double mKilometer;

    public static NearbyBookingsFragment newInstance(ArrayList<Booking> bookings, LatLng center)
    {
        NearbyBookingsFragment fragment = new NearbyBookingsFragment();
        Bundle args = new Bundle();
        args.putSerializable(BundleKeys.BOOKINGS, bookings);
        args.putParcelable(BundleKeys.MAP_CENTER, center);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected MainViewPage getAppPage()
    {
        return MainViewPage.NEARBY_JOBS;
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_nearby_bookings, container, false);
        ButterKnife.bind(this, view);

        Bundle args = getArguments();
        mBookings = (ArrayList<Booking>) args.getSerializable(BundleKeys.BOOKINGS);
        mCenter = args.getParcelable(BundleKeys.MAP_CENTER);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        NearbyBookingsMapFragment fragment =
                NearbyBookingsMapFragment.newInstance(mBookings, mCenter);
        transaction.replace(mMapContainer.getId(), fragment);
        transaction.commit();

        if (mBookings.size() > 1)
        {
            mDescriptionText.setText(getString(R.string.nearby_booking_formatted, mBookings.size()));
        }
        else if (mBookings.size() == 1)
        {
            mDescriptionText.setText(getString(R.string.nearby_booking_one));
        }
        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        setActionBar(R.string.available_jobs, false);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        bus.register(this);
        bus.post(new LogEvent.AddLogEvent(
                new NearbyJobsLog.Shown(mBookings.size())));
    }

    @Override
    public void onPause()
    {
        super.onPause();
        bus.unregister(this);
        if (mCounter != null) { mCounter.cancel(); }
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
        switch (item.getItemId())
        {
            case R.id.action_exit:
                onBackButtonPressed();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void markerClicked(final Booking booking)
    {
        bus.post(new LogEvent.AddLogEvent(new NearbyJobsLog.PinSelected(booking.getId())));
        setBookingInfoDisplay(booking);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data)
    {
        if (requestCode == RequestCode.CONFIRM_REQUEST && resultCode == Activity.RESULT_OK)
        {
            Booking booking = (Booking) data.getSerializableExtra(BundleKeys.BOOKING);
            bus.post(new LogEvent.AddLogEvent(
                    new AvailableJobsLog.ClaimSubmitted(booking, SOURCE, null, mKilometer * 1000)));
            bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
            bus.post(new HandyEvent.RequestClaimJob(booking, SOURCE, null));
        }
    }

    @Subscribe
    public void onReceiveClaimJobSuccess(final HandyEvent.ReceiveClaimJobSuccess event)
    {
        Booking booking = event.bookingClaimDetails.getBooking();
        bus.post(new LogEvent.AddLogEvent(
                new AvailableJobsLog.ClaimSuccess(booking, SOURCE, null, mKilometer * 1000)));
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        Bundle arguments = new Bundle();
        arguments.putLong(BundleKeys.DATE_EPOCH_TIME, booking.getStartDate().getTime());
        bus.post(new NavigationEvent.NavigateToPage(MainViewPage.SCHEDULED_JOBS, arguments, null));
    }

    @Subscribe
    public void onReceiveClaimJobError(final HandyEvent.ReceiveClaimJobError event)
    {
        String errorMessage = event.error.getMessage();
        if (errorMessage == null)
        {
            errorMessage = getString(R.string.job_claim_error);
        }
        bus.post(new LogEvent.AddLogEvent(
                new AvailableJobsLog.ClaimError(event.getBooking(), SOURCE, null, mKilometer * 1000, errorMessage)));
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        showToast(errorMessage);
    }

    private void setBookingInfoDisplay(final Booking booking)
    {
        Address address = booking.getAddress();
        PaymentInfo paymentInfo = booking.getPaymentToProvider();

        setCountDownTimer(booking.getStartDate().getTime() - System.currentTimeMillis());

        mBookingAddressText.setText(booking.getAddress().getShortRegion());

        String startTime = DateTimeUtils.CLOCK_FORMATTER_12HR.format(booking.getStartDate());
        String endTime = DateTimeUtils.CLOCK_FORMATTER_12HR.format(booking.getEndDate());
        mBookingTimeText.setText(getString(R.string.time_interval_formatted, startTime, endTime));

        mKilometer = MathUtils.getDistance(mCenter.latitude, mCenter.longitude,
                address.getLatitude(), address.getLongitude());
        Provider provider = mProviderManager.getCachedActiveProvider();
        final String distance;
        if (provider != null && !Country.US.equalsIgnoreCase(provider.getCountry()))
        {
            distance = getString(R.string.kilometers_away_formatted,
                    MathUtils.TWO_DECIMALS_FORMAT.format(mKilometer));
        }
        else
        {
            distance = getString(R.string.miles_away_formatted,
                    MathUtils.TWO_DECIMALS_FORMAT.format(mKilometer * MathUtils.MILES_PER_KILOMETER));
        }
        mBookingDistanceText.setText(distance);

        final ConfirmBookingActionDialogFragment dialogFragment =
                ConfirmBookingClaimDialogFragment.newInstance(booking);
        mBookingClaimButton.setText(getString(R.string.claim_n_dollar_job_formatted,
                paymentInfo.getCurrencySymbol() + paymentInfo.getAdjustedAmount()));
        mBookingClaimButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                if (dialogFragment.isVisible()) { return; }
                dialogFragment.setTargetFragment(NearbyBookingsFragment.this, RequestCode.CONFIRM_REQUEST);
                dialogFragment.show(getFragmentManager(), ConfirmBookingClaimDialogFragment.FRAGMENT_TAG);
            }
        });
    }

    private void setCountDownTimer(long timeRemainMillis)
    {
        if (mCounter != null) { mCounter.cancel(); } // cancel the previous counter

        mCounter = DateTimeUtils.setCountDownTimer(mBookingTimerText, timeRemainMillis, R.string.start_timer_formatted);
    }

}
