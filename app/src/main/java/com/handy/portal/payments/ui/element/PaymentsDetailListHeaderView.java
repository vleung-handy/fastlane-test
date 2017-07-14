package com.handy.portal.payments.ui.element;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.payments.model.NeoPaymentBatch;
import com.handy.portal.payments.viewmodel.PaymentDetailHeaderViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PaymentsDetailListHeaderView extends LinearLayout {
    @BindView(R.id.payment_detail_date_range_text)
    TextView paymentDetailDateRangeText;

    @BindView(R.id.payments_detail_total_payment_text)
    TextView paymentDetailTotalPaymentText;

    @BindView(R.id.payment_details_list_header_payment_status_layout)
    View mPaymentStatusLayout;

    @BindView(R.id.payment_details_list_header_payment_status_text)
    TextView mPaymentStatusText;

    @BindView(R.id.payment_details_list_header_payment_status_help_button)
    View mPaymentStatusHelpButton;

    @BindView(R.id.payments_detail_list_header_payment_status_expected_deposit_date)
    TextView paymentStatusExpectedDepositDate;

    @BindView(R.id.payments_detail_list_header_payment_status_payment_method_text)
    TextView mPaymentStatusPaymentMethodText;

    @BindView(R.id.payments_detail_list_header_cash_out_button_container)
    CashOutButtonContainerView mCashOutButtonContainerView;

    private Callback mCallback;

    public PaymentsDetailListHeaderView(Context context) {
        super(context);
    }

    public PaymentsDetailListHeaderView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    public void updateDisplay(@NonNull PaymentDetailHeaderViewModel paymentDetailHeaderViewModel) {
        paymentDetailDateRangeText.setText(paymentDetailHeaderViewModel.getDateRange());
        paymentDetailTotalPaymentText.setText(paymentDetailHeaderViewModel.getTotalPayment());
        updatePaymentStatusLayout(paymentDetailHeaderViewModel);
    }

    private void updateExpectedDepositDate(@NonNull PaymentDetailHeaderViewModel paymentDetailHeaderViewModel) {
        if (paymentDetailHeaderViewModel.shouldShowExpectedDepositDate()) {
            paymentStatusExpectedDepositDate.setVisibility(VISIBLE);
            paymentStatusExpectedDepositDate.setText(paymentDetailHeaderViewModel.getExpectedDepositDate(getContext()));
        }
        else {
            paymentStatusExpectedDepositDate.setVisibility(GONE);

        }
    }

    private void updatePaymentStatusText(@NonNull PaymentDetailHeaderViewModel paymentDetailHeaderViewModel) {
        final String paymentStatusFormatted = paymentDetailHeaderViewModel.getPaymentStatusFormatted(getContext());
        mPaymentStatusText.setText(paymentStatusFormatted, TextView.BufferType.SPANNABLE);

        if (!TextUtils.isEmpty(paymentDetailHeaderViewModel.getPaymentStatus())) {
            final Spannable spannable = (Spannable) mPaymentStatusText.getText();
            final int start = paymentStatusFormatted.indexOf(paymentDetailHeaderViewModel.getPaymentStatus());
            final int end = start + paymentDetailHeaderViewModel.getPaymentStatus().length();
            int colorResourceId;
            if (NeoPaymentBatch.Status.FAILED.equalsIgnoreCase(paymentDetailHeaderViewModel.getPaymentStatus())) {
                colorResourceId = R.color.error_red;
            }
            else {
                colorResourceId = R.color.handy_green;
            }
            spannable.setSpan(
                    new ForegroundColorSpan(ContextCompat.getColor(getContext(), colorResourceId)),
                    start,
                    end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }
    }

    private void updateCashOutButton(@NonNull PaymentDetailHeaderViewModel paymentDetailHeaderViewModel) {
        mCashOutButtonContainerView.setApparentlyEnabled(paymentDetailHeaderViewModel.shouldApparentlyEnableCashOutButton());
        mCashOutButtonContainerView.setVisibility(paymentDetailHeaderViewModel.shouldShowCashOutButton() ? VISIBLE : GONE);
    }

    private void updatePaymentMethodText(@NonNull PaymentDetailHeaderViewModel paymentDetailHeaderViewModel) {

        if (paymentDetailHeaderViewModel.shouldShowPaymentMethodDetails()) {
            mPaymentStatusPaymentMethodText.setVisibility(VISIBLE);
            mPaymentStatusPaymentMethodText.setText(paymentDetailHeaderViewModel.getPaymentMethodDetails(getContext()));
        }
        else {
            mPaymentStatusPaymentMethodText.setVisibility(GONE);
        }
    }

    private void updatePaymentStatusLayout(@NonNull PaymentDetailHeaderViewModel paymentDetailHeaderViewModel) {
        if (paymentDetailHeaderViewModel.shouldShowPaymentStatusLayout()) {
            mPaymentStatusLayout.setVisibility(VISIBLE);
            updateExpectedDepositDate(paymentDetailHeaderViewModel);
            updatePaymentStatusText(paymentDetailHeaderViewModel);
            updatePaymentMethodText(paymentDetailHeaderViewModel);
            updateCashOutButton(paymentDetailHeaderViewModel);
        }
        else {
            mPaymentStatusLayout.setVisibility(GONE);
        }
    }

    /**
     * this is exposed because this button is shown based
     * on factors outside of the view class's knowledge
     * (dependent on what the callback needs to do)
     * <p>
     * TODO this is a quick-fix but is not ideal because there is already an updateDisplay() method
     *
     * @param visible
     */
    public void setPaymentStatusHelpButtonVisible(boolean visible) {
        //this is visible by default in the layout xml
        mPaymentStatusHelpButton.setVisibility(visible ? VISIBLE : GONE);
    }

    @OnClick(R.id.payment_details_list_header_payment_status_help_button)
    public void onRequestStatusSupportButtonClicked() {
        if (mCallback != null) {
            mCallback.onRequestStatusSupportButtonClicked();
        }
    }

    public void setCallbackListener(Callback callbackListener) {
        mCallback = callbackListener;
    }

    public interface Callback {
        void onRequestStatusSupportButtonClicked();
    }

    public void setOnCashOutButtonClickListener(OnClickListener onCashOutButtonClickListener) {
        mCashOutButtonContainerView.setButtonOnClickListener(onCashOutButtonClickListener);
    }
}
