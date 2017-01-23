package com.handy.portal.payments.ui.fragment.dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.handy.portal.R;
import com.handy.portal.library.ui.fragment.dialog.ConfirmActionSlideUpDialogFragment;
import com.handy.portal.library.util.UIUtils;
import com.handy.portal.payments.model.PaymentSupportItem;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * captures the reason for the user wanting payment support
 */
public class PaymentSupportReasonsDialogFragment extends ConfirmActionSlideUpDialogFragment
{
    public static final String FRAGMENT_TAG = PaymentSupportReasonsDialogFragment.class.getName();
    private static final String BUNDLE_KEY_PAYMENT_SUPPORT_ITEMS = "BUNDLE_KEY_PAYMENT_SUPPORT_ITEMS";

    @BindView(R.id.payment_support_reasons_radiogroup)
    RadioGroup mSupportReasonsRadioGroup;

    private Map<RadioButton, PaymentSupportItem> mRadioButtonToPaymentSupportItemMap
            = new HashMap<>();

    public static PaymentSupportReasonsDialogFragment newInstance(
            @NonNull PaymentSupportItem paymentSupportItems[])
    {
        Bundle args = new Bundle();
        args.putSerializable(BUNDLE_KEY_PAYMENT_SUPPORT_ITEMS, paymentSupportItems);
        PaymentSupportReasonsDialogFragment fragment = new PaymentSupportReasonsDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        //don't enable confirm button until a radio button is clicked
        PaymentSupportItem[] paymentSupportItems = (PaymentSupportItem[]) getArguments().getSerializable(BUNDLE_KEY_PAYMENT_SUPPORT_ITEMS);
        mConfirmActionButton.setEnabled(false);
        mSupportReasonsRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final RadioGroup group, final int checkedId)
            {
                mConfirmActionButton.setEnabled(true);
            }
        });
        populateSupportItemsRadioGroup(paymentSupportItems);
    }

    @Override
    protected View inflateConfirmActionContentView(final LayoutInflater inflater, final ViewGroup container)
    {
        return inflater.inflate(R.layout.layout_payment_support_reasons, container, false);
    }

    @Override
    protected void onConfirmActionButtonClicked()
    {
        RadioButton checkedRadioButton = UIUtils.getCheckedRadioButton(mSupportReasonsRadioGroup);
        PaymentSupportItem paymentSupportItem = mRadioButtonToPaymentSupportItemMap.get(checkedRadioButton);
        Callback callback = (Callback) getTargetFragment();
        callback.onPaymentSupportItemSubmitted(paymentSupportItem);
        dismiss();
    }

    @Override
    protected int getConfirmButtonBackgroundResourceId()
    {
        return R.drawable.button_grey_round;
    }

    @Override
    protected String getConfirmButtonText()
    {
        return getResources().getString(R.string.payment_support_reasons_dialog_submit_button);
    }

    private void populateSupportItemsRadioGroup(PaymentSupportItem[] checkBoxListItems)
    {
        if (checkBoxListItems == null || checkBoxListItems.length == 0) { return; }

        mRadioButtonToPaymentSupportItemMap.clear();
        mSupportReasonsRadioGroup.removeAllViews();
        for (PaymentSupportItem checkBoxListItem : checkBoxListItems)
        {
            RadioButton radioButton = (RadioButton) LayoutInflater.from(getContext())
                    .inflate(R.layout.radio_button_dismissal_reason, mSupportReasonsRadioGroup, false);
            radioButton.setText(checkBoxListItem.getDisplayName());
            mRadioButtonToPaymentSupportItemMap.put(radioButton, checkBoxListItem);
            mSupportReasonsRadioGroup.addView(radioButton);
        }
    }

    public interface Callback
    {
        void onPaymentSupportItemSubmitted(PaymentSupportItem paymentSupportItem);
    }
}
