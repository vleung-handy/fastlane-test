package com.handy.portal.payments.ui.element;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.payments.PaymentsUtil;
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

    @BindView(R.id.payments_batch_list_current_week_daily_pro_pay_toggle_container)
    DailyCashOutToggleView mDailyCashOutToggleView;

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

    public void updateDisplay(@NonNull PaymentBatchListHeaderViewModel paymentBatchListHeaderViewModel) {
        currentWeekDateRangeText.setText(paymentBatchListHeaderViewModel.getCurrentWeekDateRange());
        currentWeekRemainingFeesText.setText(paymentBatchListHeaderViewModel.getCurrentWeekRemainingFees());
        currentWeekExpectedPaymentText.setText(paymentBatchListHeaderViewModel.getCurrentWeekExpectedPayment());
        currentWeekFeesText.setText(paymentBatchListHeaderViewModel.getCurrentWeekFees());
        currentWeekTotalEarningsText.setText(paymentBatchListHeaderViewModel.getCurrentWeekTotalEarnings());
        currentWeekFeesText.setTextColor(paymentBatchListHeaderViewModel.getCurrentWeekFeesColor(getContext()));

        currentWeekRemainingFeesRow.setVisibility(paymentBatchListHeaderViewModel.shouldShowCurrentWeekRemainingFees() ? VISIBLE : GONE);

        updateCashOutButton(paymentBatchListHeaderViewModel);
        updateDailyCashOutToggle(paymentBatchListHeaderViewModel);
    }

    private void updateDailyCashOutToggle(@NonNull PaymentBatchListHeaderViewModel paymentBatchListHeaderViewModel) {
        if (paymentBatchListHeaderViewModel.shouldShowDailyCashOutToggle()) {
            mDailyCashOutToggleView.setVisibility(VISIBLE);
            mDailyCashOutToggleView.setToggleChecked(paymentBatchListHeaderViewModel.isDailyCashOutEnabled());
            mDailyCashOutToggleView.setBodyText(paymentBatchListHeaderViewModel.getFormattedDailyCashOutInfoText(getContext()));
        }
        else {
            mDailyCashOutToggleView.setVisibility(GONE);
        }
    }

    private void updateCashOutButton(@NonNull PaymentBatchListHeaderViewModel paymentBatchListHeaderViewModel) {
        PaymentsUtil.CashOut.styleCashOutButtonForApparentEnabledState(mCashOutButton,
                paymentBatchListHeaderViewModel.shouldApparentlyEnableCashOutButton());
        mCashOutButton.setVisibility(paymentBatchListHeaderViewModel.shouldShowCashOutButton() ? VISIBLE : GONE);

    }

    public void setOnCashOutButtonClickedListener(OnClickListener onCashOutButtonClickedListener) {
        mCashOutButton.setOnClickListener(onCashOutButtonClickedListener);
    }

    public void setDailyCashOutToggleListeners(CompoundButton.OnCheckedChangeListener onCheckedChangeListener,
                                               OnClickListener onHelpCenterUrlClickedListener) {
        mDailyCashOutToggleView.setClickListeners(onCheckedChangeListener, onHelpCenterUrlClickedListener);
    }

}
