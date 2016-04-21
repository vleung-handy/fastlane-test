package com.handy.portal.ui.fragment.bookings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
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
import com.handy.portal.constant.BookingActionButtonType;
import com.handy.portal.constant.BookingProgress;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.constant.PrefsKey;
import com.handy.portal.constant.RequestCode;
import com.handy.portal.constant.SupportActionType;
import com.handy.portal.constant.TransitionStyle;
import com.handy.portal.constant.WarningButtonsText;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.NavigationEvent;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.AvailableJobsLog;
import com.handy.portal.logger.handylogger.model.CheckInFlowLog;
import com.handy.portal.logger.handylogger.model.ScheduledJobsLog;
import com.handy.portal.manager.PrefsManager;
import com.handy.portal.model.Booking;
import com.handy.portal.model.BookingClaimDetails;
import com.handy.portal.model.ConfigurationResponse;
import com.handy.portal.model.LocationData;
import com.handy.portal.ui.activity.BaseActivity;
import com.handy.portal.ui.element.SupportActionContainerView;
import com.handy.portal.ui.fragment.ActionBarFragment;
import com.handy.portal.ui.fragment.MainActivityFragment;
import com.handy.portal.ui.fragment.dialog.ClaimTargetDialogFragment;
import com.handy.portal.ui.fragment.dialog.ConfirmBookingCancelDialogFragment;
import com.handy.portal.ui.layout.SlideUpPanelLayout;
import com.handy.portal.util.DateTimeUtils;
import com.handy.portal.util.FragmentUtils;
import com.handy.portal.util.SupportActionUtils;
import com.handy.portal.util.UIUtils;
import com.handy.portal.util.Utils;
import com.squareup.otto.Subscribe;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BookingDetailsWrapperFragment extends ActionBarFragment implements View.OnClickListener
{
    public static final String SOURCE_LATE_DISPATCH = "late_dispatch";

    private static final Gson GSON = new Gson();

    @Inject
    PrefsManager mPrefsManager;

    @Bind(R.id.booking_details_slide_up_panel_container)
    SlideUpPanelLayout mSlideUpPanelContainer;
    @Bind(R.id.fetch_error_view)
    View mFetchErrorView;
    @Bind(R.id.fetch_error_text)
    TextView mErrorText;

    private Booking mBooking;
    private String mRequestedBookingId;
    private Booking.BookingType mRequestedBookingType;
    private Date mAssociatedBookingDate;
    private String mSource;
    private Bundle mSourceExtras;
    private boolean mFromPaymentsTab;
    private CountDownTimer mCounter;


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
        if (arguments == null) { return; }

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
        mFromPaymentsTab = arguments.getBoolean(BundleKeys.IS_FOR_PAYMENTS, false);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (!MainActivityFragment.clearingBackStack)
        {
            requestBookingDetails(mRequestedBookingId, mRequestedBookingType, mAssociatedBookingDate);
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();

        if (mCounter != null)
        { mCounter.cancel(); }

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
        handleBookingClaimError(event.error.getMessage(),
                getString(R.string.job_claim_error), getString(R.string.return_to_available_jobs),
                event.getBooking().getStartDate());
    }

    @Subscribe
    public void onReceiveNotifyJobOnMyWaySuccess(final HandyEvent.ReceiveNotifyJobOnMyWaySuccess event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        bus.post(new LogEvent.AddLogEvent(new CheckInFlowLog.OnMyWaySuccess(
                mBooking, getLocationData())));

        //refresh the page with the new booking
        mBooking = event.booking;
        updateDisplay();
    }

    @Subscribe
    public void onReceiveNotifyJobOnMyWayError(final HandyEvent.ReceiveNotifyJobOnMyWayError event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        bus.post(new LogEvent.AddLogEvent(new CheckInFlowLog.OnMyWayError(
                mBooking, getLocationData())));

        handleNotifyOnMyWayError(event);
    }

    @Subscribe
    public void onReceiveNotifyJobCheckInSuccess(final HandyEvent.ReceiveNotifyJobCheckInSuccess event)
    {
        if (!event.isAuto)
        {
            bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
            bus.post(new LogEvent.AddLogEvent(new CheckInFlowLog.CheckInSuccess(
                    mBooking, getLocationData())));

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
            bus.post(new LogEvent.AddLogEvent(new CheckInFlowLog.CheckInError(
                    mBooking, getLocationData())));

            handleNotifyCheckInError(event);
        }
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
                showCustomerNoShowDialog(event.action);
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
    }

    @Subscribe
    public void onReceiveReportNoShowSuccess(HandyEvent.ReceiveReportNoShowSuccess event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        mBooking = event.booking;
        updateDisplay();
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

            final String removalType = removeAction != null && removeAction.getKeepRate() != null ?
                    ScheduledJobsLog.RemoveJobLog.KEEP_RATE : ScheduledJobsLog.RemoveJobLog.POPUP;
            bus.post(new LogEvent.AddLogEvent(new ScheduledJobsLog.RemoveJobSuccess(
                    mBooking,
                    ScheduledJobsLog.RemoveJobLog.POPUP,
                    removalType,
                    removeAction != null ? removeAction.getFeeAmount() : 0,
                    removeAction != null ? removeAction.getWarningText() : null
            )));
            TransitionStyle transitionStyle = TransitionStyle.JOB_REMOVE_SUCCESS;
            returnToTab(MainViewTab.SCHEDULED_JOBS, event.booking.getStartDate().getTime(), transitionStyle, null);
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

    private void trackRemoveJobError(final String errorMessage)
    {
        final Booking.Action removeAction =
                mBooking.getAction(Booking.Action.ACTION_REMOVE);

        final String removalType = removeAction != null && removeAction.getKeepRate() != null ?
                ScheduledJobsLog.RemoveJobLog.KEEP_RATE : ScheduledJobsLog.RemoveJobLog.POPUP;
        bus.post(new LogEvent.AddLogEvent(new ScheduledJobsLog.RemoveJobError(
                mBooking,
                ScheduledJobsLog.RemoveJobLog.POPUP,
                removalType,
                removeAction != null ? removeAction.getFeeAmount() : 0,
                removeAction != null ? removeAction.getWarningText() : null,
                errorMessage
        )));
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

    private void updateDisplay()
    {
        mSlideUpPanelContainer.removeAllViews();

        int bookingProgress = mBooking.getBookingProgress(getLoggedInUserId());

        if(bookingProgress == BookingProgress.READY_FOR_CHECK_OUT
                && mBooking.getCustomerPreferences().size() > 0)
        //in progress booking (after check in and before check out)
        {
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.replace(mSlideUpPanelContainer.getId(), InProgressBookingFragment.newInstance(mBooking, mSource)).commit();
            setTimerIfNeeded();
        }
        else //not in progress booking
        {
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.replace(mSlideUpPanelContainer.getId(),
                    BookingFragment.newInstance(mBooking, mSource, mFromPaymentsTab, false)).commit();
            if(bookingProgress == BookingProgress.READY_FOR_CLAIM)
            {
                setActionBarTitle(R.string.available_job);
            }
            else if(bookingProgress == BookingProgress.READY_FOR_ON_MY_WAY ||
                    bookingProgress == BookingProgress.READY_FOR_CHECK_IN ||
                    bookingProgress == BookingProgress.READY_FOR_CHECK_OUT)
            {
                setTimerIfNeeded();
            }
            else //completed
            {
                setActionBarTitle(R.string.completed_job);
            }
        }
    }

    private ActionBar getActionBar()
    {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
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

    private void showCustomerNoShowDialog(final Booking.Action action)
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

    private void requestNotifyUpdateArrivalTime(String bookingId, Booking.ArrivalTimeOption arrivalTimeOption)
    {
        mSlideUpPanelContainer.hidePanel();
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        bus.post(new HandyEvent.RequestNotifyJobUpdateArrivalTime(bookingId, arrivalTimeOption));
    }

    private void requestReportNoShow()
    {
        mSlideUpPanelContainer.hidePanel();
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        bus.post(new HandyEvent.RequestReportNoShow(mBooking.getId(), getLocationData()));
    }

    private void requestCancelNoShow()
    {
        mSlideUpPanelContainer.hidePanel();
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        bus.post(new HandyEvent.RequestCancelNoShow(mBooking.getId(), getLocationData()));
    }

    private LocationData getLocationData()
    {
        return Utils.getCurrentLocation((BaseActivity) getActivity());
    }

    private void goToHelpCenter(final Booking.Action action)
    {
        final ConfigurationResponse configuration = configManager.getConfigurationResponse();
        if (configuration != null && configuration.shouldUseHelpCenterWebView())
        {
            final Bundle arguments = new Bundle();
            arguments.putString(BundleKeys.HELP_REDIRECT_PATH, action.getHelpRedirectPath());
            bus.post(new NavigationEvent.NavigateToTab(MainViewTab.HELP_WEBVIEW, arguments, true));
        }
        else
        {
            final Bundle arguments = new Bundle();
            arguments.putString(BundleKeys.HELP_NODE_ID, action.getDeepLinkData());
            bus.post(new NavigationEvent.NavigateToTab(MainViewTab.HELP, arguments, true));
        }
    }

    private void unassignJob(@NonNull Booking.Action removeAction)
    {
        Bundle arguments = new Bundle();
        arguments.putSerializable(BundleKeys.BOOKING, mBooking);
        arguments.putSerializable(BundleKeys.BOOKING_ACTION, removeAction);
        bus.post(new NavigationEvent.NavigateToTab(MainViewTab.CANCELLATION_REQUEST, arguments));
    }

    private void removeJob(@NonNull Booking.Action removeAction)
    {
        showRemoveJobWarningDialog(removeAction.getWarningText(), removeAction);
    }

    /**
     * @return true if the custom warning dialog was shown/is already showing, false otherwise
     */
    private boolean showCustomRemoveJobWarningDialog()
    {
        final Booking.Action removeAction = mBooking.getAction(Booking.Action.ACTION_REMOVE);
        if(removeAction != null)
        {
            final Booking.Action.Extras.KeepRate keepRate = removeAction.getKeepRate();
            if (keepRate != null)
            {
                if(getActivity().getSupportFragmentManager().findFragmentByTag(ConfirmBookingCancelDialogFragment.FRAGMENT_TAG) == null)
                {
                    final DialogFragment fragment = ConfirmBookingCancelDialogFragment.newInstance(mBooking);
                    fragment.setTargetFragment(BookingDetailsWrapperFragment.this, RequestCode.REMOVE_BOOKING);
                    FragmentUtils.safeLaunchDialogFragment(fragment, getActivity(), ConfirmBookingCancelDialogFragment.FRAGMENT_TAG);
                }
                return true;
            }
        }
        return false;
    }

    private void showRemoveJobWarningDialog(final String warning, final Booking.Action action)
    {
        bus.post(new LogEvent.AddLogEvent(new ScheduledJobsLog.RemoveJobConfirmationShown(
                mBooking, ScheduledJobsLog.RemoveJobLog.POPUP, action.getFeeAmount(),
                action.getWarningText())));
        bus.post(new HandyEvent.ShowConfirmationRemoveJob());

        boolean customWarningDialogShown = showCustomRemoveJobWarningDialog();
        if(!customWarningDialogShown)
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
        bus.post(new HandyEvent.RequestBookingDetails(bookingId, type, bookingDate));
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
                warning
        )));
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        bus.post(new HandyEvent.RequestRemoveJob(mBooking));
    }

    private void setTimerIfNeeded()
    {
        if (mCounter != null) { mCounter.cancel(); } // cancel the previous counter

        if (DateTimeUtils.isTimeWithinXMillisecondsFromNow(mBooking.getStartDate(),
                DateUtils.HOUR_IN_MILLIS * 3))
        {
            mCounter = DateTimeUtils.setActionBarCountdownTimer(getContext(), getActionBar(),
                    mBooking.getStartDate().getTime() - System.currentTimeMillis(),
                    R.string.start_timer_lowercase_formatted);
        }
        else if (DateTimeUtils.isTimeWithinXMillisecondsFromNow(mBooking.getEndDate(),
                mBooking.getEndDate().getTime() - mBooking.getStartDate().getTime()))
        {
            mCounter = DateTimeUtils.setActionBarCountdownTimer(getContext(), getActionBar(),
                    mBooking.getEndDate().getTime() - System.currentTimeMillis(),
                    R.string.end_timer_lowercase_formatted);
        }
        else if (System.currentTimeMillis() < mBooking.getStartDate().getTime())
        {
            // More than 3 hours before booking start
            setActionBarTitle(R.string.your_job);
        }
        else
        {
            setActionBarTitle(R.string.time_expired);
        }
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
}
