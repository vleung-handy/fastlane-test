package com.handy.portal.payments.viewmodel;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import com.handy.portal.R;
import com.handy.portal.library.util.CurrencyUtils;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.payments.model.NeoPaymentBatch;
import com.handy.portal.payments.model.PaymentBatches;

/**
 * ViewModel for {@link com.handy.portal.payments.ui.element.PaymentsBatchListHeaderView}
 */
public class PaymentBatchListHeaderViewModel {

    private final NeoPaymentBatch mNeoPaymentBatch;
    private final boolean mShouldShowCashOutButton;
    private final DailyCashOutToggleContainerViewModel mDailyCashOutToggleContainerViewModel;

    public PaymentBatchListHeaderViewModel(@NonNull NeoPaymentBatch neoPaymentBatch,
                                           @Nullable PaymentBatches.RecurringCashOutInfo recurringCashOutInfo,
                                           boolean canShowCashOutButton) {
        mNeoPaymentBatch = neoPaymentBatch;
        mShouldShowCashOutButton = canShowCashOutButton;
        mDailyCashOutToggleContainerViewModel = new DailyCashOutToggleContainerViewModel(recurringCashOutInfo);
    }

    @NonNull
    public DailyCashOutToggleContainerViewModel getDailyCashOutToggleContainerViewModel() {
        return mDailyCashOutToggleContainerViewModel;
    }

    public boolean shouldShowCashOutButton() {
        return mShouldShowCashOutButton;
    }

    public boolean shouldApparentlyEnableCashOutButton() {
        return mNeoPaymentBatch.isCashOutEnabled();
    }

    public boolean shouldShowCurrentWeekRemainingFees() {
        return mNeoPaymentBatch.getRemainingFeeAmount() > 0;
    }

    public String getExpectedDepositDate(@NonNull Context context) {
        return context.getResources().getString(R.string.expected_deposit_formatted,
                DateTimeUtils.DAY_OF_WEEK_MONTH_DATE_FORMATTER.format(
                        mNeoPaymentBatch.getExpectedDepositDate()));
    }

    public String getCurrentWeekDateRange() {
        return DateTimeUtils.formatDayRange(
                DateTimeUtils.SHORT_DAY_OF_WEEK_MONTH_DAY_FORMATTER,
                mNeoPaymentBatch.getStartDate(),
                mNeoPaymentBatch.getEndDate());
    }

    public String getCurrentWeekRemainingFees() {
        return CurrencyUtils.formatPriceWithCents(
                mNeoPaymentBatch.getRemainingFeeAmount(),
                mNeoPaymentBatch.getCurrencySymbol());

    }

    public String getCurrentWeekExpectedPayment() {
        return CurrencyUtils.formatPriceWithCents(
                mNeoPaymentBatch.getNetEarningsTotalAmount(),
                mNeoPaymentBatch.getCurrencySymbol());
    }

    public String getCurrentWeekFees() {
        return CurrencyUtils.formatPriceWithCents(
                mNeoPaymentBatch.getFeesTotalAmount(),
                mNeoPaymentBatch.getCurrencySymbol());
    }

    public String getCurrentWeekTotalEarnings() {
        return CurrencyUtils.formatPriceWithCents(
                mNeoPaymentBatch.getGrossEarningsTotalAmount(),
                mNeoPaymentBatch.getCurrencySymbol());
    }

    public int getCurrentWeekFeesColor(@NonNull Context context) {
        return ContextCompat.getColor(context,
                mNeoPaymentBatch.getFeesTotalAmount() < 0 ? R.color.plumber_red : R.color.black);
    }
}
