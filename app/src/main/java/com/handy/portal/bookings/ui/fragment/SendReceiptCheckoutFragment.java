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
import com.handy.portal.bookings.manager.BookingManager;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.bookings.model.CheckoutRequest;
import com.handy.portal.bookings.ui.fragment.dialog.RateBookingDialogFragment;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewPage;
import com.handy.portal.constant.TransitionStyle;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.NavigationEvent;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.library.util.UIUtils;
import com.handy.portal.location.manager.LocationManager;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.CheckOutFlowLog;
import com.handy.portal.manager.ConfigManager;
import com.handy.portal.manager.PrefsManager;
import com.handy.portal.model.LocationData;
import com.handy.portal.model.ProBookingFeedback;
import com.handy.portal.ui.fragment.ActionBarFragment;
import com.handy.portal.ui.view.CheckoutCompletedTaskView;

import org.greenrobot.eventbus.Subscribe;

import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;


public class SendReceiptCheckoutFragment extends ActionBarFragment implements View.OnFocusChangeListener, SignaturePad.OnSignedListener
{
    @Inject
    ConfigManager mConfigManager;
    @Inject
    PrefsManager mPrefsManager;
    @Inject
    LocationManager mLocationManager;
    @Inject
    BookingManager mBookingManager;

    @BindView(R.id.started_time_text)
    TextView mStartTimeText;
    @BindView(R.id.ended_time_text)
    TextView mEndTimeText;
    @BindView(R.id.send_note_formatted_text)
    TextView mSendNoteText;
    @BindView(R.id.send_note_edit_text)
    EditText mSendNoteEditText;
    @BindView(R.id.completed_tasks_header)
    View mCompletedTasksHeader;
    @BindView(R.id.checklist_column_one)
    ViewGroup mChecklistFirstColumn;
    @BindView(R.id.checklist_column_two)
    ViewGroup mChecklistSecondColumn;
    @BindView(R.id.signature_pad)
    SignaturePad mSignaturePad;
    @BindView(R.id.complete_checkout_button)
    Button mCompleteCheckoutButton;

    private static final IntentFilter mTimeIntentFilter = new IntentFilter(Intent.ACTION_TIME_TICK);
    private Booking mBooking;
    private BroadcastReceiver mTimeBroadcastReceiver;
    private int mHiddenTasksCount;

    @Override
    protected MainViewPage getAppPage()
    {
        return MainViewPage.SEND_RECEIPT_CHECKOUT;
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
        bus.register(this);
        getContext().registerReceiver(mTimeBroadcastReceiver, mTimeIntentFilter);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        bus.unregister(this);
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
        String noteToCustomer = mSendNoteEditText.getText().toString();
        CheckoutRequest checkoutRequest = new CheckoutRequest(locationData,
                new ProBookingFeedback(-1, ""), noteToCustomer, mBooking.getCustomerPreferences());
        requestNotifyCheckOutJob(mBooking.getId(), checkoutRequest, locationData);
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
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        bus.post(new LogEvent.AddLogEvent(new CheckOutFlowLog.CheckOutSuccess(
                mBooking, getLocationData())));

        mPrefsManager.setBookingInstructions(mBooking.getId(), null);

        showToast(R.string.check_out_success, Toast.LENGTH_LONG);

        showCheckoutRatingFlowIfNeeded();

        returnToPage(MainViewPage.SCHEDULED_JOBS, mBooking.getStartDate().getTime(),
                TransitionStyle.REFRESH_PAGE);
    }

    @Subscribe
    public void onReceiveNotifyJobCheckOutError(final HandyEvent.ReceiveNotifyJobCheckOutError event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        bus.post(new LogEvent.AddLogEvent(new CheckOutFlowLog.CheckOutFailure(
                mBooking, getLocationData())));
        handleNotifyCheckOutError(event);
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

    private void addTaskItem(String taskText)
    {
        // 6 total items, 3 on the left and 3 on the right. Anytime there are more, we replace the
        // last item in the second column with a string of the number of items that aren't being
        // shown
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
                if (mChecklistSecondColumn.getChildCount() == 3)
                {
                    mChecklistSecondColumn.removeViewAt(2); // Remove 3rd item(replaced with count below)
                }
            }

            mChecklistSecondColumn.addView(checkoutCompletedTaskView);
        }
    }

    private void requestNotifyCheckOutJob(String bookingId, CheckoutRequest checkoutRequest, LocationData locationData)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        bus.post(new LogEvent.AddLogEvent(new CheckOutFlowLog.CheckOutSubmitted(mBooking, locationData)));
        mBookingManager.requestNotifyCheckOut(bookingId, checkoutRequest);
    }

    private void showCheckoutRatingFlowIfNeeded()
    {
        if (mConfigManager.getConfigurationResponse() != null &&
                mConfigManager.getConfigurationResponse().isCheckoutRatingFlowEnabled())
        {
            RateBookingDialogFragment rateBookingDialogFragment = new RateBookingDialogFragment();
            Bundle arguments = new Bundle();
            arguments.putSerializable(BundleKeys.BOOKING, mBooking);
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

    private void returnToPage(MainViewPage targetPage, long epochTime, TransitionStyle transitionStyle)
    {
        //Return to available jobs with success
        Bundle arguments = new Bundle();
        arguments.putLong(BundleKeys.DATE_EPOCH_TIME, epochTime);
        //Return to available jobs on that day
        bus.post(new NavigationEvent.NavigateToPage(targetPage, arguments, transitionStyle));
    }

    private LocationData getLocationData()
    {
        return mLocationManager.getLastKnownLocationData();
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
