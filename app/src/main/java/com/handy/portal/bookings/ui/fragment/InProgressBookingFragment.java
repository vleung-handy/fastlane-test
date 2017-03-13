package com.handy.portal.bookings.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
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
import com.handy.portal.bookings.model.ChatOptions;
import com.handy.portal.bookings.model.User;
import com.handy.portal.bookings.ui.element.CustomerRequestsView;
import com.handy.portal.core.constant.BundleKeys;
import com.handy.portal.core.constant.MainViewPage;
import com.handy.portal.core.event.HandyEvent;
import com.handy.portal.core.event.NavigationEvent;
import com.handy.portal.core.manager.PrefsManager;
import com.handy.portal.core.ui.fragment.TimerActionBarFragment;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.library.util.UIUtils;
import com.handy.portal.library.util.Utils;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.EventType;
import com.handy.portal.logger.handylogger.model.ScheduledJobsLog;
import com.handybook.shared.core.HandyLibrary;
import com.handybook.shared.layer.LayerConstants;
import com.handybook.shared.layer.model.CreateConversationResponse;
import com.handybook.shared.layer.ui.MessagesListActivity;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.handy.portal.bookings.model.Booking.BookingInstructionGroup.GROUP_NOTE_TO_PRO;

/**
 * fragment for handling bookings that are in progress/after provider has checked in & before check out
 */
public class InProgressBookingFragment extends TimerActionBarFragment {

    @Inject
    PrefsManager mPrefsManager;

    @BindView(R.id.in_progress_booking_no_show_banner_text)
    View mNoShowBannerView;
    @BindView(R.id.in_progress_booking_customer_name_text)
    TextView mCustomerNameText;
    @BindView(R.id.in_progress_booking_job_start_time)
    TextView mJobStartTimeText;
    @BindView(R.id.in_progress_booking_call_customer_view)
    View mCallCustomerView;
    @BindView(R.id.in_progress_booking_message_customer_view)
    View mMessageCustomerView;
    @BindView(R.id.in_progress_booking_booking_support_button)
    Button mSupportButton;
    @BindView(R.id.in_progress_booking_note_to_pro_layout)
    ViewGroup mNoteToProLayout;
    @BindView(R.id.in_progress_booking_note_to_pro_text)
    TextView mNoteToProText;
    @BindView(R.id.in_progress_booking_checklist)
    CustomerRequestsView mCustomerRequestsView;
    @BindView(R.id.in_progress_booking_details_action_helper_text)
    TextView mBookingDetailsActionHelperText;
    @BindView(R.id.in_progress_booking_action_button)
    Button mActionButton;

    private static final Gson GSON = new Gson();

    private Booking mBooking;
    private String mSource; // TODO: refactor this into a enum?
    private View.OnClickListener mOnSupportClickListener;


    public static InProgressBookingFragment newInstance(@NonNull final Booking booking, String source) {
        InProgressBookingFragment fragment = new InProgressBookingFragment();
        Bundle args = new Bundle();
        args.putSerializable(BundleKeys.BOOKING, booking);
        args.putString(BundleKeys.BOOKING_SOURCE, source);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected MainViewPage getAppPage() {
        return null;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBooking = (Booking) getArguments().getSerializable(BundleKeys.BOOKING);
        mSource = getArguments().getString(BundleKeys.BOOKING_SOURCE);
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        mOnSupportClickListener = (View.OnClickListener) getParentFragment();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_in_progress_booking, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        setDisplay();
        setOptionsMenuEnabled(true);
        setBackButtonEnabled(true);
    }

    public void setDisplay() {
        mSupportButton.setOnClickListener(mOnSupportClickListener);

        // Booking actions
        List<Booking.Action> allowedActions = mBooking.getAllowedActions();
        for (Booking.Action action : allowedActions) {
            enableActionsIfNeeded(action);
        }

        User user = mBooking.getUser();
        if (user != null) {
            mCustomerNameText.setText(user.getFullName());
        }

        if (mBooking.getCheckInSummary() != null && mBooking.getCheckInSummary().getCheckInTime() != null) {
            String dateString = DateTimeUtils.formatDateTo12HourClock(
                    mBooking.getCheckInSummary().getCheckInTime());
            if (dateString != null) {
                mJobStartTimeText.setText(dateString.toLowerCase());
            }
        }

        // Booking Instructions
        List<Booking.BookingInstructionGroup> bookingInstructionGroups = mBooking.getBookingInstructionGroups();
        if (bookingInstructionGroups != null && bookingInstructionGroups.size() > 0) {
            Booking.BookingInstructionGroup preferencesGroup = null;
            for (Booking.BookingInstructionGroup group : bookingInstructionGroups) {
                String groupString = group.getGroup();

                if (groupString.equals(GROUP_NOTE_TO_PRO)) {
                    List<Booking.BookingInstruction> instructions = group.getInstructions();
                    if (instructions != null && !instructions.isEmpty()) {
                        mNoteToProText.setText(instructions.get(0).getDescription());
                        mNoteToProLayout.setVisibility(View.VISIBLE);
                    }
                }
                else if (Booking.BookingInstructionGroup.GROUP_PREFERENCES.equals(group.getGroup())) {
                    preferencesGroup = group;
                }
            }

            if (preferencesGroup != null) {
                List<Booking.BookingInstructionUpdateRequest> checklist = null;
                if (TextUtils.isEmpty(mPrefsManager.getBookingInstructions(mBooking.getId()))) {
                    checklist = mBooking.getCustomerPreferences();
                }
                else {
                    try {
                        Booking.BookingInstructionUpdateRequest[] checklistArray = GSON.fromJson(
                                mPrefsManager.getBookingInstructions(mBooking.getId()),
                                Booking.BookingInstructionUpdateRequest[].class);
                        checklist = Arrays.asList(checklistArray);
                        mBooking.setCustomerPreferences(checklist);
                    }
                    catch (JsonSyntaxException e) {
                        Crashlytics.logException(e);
                    }
                }
                if (checklist != null) {
                    mCustomerRequestsView.setDisplay(checklist);
                }
            }
        }

        boolean noShowReported = mBooking.getAction(Booking.Action.ACTION_RETRACT_NO_SHOW) != null;
        if (noShowReported) {
            mNoShowBannerView.setVisibility(View.VISIBLE);
        }

        setTimerIfNeeded(mBooking.getStartDate(), mBooking.getEndDate());
    }

    @OnClick(R.id.in_progress_booking_details_view)
    public void swapToJobDetails() {
        Bundle args = new Bundle();
        args.putSerializable(BundleKeys.BOOKING, mBooking);
        args.putString(BundleKeys.BOOKING_SOURCE, mSource);
        args.putBoolean(BundleKeys.BOOKING_SHOULD_HIDE_ACTION_BUTTONS, true);

        bus.post(new NavigationEvent.NavigateToPage(MainViewPage.NOT_IN_PROGRESS_JOB_DETAILS, args, true));
    }

    @OnClick(R.id.in_progress_booking_action_button)
    public void checkOut() {
        final boolean proReportedNoShow = mBooking.getAction(Booking.Action.ACTION_RETRACT_NO_SHOW) != null;
        if (proReportedNoShow || mBooking.isAnyPreferenceChecked()) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(BundleKeys.BOOKING, mBooking);
            bus.post(new NavigationEvent.NavigateToPage(MainViewPage.SEND_RECEIPT_CHECKOUT, bundle, true));
        }
        else {
            showToast(getContext().getString(R.string.tap_preferences_before_checkout),
                    Toast.LENGTH_LONG, Gravity.TOP);
        }
    }

    @OnClick(R.id.in_progress_booking_call_customer_view)
    public void callCustomer() {
        bus.post(new HandyEvent.CallCustomerClicked());
        User user = mBooking.getUser();
        bus.post(new LogEvent.AddLogEvent(new ScheduledJobsLog.ContactCustomerLog(
                EventType.CALL_CUSTOMER_SELECTED, user == null ? null : user.getId())));

        String phoneNumber = mBooking.getBookingPhone();
        if (phoneNumber == null) {
            bus.post(new LogEvent.AddLogEvent(new ScheduledJobsLog.ContactCustomerLog(
                    EventType.CALL_CUSTOMER_FAILED, user == null ? null : user.getId())));
            showInvalidPhoneNumberToast();
            Crashlytics.logException(new Exception("Phone number is null for booking " + mBooking.getId()));
            return;
        }

        Utils.safeLaunchIntent(new Intent(Intent.ACTION_VIEW, Uri.fromParts("tel", phoneNumber, null)), getContext());
    }

    @OnClick(R.id.in_progress_booking_message_customer_view)
    public void messageCustomer() {
        bus.post(new HandyEvent.TextCustomerClicked());

        ChatOptions chatOptions = mBooking.getChatOptions();
        final User user = mBooking.getUser();
        if (chatOptions != null && chatOptions.isDirectToInAppChat() && user != null
                && !android.text.TextUtils.isEmpty(user.getId())) {
            bus.post(new LogEvent.AddLogEvent(new ScheduledJobsLog.ContactCustomerLog(
                    EventType.IN_APP_CHAT_WITH_CUSTOMER_SELECTED, user.getId())));
            HandyLibrary.getInstance().getHandyService().createConversationForPro(
                    user.getId(), "", new Callback<CreateConversationResponse>() {
                        @Override
                        public void success(
                                final CreateConversationResponse conversationResponse,
                                final Response response) {
                            Intent intent = new Intent(getContext(), MessagesListActivity.class);
                            intent.putExtra(LayerConstants.LAYER_CONVERSATION_KEY,
                                    Uri.parse(conversationResponse.getConversationId()));
                            intent.putExtra(LayerConstants.KEY_HIDE_ATTACHMENT_BUTTON, true);
                            startActivity(intent);
                        }

                        @Override
                        public void failure(final RetrofitError error) {
                            bus.post(new LogEvent.AddLogEvent(new ScheduledJobsLog.ContactCustomerLog(
                                    EventType.IN_APP_CHAT_WITH_CUSTOMER_FAILED, user.getId())));
                            showToast(R.string.an_error_has_occurred);
                        }
                    });
        }
        else {
            bus.post(new LogEvent.AddLogEvent(new ScheduledJobsLog.ContactCustomerLog(
                    EventType.TEXT_CUSTOMER_SELECTED, user == null ? null : user.getId())));
            String phoneNumber = mBooking.getBookingPhone();
            if (phoneNumber == null) {
                bus.post(new LogEvent.AddLogEvent(new ScheduledJobsLog.ContactCustomerLog(
                        EventType.TEXT_CUSTOMER_FAILED, user == null ? null : user.getId())));
                showInvalidPhoneNumberToast();
                Crashlytics.logException(
                        new Exception("Phone number is null for booking " + mBooking.getId()));
                return;
            }

            Utils.safeLaunchIntent(
                    new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", phoneNumber, null)), getContext());
        }
    }

    private void enableActionsIfNeeded(Booking.Action action) {
        BookingActionButtonType buttonActionType = UIUtils.getAssociatedActionType(action);
        if (buttonActionType == null) {
            Crashlytics.log("Could not find action type for " + action.getActionName());
            return;
        }

        switch (buttonActionType) {
            case CHECK_OUT: {
                mActionButton.setVisibility(action.isEnabled() ? View.VISIBLE : View.GONE);

                if (!TextUtils.isEmpty(action.getHelperText())) {
                    mBookingDetailsActionHelperText.setVisibility(View.VISIBLE);
                    mBookingDetailsActionHelperText.setText(action.getHelperText());
                }
                break;
            }
            case CONTACT_PHONE: {
                mCallCustomerView.setVisibility(View.VISIBLE);
                break;
            }
            case CONTACT_TEXT: {
                mMessageCustomerView.setVisibility(View.VISIBLE);
                break;
            }
        }
    }

    private void showInvalidPhoneNumberToast() {
        Toast.makeText(getContext(),
                getString(R.string.invalid_phone_number), Toast.LENGTH_LONG).show();
    }
}
