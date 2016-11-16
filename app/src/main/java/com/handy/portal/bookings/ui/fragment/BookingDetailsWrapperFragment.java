package com.handy.portal.bookings.ui.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.gson.Gson;
import com.handy.portal.R;
import com.handy.portal.bookings.constant.BookingActionButtonType;
import com.handy.portal.bookings.constant.BookingProgress;
import com.handy.portal.bookings.manager.BookingManager;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.bookings.model.BookingClaimDetails;
import com.handy.portal.bookings.ui.fragment.dialog.ClaimTargetDialogFragment;
import com.handy.portal.bookings.ui.fragment.dialog.ConfirmBookingCancelCancellationPolicyDialogFragment;
import com.handy.portal.bookings.ui.fragment.dialog.ConfirmBookingCancelKeepRateDialogFragment;
import com.handy.portal.bookings.ui.fragment.dialog.CustomerNoShowDialogFragment;
import com.handy.portal.bookings.util.SupportActionUtils;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewPage;
import com.handy.portal.constant.PrefsKey;
import com.handy.portal.constant.RequestCode;
import com.handy.portal.constant.SupportActionType;
import com.handy.portal.constant.TransitionStyle;
import com.handy.portal.constant.WarningButtonsText;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.NavigationEvent;
import com.handy.portal.library.ui.layout.SlideUpPanelLayout;
import com.handy.portal.library.util.FragmentUtils;
import com.handy.portal.library.util.UIUtils;
import com.handy.portal.location.manager.LocationManager;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.AvailableJobsLog;
import com.handy.portal.logger.handylogger.model.CheckInFlowLog;
import com.handy.portal.logger.handylogger.model.RequestedJobsLog;
import com.handy.portal.logger.handylogger.model.ScheduledJobsLog;
import com.handy.portal.manager.PrefsManager;
import com.handy.portal.ui.element.SupportActionContainerView;
import com.handy.portal.ui.fragment.ActionBarFragment;
import com.handy.portal.ui.fragment.MainActivityFragment;

import org.greenrobot.eventbus.Subscribe;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BookingDetailsWrapperFragment extends ActionBarFragment implements View.OnClickListener, CustomerNoShowDialogFragment.OnReportCustomerNoShowButtonClickedListener
{
    public static final String SOURCE_LATE_DISPATCH = "late_dispatch";

    private static final Gson GSON = new Gson();

    @Inject
    PrefsManager mPrefsManager;
    @Inject
    LocationManager mLocationManager;
    @Inject
    BookingManager mBookingManager;

    @BindView(R.id.booking_details_slide_up_panel_container)
    SlideUpPanelLayout mSlideUpPanelContainer;
    @BindView(R.id.fetch_error_view)
    View mFetchErrorView;
    @BindView(R.id.fetch_error_text)
    TextView mErrorText;

    private Booking mBooking;
    private String mRequestedBookingId;
    private Booking.BookingType mRequestedBookingType;
    private Date mAssociatedBookingDate;
    private String mSource;
    private Bundle mSourceExtras;
    private MainViewPage mCurrentPage;


    @Override
    protected MainViewPage getAppPage()
    {
        return mCurrentPage;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments == null) { return; }

        mBooking = (Booking) arguments.getSerializable(BundleKeys.BOOKING);

        mRequestedBookingId = arguments.getString(BundleKeys.BOOKING_ID);

        // TODO: pass enum value as serializable instead of string wrapping/unwrapping
        // http://stackoverflow.com/questions/3293020/android-how-to-put-an-enum-in-a-bundle

        final String bookingType = arguments.getString(BundleKeys.BOOKING_TYPE);
        if (bookingType != null)
        {
            mRequestedBookingType = Booking.BookingType.valueOf(bookingType.toUpperCase());
        }

        mAssociatedBookingDate = new Date(arguments.getLong(BundleKeys.BOOKING_DATE, 0L));
        if (arguments.containsKey(BundleKeys.BOOKING_SOURCE))
        {
            mSource = arguments.getString(BundleKeys.BOOKING_SOURCE);
        }
        else if (arguments.containsKey(BundleKeys.DEEPLINK))
        {
            mSource = SOURCE_LATE_DISPATCH;
            mSourceExtras = arguments;
        }
        mCurrentPage = (MainViewPage) arguments.getSerializable(BundleKeys.PAGE);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        if (mBooking != null)
        {
            onReceiveBookingDetailsSuccess(new HandyEvent.ReceiveBookingDetailsSuccess(mBooking));
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        bus.register(this);

        if (!MainActivityFragment.clearingBackStack && mBooking == null)
        {
            requestBookingDetails(mRequestedBookingId, mRequestedBookingType, mAssociatedBookingDate);
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        bus.unregister(this);

        if (mBooking != null && mBooking.isCheckedIn())
        {
            List<Booking.BookingInstructionUpdateRequest> checklist = mBooking.getCustomerPreferences();
            mPrefsManager.setBookingInstructions(mBooking.getId(), GSON.toJson(checklist));
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_new_booking_details, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.try_again_button)
    public void onClickRequestDetails()
    {
        requestBookingDetails(mRequestedBookingId, mRequestedBookingType, mAssociatedBookingDate);
    }

    @Subscribe
    public void onReceiveBookingDetailsSuccess(HandyEvent.ReceiveBookingDetailsSuccess event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        mBooking = event.booking;
        final Booking.BookingStatus bookingStatus = mBooking.inferBookingStatus(getLoggedInUserId());
        if (bookingStatus == Booking.BookingStatus.UNAVAILABLE)
        {
            final Bundle arguments = new Bundle();
            arguments.putString(BundleKeys.MESSAGE, getString(R.string.job_no_longer_available));
            arguments.putBundle(BundleKeys.EXTRAS, getArguments());
            returnToPage(MainViewPage.AVAILABLE_JOBS, 0, TransitionStyle.REFRESH_PAGE, arguments);
        }
        else
        {
            updateDisplay();
        }
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
        final BookingClaimDetails bookingClaimDetails = event.bookingClaimDetails;
        final Booking booking = bookingClaimDetails.getBooking();
        if (mBooking != null && mBooking.isRequested())
        {
            bus.post(new LogEvent.AddLogEvent(new RequestedJobsLog.ClaimSuccess(mBooking)));
        }
        else
        {
            bus.post(new LogEvent.AddLogEvent(new AvailableJobsLog.ClaimSuccess(booking, mSource, mSourceExtras, 0.0f)));
        }

        if (booking.isClaimedByMe() || booking.getProviderId().equals(getLoggedInUserId()))
        {
            if (bookingClaimDetails.shouldShowClaimTarget())
            {
                BookingClaimDetails.ClaimTargetInfo claimTargetInfo = bookingClaimDetails.getClaimTargetInfo();
                ClaimTargetDialogFragment claimTargetDialogFragment = new ClaimTargetDialogFragment();
                claimTargetDialogFragment.setDisplayData(claimTargetInfo); //wrong way to pass argument to a fragment
                claimTargetDialogFragment.show(getFragmentManager(), ClaimTargetDialogFragment.FRAGMENT_TAG);

                returnToPage(MainViewPage.SCHEDULED_JOBS, booking.getStartDate().getTime(), null, null);
            }
            else
            {
                TransitionStyle transitionStyle = (booking.isRecurring() ? TransitionStyle.SERIES_CLAIM_SUCCESS : TransitionStyle.JOB_CLAIM_SUCCESS);
                returnToPage(MainViewPage.SCHEDULED_JOBS, booking.getStartDate().getTime(), transitionStyle, null);
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
        if (mBooking != null && mBooking.isRequested())
        {
            bus.post(new LogEvent.AddLogEvent(new RequestedJobsLog.ClaimError(mBooking,
                    event.error.getMessage())));
        }
        else
        {
            bus.post(new LogEvent.AddLogEvent(new AvailableJobsLog.ClaimError(event.getBooking(), mSource, mSourceExtras, 0.0f, event.error.getMessage())));
        }
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        handleBookingClaimError(event.error.getMessage(),
                getString(R.string.job_claim_error), getString(R.string.return_to_available_jobs),
                event.getBooking().getStartDate());
    }

    @Subscribe
    public void onReceiveNotifyJobOnMyWaySuccess(final HandyEvent.ReceiveNotifyJobOnMyWaySuccess event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        bus.post(new LogEvent.AddLogEvent(new CheckInFlowLog.OnMyWaySuccess(
                mBooking, mLocationManager.getLastKnownLocationData())));

        //refresh the page with the new booking
        mBooking = event.booking;
        updateDisplay();
    }

    @Subscribe
    public void onReceiveNotifyJobOnMyWayError(final HandyEvent.ReceiveNotifyJobOnMyWayError event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        if (mBooking != null)
        {
            bus.post(new LogEvent.AddLogEvent(new CheckInFlowLog.OnMyWayFailure(
                    mBooking, mLocationManager.getLastKnownLocationData())));
        }
        /*
            else, mBooking hasn't been set yet.
            one obvious case in which this could happen
            is when this event is the result of a previous instance of this fragment
         */

        handleNotifyOnMyWayError(event);
    }

    @Subscribe
    public void onReceiveNotifyJobCheckInSuccess(final HandyEvent.ReceiveNotifyJobCheckInSuccess event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        bus.post(new LogEvent.AddLogEvent(new CheckInFlowLog.CheckInSuccess(
                mBooking, mLocationManager.getLastKnownLocationData())));

        //refresh the page with the new booking
        mBooking = event.booking;
        updateDisplay();
    }

    @Subscribe
    public void onReceiveNotifyJobCheckInError(final HandyEvent.ReceiveNotifyJobCheckInError event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        bus.post(new LogEvent.AddLogEvent(new CheckInFlowLog.CheckInFailure(
                mBooking, mLocationManager.getLastKnownLocationData())));

        handleNotifyCheckInError(event);
    }

    @Subscribe
    public void onSupportActionTriggered(HandyEvent.SupportActionTriggered event)
    {
        SupportActionType supportActionType = SupportActionUtils.getSupportActionType(event.action);
        String bookingId = mBooking.getId();
        bus.post(new LogEvent.AddLogEvent(new ScheduledJobsLog.JobSupportItemSelected(bookingId, event.action.getActionName())));

        switch (supportActionType)
        {
            case NOTIFY_EARLY:
                showUpdateArrivalTimeDialog(mBooking, R.string.notify_customer_of_earliness, Booking.ArrivalTimeOption.earlyValues());
                break;
            case NOTIFY_LATE:
                showUpdateArrivalTimeDialog(mBooking, R.string.notify_customer_of_lateness, Booking.ArrivalTimeOption.lateValues());
                break;
            case REPORT_NO_SHOW:
                showCustomerNoShowConfirmation(event.action);
                break;
            case RETRACT_NO_SHOW:
                requestCancelNoShow();
                break;
            case ISSUE_UNSAFE:
            case ISSUE_HOURS:
            case ISSUE_OTHER:
            case RESCHEDULE:
            case CANCELLATION_POLICY:
                goToHelpCenter(event.action);
                break;
            case REMOVE:
                removeJob(event.action);
                break;
            case UNASSIGN_FLOW:
                unassignJob(event.action);
                break;
        }
    }


    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data)
    {
        if (resultCode == Activity.RESULT_OK)
        {
            switch (requestCode)
            {
                case RequestCode.REMOVE_BOOKING:
                    requestRemoveJob();
                    break;

            }
        }
        else if (resultCode == Activity.RESULT_CANCELED)
        {
            switch (requestCode)
            {
                case RequestCode.REMOVE_BOOKING:
                    showJobSupportSlideUpPanel(); //show it again
                    break;
            }
        }
    }

    @Subscribe
    public void onReceiveReportNoShowSuccess(HandyEvent.ReceiveReportNoShowSuccess event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        mBooking = event.booking;
        updateDisplay();

        final String toastMessageFormatted =
                getString(R.string.customer_no_show_success_message_formatted,
                        mBooking.getFormattedProviderPayout());
        showToast(toastMessageFormatted, Toast.LENGTH_LONG);
    }

    @Subscribe
    public void onReceiveReportNoShowError(HandyEvent.ReceiveReportNoShowError event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        showToast(R.string.unable_to_report_no_show, Toast.LENGTH_LONG);
    }

    @Subscribe
    public void onReceiveCancelNoShowSuccess(HandyEvent.ReceiveCancelNoShowSuccess event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        mBooking = event.booking;
        updateDisplay();
        showToast(R.string.customer_no_show_cancelled, Toast.LENGTH_LONG);
    }

    @Subscribe
    public void onReceiveCancelNoShowError(HandyEvent.ReceiveCancelNoShowError event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        showToast(R.string.unable_to_cancel_no_show, Toast.LENGTH_LONG);
    }

    @Override
    public void onClick(final View v)
    {
        showJobSupportSlideUpPanel();
    }

    private void showJobSupportSlideUpPanel()
    {
        bus.post(new LogEvent.AddLogEvent(new ScheduledJobsLog.JobSupportSelected(mBooking.getId())));

        LinearLayout layout = UIUtils.createLinearLayout(getContext(), LinearLayout.VERTICAL);
        layout.addView(new SupportActionContainerView(
                getContext(), SupportActionUtils.ETA_ACTION_NAMES, mBooking));
        layout.addView(new SupportActionContainerView(
                getContext(), SupportActionUtils.ISSUE_ACTION_NAMES, mBooking));
        mSlideUpPanelContainer.showPanel(R.string.job_support, layout);
    }

    @Subscribe
    public void onReceiveRemoveJobSuccess(final HandyEvent.ReceiveRemoveJobSuccess event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        if (!event.booking.isClaimedByMe() || event.booking.getProviderId().equals(Booking.NO_PROVIDER_ASSIGNED))
        {
            final Booking.Action removeAction =
                    mBooking.getAction(Booking.Action.ACTION_REMOVE);

            bus.post(new LogEvent.AddLogEvent(new ScheduledJobsLog.RemoveJobSuccess(
                    mBooking,
                    ScheduledJobsLog.RemoveJobLog.POPUP,
                    getRemovalTypeFromBookingRemoveAction(removeAction),
                    removeAction != null ? removeAction.getFeeAmount() : 0,
                    removeAction != null ? removeAction.getWaivedAmount() : 0,
                    removeAction != null ? removeAction.getWarningText() : null
            )));
            TransitionStyle transitionStyle = TransitionStyle.JOB_REMOVE_SUCCESS;
            returnToPage(MainViewPage.SCHEDULED_JOBS, event.booking.getStartDate().getTime(), transitionStyle, null);
        }
        else
        {
            //Something has gone very wrong, show a generic error and return to date based on original associated booking
            final String errorMessage = getString(R.string.job_remove_error_generic);
            trackRemoveJobError(errorMessage);
            handleBookingRemoveError(errorMessage);
        }
    }

    @Subscribe
    public void onReceiveRemoveJobError(final HandyEvent.ReceiveRemoveJobError event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        trackRemoveJobError(event.error.getMessage());
        handleBookingRemoveError(event.error.getMessage());
    }

    @Subscribe
    public void onReceiveNotifyJobUpdateArrivalTimeSuccess(final HandyEvent.ReceiveNotifyJobUpdateArrivalTimeSuccess event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));

        //refresh the page with the new booking
        mBooking = event.booking;
        updateDisplay();

        showToast(R.string.eta_success, Toast.LENGTH_LONG);
    }

    @Subscribe
    public void onReceiveNotifyJobUpdateArrivalTimeError(final HandyEvent.ReceiveNotifyJobUpdateArrivalTimeError event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));

        String errorMessage = event.error.getMessage();
        if (errorMessage != null)
        {
            showToast(errorMessage);
        }
        else
        {
            showNetworkErrorToast();
        }
    }

    @NonNull
    private String getRemovalTypeFromBookingRemoveAction(@Nullable Booking.Action bookingRemoveAction)
    {
        return bookingRemoveAction != null && bookingRemoveAction.getKeepRate() != null ?
                ScheduledJobsLog.RemoveJobLog.KEEP_RATE : ScheduledJobsLog.RemoveJobLog.POPUP;
    }

    private void trackRemoveJobError(final String errorMessage)
    {
        final Booking.Action removeAction =
                mBooking.getAction(Booking.Action.ACTION_REMOVE);

        bus.post(new LogEvent.AddLogEvent(new ScheduledJobsLog.RemoveJobError(
                mBooking,
                ScheduledJobsLog.RemoveJobLog.POPUP,
                getRemovalTypeFromBookingRemoveAction(removeAction),
                removeAction != null ? removeAction.getFeeAmount() : 0,
                removeAction != null ? removeAction.getWaivedAmount() : 0,
                removeAction != null ? removeAction.getWarningText() : null,
                errorMessage
        )));
    }

    private String getLoggedInUserId()
    {
        return mPrefsManager.getSecureString(PrefsKey.LAST_PROVIDER_ID);
    }

    private void returnToPage(MainViewPage targetPage, long epochTime, TransitionStyle transitionStyle, Bundle additionalArguments)
    {
        //Return to available jobs with success
        Bundle arguments = new Bundle();
        arguments.putLong(BundleKeys.DATE_EPOCH_TIME, epochTime);
        //Return to available jobs on that day
        if (additionalArguments != null)
        {
            arguments.putAll(additionalArguments);
        }
        bus.post(new NavigationEvent.NavigateToPage(targetPage, arguments, transitionStyle));
    }

    private void handleBookingDetailsError(String errorMessage)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        if (errorMessage != null)
        {
            mErrorText.setText(errorMessage);
        }
        else
        {
            mErrorText.setText(R.string.error_fetching_connectivity_issue);
        }
        mFetchErrorView.setVisibility(View.VISIBLE);
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
                        bus.post(new NavigationEvent.NavigateToPage(MainViewPage.SCHEDULED_JOBS, arguments, TransitionStyle.REFRESH_PAGE));
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    private void showNetworkErrorToast()
    {
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

    public void updateDisplay()
    {
        mSlideUpPanelContainer.removeAllViews();

        int bookingProgress = mBooking.getBookingProgress();
        if (bookingProgress == BookingProgress.READY_FOR_CHECK_OUT
                && mBooking.getCustomerPreferences().size() > 0)
        //in progress booking (after check in and before check out)
        {
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.replace(mSlideUpPanelContainer.getId(), InProgressBookingFragment.newInstance(mBooking, mSource)).commit();
        }
        else //not in progress booking
        {
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.replace(mSlideUpPanelContainer.getId(),
                    BookingFragment.newInstance(mBooking, mSource, false))
                    .commit();
        }
    }

    //Show a radio button option dialog to select arrival time for the ETA action
    private void showUpdateArrivalTimeDialog(Booking booking, int titleStringId, final List<Booking.ArrivalTimeOption> options)
    {
        final String bookingId = booking.getId();

        String[] optionStrings = Collections2.transform(options, new Function<Booking.ArrivalTimeOption, String>()
        {
            @Override
            public String apply(Booking.ArrivalTimeOption input)
            {
                return getString(input.getStringId());
            }
        }).toArray(new String[options.size()]);

        new AlertDialog.Builder(getActivity())
                .setTitle(titleStringId)
                .setSingleChoiceItems(optionStrings, 0, null)
                .setPositiveButton(R.string.send_update, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int id)
                            {
                                int checkedItemPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                                if (checkedItemPosition >= 0 && checkedItemPosition < options.size())
                                {
                                    requestNotifyUpdateArrivalTime(bookingId, options.get(checkedItemPosition));
                                }
                            }
                        }
                )
                .setNegativeButton(R.string.back, null)
                .create()
                .show();
    }

    private void showCustomerNoShowDialogFragment()
    {
        if (getChildFragmentManager().findFragmentByTag(CustomerNoShowDialogFragment.FRAGMENT_TAG) == null)
        {
            CustomerNoShowDialogFragment customerNoShowDialogFragment =
                    CustomerNoShowDialogFragment.newInstance(mBooking);
            FragmentUtils.safeLaunchDialogFragment(
                    customerNoShowDialogFragment,
                    this,
                    CustomerNoShowDialogFragment.FRAGMENT_TAG);
        }
    }

    private void showCustomerNoShowAlertDialog(@NonNull final Booking.Action action)
    {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.report_customer_no_show)
                .setMessage(action.getWarningText())
                .setPositiveButton(R.string.report_no_show, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which)
                    {
                        bus.post(new HandyEvent.ActionWarningAccepted(action));
                        requestReportNoShow();
                    }
                })
                .setNegativeButton(R.string.back, null)
                .create()
                .show();
    }

    private void showCustomerNoShowConfirmation(@NonNull final Booking.Action action)
    {
        boolean customerNoShowModalEnabled =
                configManager.getConfigurationResponse() != null
                        && configManager.getConfigurationResponse().isCustomerNoShowModalEnabled();
        if (customerNoShowModalEnabled)
        {
            showCustomerNoShowDialogFragment();
        }
        else
        {
            showCustomerNoShowAlertDialog(action);
        }
    }

    private void requestNotifyUpdateArrivalTime(String bookingId, Booking.ArrivalTimeOption arrivalTimeOption)
    {
        mSlideUpPanelContainer.hidePanel();
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        mBookingManager.requestNotifyUpdateArrivalTime(bookingId, arrivalTimeOption);
    }

    private void requestReportNoShow()
    {
        mSlideUpPanelContainer.hidePanel();
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        mBookingManager.requestReportNoShow(mBooking.getId(), mLocationManager.getLastKnownLocationData());
    }

    private void requestCancelNoShow()
    {
        mSlideUpPanelContainer.hidePanel();
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        mBookingManager.requestCancelNoShow(mBooking.getId(), mLocationManager.getLastKnownLocationData());
    }

    private void goToHelpCenter(final Booking.Action action)
    {
        final Bundle arguments = new Bundle();
        arguments.putString(BundleKeys.HELP_REDIRECT_PATH, action.getHelpRedirectPath());
        bus.post(new NavigationEvent.NavigateToPage(MainViewPage.HELP_WEBVIEW, arguments, true));
    }

    private void unassignJob(@NonNull Booking.Action removeAction)
    {
        Bundle arguments = new Bundle();
        arguments.putSerializable(BundleKeys.BOOKING, mBooking);
        arguments.putSerializable(BundleKeys.BOOKING_ACTION, removeAction);
        bus.post(new NavigationEvent.NavigateToPage(MainViewPage.CANCELLATION_REQUEST, arguments));
    }

    private void removeJob(@NonNull Booking.Action removeAction)
    {
        showRemoveJobWarningDialog(removeAction.getWarningText(), removeAction);
    }

    /**
     * shows the custom remove job warning dialog if the keep rate data is there,
     * based on the given booking
     *
     * @return true if the custom warning dialog was shown/is already showing, false otherwise
     */
    private boolean showCustomRemoveJobWarningDialogIfNecessary()
    {
        final Booking.Action removeAction = mBooking.getAction(Booking.Action.ACTION_REMOVE);
        if (removeAction != null)
        {
            if (removeAction.getExtras() != null
                    && removeAction.getExtras().getCancellationPolicy() != null)
            {
                if (getChildFragmentManager().findFragmentByTag(ConfirmBookingCancelCancellationPolicyDialogFragment.FRAGMENT_TAG) == null)
                {
                    final DialogFragment fragment = ConfirmBookingCancelCancellationPolicyDialogFragment.newInstance(mBooking);
                    fragment.setTargetFragment(BookingDetailsWrapperFragment.this, RequestCode.REMOVE_BOOKING);
                    FragmentUtils.safeLaunchDialogFragment(fragment, this, ConfirmBookingCancelCancellationPolicyDialogFragment.FRAGMENT_TAG);
                }
                return true;
            }
            else if (removeAction.getKeepRate() != null)
            {
                if (getChildFragmentManager().findFragmentByTag(ConfirmBookingCancelKeepRateDialogFragment.FRAGMENT_TAG) == null)
                {
                    final DialogFragment fragment = ConfirmBookingCancelKeepRateDialogFragment.newInstance(mBooking);
                    fragment.setTargetFragment(BookingDetailsWrapperFragment.this, RequestCode.REMOVE_BOOKING);
                    FragmentUtils.safeLaunchDialogFragment(fragment, this, ConfirmBookingCancelKeepRateDialogFragment.FRAGMENT_TAG);
                }
                return true;
            }
        }
        return false;
    }

    private void showRemoveJobWarningDialog(final String warning, @NonNull final Booking.Action action)
    {
        bus.post(new LogEvent.AddLogEvent(new ScheduledJobsLog.RemoveJobConfirmationShown(
                mBooking, ScheduledJobsLog.RemoveJobLog.POPUP, action.getFeeAmount(),
                action.getWaivedAmount(), action.getWarningText())));
        bus.post(new HandyEvent.ShowConfirmationRemoveJob());

        boolean customWarningDialogShown = showCustomRemoveJobWarningDialogIfNecessary();
        if (customWarningDialogShown)
        {
            mSlideUpPanelContainer.hidePanel();
        }
        else
        {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
            WarningButtonsText warningButtonsText = WarningButtonsText.REMOVE_JOB;

            // set dialog message
            alertDialogBuilder
                    .setTitle(warningButtonsText.getTitleStringId())
                    .setMessage(warning)
                    .setPositiveButton(warningButtonsText.getPositiveStringId(), new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int id)
                        {
                            //proceed with action, we have accepted the warning
                            bus.post(new HandyEvent.ActionWarningAccepted(BookingActionButtonType.REMOVE));
                            requestRemoveJob();
                        }
                    })
                    .setNegativeButton(warningButtonsText.getNegativeStringId(), null);

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();
            // show it
            alertDialog.show();
        }
    }

    private void requestBookingDetails(String bookingId, Booking.BookingType type, Date bookingDate)
    {
        mFetchErrorView.setVisibility(View.GONE);
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        mBookingManager.requestBookingDetails(bookingId, type, bookingDate);
    }

    private void requestRemoveJob()
    {
        final Booking.Action removeAction = mBooking.getAction(Booking.Action.ACTION_REMOVE);
        String warning = (removeAction != null) ? removeAction.getWarningText() : null;
        bus.post(new LogEvent.AddLogEvent(new ScheduledJobsLog.RemoveJobSubmitted(
                mBooking,
                ScheduledJobsLog.RemoveJobLog.POPUP,
                null,
                removeAction != null ? removeAction.getFeeAmount() : 0,
                removeAction != null ? removeAction.getWaivedAmount() : 0,
                warning
        )));
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        mBookingManager.requestRemoveJob(mBooking);
    }

    private void handleBookingRemoveError(String errorMessage)
    {
        if (errorMessage != null)
        {
            bus.post(new HandyEvent.RemoveJobError(errorMessage));
            showErrorDialogReturnToAvailable(errorMessage, getString(R.string.job_remove_error),
                    getString(R.string.return_to_schedule), mBooking.getStartDate().getTime());
        }
        else
        {
            showNetworkErrorToast();
        }
    }

    /**
     * interface method called by the customer no show dialog fragment
     * <p/>
     * this fragment's onPause() isn't called when launching a DialogFragment,
     * so this is already resumed (and bus is registered) at this point
     */
    @Override
    public void onReportCustomerNoShowButtonClicked()
    {
        requestReportNoShow();
    }
}
