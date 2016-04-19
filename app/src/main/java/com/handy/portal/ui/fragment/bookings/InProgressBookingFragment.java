package com.handy.portal.ui.fragment.bookings;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import com.handy.portal.constant.BookingActionButtonType;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.constant.PrefsKey;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.NavigationEvent;
import com.handy.portal.manager.PrefsManager;
import com.handy.portal.model.Booking;
import com.handy.portal.ui.element.bookings.CustomerRequestsView;
import com.handy.portal.ui.fragment.InjectedFragment;
import com.handy.portal.util.DateTimeUtils;
import com.handy.portal.util.TextUtils;
import com.handy.portal.util.UIUtils;
import com.handy.portal.util.Utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.handy.portal.model.Booking.BookingInstructionGroup.GROUP_ENTRY_METHOD;
import static com.handy.portal.model.Booking.BookingInstructionGroup.GROUP_LINENS_LAUNDRY;
import static com.handy.portal.model.Booking.BookingInstructionGroup.GROUP_NOTE_TO_PRO;
import static com.handy.portal.model.Booking.BookingInstructionGroup.GROUP_PREFERENCES;
import static com.handy.portal.model.Booking.BookingInstructionGroup.GROUP_REFRIGERATOR;
import static com.handy.portal.model.Booking.BookingInstructionGroup.GROUP_TRASH;

/**
 * fragment for handling bookings that are in progress/after provider has checked in & before check out
 */
public class InProgressBookingFragment extends InjectedFragment
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

    private Booking mBooking;
    private String mSource; // TODO: refactor this into a enum?
    private Toast mToast;

    private static final Map<String, Integer> GROUP_ICONS;
    private static final Gson GSON = new Gson();

    private View.OnClickListener mOnSupportClickListener;

    static
    {
        GROUP_ICONS = new HashMap<>();
        GROUP_ICONS.put(GROUP_ENTRY_METHOD, R.drawable.ic_details_entry);
        GROUP_ICONS.put(GROUP_LINENS_LAUNDRY, R.drawable.ic_details_linens);
        GROUP_ICONS.put(GROUP_REFRIGERATOR, R.drawable.ic_details_fridge);
        GROUP_ICONS.put(GROUP_TRASH, R.drawable.ic_details_trash);
        GROUP_ICONS.put(GROUP_NOTE_TO_PRO, R.drawable.ic_details_request);
        GROUP_ICONS.put(GROUP_PREFERENCES, R.drawable.ic_details_request);
    }

    public static InProgressBookingFragment newInstance(final Booking booking, String source)
    {
        InProgressBookingFragment fragment = new InProgressBookingFragment();
        Bundle args = new Bundle();
        args.putSerializable(BundleKeys.BOOKING, booking);
        args.putString(BundleKeys.BOOKING_SOURCE, source);

        fragment.setArguments(args);
        return fragment;
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

        mJobStartTimeText.setText(DateTimeUtils.formatDateTo12HourClock(
                mBooking.getCheckInSummary().getCheckInTime()).toLowerCase());

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
                    mCustomerRequestsView.setDisplay(preferencesGroup.getLabel(),
                            GROUP_ICONS.get(preferencesGroup.getGroup()), checklist);
                }
            }
        }

        boolean noShowReported = mBooking.getAction(Booking.Action.ACTION_RETRACT_NO_SHOW) != null;
        if (noShowReported)
        {
            mNoShowBannerView.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.in_progress_booking_details_view)
    public void swapToJobDetails()
    {
        Bundle args = new Bundle();
        args.putSerializable(BundleKeys.BOOKING, mBooking);
        args.putString(BundleKeys.BOOKING_SOURCE, mSource);
        args.putBoolean(BundleKeys.BOOKING_FROM_PAYMENT_TAB, false);
        args.putBoolean(BundleKeys.BOOKING_SHOULD_HIDE_ACTION_BUTTONS, true);

        bus.post(new NavigationEvent.NavigateToTab(MainViewTab.CHECKOUT_JOB_DETAILS, args, true));
    }

    @OnClick(R.id.in_progress_booking_action_button)
    public void checkOut()
    {
        final boolean proReportedNoShow = mBooking.getAction(Booking.Action.ACTION_RETRACT_NO_SHOW) != null;
        if (proReportedNoShow || mBooking.isAnyPreferenceChecked())
        {
            Bundle bundle = new Bundle();
            bundle.putSerializable(BundleKeys.BOOKING, mBooking);
            bus.post(new NavigationEvent.NavigateToTab(MainViewTab.SEND_RECEIPT_CHECKOUT, bundle, true));
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
        try
        {
            Utils.safeLaunchIntent(new Intent(Intent.ACTION_VIEW, Uri.fromParts("tel", phoneNumber, null)), getContext());
        }
        catch (ActivityNotFoundException activityException)
        {
            Crashlytics.logException(new RuntimeException("Calling a Phone Number failed", activityException));
        }
    }

    @OnClick(R.id.in_progress_booking_message_customer_view)
    public void messageCustomer()
    {
        bus.post(new HandyEvent.TextCustomerClicked());

        String phoneNumber = mBooking.getBookingPhone();
        try
        {
            Utils.safeLaunchIntent(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", phoneNumber, null)), getContext());
        }
        catch (ActivityNotFoundException activityException)
        {
            Crashlytics.logException(new RuntimeException("Texting a Phone Number failed", activityException));
        }
    }

    private String getLoggedInUserId()
    {
        return mPrefsManager.getString(PrefsKey.LAST_PROVIDER_ID);
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

    protected void showToast(String message, int length, int gravity)
    {
        mToast = Toast.makeText(getContext(), message, length);
        mToast.setGravity(gravity, 0, 0);
        mToast.show();
    }
}
