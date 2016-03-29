package com.handy.portal.ui.fragment.booking;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.constant.PrefsKey;
import com.handy.portal.constant.TransitionStyle;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.NavigationEvent;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.AvailableJobsLog;
import com.handy.portal.manager.PrefsManager;
import com.handy.portal.model.booking.Booking;
import com.handy.portal.model.booking.BookingClaimDetails;
import com.handy.portal.ui.element.bookings.CheckOutBookingView;
import com.handy.portal.ui.element.bookings.AvailableBookingView;
import com.handy.portal.ui.element.bookings.ClaimedBookingView;
import com.handy.portal.ui.element.bookings.FinishedBookingView;
import com.handy.portal.ui.fragment.ActionBarFragment;
import com.handy.portal.ui.fragment.dialog.ClaimTargetDialogFragment;
import com.squareup.otto.Subscribe;

import java.util.Date;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NewBookingDetailsFragment extends ActionBarFragment
{
    public static final String SOURCE_LATE_DISPATCH = "late_dispatch";

    @Inject
    PrefsManager mPrefsManager;

    @Bind(R.id.booking_details_container)
    ViewGroup mContainer;

    private Booking mBooking;
    private String mRequestedBookingId;
    private Booking.BookingType mRequestedBookingType;
    private Date mAssociatedBookingDate;
    private String mSource;
    private Bundle mSourceExtras;

    @Override
    protected MainViewTab getTab()
    {
        return null;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        mRequestedBookingId = arguments.getString(BundleKeys.BOOKING_ID);
        mRequestedBookingType = Booking.BookingType.valueOf(arguments.getString(BundleKeys.BOOKING_TYPE));
        mAssociatedBookingDate = new Date(arguments.getLong(BundleKeys.BOOKING_DATE, 0L));

        setSourceInfo();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        bus.post(new HandyEvent.RequestBookingDetails(mRequestedBookingId, mRequestedBookingType, mAssociatedBookingDate));
    }
 
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_new_booking_details, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        setOptionsMenuEnabled(true);
        setBackButtonEnabled(true);
    }

    @Subscribe
    public void onReceiveBookingDetailsSuccess(HandyEvent.ReceiveBookingDetailsSuccess event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        mBooking = event.booking;
        updateDisplay();
    }

    @Subscribe
    public void onReceiveBookingDetailsError(HandyEvent.ReceiveBookingDetailsError event)
    {
        handleBookingDetailsError(event.error.getMessage());
    }

    @Subscribe
    public void onReceiveClaimJobSuccess(final HandyEvent.ReceiveClaimJobSuccess event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        BookingClaimDetails bookingClaimDetails = event.bookingClaimDetails;
        bus.post(new LogEvent.AddLogEvent(new AvailableJobsLog.ClaimSuccess(bookingClaimDetails.getBooking(), mSource, mSourceExtras, 0.0f)));

        if (bookingClaimDetails.getBooking().isClaimedByMe() || bookingClaimDetails.getBooking().getProviderId().equals(getLoggedInUserId()))
        {
            if (bookingClaimDetails.shouldShowClaimTarget())
            {
                BookingClaimDetails.ClaimTargetInfo claimTargetInfo = bookingClaimDetails.getClaimTargetInfo();
                ClaimTargetDialogFragment claimTargetDialogFragment = new ClaimTargetDialogFragment();
                claimTargetDialogFragment.setDisplayData(claimTargetInfo); //wrong way to pass argument to a fragment
                claimTargetDialogFragment.show(getFragmentManager(), ClaimTargetDialogFragment.FRAGMENT_TAG);

                returnToTab(MainViewTab.SCHEDULED_JOBS, bookingClaimDetails.getBooking().getStartDate().getTime(), null, null);
            }
            else
            {
                TransitionStyle transitionStyle = (bookingClaimDetails.getBooking().isRecurring() ? TransitionStyle.SERIES_CLAIM_SUCCESS : TransitionStyle.JOB_CLAIM_SUCCESS);
                returnToTab(MainViewTab.SCHEDULED_JOBS, bookingClaimDetails.getBooking().getStartDate().getTime(), transitionStyle, null);
            }
        }
        else
        {
            //Something has gone very wrong, the claim came back as success but the data shows not claimed, show a generic error and return to date based on original associated booking
            handleBookingClaimError(getString(R.string.job_claim_error), getString(R.string.job_claim_error_generic), getString(R.string.return_to_available_jobs), mBooking.getStartDate());
        }
    }

    @Subscribe
    public void onReceiveClaimJobError(final HandyEvent.ReceiveClaimJobError event)
    {
        bus.post(new LogEvent.AddLogEvent(new AvailableJobsLog.ClaimError(event.getBooking(), mSource, mSourceExtras, 0.0f, event.error.getMessage())));
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        handleBookingClaimError(event.error.getMessage(), null, null, null);
    }

    @Subscribe
    public void onReceiveNotifyJobOnMyWaySuccess(final HandyEvent.ReceiveNotifyJobOnMyWaySuccess event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));

        //refresh the page with the new booking
        mBooking = event.booking;
        updateDisplay();
    }

    @Subscribe
    public void onReceiveNotifyJobOnMyWayError(final HandyEvent.ReceiveNotifyJobOnMyWayError event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        handleNotifyOnMyWayError(event);
    }

    @Subscribe
    public void onReceiveNotifyJobCheckInSuccess(final HandyEvent.ReceiveNotifyJobCheckInSuccess event)
    {
        if (!event.isAuto)
        {
            bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));

            //refresh the page with the new booking
            mBooking = event.booking;
            updateDisplay();
        }
    }

    @Subscribe
    public void onReceiveNotifyJobCheckInError(final HandyEvent.ReceiveNotifyJobCheckInError event)
    {
        if (!event.isAuto)
        {
            bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
            handleNotifyCheckInError(event);
        }
    }

    private String getLoggedInUserId()
    {
        return mPrefsManager.getString(PrefsKey.LAST_PROVIDER_ID);
    }

    private void returnToTab(MainViewTab targetTab, long epochTime, TransitionStyle transitionStyle, Bundle additionalArguments)
    {
        //Return to available jobs with success
        Bundle arguments = new Bundle();
        arguments.putLong(BundleKeys.DATE_EPOCH_TIME, epochTime);
        //Return to available jobs on that day
        if (additionalArguments != null)
        {
            arguments.putAll(additionalArguments);
        }
        bus.post(new NavigationEvent.NavigateToTab(targetTab, arguments, transitionStyle));
    }

    //if we had problems retrieving the booking show a toast and return to available bookings
    private void handleBookingDetailsError(String errorMessage)
    {
        Log.d("Handy Error", "Error getting booking details: " + errorMessage);
    }

    private void handleBookingClaimError(String errorMessage, String title, String option1, Date returnDate)
    {
        if (errorMessage != null)
        {
            bus.post(new HandyEvent.ClaimJobError(errorMessage));
            showErrorDialogReturnToAvailable(errorMessage, title, option1, returnDate.getTime());
        }
        else
        {
            showNetworkErrorToast();
        }
    }

    private void showErrorDialogReturnToAvailable(String errorMessage, String title, String option1, final long returnDateEpochTime)
    {
        //specific booking error, show an alert dialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        final Bundle arguments = new Bundle();
        arguments.putLong(BundleKeys.DATE_EPOCH_TIME, returnDateEpochTime);

        // set dialog message
        alertDialogBuilder
                .setTitle(title)
                .setMessage(errorMessage)
                .setCancelable(false)
                .setPositiveButton(option1, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        bus.post(new NavigationEvent.NavigateToTab(MainViewTab.SCHEDULED_JOBS, arguments, TransitionStyle.REFRESH_TAB));
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    private void showNetworkErrorToast()
    {
        //generic network problem
        //show a toast about connectivity issues
        showToast(R.string.error_connectivity, Toast.LENGTH_LONG);
    }

    private void handleNotifyOnMyWayError(final HandyEvent.ReceiveNotifyJobOnMyWayError event)
    {
        handleCheckInFlowError(event.error.getMessage());
    }

    private void handleNotifyCheckInError(final HandyEvent.ReceiveNotifyJobCheckInError event)
    {
        handleCheckInFlowError(event.error.getMessage());
    }

    private void handleCheckInFlowError(String errorMessage)
    {
        if (errorMessage != null)
        {
            showToast(errorMessage);
        }
        else
        {
            showNetworkErrorToast();
        }
    }

    private void setSourceInfo()
    {
        if (getArguments().containsKey(BundleKeys.BOOKING_SOURCE))
        {
            mSource = getArguments().getString(BundleKeys.BOOKING_SOURCE);
        }
        else if (getArguments().containsKey(BundleKeys.DEEPLINK))
        {
            mSource = SOURCE_LATE_DISPATCH;
            mSourceExtras = getArguments();
        }
    }

    private void updateDisplay()
    {
        mContainer.removeAllViews();
        switch (mBooking.getBookingProgress(getLoggedInUserId()))
        {
            case READY_FOR_CLAIM:
                AvailableBookingView availableBookingView = new AvailableBookingView(getContext());
                availableBookingView.setBooking(mBooking);
                mContainer.addView(availableBookingView);
                setActionBarTitle(R.string.available_job);
                break;
            case READY_FOR_ON_MY_WAY:
                ClaimedBookingView claimedBookingView = new ClaimedBookingView(getContext());
                claimedBookingView.setBooking(mBooking);
                mContainer.addView(claimedBookingView);
                setActionBarTitle(R.string.claimed_job);
                break;
            case READY_FOR_CHECK_IN:
                ClaimedBookingView checkinBookingView = new ClaimedBookingView(getContext());
                checkinBookingView.setBooking(mBooking);
                checkinBookingView.setCheckedIn(true);
                mContainer.addView(checkinBookingView);
                setActionBarTitle(R.string.claimed_job);
                break;
            case READY_FOR_CHECK_OUT:
                CheckOutBookingView checkOutBookingView = new CheckOutBookingView(getContext());
                checkOutBookingView.setBooking(mBooking);
                mContainer.addView(checkOutBookingView);
                setActionBarTitle(R.string.claimed_job);
                break;
            case FINISHED:
            default:
                FinishedBookingView finishedBookingView = new FinishedBookingView(getContext());
                finishedBookingView.setBooking(mBooking);
                mContainer.addView(finishedBookingView);
                break;
        }
    }
}
