package com.handy.portal.ui.fragment.booking;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.Country;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.manager.ProviderManager;
import com.handy.portal.model.Address;
import com.handy.portal.model.Booking;
import com.handy.portal.model.PaymentInfo;
import com.handy.portal.model.Provider;
import com.handy.portal.ui.fragment.ActionBarFragment;
import com.handy.portal.util.DateTimeUtils;
import com.handy.portal.util.MathUtils;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class NearbyBookingsFragment extends ActionBarFragment
        implements NearbyBookingsMapFragment.MarkerClickedCallback
{
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

    public static NearbyBookingsFragment newInstance(ArrayList<Booking> bookings, LatLng center)
    {
        NearbyBookingsFragment fragment = new NearbyBookingsFragment();
        Bundle args = new Bundle();
        args.putSerializable(BundleKeys.BOOKINGS, bookings);
        args.putParcelable(BundleKeys.MAP_CENTER, center);
        fragment.setArguments(args);
        return fragment;
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

        mDescriptionText.setText(getString(R.string.nearby_booking, mBookings.size()));

        return view;
    }

    @Override
    public void markerClicked(final Booking booking)
    {
        setBookingInfoDisplay(booking);
    }

    private void setBookingInfoDisplay(final Booking booking)
    {
        Address address = booking.getAddress();
        PaymentInfo paymentInfo = booking.getPaymentToProvider();
        String timer = DateTimeUtils.millisecondsToFormattedString(
                booking.getStartDate().getTime() - System.currentTimeMillis());
        mBookingTimerText.setText(getString(R.string.start_timer, timer));
        mBookingAddressText.setText(address.getAddress1());

        String startTime = DateTimeUtils.CLOCK_FORMATTER_12HR.format(booking.getStartDate());
        String endTime = DateTimeUtils.CLOCK_FORMATTER_12HR.format(booking.getEndDate());
        mBookingTimeText.setText(startTime + " - " + endTime);
        mBookingClaimButton.setText(getString(R.string.claim_n_dollar_job,
                paymentInfo.getCurrencySymbol() + paymentInfo.getAdjustedAmount()));

        double km = MathUtils.getDistance(mCenter.latitude, mCenter.longitude,
                address.getLatitude(), address.getLongitude());
        Provider provider = mProviderManager.getCachedActiveProvider();
        if (provider != null && Country.US.equalsIgnoreCase(provider.getCountry()))
        {
            mBookingDistanceText.setText(getString(R.string.kilometers_away,
                    MathUtils.TWO_DECIMALS_FORMAT.format(km)));
        }
        else
        {
            mBookingDistanceText.setText(getString(R.string.miles_away,
                    MathUtils.TWO_DECIMALS_FORMAT.format(km * MathUtils.MILES_PER_KILOMETER)));
        }
    }
}
