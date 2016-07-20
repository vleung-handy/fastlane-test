package com.handy.portal.bookings.ui.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.maps.model.LatLng;
import com.handy.portal.R;
import com.handy.portal.bookings.BookingEvent;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.bookings.ui.element.BookingsMapView;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewPage;
import com.handy.portal.constant.RequestCode;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.NavigationEvent;
import com.handy.portal.library.util.CurrencyUtils;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.AvailableJobsLog;
import com.handy.portal.logger.handylogger.model.NearbyJobsLog;
import com.handy.portal.manager.ProviderManager;
import com.handy.portal.model.ZipClusterPolygons;
import com.handy.portal.ui.fragment.ActionBarFragment;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BookingsMapFragment extends ActionBarFragment
        implements NearbyBookingsMapFragment.MarkerClickedCallback, BookingsMapView.BookingsMapListener
{
    private static final String SOURCE = "nearby_jobs_view";

    @Inject
    ProviderManager mProviderManager;

//    @BindView(R.id.nearby_bookings_description)
//    TextView mDescriptionText;
//    @BindView(R.id.nearby_bookings_map)
//    ViewGroup mMapContainer;
//    @BindView(R.id.booking_info_timer)
//    TextView mBookingTimerText;
//    @BindView(R.id.booking_info_address)
//    TextView mBookingAddressText;
//    @BindView(R.id.booking_info_time)
//    TextView mBookingTimeText;
//    @BindView(R.id.booking_info_claim_button)
//    Button mBookingClaimButton;
//    @BindView(R.id.booking_info_distance)
//    TextView mBookingDistanceText;
    @BindView(R.id.bookings_map_view)
    BookingsMapView mBookingsMapView;

    //all bookings for selected day
    private ArrayList<Booking> mBookings;
    private LatLng mCenter;
    private CountDownTimer mCounter;
    private double mKilometer;
    private LinkedList<ZipClusterPolygons> mPolygonsBuffer = new LinkedList<>();
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_bookings_map, container, false);
        ButterKnife.bind(this, view);

        Bundle args = getArguments();
        mBookings = (ArrayList<Booking>) args.getSerializable(BundleKeys.BOOKINGS);
//        mCenter = args.getParcelable(BundleKeys.MAP_CENTER);
//        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
//        NearbyBookingsMapFragment fragment =
//                NearbyBookingsMapFragment.newInstance(mBookings, mCenter);
//        transaction.replace(mMapContainer.getId(), fragment);
//        transaction.commit();

//        if (mBookings.size() > 1)
//        {
//            mDescriptionText.setText(getString(R.string.nearby_booking_formatted, mBookings.size()));
//        }
//        else if (mBookings.size() == 1)
//        {
//            mDescriptionText.setText(getString(R.string.nearby_booking_one));
//        }
        mBookingsMapView.setBookingsMapListener(this);
        return view;
    }

    private void requestZipClusters()
    {
        //FIXME wait for map ready
        for(Booking booking : mBookings)
        {
            bus.post(new BookingEvent.RequestZipClusterPolygonsWithAssociatedBooking(booking.getZipClusterId(), booking));
        }
    }

    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
        mBookingsMapView.onLowMemory();
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        setActionBar(R.string.available_jobs, false);
        mBookingsMapView.onCreate(savedInstanceState);
//        mBookingsMapView.disableParentScrolling(mScrollView);
    }

    @Override
    public void onDestroy()
    {
        try
        {
            mBookingsMapView.onDestroy();
        }
        catch (NullPointerException e)
        {
            Log.e(getClass().getSimpleName(),
                    "Error while attempting MapView.onDestroy(), ignoring exception", e);
        }
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        try
        {
            /*
                similar to the exception thrown by mBookingsMapView.onDestroy()
                not caused by mBookingsMapView = null
             */
            mBookingsMapView.onSaveInstanceState(outState);
        }
        catch (Exception e)
        {
            Crashlytics.log("Error while attempting MapView.onSaveInstanceState(). Ignoring exception: " + e.getMessage());
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        bus.register(this);
        mBookingsMapView.onResume();
        bus.post(new LogEvent.AddLogEvent(
                new NearbyJobsLog.Shown(mBookings.size())));
    }

    @Override
    public void onPause()
    {
        mBookingsMapView.onPause();
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
//        setBookingInfoDisplay(booking);
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
    public void onReceiveZipClusterPolygonsWithAssociatedBookingSuccess(BookingEvent.ReceiveZipClusterPolygonsWithAssociatedBookingSuccess event)
    {
        mBookingsMapView.drawPolygon(event.zipClusterPolygons, event.booking);
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

    @Override
    public void onMapReady()
    {
        requestZipClusters();
    }

    @Override
    public void onZipClusterPolygonClicked(final List<Booking> associatedBookings)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        CharSequence[] ids = new CharSequence[associatedBookings.size()];
        for (int i = 0; i < associatedBookings.size(); ++i)
        {
            Booking b = associatedBookings.get(i);
            Booking.DisplayAttributes proRequestDisplayAttributes = b.getProviderRequestDisplayAttributes();

            String dollarAmount = CurrencyUtils.formatPriceWithCents(b.getPaymentToProvider().getAmount(), b.getPaymentToProvider().getCurrencySymbol());
            String requestedText = b.isRequested() && proRequestDisplayAttributes != null
                    && proRequestDisplayAttributes.getListingTitle() != null ? proRequestDisplayAttributes.getListingTitle() : "";
            ids[i] = (requestedText) +  "\n" +
                    DateTimeUtils.formatDateRange(DateTimeUtils.LOCAL_TIME_12_HOURS, b.getStartDate(), b.getEndDate()) + "\t\t" + dollarAmount
                    ;
        }
        builder.setTitle("Available jobs")
                .setItems(ids, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Booking booking = associatedBookings.get(which);
                        Bundle arguments= new Bundle();
                        arguments.putString(BundleKeys.BOOKING_ID, booking.getId());
                        arguments.putString(BundleKeys.BOOKING_TYPE, booking.getType().toString());
                        arguments.putLong(BundleKeys.BOOKING_DATE, booking.getStartDate().getTime());
                        bus.post(new NavigationEvent.NavigateToPage(MainViewPage.JOB_DETAILS, arguments, true));
                    }
                });
        builder.create().show();
    }
}
