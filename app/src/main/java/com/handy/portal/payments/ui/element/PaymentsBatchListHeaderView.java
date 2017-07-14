package com.handy.portal.payments.ui.element;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.payments.viewmodel.PaymentBatchListHeaderViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PaymentsBatchListHeaderView extends FrameLayout //TODO: see if we can make this more linked with the batch adapter data
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

    @BindView(R.id.payments_batch_list_current_week_expected_deposit_date)
    TextView mExpectedDepositDate;

    @BindView(R.id.payments_batch_list_current_week_cash_out_button_container)
    CashOutButtonContainerView mCashOutButtonContainerView;

    @BindView(R.id.payments_batch_list_current_week_daily_pro_pay_toggle_container)
    DailyCashOutToggleContainerView mDailyCashOutToggleContainerView;

    public PaymentsBatchListHeaderView(final Context context) {
        super(context);
        init();
    }

    public PaymentsBatchListHeaderView(final Context context, @Nullable final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PaymentsBatchListHeaderView(final Context context, @Nullable final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PaymentsBatchListHeaderView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.element_payments_batch_list_current_week_header, this);
        ButterKnife.bind(this);
    }

    public void updateDisplay(@NonNull PaymentBatchListHeaderViewModel paymentBatchListHeaderViewModel) {
        currentWeekDateRangeText.setText(paymentBatchListHeaderViewModel.getCurrentWeekDateRange());
        currentWeekRemainingFeesText.setText(paymentBatchListHeaderViewModel.getCurrentWeekRemainingFees());
        currentWeekExpectedPaymentText.setText(paymentBatchListHeaderViewModel.getCurrentWeekExpectedPayment());
        currentWeekFeesText.setText(paymentBatchListHeaderViewModel.getCurrentWeekFees());
        currentWeekTotalEarningsText.setText(paymentBatchListHeaderViewModel.getCurrentWeekTotalEarnings());
        currentWeekFeesText.setTextColor(paymentBatchListHeaderViewModel.getCurrentWeekFeesColor(getContext()));

        currentWeekRemainingFeesRow.setVisibility(paymentBatchListHeaderViewModel.shouldShowCurrentWeekRemainingFees() ? VISIBLE : GONE);
        mExpectedDepositDate.setText(paymentBatchListHeaderViewModel.getExpectedDepositDate(getContext()));

        updateCashOutButton(paymentBatchListHeaderViewModel);
        mDailyCashOutToggleContainerView.updateWithModel(paymentBatchListHeaderViewModel.getDailyCashOutToggleContainerViewModel());
    }

    private void updateCashOutButton(@NonNull PaymentBatchListHeaderViewModel paymentBatchListHeaderViewModel) {
        mCashOutButtonContainerView.setApparentlyEnabled(paymentBatchListHeaderViewModel.shouldApparentlyEnableCashOutButton());
        mCashOutButtonContainerView.setVisibility(paymentBatchListHeaderViewModel.shouldShowCashOutButton() ? VISIBLE : GONE);
    }

    public void setOnCashOutButtonClickedListener(OnClickListener onCashOutButtonClickedListener) {
        mCashOutButtonContainerView.setButtonOnClickListener(onCashOutButtonClickedListener);
    }

    public void setDailyCashOutToggleContainerClickedListener(DailyCashOutToggleContainerView.ToggleContainerClickListener toggleContainerClickedListener) {
        mDailyCashOutToggleContainerView.setClickListeners(toggleContainerClickedListener);
    }
}
