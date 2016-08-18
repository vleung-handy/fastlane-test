package com.handy.portal.bookings.ui.fragment.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;
import com.handy.portal.bookings.BookingEvent;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.bookings.model.PostCheckoutInfo;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.library.ui.fragment.dialog.InjectedDialogFragment;
import com.handy.portal.library.util.CurrencyUtils;
import com.handy.portal.library.util.UIUtils;
import com.handy.portal.library.util.Utils;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.CheckOutFlowLog;
import com.handy.portal.logger.handylogger.model.ScheduledJobsLog;
import com.handy.portal.manager.PrefsManager;
import com.handy.portal.model.LocationData;
import com.handy.portal.payments.model.PaymentInfo;
import com.handy.portal.ui.activity.BaseActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RateBookingDialogFragment extends InjectedDialogFragment
{
    @Inject
    PrefsManager mPrefsManager;
    @Inject
    EventBus mBus;

    @BindView(R.id.rate_booking_amount_text)
    TextView mAmountText;
    @BindView(R.id.rate_booking_bonus_amount_text)
    TextView mBonusAmountText;
    @BindView(R.id.rate_booking_experience_text)
    TextView mExperienceText;
    @BindView(R.id.rate_booking_comment_text)
    EditText mCommentText;
    @BindView(R.id.rate_booking_rating_radiogroup)
    RadioGroup mRatingRadioGroup;
    @BindView(R.id.rate_booking_submit_button)
    Button mSubmitButton;

    public static final String FRAGMENT_TAG = "fragment_dialog_rate_booking";

    private Booking mBooking;

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
            if (mBooking != null)
            {
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
        }

        if (mBooking == null)
        {
            Crashlytics.logException(new Exception("No valid booking passed to RateBookingDialogFragment, aborting rating"));
        }

        mBus.post(new LogEvent.AddLogEvent(new ScheduledJobsLog.CustomerRatingShown()));
    }

    @Override
    public void onResume()
    {
        super.onResume();
        mBus.register(this);
    }

    @Override
    public void onPause()
    {
        mBus.unregister(this);
        super.onPause();
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
            mSubmitButton.setEnabled(false);
            final LocationData locationData = getLocationData();
            mBus.post(new LogEvent.AddLogEvent(
                    new CheckOutFlowLog.CheckOutSubmitted(mBooking, locationData)));
            mBus.post(new BookingEvent.RateCustomer(
                    mBooking.getId(), bookingRatingScore, getBookingRatingComment()));
        }
        else
        {
            UIUtils.showToast(getContext(), getString(R.string.rate_booking_need_rating), Toast.LENGTH_SHORT);
        }
    }

    @Subscribe
    public void onReceiveRateCustomerSuccess(final BookingEvent.RateCustomerSuccess event)
    {
        mBus.post(new BookingEvent.RequestPostCheckoutInfo(mBooking.getId()));
    }

    @Subscribe
    public void onRateCustomerError(final BookingEvent.RateCustomerError event)
    {
        mSubmitButton.setEnabled(true);
        UIUtils.showToast(getContext(), getString(R.string.an_error_has_occurred), Toast.LENGTH_SHORT);
        //allow them to try again. they can always click the X button if they don't want to.
    }

    @Subscribe
    public void onReceivePostCheckoutInfoSuccess(
            final BookingEvent.ReceivePostCheckoutInfoSuccess event)
    {
        final PostCheckoutInfo postCheckoutInfo = event.getPostCheckoutInfo();
        if (!postCheckoutInfo.getSuggestedJobs().isEmpty())
        {
            // FIXME: Implement
        }
        dismiss();
    }

    @Subscribe
    public void onReceivePostCheckoutInfoError(
            final BookingEvent.ReceivePostCheckoutInfoError event)
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
