package com.handy.portal.payments.ui.fragment.dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.library.ui.fragment.ConfirmActionSlideUpDialogFragment;
import com.handy.portal.payments.model.PaymentSupportItem;

import butterknife.BindView;

public class PaymentDetailsSupportRequestReviewDialogFragment extends ConfirmActionSlideUpDialogFragment
{
    public static final String FRAGMENT_TAG = PaymentDetailsSupportRequestReviewDialogFragment.class.getName();
    private static final String BUNDLE_KEY_PRO_EMAIL = "BUNDLE_KEY_PRO_EMAIL";
    private static final String BUNDLE_KEY_EXPECTED_DEPOSIT_DATE
            = "BUNDLE_KEY_EXPECTED_DEPOSIT_DATE";
    private static final String BUNDLE_KEY_SELECTED_PAYMENT_SUPPORT_ITEM
            = "BUNDLE_KEY_SELECTED_PAYMENT_SUPPORT_ITEM";

    @BindView(R.id.payment_details_support_request_review_title_text)
    TextView mTitleText;
    @BindView(R.id.payment_details_support_request_review_deposit_delay_text)
    TextView mDepositDelayText;
    @BindView(R.id.payment_details_support_request_review_instructions)
    TextView mInstructionsText;
    @BindView(R.id.payment_details_support_request_review_expected_deposit)
    TextView mExpectedDepositDateText;

    public static PaymentDetailsSupportRequestReviewDialogFragment newInstance(@NonNull String proEmail,
                                                                               @NonNull String expectedDepositDateFormatted,
                                                                               @NonNull PaymentSupportItem paymentSupportItem)//tODO can we just send the title
    {
        Bundle args = new Bundle();
        args.putString(BUNDLE_KEY_PRO_EMAIL, proEmail);
        args.putString(BUNDLE_KEY_EXPECTED_DEPOSIT_DATE, expectedDepositDateFormatted);
        args.putSerializable(BUNDLE_KEY_SELECTED_PAYMENT_SUPPORT_ITEM, paymentSupportItem);
        PaymentDetailsSupportRequestReviewDialogFragment fragment =
                new PaymentDetailsSupportRequestReviewDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        PaymentSupportItem paymentSupportItem =
                (PaymentSupportItem) getArguments().getSerializable(BUNDLE_KEY_SELECTED_PAYMENT_SUPPORT_ITEM);
        mTitleText.setText(paymentSupportItem.getDisplayName());
        mDepositDelayText.setText(Html.fromHtml(getString(R.string.payment_details_support_request_review_deposit_delay)));
        mExpectedDepositDateText.setText(getArguments().getString(BUNDLE_KEY_EXPECTED_DEPOSIT_DATE));

        String proEmail = getArguments().getString(BUNDLE_KEY_PRO_EMAIL);
        String emailTextFormatted = getString(R.string.payment_details_support_request_review_formatted,
                proEmail);
        mInstructionsText.setText(emailTextFormatted, TextView.BufferType.SPANNABLE);
        final Spannable spannable = (Spannable) mInstructionsText.getText();
        final int start = emailTextFormatted.indexOf(proEmail);
        final int end = start + proEmail.length();

        spannable.setSpan(
                new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.handy_blue)),
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );
    }

    @Override
    protected View inflateConfirmActionContentView(final LayoutInflater inflater, final ViewGroup container)
    {
        return inflater.inflate(R.layout.layout_payment_details_support_request_review, container, false);
    }

    @Override
    protected void onConfirmActionButtonClicked()
    {
        PaymentSupportItem paymentSupportItem =
                (PaymentSupportItem) getArguments().getSerializable(BUNDLE_KEY_SELECTED_PAYMENT_SUPPORT_ITEM);
        ((Callbacks) getTargetFragment()).onRequestDepositReviewButtonClicked(paymentSupportItem);
        dismiss();
    }

    @Override
    protected int getConfirmButtonBackgroundResourceId()
    {
        return R.drawable.button_green_round;
    }

    @Override
    protected String getConfirmButtonText()
    {
        return getString(R.string.payment_details_support_request_review_confirm_button);
    }

    public interface Callbacks
    {
        void onRequestDepositReviewButtonClicked(@NonNull PaymentSupportItem paymentSupportItem);
    }
}
