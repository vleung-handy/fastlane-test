package com.handy.portal.ui.fragment.booking;

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
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.Country;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.constant.RequestCode;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.manager.ProviderManager;
import com.handy.portal.model.Address;
import com.handy.portal.model.Booking;
import com.handy.portal.model.PaymentInfo;
import com.handy.portal.model.Provider;
import com.handy.portal.ui.fragment.ActionBarFragment;
import com.handy.portal.ui.fragment.dialog.ConfirmBookingDialogFragment;
import com.handy.portal.util.DateTimeUtils;
import com.handy.portal.util.MathUtils;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class NearbyBookingsFragment extends ActionBarFragment
        implements NearbyBookingsMapFragment.MarkerClickedCallback
{
    private static final String SOURCE = "nearby job";

    @Inject
    ProviderManager mProviderManager;

    @InjectView(R.id.nearby_bookings_description)
    TextView mDescriptionText;
    @InjectView(R.id.nearby_bookings_map)
    ViewGroup mMapContainer;
    @InjectView(R.id.booking_info_timer)
    TextView mBookingTimerText;
    @InjectView(R.id.booking_info_address)
    TextView mBookingAddressText;
    @InjectView(R.id.booking_info_time)
    TextView mBookingTimeText;
    @InjectView(R.id.booking_info_claim_button)
    Button mBookingClaimButton;
    @InjectView(R.id.booking_info_distance)
    TextView mBookingDistanceText;

    private ArrayList<Booking> mBookings;
    private LatLng mCenter;
    private CountDownTimer mCounter;

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
    protected MainViewTab getTab()
    {
        return MainViewTab.NEARBY_JOBS;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setActionBar(R.string.available_jobs, false);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_nearby_bookings, container, false);
        ButterKnife.inject(this, view);

        Bundle args = getArguments();
        mBookings = (ArrayList<Booking>) args.getSerializable(BundleKeys.BOOKINGS);
        mCenter = args.getParcelable(BundleKeys.MAP_CENTER);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        NearbyBookingsMapFragment fragment =
                NearbyBookingsMapFragment.newInstance(mBookings, mCenter);
        transaction.replace(mMapContainer.getId(), fragment);
        transaction.commit();

        mDescriptionText.setText(getString(R.string.nearby_booking_formatted, mBookings.size()));

        return view;
    }

    @Override
    public void onPause()
    {
        super.onPause();
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
        setBookingInfoDisplay(booking);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data)
    {
        if (requestCode == RequestCode.CONFIRM_REQUEST && resultCode == Activity.RESULT_OK)
        {
            bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
            Booking booking = (Booking) data.getSerializableExtra(BundleKeys.BOOKING);
            bus.post(new HandyEvent.RequestClaimJob(booking, SOURCE));
        }
    }

    @Subscribe
    public void onReceiveClaimJobSuccess(final HandyEvent.ReceiveClaimJobSuccess event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        Bundle arguments = new Bundle();
        arguments.putLong(BundleKeys.DATE_EPOCH_TIME, event.bookingClaimDetails.getBooking().getStartDate().getTime());
        bus.post(new HandyEvent.NavigateToTab(MainViewTab.SCHEDULED_JOBS, arguments, null));
    }

    @Subscribe
    public void onReceiveClaimJobError(final HandyEvent.ReceiveClaimJobError event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        showToast(R.string.job_claim_error);
    }

    private void setBookingInfoDisplay(final Booking booking)
    {
        Address address = booking.getAddress();
        PaymentInfo paymentInfo = booking.getPaymentToProvider();

        setCountDownTimer(booking.getStartDate().getTime() - System.currentTimeMillis());

        mBookingAddressText.setText(booking.getFormattedLocation(Booking.BookingStatus.AVAILABLE));

        String startTime = DateTimeUtils.CLOCK_FORMATTER_12HR.format(booking.getStartDate());
        String endTime = DateTimeUtils.CLOCK_FORMATTER_12HR.format(booking.getEndDate());
        mBookingTimeText.setText(getString(R.string.time_interval_formatted, startTime, endTime));

        double km = MathUtils.getDistance(mCenter.latitude, mCenter.longitude,
                address.getLatitude(), address.getLongitude());
        Provider provider = mProviderManager.getCachedActiveProvider();
        final String distance;
        if (provider != null && !Country.US.equalsIgnoreCase(provider.getCountry()))
        {
            distance = getString(R.string.kilometers_away_formatted,
                    MathUtils.TWO_DECIMALS_FORMAT.format(km));
        }
        else
        {
            distance = getString(R.string.miles_away_formatted,
                    MathUtils.TWO_DECIMALS_FORMAT.format(km * MathUtils.MILES_PER_KILOMETER));
        }
        mBookingDistanceText.setText(distance);

        final ConfirmBookingDialogFragment dialogFragment =
                ConfirmBookingDialogFragment.newInstance(booking);
        mBookingClaimButton.setText(getString(R.string.claim_n_dollar_job_formatted,
                paymentInfo.getCurrencySymbol() + paymentInfo.getAdjustedAmount()));
        mBookingClaimButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                if (dialogFragment.isVisible()) { return; }
                dialogFragment.setTargetFragment(NearbyBookingsFragment.this, RequestCode.CONFIRM_REQUEST);
                dialogFragment.show(getFragmentManager(), ConfirmBookingDialogFragment.FRAGMENT_TAG);
            }
        });
    }

    private void setCountDownTimer(long timeRemainMillis)
    {
        if (mCounter != null) { mCounter.cancel(); } // cancel the previous counter

        mCounter = DateTimeUtils.setCountDownTimer(mBookingTimerText, timeRemainMillis);
    }

}
