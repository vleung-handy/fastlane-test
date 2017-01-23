package com.handy.portal.payments.ui.fragment.dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.library.ui.fragment.dialog.ConfirmActionSlideUpDialogFragment;

import butterknife.BindView;

//TODO better name?
public class PaymentSupportRedirectToBookingTransactionsDialogFragment extends ConfirmActionSlideUpDialogFragment
{
    public static final String FRAGMENT_TAG =
            PaymentSupportRedirectToBookingTransactionsDialogFragment.class.getName();

    public static final String BUNDLE_KEY_PAYMENT_SUPPORT_ITEM_DISPLAY_NAME
            = "BUNDLE_KEY_PAYMENT_SUPPORT_ITEM_DISPLAY_NAME";

    @BindView(R.id.payment_support_redirect_to_booking_transactions_title_text)
    TextView mTitleText;

    public static PaymentSupportRedirectToBookingTransactionsDialogFragment newInstance(
            @NonNull String paymentSupportItemDisplayName
    )
    {
        Bundle args = new Bundle();
        args.putString(BUNDLE_KEY_PAYMENT_SUPPORT_ITEM_DISPLAY_NAME, paymentSupportItemDisplayName);
        PaymentSupportRedirectToBookingTransactionsDialogFragment fragment =
                new PaymentSupportRedirectToBookingTransactionsDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    protected View inflateConfirmActionContentView(final LayoutInflater inflater, final ViewGroup container)
    {
        return inflater.inflate(R.layout.layout_payment_support_redirect_to_booking_transactions, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        String paymentSupportItemDisplayName =
                getArguments().getString(BUNDLE_KEY_PAYMENT_SUPPORT_ITEM_DISPLAY_NAME);
        mTitleText.setText(paymentSupportItemDisplayName);
    }

    @Override
    protected void onConfirmActionButtonClicked()
    {
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
        return getString(R.string.payment_support_redirect_to_booking_transactions_dialog_confirm_button);
    }
}
