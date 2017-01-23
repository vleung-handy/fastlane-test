package com.handy.portal.payments;

import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;

import com.handy.portal.core.constant.RequestCode;
import com.handy.portal.library.util.FragmentUtils;
import com.handy.portal.payments.model.PaymentSupportItem;
import com.handy.portal.payments.ui.fragment.dialog.PaymentSupportReasonsDialogFragment;

public class PaymentsUtil
{
    /**
     * this is in util because there are 3 places where this dialog can be triggered
     */
    public static void showPaymentSupportReasonsDialog(@NonNull Fragment targetFragment,
                                                       @NonNull PaymentSupportItem[] paymentSupportItems)
    {
        if (targetFragment.getChildFragmentManager().findFragmentByTag(PaymentSupportReasonsDialogFragment.FRAGMENT_TAG) == null)
        {
            final DialogFragment fragment = PaymentSupportReasonsDialogFragment.newInstance(paymentSupportItems);
            fragment.setTargetFragment(targetFragment, RequestCode.PAYMENT_SUPPORT_ITEM_SUBMITTED);
            FragmentUtils.safeLaunchDialogFragment(fragment,
                    targetFragment,
                    PaymentSupportReasonsDialogFragment.FRAGMENT_TAG);
        }
    }
}
