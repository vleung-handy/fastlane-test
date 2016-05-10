package com.handy.portal.bookings.ui.fragment.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.maps.model.LatLng;
import com.handy.portal.R;
import com.handy.portal.bookings.BookingEvent;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.bookings.model.CheckoutRequest;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.NavigationEvent;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.CheckInFlowLog;
import com.handy.portal.logger.handylogger.model.ScheduledJobsLog;
import com.handy.portal.manager.PrefsManager;
import com.handy.portal.model.Address;
import com.handy.portal.model.LocationData;
import com.handy.portal.model.ProBookingFeedback;
import com.handy.portal.payments.model.PaymentInfo;
import com.handy.portal.ui.activity.BaseActivity;
import com.handy.portal.ui.fragment.dialog.InjectedDialogFragment;
import com.handy.portal.util.CurrencyUtils;
import com.handy.portal.util.UIUtils;
import com.handy.portal.util.Utils;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RateBookingDialogFragment extends InjectedDialogFragment
{
    @Inject
    PrefsManager mPrefsManager;

    @Bind(R.id.rate_booking_amount_text)
    TextView mAmountText;
    @Bind(R.id.rate_booking_bonus_amount_text)
    TextView mBonusAmountText;
    @Bind(R.id.rate_booking_experience_text)
    TextView mExperienceText;
    @Bind(R.id.rate_booking_comment_text)
    EditText mCommentText;
    @Bind(R.id.rate_booking_rating_radiogroup)
    RadioGroup mRatingRadioGroup;

    public static final String FRAGMENT_TAG = "fragment_dialog_rate_booking";

    private Booking mBooking;
    private String mNoteToCustomer;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState)
    {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().setWindowAnimations(R.style.dialog_animation_slide_up_down_from_bottom);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_dialog_rate_booking, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        mBooking = null;

        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(BundleKeys.BOOKING))
        {
            mBooking = (Booking) bundle.getSerializable(BundleKeys.BOOKING);
            mNoteToCustomer = bundle.getString(BundleKeys.NOTE_TO_CUSTOMER);

            PaymentInfo paymentInfo = mBooking.getPaymentToProvider();
            if (paymentInfo != null)
            {
                String amount = CurrencyUtils.formatPrice(
                        paymentInfo.getAdjustedAmount(), paymentInfo.getCurrencySymbol());
                mAmountText.setText(getString(R.string.you_earned_money_formatted, amount));
            }
            PaymentInfo bonusInfo = mBooking.getBonusPaymentToProvider();
            if (bonusInfo != null && bonusInfo.getAdjustedAmount() > 0)
            {
                String amount = CurrencyUtils.formatPrice(
                        bonusInfo.getAdjustedAmount(), bonusInfo.getCurrencySymbol());
                mBonusAmountText.setText(getString(R.string.bonus_formatted, amount));
                mBonusAmountText.setVisibility(View.VISIBLE);
            }
            String name = mBooking.getUser().getFirstName();
            mExperienceText.setText(getString(R.string.how_was_experience_formatted, name));


        }

        if (mBooking == null)
        {
            Crashlytics.logException(new Exception("No valid booking passed to RateBookingDialogFragment, aborting rating"));
            mBus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
            final LocationData locationData = getLocationData();
            mBus.post(new LogEvent.AddLogEvent(new CheckInFlowLog.CheckOutSubmitted(mBooking, locationData)));
            mBus.post(new HandyEvent.RequestNotifyJobCheckOut(mBooking.getId(), new CheckoutRequest(
                    locationData, new ProBookingFeedback(getBookingRatingScore(),
                    getBookingRatingComment()), mNoteToCustomer, null)));
        }

        mBus.post(new LogEvent.AddLogEvent(new ScheduledJobsLog.CustomerRatingShown()));
    }

    @OnClick(R.id.close_button)
    public void onCloseButtonClick()
    {
        dismiss();
    }

    @OnClick(R.id.rate_booking_submit_button)
    public void onConfirmCheckoutButtonClick()
    {
        //Endpoint is expecting a rating of 1 - 5
        final int bookingRatingScore = getBookingRatingScore();
        mBus.post(new LogEvent.AddLogEvent(new ScheduledJobsLog.CustomerRatingSubmitted(bookingRatingScore)));
        if (bookingRatingScore > 0)
        {
            // TODO: combine this with line 71
            mBus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
            final LocationData locationData = getLocationData();
            mBus.post(new LogEvent.AddLogEvent(new CheckInFlowLog.CheckOutSubmitted(mBooking, locationData)));
            mBus.post(new HandyEvent.RequestNotifyJobCheckOut(mBooking.getId(), new CheckoutRequest(
                    locationData, new ProBookingFeedback(bookingRatingScore,
                    getBookingRatingComment()), mNoteToCustomer, mBooking.getCustomerPreferences())
            ));
        }
        else
        {
            UIUtils.showToast(getContext(), getString(R.string.rate_booking_need_rating), Toast.LENGTH_SHORT);
        }
    }

    //when clicked on, close the dialog, the fragment will listen for the event to come back and transition correctly, if fails brings back
    @Subscribe
    public void onReceiveNotifyJobCheckOutSuccess(final HandyEvent.ReceiveNotifyJobCheckOutSuccess event)
    {
        Address address = mBooking.getAddress();
        if (address != null)
        {
            mPrefsManager.setBookingInstructions(mBooking.getId(), null);

            mBus.post(new BookingEvent.RequestNearbyBookings(mBooking.getRegionId(),
                    address.getLatitude(), address.getLongitude()));
        }
        else
        {
            dismiss();
        }
    }

    @Subscribe
    public void onReceiveNotifyJobCheckOutError(final HandyEvent.ReceiveNotifyJobCheckOutError event)
    {
        mBus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        UIUtils.showToast(getContext(), getString(R.string.an_error_has_occurred), Toast.LENGTH_SHORT);
        //allow them to try again. they can always click the X button if they don't want to.
    }

    @Subscribe
    public void onReceiveNearbyBookingsSuccess(final BookingEvent.ReceiveNearbyBookingsSuccess event)
    {
        if (event.getBookings().size() > 0)
        {
            Address address = mBooking.getAddress();
            if (address != null)
            {
                Bundle args = new Bundle();
                args.putSerializable(BundleKeys.BOOKINGS, new ArrayList<>(event.getBookings()));
                args.putParcelable(BundleKeys.MAP_CENTER,
                        new LatLng(address.getLatitude(), address.getLongitude()));
                mBus.post(new NavigationEvent.NavigateToTab(MainViewTab.NEARBY_JOBS, args, true));
            }
        }
        dismiss();
    }

    @Subscribe
    public void onReceiveNearbyBookingsError(final BookingEvent.ReceiveNearbyBookingsError event)
    {
        dismiss();
    }

    private LocationData getLocationData()
    {
        return Utils.getCurrentLocation((BaseActivity) getActivity());
    }

    private int getBookingRatingScore()
    {
        //Endpoint is expected a 1 indexed rating
        return 1 + UIUtils.indexOfCheckedRadioButton(mRatingRadioGroup);
    }

    private String getBookingRatingComment()
    {
        return (mCommentText.getText() != null ? mCommentText.getText().toString() : "");
    }
}