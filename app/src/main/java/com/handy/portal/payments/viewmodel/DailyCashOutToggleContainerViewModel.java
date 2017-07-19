package com.handy.portal.payments.viewmodel;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.handy.portal.R;
import com.handy.portal.library.util.CurrencyUtils;
import com.handy.portal.payments.model.PaymentBatches;

/**
 * ViewModel for {@link com.handy.portal.payments.ui.element.DailyCashOutToggleContainerView}
 */
public class DailyCashOutToggleContainerViewModel {

    private final PaymentBatches.RecurringCashOutInfo mRecurringCashOutInfo;

    DailyCashOutToggleContainerViewModel(@Nullable PaymentBatches.RecurringCashOutInfo recurringCashOutInfo) {
        mRecurringCashOutInfo = recurringCashOutInfo;
    }

    public boolean isViewVisible() {
        return mRecurringCashOutInfo != null;
    }

    public boolean isViewApparentlyEnabled() {
        return mRecurringCashOutInfo != null
                && mRecurringCashOutInfo.getPaymentBatchPeriodInfo().isEditable();
    }

    public boolean isToggleChecked() {
        return mRecurringCashOutInfo != null
                && mRecurringCashOutInfo.getPaymentBatchPeriodInfo().getDays() != null
                && mRecurringCashOutInfo.getPaymentBatchPeriodInfo().getDays() == PaymentBatches.RecurringCashOutInfo.PaymentBatchPeriodInfo.PAYMENT_BATCH_PERIOD_DAYS_DAILY;
    }

    @Nullable
    public String getInfoTextFormatted(@NonNull Context context) {
        if (mRecurringCashOutInfo == null
                || mRecurringCashOutInfo.getRecurringFee().getAmountCents() == null) {
            return null;
        }
        String formattedFee = CurrencyUtils.formatPrice(
                mRecurringCashOutInfo.getRecurringFee().getAmountCents(),
                mRecurringCashOutInfo.getRecurringFee().getCurrencySymbol(),
                true);
        return context.getResources().getString(
                R.string.payment_recurring_cash_out_toggle_info_text,
                formattedFee,
                mRecurringCashOutInfo.getHelpCenterArticleUrl()
        );
    }
}
