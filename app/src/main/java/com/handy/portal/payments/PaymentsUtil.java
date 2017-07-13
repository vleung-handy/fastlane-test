package com.handy.portal.payments;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.handy.portal.R;
import com.handy.portal.library.util.CurrencyUtils;
import com.handy.portal.library.util.FragmentUtils;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.PaymentsLog;
import com.handy.portal.payments.model.PaymentBatches;
import com.handy.portal.payments.model.PaymentSupportItem;
import com.handy.portal.payments.ui.fragment.dialog.AdhocCashOutDialogFragment;
import com.handy.portal.payments.ui.fragment.dialog.PaymentSupportReasonsDialogFragment;

import org.greenrobot.eventbus.EventBus;

public abstract class PaymentsUtil {
    /**
     * this is in util because there are 3 places where this dialog can be triggered
     */
    public static void showPaymentSupportReasonsDialog(@NonNull Fragment targetFragment,
                                                       @NonNull PaymentSupportItem[] paymentSupportItems) {
        if (targetFragment.getChildFragmentManager().findFragmentByTag(PaymentSupportReasonsDialogFragment.FRAGMENT_TAG) == null) {
            final DialogFragment fragment = PaymentSupportReasonsDialogFragment.newInstance(paymentSupportItems);
            FragmentUtils.safeLaunchDialogFragment(fragment,
                    targetFragment,
                    PaymentSupportReasonsDialogFragment.FRAGMENT_TAG);
        }
    }

    public abstract static class CashOut {
        /**
         * @param callbackFragment the callback fragment that should be used to launch another fragment if necessary
         * @param adhocCashOutInfo
         * @param logEventBus      for logging purposes only
         * @return a click listener that shows one of the following:
         * - dialog fragment
         * - toast
         * - alert dialog
         */
        public static View.OnClickListener createCashOutButtonClickListener(
                @NonNull final Fragment callbackFragment,
                boolean cashOutEnabled,
                @Nullable PaymentBatches.AdhocCashOutInfo adhocCashOutInfo,
                @NonNull final EventBus logEventBus) {
            final Context context = callbackFragment.getContext();
            View.OnClickListener cashOutButtonClickedListener;
            if (cashOutEnabled) {
                cashOutButtonClickedListener =
                        new View.OnClickListener() {
                            @Override
                            public void onClick(final View v) {
                                logEventBus.post(new LogEvent.AddLogEvent(
                                        new PaymentsLog.CashOutEarlySelected()));

                                //this needs to be launched from a fragment so that callbacks can be properly handled
                                FragmentUtils.safeLaunchDialogFragment(
                                        AdhocCashOutDialogFragment.newInstance(),
                                        callbackFragment,
                                        AdhocCashOutDialogFragment.TAG,
                                        true);
                            }
                        };
            }
            else {
                if (adhocCashOutInfo == null
                        || adhocCashOutInfo.getCashOutThresholdExceeded() == null
                        || adhocCashOutInfo.getCashOutMinimumThresholdCents() == null
                        || adhocCashOutInfo.getCashOutThresholdExceeded()) {
                    cashOutButtonClickedListener = new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            Toast.makeText(context,
                                    context.getResources().getString(R.string.payment_cash_out_denied_error),
                                    Toast.LENGTH_SHORT).show();
                        }
                    };
                }
                else {
                    final String cashOutThresholdFormatted =
                            CurrencyUtils.formatPrice(
                                    adhocCashOutInfo.getCashOutMinimumThresholdCents(),
                                    adhocCashOutInfo.getCashOutCurrencySymbol(),
                                    false);

                    cashOutButtonClickedListener = new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            //cash out disabled
                            final AlertDialog alertDialog = new AlertDialog.Builder(context)
                                    .setCancelable(true)
                                    .setTitle(context.getString(R.string.payment_cash_out_disabled_dialog_title_formatted, cashOutThresholdFormatted))
                                    .setMessage(context.getString(R.string.payment_cash_out_disabled_dialog_body_formatted, cashOutThresholdFormatted))
                                    .setPositiveButton(R.string.ok, null)
                                    .create();
                            alertDialog.show();
                        }
                    };
                }
            }
            return cashOutButtonClickedListener;
        }

        /**
         * we need to style the cash out button in multiple components
         * based on whether it should look enabled.
         * <p>
         * this cannot be put into styles.xml and has to be done at runtime
         * because we cannot use a native state, such as "state_enabled"
         * because we still need it to be clickable
         */
        public static void styleCashOutButtonForApparentEnabledState(
                @NonNull Button mCashOutButton,
                boolean apparentlyEnabled) {
            Context context = mCashOutButton.getContext();
            if (apparentlyEnabled) {
                mCashOutButton.setBackground(ContextCompat.getDrawable(context,
                        R.drawable.button_green_round));
                mCashOutButton.setTextColor(ContextCompat.getColorStateList(
                        context,
                        R.color.button_text_booking_action_white));
            }
            else {
                mCashOutButton.setBackground(ContextCompat.getDrawable(context,
                        R.drawable.button_apparently_disabled));
                mCashOutButton.setTextColor(ContextCompat.getColor(context,
                        R.color.border_mid_gray));
            }
        }
    }

}
