package com.handy.portal.payments.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.collect.ObjectArrays;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PaymentBatches implements Serializable {
    @SerializedName("payments_batches")
    private NeoPaymentBatch[] neoPaymentBatches;

    @SerializedName("legacy_payments_batches")
    private LegacyPaymentBatch[] legacyPaymentsBatchBatches;

    /**
     * acknowledged that this key name is confusing, but it is too late to rename now
     */
    @SerializedName("cash_out")
    private OneTimeCashOutInfo mOneTimeCashOutInfo;

    @SerializedName("daily_cash_out")
    private DailyCashOutInfo mDailyCashOutInfo;

    @Nullable
    public DailyCashOutInfo getDailyCashOutInfo() {
        return mDailyCashOutInfo;
    }

    @Nullable
    public OneTimeCashOutInfo getOneTimeCashOutInfo() {
        return mOneTimeCashOutInfo;
    }

    public NeoPaymentBatch[] getNeoPaymentBatches() {
        return neoPaymentBatches;
    }

    public LegacyPaymentBatch[] getLegacyPaymentsBatchBatches() {
        return legacyPaymentsBatchBatches;
    }

    public boolean isEmpty() {
        return neoPaymentBatches.length == 0 && legacyPaymentsBatchBatches.length == 0;
    }

    public PaymentBatch[] getAggregateBatchList() {
        //some defense against null batch lists
        if (getNeoPaymentBatches() == null
                && getLegacyPaymentsBatchBatches() == null) {
            return new PaymentBatch[]{};
        }
        else if (getNeoPaymentBatches() == null) {
            return getLegacyPaymentsBatchBatches();
        }
        else if (getLegacyPaymentsBatchBatches() == null) {
            return getNeoPaymentBatches();
        }
        else {
            return ObjectArrays.concat(getNeoPaymentBatches(), getLegacyPaymentsBatchBatches(), PaymentBatch.class);
        }

    }

    /**
     * used to render UI for the "cash out now" feature
     */
    public static class OneTimeCashOutInfo implements Serializable
    {
        @SerializedName("cash_out_minimum_threshold")
        private Integer mCashOutMinimumThresholdCents;

        @SerializedName("cash_out_threshold_exceeded")
        private Boolean mCashOutThresholdExceeded;

        @SerializedName("cash_out_currency_symbol")
        private String mCashOutCurrencySymbol;

        @NonNull
        public String getCashOutCurrencySymbol() {
            return mCashOutCurrencySymbol;
        }

        @Nullable
        public Boolean getCashOutThresholdExceeded() {
            return mCashOutThresholdExceeded;
        }

        @Nullable
        public Integer getCashOutMinimumThresholdCents() {
            return mCashOutMinimumThresholdCents;
        }
    }


    /**
     * used to render UI related to the daily cash out feature,
     * including the toggle and confirmation screen.
     * <p>
     * we will never support any frequency besides daily.
     */
    public static class DailyCashOutInfo implements Serializable {
        @SerializedName("enabled")
        private boolean mEnabled;
        /**
         * the fee charged per cash out
         */
        @SerializedName("fee")
        private PaymentAmount mCashOutFee;
        @SerializedName("help_center_url")
        private String mHelpCenterArticleUrl;
        @SerializedName("toggle_confirmation_copy")
        private ToggleConfirmationCopy mToggleConfirmationCopy;

        public boolean isEnabled() {
            return mEnabled;
        }

        @NonNull
        public PaymentAmount getCashOutFee() {
            return mCashOutFee;
        }

        @NonNull
        public ToggleConfirmationCopy getToggleConfirmationCopy() {
            return mToggleConfirmationCopy;
        }

        @NonNull
        public String getHelpCenterArticleUrl() {
            return mHelpCenterArticleUrl;
        }


        public static class ToggleConfirmationCopy implements Serializable {
            @SerializedName("title")
            private String mTitleText;

            @SerializedName("body")
            private String mBodyText;

            @SerializedName("confirm_button")
            private String mConfirmButtonText;

            @SerializedName("cancel_button")
            private String mCancelButtonText;

            @NonNull
            public String getTitleText() {
                return mTitleText;
            }

            @NonNull
            public String getBodyText() {
                return mBodyText;
            }

            @NonNull
            public String getConfirmButtonText() {
                return mConfirmButtonText;
            }

            @NonNull
            public String getCancelButtonText() {
                return mCancelButtonText;
            }
        }
    }
}
