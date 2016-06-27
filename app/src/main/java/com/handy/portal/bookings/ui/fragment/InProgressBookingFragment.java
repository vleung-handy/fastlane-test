package com.handy.portal.bookings.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.handy.portal.R;
import com.handy.portal.bookings.constant.BookingActionButtonType;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.bookings.ui.element.CustomerRequestsView;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewPage;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.NavigationEvent;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.library.util.TextUtils;
import com.handy.portal.library.util.UIUtils;
import com.handy.portal.library.util.Utils;
import com.handy.portal.manager.PrefsManager;
import com.handy.portal.ui.fragment.TimerActionBarFragment;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.handy.portal.bookings.model.Booking.BookingInstructionGroup.GROUP_NOTE_TO_PRO;

/**
 * fragment for handling bookings that are in progress/after provider has checked in & before check out
 */
public class InProgressBookingFragment extends TimerActionBarFragment
{

    @Inject
    PrefsManager mPrefsManager;

    @Bind(R.id.in_progress_booking_no_show_banner_text)
    View mNoShowBannerView;
    @Bind(R.id.in_progress_booking_customer_name_text)
    TextView mCustomerNameText;
    @Bind(R.id.in_progress_booking_job_start_time)
    TextView mJobStartTimeText;
    @Bind(R.id.in_progress_booking_call_customer_view)
    View mCallCustomerView;
    @Bind(R.id.in_progress_booking_message_customer_view)
    View mMessageCustomerView;
    @Bind(R.id.in_progress_booking_booking_support_button)
    Button mSupportButton;
    @Bind(R.id.in_progress_booking_note_to_pro_layout)
    ViewGroup mNoteToProLayout;
    @Bind(R.id.in_progress_booking_note_to_pro_text)
    TextView mNoteToProText;
    @Bind(R.id.in_progress_booking_checklist)
    CustomerRequestsView mCustomerRequestsView;
    @Bind(R.id.in_progress_booking_details_action_helper_text)
    TextView mBookingDetailsActionHelperText;
    @Bind(R.id.in_progress_booking_action_button)
    Button mActionButton;

    private static final Gson GSON = new Gson();

    private Booking mBooking;
    private String mSource; // TODO: refactor this into a enum?
    private View.OnClickListener mOnSupportClickListener;


    public static InProgressBookingFragment newInstance(@NonNull final Booking booking, String source)
    {
        InProgressBookingFragment fragment = new InProgressBookingFragment();
        Bundle args = new Bundle();
        args.putSerializable(BundleKeys.BOOKING, booking);
        args.putString(BundleKeys.BOOKING_SOURCE, source);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected MainViewPage getAppPage()
    {
        return null;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mBooking = (Booking) getArguments().getSerializable(BundleKeys.BOOKING);
        mSource = getArguments().getString(BundleKeys.BOOKING_SOURCE);
    }

    @Override
    public void onAttach(final Context context)
    {
        super.onAttach(context);
        mOnSupportClickListener = (View.OnClickListener) getParentFragment();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_in_progress_booking, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState)
    {
        setDisplay();
        setOptionsMenuEnabled(true);
        setBackButtonEnabled(true);
    }

    public void setDisplay()
    {
        mSupportButton.setOnClickListener(mOnSupportClickListener);

        // Booking actions
        List<Booking.Action> allowedActions = mBooking.getAllowedActions();
        for (Booking.Action action : allowedActions)
        {
            enableActionsIfNeeded(action);
        }

        Booking.User user = mBooking.getUser();
        if (user != null)
        {
            mCustomerNameText.setText(user.getFullName());
        }

        if (mBooking.getCheckInSummary() != null && mBooking.getCheckInSummary().getCheckInTime() != null)
        {
            String dateString = DateTimeUtils.formatDateTo12HourClock(
                    mBooking.getCheckInSummary().getCheckInTime());
            if (dateString != null)
            {
                mJobStartTimeText.setText(dateString.toLowerCase());
            }
        }

        // Booking Instructions
        List<Booking.BookingInstructionGroup> bookingInstructionGroups = mBooking.getBookingInstructionGroups();
        if (bookingInstructionGroups != null && bookingInstructionGroups.size() > 0)
        {
            Booking.BookingInstructionGroup preferencesGroup = null;
            for (Booking.BookingInstructionGroup group : bookingInstructionGroups)
            {
                String groupString = group.getGroup();

                if (groupString.equals(GROUP_NOTE_TO_PRO))
                {
                    List<Booking.BookingInstruction> instructions = group.getInstructions();
                    if (instructions != null && !instructions.isEmpty())
                    {
                        mNoteToProText.setText(instructions.get(0).getDescription());
                        mNoteToProLayout.setVisibility(View.VISIBLE);
                    }
                }
                else if (Booking.BookingInstructionGroup.GROUP_PREFERENCES.equals(group.getGroup()))
                {
                    preferencesGroup = group;
                }
            }

            if (preferencesGroup != null)
            {
                List<Booking.BookingInstructionUpdateRequest> checklist = null;
                if (TextUtils.isNullOrEmpty(mPrefsManager.getBookingInstructions(mBooking.getId())))
                {
                    checklist = mBooking.getCustomerPreferences();
                }
                else
                {
                    try
                    {
                        Booking.BookingInstructionUpdateRequest[] checklistArray = GSON.fromJson(
                                mPrefsManager.getBookingInstructions(mBooking.getId()),
                                Booking.BookingInstructionUpdateRequest[].class);
                        checklist = Arrays.asList(checklistArray);
                        mBooking.setCustomerPreferences(checklist);
                    }
                    catch (JsonSyntaxException e)
                    {
                        Crashlytics.logException(e);
                    }
                }
                if (checklist != null)
                {
                    mCustomerRequestsView.setDisplay(checklist);
                }
            }
        }

        boolean noShowReported = mBooking.getAction(Booking.Action.ACTION_RETRACT_NO_SHOW) != null;
        if (noShowReported)
        {
            mNoShowBannerView.setVisibility(View.VISIBLE);
        }

        setTimerIfNeeded(mBooking.getStartDate(), mBooking.getEndDate());
    }

    @OnClick(R.id.in_progress_booking_details_view)
    public void swapToJobDetails()
    {
        Bundle args = new Bundle();
        args.putSerializable(BundleKeys.BOOKING, mBooking);
        args.putString(BundleKeys.BOOKING_SOURCE, mSource);
        args.putBoolean(BundleKeys.BOOKING_SHOULD_HIDE_ACTION_BUTTONS, true);

        bus.post(new NavigationEvent.NavigateToPage(MainViewPage.NOT_IN_PROGRESS_JOB_DETAILS, args, true));
    }

    @OnClick(R.id.in_progress_booking_action_button)
    public void checkOut()
    {
        final boolean proReportedNoShow = mBooking.getAction(Booking.Action.ACTION_RETRACT_NO_SHOW) != null;
        if (proReportedNoShow || mBooking.isAnyPreferenceChecked())
        {
            Bundle bundle = new Bundle();
            bundle.putSerializable(BundleKeys.BOOKING, mBooking);
            bus.post(new NavigationEvent.NavigateToPage(MainViewPage.SEND_RECEIPT_CHECKOUT, bundle, true));
        }
        else
        {
            showToast(getContext().getString(R.string.tap_preferences_before_checkout),
                    Toast.LENGTH_LONG, Gravity.TOP);
        }
    }

    @OnClick(R.id.in_progress_booking_call_customer_view)
    public void callCustomer()
    {
        bus.post(new HandyEvent.CallCustomerClicked());

        String phoneNumber = mBooking.getBookingPhone();
        if (phoneNumber == null)
        {
            Crashlytics.logException(new Exception("Phone number is null for booking " + mBooking.getId()));
            return;
        }
        Utils.safeLaunchIntent(new Intent(Intent.ACTION_VIEW, Uri.fromParts("tel", phoneNumber, null)), getContext());
    }

    @OnClick(R.id.in_progress_booking_message_customer_view)
    public void messageCustomer()
    {
        bus.post(new HandyEvent.TextCustomerClicked());

        String phoneNumber = mBooking.getBookingPhone();
        if (phoneNumber == null)
        {
            Crashlytics.logException(new Exception("Phone number is null for booking " + mBooking.getId()));
            return;
        }
        Utils.safeLaunchIntent(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", phoneNumber, null)), getContext());
    }

    private void enableActionsIfNeeded(Booking.Action action)
    {
        BookingActionButtonType buttonActionType = UIUtils.getAssociatedActionType(action);
        if (buttonActionType == null)
        {
            Crashlytics.log("Could not find action type for " + action.getActionName());
            return;
        }

        switch (buttonActionType)
        {
            case CHECK_OUT:
            {
                mActionButton.setVisibility(action.isEnabled() ? View.VISIBLE : View.GONE);

                if (!TextUtils.isNullOrEmpty(action.getHelperText()))
                {
                    mBookingDetailsActionHelperText.setVisibility(View.VISIBLE);
                    mBookingDetailsActionHelperText.setText(action.getHelperText());
                }
                break;
            }
            case CONTACT_PHONE:
            {
                mCallCustomerView.setVisibility(View.VISIBLE);
                break;
            }
            case CONTACT_TEXT:
            {
                mMessageCustomerView.setVisibility(View.VISIBLE);
                break;
            }
        }
    }
}
