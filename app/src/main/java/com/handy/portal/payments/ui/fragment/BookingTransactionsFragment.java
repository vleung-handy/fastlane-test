package com.handy.portal.payments.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.CompletedJobsLog;
import com.handy.portal.payments.model.BookingTransactions;
import com.handy.portal.payments.model.Transaction;
import com.handy.portal.payments.ui.element.TransactionView;
import com.handy.portal.ui.element.bookings.BookingResultBannerTextView;
import com.handy.portal.ui.fragment.InjectedFragment;
import com.handy.portal.util.CurrencyUtils;
import com.handy.portal.util.DateTimeUtils;
import com.handy.portal.util.TextUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * fragment for handling bookings that are viewed for payment details
 */
public class BookingTransactionsFragment extends InjectedFragment
{
    @Bind(R.id.booking_transactions_banner_text)
    BookingResultBannerTextView mBannerText;
    @Bind(R.id.booking_transactions_location_text)
    TextView mLocationText;
    @Bind(R.id.booking_transactions_date_text)
    TextView mDateText;
    @Bind(R.id.booking_transactions_time_text)
    TextView mTimeText;
    @Bind(R.id.booking_transactions_unassigned_time_text)
    TextView mUnassignedTimeText;
    @Bind(R.id.booking_transactions_unassigned_help_text)
    TextView mUnassignedHelpText;
    @Bind(R.id.booking_transactions_check_in_time_text)
    TextView mCheckInTimeText;
    @Bind(R.id.booking_transactions_check_in_label)
    TextView mCheckInLabelText;
    @Bind(R.id.booking_transactions_late_text)
    TextView mLateText;
    @Bind(R.id.booking_transactions_check_out_time_text)
    TextView mCheckOutTimeText;
    @Bind(R.id.booking_transactions_check_out_label)
    TextView mCheckOutLabelText;
    @Bind(R.id.booking_transactions_check_out_help_text)
    TextView mCheckOutHelpText;
    @Bind(R.id.booking_transactions_transactions_layout)
    ViewGroup mTransactionsLayout;
    @Bind(R.id.booking_transactions_net_earnings_amount_text)
    TextView mNetEarningAmountText;
    @Bind(R.id.booking_transactions_job_number_text)
    TextView mJobNumberText;
    @Bind(R.id.booking_transactions_help_text)
    TextView mHelpText;


    private BookingTransactions mBookingTransactions;
    private Booking mBooking;
    private Transaction[] mTransactions;

    public static BookingTransactionsFragment newInstance(
            @NonNull final BookingTransactions bookingTransactions)
    {
        BookingTransactionsFragment fragment = new BookingTransactionsFragment();
        Bundle args = new Bundle();
        args.putSerializable(BundleKeys.BOOKING_TRANSACTIONS, bookingTransactions);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mBookingTransactions = (BookingTransactions)
                getArguments().getSerializable(BundleKeys.BOOKING_TRANSACTIONS);
        if (mBookingTransactions == null)
        {
            Crashlytics.log("Either booking or transactions is null in onReceiveBookingDetailsSuccess");
            return;
        }
        mBooking = mBookingTransactions.getBooking();
        mTransactions = mBookingTransactions.getTransactions();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_booking_transactions, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        setDisplay();
    }

    private void setDisplay()
    {
        mLocationText.setText(mBooking.getRegionName());
        mDateText.setText(DateTimeUtils.formatDateDayOfWeekMonthDay(mBooking.getStartDate()));
        String startTime = DateTimeUtils.getTimeWithoutDate(mBooking.getStartDate());
        String endTime = DateTimeUtils.getTimeWithoutDate(mBooking.getEndDate());
        mTimeText.setText(getString(R.string.dash_formatted, startTime, endTime));
        if (mBooking.getCheckInTime() != null)
        {
            mCheckInTimeText.setText(DateTimeUtils.getTimeWithoutDate(mBooking.getCheckInTime()));
        }
        else
        {
            mCheckInLabelText.setTextColor(ContextCompat.getColor(getContext(), R.color.text_light_gray));
        }
        if (mBooking.getCheckOutTime() != null)
        {
            mCheckOutTimeText.setText(DateTimeUtils.getTimeWithoutDate(mBooking.getCheckOutTime()));
        }
        else
        {
            mCheckOutLabelText.setTextColor(ContextCompat.getColor(getContext(), R.color.text_light_gray));
        }

        for (int i = 0; i < mTransactions.length; ++i)
        {
            TransactionView transactionView = new TransactionView(getContext());
            transactionView.setDisplay(mTransactions[i]);
            mTransactionsLayout.addView(transactionView);
        }
        mNetEarningAmountText.setText(CurrencyUtils.formatPriceWithCents(
                mBookingTransactions.getNetEarnings(), mBookingTransactions.getCurrencySymbol()));

        mJobNumberText.setText(getString(R.string.job_number_formatted, mBooking.getId()));

        mHelpText.setLinkTextColor(ContextCompat.getColor(getContext(), R.color.partner_blue));
        mHelpText.setMovementMethod(LinkMovementMethod.getInstance());

        TextUtils.setTextViewHTML(mHelpText, getString(R.string.question_about_payment), new ClickableSpan()
        {
            @Override
            public void onClick(final View widget)
            {
                bus.post(new LogEvent.AddLogEvent(new CompletedJobsLog.HelpClicked(mBooking)));
            }
        });
    }
}