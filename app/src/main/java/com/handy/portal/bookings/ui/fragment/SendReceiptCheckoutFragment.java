package com.handy.portal.bookings.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.handy.portal.R;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.bookings.model.CheckoutRequest;
import com.handy.portal.bookings.ui.fragment.dialog.RateBookingDialogFragment;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.constant.TransitionStyle;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.NavigationEvent;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.CheckInFlowLog;
import com.handy.portal.manager.ConfigManager;
import com.handy.portal.manager.PrefsManager;
import com.handy.portal.model.LocationData;
import com.handy.portal.model.ProBookingFeedback;
import com.handy.portal.ui.activity.BaseActivity;
import com.handy.portal.ui.fragment.ActionBarFragment;
import com.handy.portal.ui.view.CheckoutCompletedTaskView;
import com.handy.portal.util.DateTimeUtils;
import com.handy.portal.util.UIUtils;
import com.handy.portal.util.Utils;
import com.squareup.otto.Subscribe;

import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;


public class SendReceiptCheckoutFragment extends ActionBarFragment implements View.OnFocusChangeListener, SignaturePad.OnSignedListener
{
    @Inject
    ConfigManager mConfigManager;
    @Inject
    PrefsManager mPrefsManager;

    @Bind(R.id.started_time_text)
    TextView mStartTimeText;
    @Bind(R.id.ended_time_text)
    TextView mEndTimeText;
    @Bind(R.id.send_note_formatted_text)
    TextView mSendNoteText;
    @Bind(R.id.send_note_edit_text)
    EditText mSendNoteEditText;
    @Bind(R.id.completed_tasks_header)
    View mCompletedTasksHeader;
    @Bind(R.id.checklist_column_one)
    ViewGroup mChecklistFirstColumn;
    @Bind(R.id.checklist_column_two)
    ViewGroup mChecklistSecondColumn;
    @Bind(R.id.signature_pad)
    SignaturePad mSignaturePad;
    @Bind(R.id.complete_checkout_button)
    Button mCompleteCheckoutButton;

    private static final IntentFilter mTimeIntentFilter = new IntentFilter(Intent.ACTION_TIME_TICK);
    private Booking mBooking;
    private BroadcastReceiver mTimeBroadcastReceiver;
    private int mHiddenTasksCount;

    @Override
    protected MainViewTab getTab()
    {
        return MainViewTab.SEND_RECEIPT_CHECKOUT;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mHiddenTasksCount = 0;
        mTimeBroadcastReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(final Context context, final Intent intent)
            {
                mEndTimeText.setText(DateTimeUtils.getTimeWithoutDate(Calendar.getInstance().getTime()));
            }
        };

        Bundle args = getArguments();
        if (args != null)
        {
            mBooking = (Booking) args.getSerializable(BundleKeys.BOOKING);
        }
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
    {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_send_receipt_checkout, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        setOptionsMenuEnabled(true);
        setBackButtonEnabled(true);
        setActionBarTitle(R.string.send_your_receipt);
        setActionBarVisible(true);

        initialize();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        getContext().registerReceiver(mTimeBroadcastReceiver, mTimeIntentFilter);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        getContext().unregisterReceiver(mTimeBroadcastReceiver);
    }

    @OnClick(R.id.clear_signature_button)
    public void clearSignature()
    {
        mSignaturePad.clear();
    }

    @OnClick(R.id.complete_checkout_button)
    public void completeCheckout()
    {
        LocationData locationData = getLocationData();

        boolean showCheckoutRatingFlow = false;
        if (mConfigManager.getConfigurationResponse() != null)
        {
            showCheckoutRatingFlow = mConfigManager.getConfigurationResponse().isCheckoutRatingFlowEnabled();
        }

        if (showCheckoutRatingFlow)
        {
            showCheckoutRatingFlow();
        }
        else
        {
            String noteToCustomer = mSendNoteEditText.getText().toString();
            CheckoutRequest checkoutRequest = new CheckoutRequest(locationData,
                    new ProBookingFeedback(-1, ""), noteToCustomer, mBooking.getCustomerPreferences());
            requestNotifyCheckOutJob(mBooking.getId(), checkoutRequest, locationData);
        }
    }

    @OnFocusChange(R.id.send_note_edit_text)
    public void onFocusChange(final View v, final boolean hasFocus)
    {
        if (!hasFocus)
        { UIUtils.dismissKeyboard(getActivity()); }
    }

    @Subscribe
    public void onReceiveNotifyJobCheckOutSuccess(final HandyEvent.ReceiveNotifyJobCheckOutSuccess event)
    {
        if (!event.isAutoCheckIn)
        {
            bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
            bus.post(new LogEvent.AddLogEvent(new CheckInFlowLog.CheckOutSuccess(
                    mBooking, getLocationData())));

            mPrefsManager.setBookingInstructions(mBooking.getId(), null);

            //return to schedule page
            returnToTab(MainViewTab.SCHEDULED_JOBS, mBooking.getStartDate().getTime(), TransitionStyle.REFRESH_TAB);

            showToast(R.string.check_out_success, Toast.LENGTH_LONG);
        }
    }

    @Subscribe
    public void onReceiveNotifyJobCheckOutError(final HandyEvent.ReceiveNotifyJobCheckOutError event)
    {
        if (!event.isAuto)
        {
            bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
            bus.post(new LogEvent.AddLogEvent(new CheckInFlowLog.CheckOutSuccess(
                    mBooking, getLocationData())));
            handleNotifyCheckOutError(event);
        }
    }

    private void initialize()
    {
        mSignaturePad.setOnSignedListener(this);

        if (mBooking != null)
        {
            String startTime = DateTimeUtils.getTimeWithoutDate(mBooking.getCheckInSummary().getCheckInTime());
            String endTime = DateTimeUtils.getTimeWithoutDate(Calendar.getInstance().getTime());
            List<Booking.BookingInstructionUpdateRequest> completedTasks = mBooking.getCustomerPreferences();

            mStartTimeText.setText(startTime);
            mEndTimeText.setText(endTime);

            String firstName = mBooking.getUser().getFirstName();
            mSendNoteText.setText(getResources().getString(
                    R.string.send_note_to_customer_formatted, firstName));

            if (completedTasks != null)
            {
                if (completedTasks.size() == 0)
                {
                    mCompletedTasksHeader.setVisibility(View.GONE);
                }
                else
                {
                    for (Booking.BookingInstructionUpdateRequest completedTask : completedTasks)
                    {
                        if (completedTask.isInstructionCompleted())
                        { addTaskItem(completedTask.getTitle()); }
                    }
                }
            }
        }
    }

    private void addTaskItem(String taskText)
    {
        CheckoutCompletedTaskView checkoutCompletedTaskView = new CheckoutCompletedTaskView(getContext());
        checkoutCompletedTaskView.setTaskText(taskText);

        if (mChecklistFirstColumn.getChildCount() < 3)
        {
            mChecklistFirstColumn.addView(checkoutCompletedTaskView);
        }
        else if (mChecklistSecondColumn.getChildCount() < 2)
        {
            // If more than two, then the third item will be a number of hidden tasks
            mChecklistSecondColumn.addView(checkoutCompletedTaskView);
        }
        else
        {
            ++mHiddenTasksCount;
            if (mHiddenTasksCount != 1)
            {
                checkoutCompletedTaskView.setTaskText(getResources().getString(R.string.more_tasks_formatted, mHiddenTasksCount));
                mChecklistSecondColumn.removeViewAt(2); // Remove 3rd item and replace with count
            }

            mChecklistSecondColumn.addView(checkoutCompletedTaskView);
        }
    }

    private void requestNotifyCheckOutJob(String bookingId, CheckoutRequest checkoutRequest, LocationData locationData)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        bus.post(new LogEvent.AddLogEvent(new CheckInFlowLog.CheckOutSubmitted(mBooking, locationData)));
        bus.post(new HandyEvent.RequestNotifyJobCheckOut(bookingId, checkoutRequest));
    }

    //TODO: check if the dialog is already shown
    private void showCheckoutRatingFlow()
    {
        String noteToCustomer = mSendNoteEditText.getText().toString();

        RateBookingDialogFragment rateBookingDialogFragment = new RateBookingDialogFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(BundleKeys.BOOKING, mBooking);
        arguments.putString(BundleKeys.NOTE_TO_CUSTOMER, noteToCustomer);
        rateBookingDialogFragment.setArguments(arguments);
        try
        {
            rateBookingDialogFragment.show(getFragmentManager(), RateBookingDialogFragment.FRAGMENT_TAG);
        }
        catch (IllegalStateException e)
        {
            Crashlytics.logException(e);
        }
    }

    private void handleNotifyCheckOutError(final HandyEvent.ReceiveNotifyJobCheckOutError event)
    {
        String errorMessage = event.error.getMessage();
        if (errorMessage != null)
        {
            showToast(errorMessage);
        }
        else
        {
            showToast(R.string.error_connectivity, Toast.LENGTH_LONG);
        }
    }

    private void returnToTab(MainViewTab targetTab, long epochTime, TransitionStyle transitionStyle)
    {
        //Return to available jobs with success
        Bundle arguments = new Bundle();
        arguments.putLong(BundleKeys.DATE_EPOCH_TIME, epochTime);
        //Return to available jobs on that day
        bus.post(new NavigationEvent.NavigateToTab(targetTab, arguments, transitionStyle));
    }

    private LocationData getLocationData()
    {
        return Utils.getCurrentLocation((BaseActivity) getActivity());
    }

    @Override
    public void onStartSigning()
    {
        mCompleteCheckoutButton.setAlpha(1.0f);
        mCompleteCheckoutButton.setEnabled(true);
        mCompleteCheckoutButton.setClickable(true);
    }

    @Override
    public void onSigned() { }

    @Override
    public void onClear()
    {
        mCompleteCheckoutButton.setAlpha(0.5f);
        mCompleteCheckoutButton.setEnabled(false);
        mCompleteCheckoutButton.setClickable(false);
    }
}
