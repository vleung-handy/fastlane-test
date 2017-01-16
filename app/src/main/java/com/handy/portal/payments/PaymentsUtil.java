package com.handy.portal.payments;

import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;

import com.handy.portal.core.constant.RequestCode;
import com.handy.portal.library.util.FragmentUtils;
import com.handy.portal.payments.model.PaymentSupportItem;
import com.handy.portal.payments.ui.fragment.dialog.PaymentDetailsSupportDialogFragment;

public class PaymentsUtil
{
    /**
     * there are 3 places where this dialog can be triggered
     *
     * assumptions: payment support items not null or empty
     */
    public static void showPaymentSupportDialog(@NonNull Fragment targetFragment,
                                          @NonNull PaymentSupportItem[] paymentSupportItems)
    {
        if (targetFragment.getChildFragmentManager().findFragmentByTag(PaymentDetailsSupportDialogFragment.FRAGMENT_TAG) == null)
        {
            final DialogFragment fragment = PaymentDetailsSupportDialogFragment.newInstance(paymentSupportItems);
            fragment.setTargetFragment(targetFragment, RequestCode.PAYMENT_SUPPORT_ITEM_SUBMITTED);
            FragmentUtils.safeLaunchDialogFragment(fragment,
                    targetFragment,
                    PaymentDetailsSupportDialogFragment.FRAGMENT_TAG);
        }
    }
}
