package com.handy.portal.payments.ui.element;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.payments.viewmodel.PaymentBatchListHeaderViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PaymentsBatchListHeaderView extends LinearLayout //TODO: see if we can make this more linked with the batch adapter data
{
    @BindView(R.id.payments_current_week_date_range_text)
    TextView currentWeekDateRangeText;

    @BindView(R.id.payments_current_week_total_earnings)
    TextView currentWeekTotalEarningsText;

    @BindView(R.id.payments_current_week_fees)
    TextView currentWeekFeesText;

    @BindView(R.id.payments_current_week_expected_payment)
    TextView currentWeekExpectedPaymentText;

    @BindView(R.id.payments_current_week_remaining_fees)
    TextView currentWeekRemainingFeesText;

    @BindView(R.id.payments_current_week_remaining_fees_row)
    ViewGroup currentWeekRemainingFeesRow;

    @BindView(R.id.payments_batch_list_current_week_cash_out_button)
    Button mCashOutButton;

    public PaymentsBatchListHeaderView(Context context) {
        super(context);
    }

    public PaymentsBatchListHeaderView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    public void updateDisplay(@NonNull PaymentBatchListHeaderViewModel paymentBatchListHeaderViewModel)
    {
        currentWeekDateRangeText.setText(paymentBatchListHeaderViewModel.getCurrentWeekDateRange());
        currentWeekRemainingFeesText.setText(paymentBatchListHeaderViewModel.getCurrentWeekRemainingFees());
        currentWeekExpectedPaymentText.setText(paymentBatchListHeaderViewModel.getCurrentWeekExpectedPayment());
        currentWeekFeesText.setText(paymentBatchListHeaderViewModel.getCurrentWeekFees());
        currentWeekTotalEarningsText.setText(paymentBatchListHeaderViewModel.getCurrentWeekTotalEarnings());
        currentWeekFeesText.setTextColor(paymentBatchListHeaderViewModel.getCurrentWeekFeesColor(getContext()));

        currentWeekRemainingFeesRow.setVisibility(paymentBatchListHeaderViewModel.shouldShowCurrentWeekRemainingFees() ? VISIBLE : GONE);

        mCashOutButton.setEnabled(paymentBatchListHeaderViewModel.shouldEnableCashOutButton());
        mCashOutButton.setVisibility(paymentBatchListHeaderViewModel.shouldShowCashOutButton() ? VISIBLE : GONE);
    }

    public void setOnCashOutButtonClickedListener(OnClickListener onCashOutButtonClickedListener)
    {
        mCashOutButton.setOnClickListener(onCashOutButtonClickedListener);
    }

}
