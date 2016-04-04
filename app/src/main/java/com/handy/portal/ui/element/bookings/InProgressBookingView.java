package com.handy.portal.ui.element.bookings;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.handy.portal.model.booking.Booking;
import com.handy.portal.model.booking.Booking.BookingInstructionGroup;
import com.handy.portal.ui.element.BookingDetailsJobInstructionsSectionView;
import com.handy.portal.ui.view.InjectedBusView;
import com.handy.portal.util.DateTimeUtils;
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

import static com.handy.portal.model.booking.Booking.BookingInstructionGroup.GROUP_ENTRY_METHOD;
import static com.handy.portal.model.booking.Booking.BookingInstructionGroup.GROUP_LINENS_LAUNDRY;
import static com.handy.portal.model.booking.Booking.BookingInstructionGroup.GROUP_NOTE_TO_PRO;
import static com.handy.portal.model.booking.Booking.BookingInstructionGroup.GROUP_PREFERENCES;
import static com.handy.portal.model.booking.Booking.BookingInstructionGroup.GROUP_REFRIGERATOR;
import static com.handy.portal.model.booking.Booking.BookingInstructionGroup.GROUP_TRASH;

public class InProgressBookingView extends InjectedBusView
{
    @Inject
    PrefsManager mPrefsManager;

    @Bind(R.id.no_show_banner_text)
    View mNoShowBanner;
    @Bind(R.id.customer_name_text)
    TextView mCustomerNameText;
    @Bind(R.id.job_start_time)
    TextView mJobStartTime;
    @Bind(R.id.call_customer_view)
    View mCallCustomerView;
    @Bind(R.id.message_customer_view)
    View mMessageCustomerView;
    @Bind(R.id.booking_support_button)
    Button mSupportButton;
    @Bind(R.id.entry_method_layout)
    ViewGroup mEntryMethodLayout;
    @Bind(R.id.entry_method_text)
    TextView mEntryMethodText;
    @Bind(R.id.booking_details_job_instructions_list_layout)
    LinearLayout mInstructionsLayout;
    @Bind(R.id.job_number_text)
    TextView mJobNumberText;
    @Bind(R.id.booking_details_action_helper_text)
    TextView mBookingDetailsActionHelperText;
    @Bind(R.id.booking_action_button)
    Button mActionButton;

    private Booking mBooking;
    private String mSource;
    private Bundle mSourceExtras;
    private ActionBar mActionBar;
    private CountDownTimer mCounter;

    private boolean mFromPaymentsTab;

    private static final Map<String, Integer> GROUP_ICONS;
    private static final Gson GSON = new Gson();

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

    public InProgressBookingView(
            final Context context, @NonNull Booking booking, String source, Bundle sourceExtras,
            boolean fromPaymentsTab, ActionBar actionBar, OnClickListener onSupportClickListener,
            boolean noShowReported)
    {
        super(context);
        init();
        setDisplay(booking, source, sourceExtras, fromPaymentsTab, actionBar,
                onSupportClickListener, noShowReported);
    }

    public InProgressBookingView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public InProgressBookingView(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public InProgressBookingView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void setDisplay(
            @NonNull Booking booking, String source, Bundle sourceExtras, boolean fromPaymentsTab,
            ActionBar actionBar, OnClickListener onSupportClickListener,  boolean noShowReported)
    {
        mBooking = booking;
        mSource = source;
        mSourceExtras = sourceExtras;
        mActionBar = actionBar;
        mFromPaymentsTab = fromPaymentsTab;
        mSupportButton.setOnClickListener(onSupportClickListener);

        if (DateTimeUtils.isTimeWithinXHoursFromNow(booking.getStartDate(), 3))
        {
            setCountDownTimer(booking.getEndDate().getTime() - System.currentTimeMillis());
        }

        // Booking actions
        List<Booking.Action> allowedActions = mBooking.getAllowedActions();
        for (Booking.Action action : allowedActions)
        {
            enableActionsIfNeeded(action);
        }

        Booking.BookingStatus bookingStatus = mBooking.inferBookingStatus(getLoggedInUserId());

        Booking.User user = mBooking.getUser();
        if (user != null)
        {
            mCustomerNameText.setText(user.getFullName());
        }

        mJobStartTime.setText(DateTimeUtils.formatDateTo12HourClock(mBooking.getCheckInSummary().getCheckInTime()));

        // Booking Instructions
        List<BookingInstructionGroup> bookingInstructionGroups = mBooking.getBookingInstructionGroups();
        if (bookingInstructionGroups != null && bookingInstructionGroups.size() > 0)
        {
            BookingInstructionGroup preferencesGroup = null;
            for (BookingInstructionGroup group : bookingInstructionGroups)
            {
                String groupString = group.getGroup();

                if (groupString.equals(GROUP_ENTRY_METHOD))
                {
                    mEntryMethodLayout.setVisibility(VISIBLE);

                    // TODO: Set entry method here
                }
                else if (Booking.BookingInstructionGroup.GROUP_PREFERENCES.equals(group.getGroup()))
                {
                    preferencesGroup = group;
                }
                else
                {
                    BookingDetailsJobInstructionsSectionView sectionView = addSection(mInstructionsLayout);
                    sectionView.init(group.getLabel(), GROUP_ICONS.get(group.getGroup()),
                            group.getInstructions());
                }
            }

            if (preferencesGroup != null)
            {
                List<Booking.BookingInstructionUpdateRequest> checklist = null;
                if (mPrefsManager.getBookingInstructions(mBooking.getId()).isEmpty())
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
                    CustomerRequestsView customerRequestsView = new CustomerRequestsView(getContext(),
                            preferencesGroup.getLabel(), GROUP_ICONS.get(preferencesGroup.getGroup()),
                            checklist);
                    customerRequestsView.setEnabled(mBooking.isCheckedIn());
                    mInstructionsLayout.addView(customerRequestsView);
                }
            }
        }

        if (noShowReported)
        {
            mNoShowBanner.setVisibility(VISIBLE);
        }

        mJobNumberText.setText(getResources().getString(R.string.job_number_formatted, mBooking.getId()));
    }

    @OnClick(R.id.booking_action_button)
    public void checkOut()
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable(BundleKeys.BOOKING, mBooking);
        mBus.post(new NavigationEvent.NavigateToTab(MainViewTab.SEND_RECEIPT_CHECKOUT, bundle));
    }

    private void init()
    {
        inflate(getContext(), R.layout.view_in_progress_booking, this);
        ButterKnife.bind(this);
        Utils.inject(getContext(), this);
    }

    private String getLoggedInUserId()
    {
        return mPrefsManager.getString(PrefsKey.LAST_PROVIDER_ID);
    }

    @OnClick(R.id.call_customer_view)
    public void callCustomer()
    {
        mBus.post(new HandyEvent.CallCustomerClicked());

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

    @OnClick(R.id.message_customer_view)
    public void messageCustomer()
    {
        mBus.post(new HandyEvent.TextCustomerClicked());

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
                mActionButton.setVisibility(VISIBLE);

                if (action.getHelperText() != null && !action.getHelperText().isEmpty())
                {
                    mBookingDetailsActionHelperText.setVisibility(View.VISIBLE);
                    mBookingDetailsActionHelperText.setText(action.getHelperText());
                }
                break;
            }
            case CONTACT_PHONE:
            {
                mCallCustomerView.setVisibility(VISIBLE);
                break;
            }
            case CONTACT_TEXT:
            {
                mMessageCustomerView.setVisibility(VISIBLE);
                break;
            }
        }
    }

    private BookingDetailsJobInstructionsSectionView addSection(LinearLayout instructionsLayout)
    {
        LayoutInflater.from(getContext()).inflate(R.layout.element_booking_details_job_instructions_section, instructionsLayout);
        return (BookingDetailsJobInstructionsSectionView) instructionsLayout.getChildAt(instructionsLayout.getChildCount() - 1);
    }

    private void setCountDownTimer(long timeRemainMillis)
    {
        if (mCounter != null) { mCounter.cancel(); } // cancel the previous counter
        mCounter = DateTimeUtils.setCountDownTimer(getContext(), mActionBar, timeRemainMillis);
    }
}
