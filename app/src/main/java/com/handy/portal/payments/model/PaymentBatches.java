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
    private AdhocCashOutInfo mAdhocCashOutInfo;

    @SerializedName("recurring_cash_out")
    private RecurringCashOutInfo mRecurringCashOutInfo;

    @Nullable
    public RecurringCashOutInfo getRecurringCashOutInfo() {
        return mRecurringCashOutInfo;
    }

    @Nullable
    public AdhocCashOutInfo getAdhocCashOutInfo() {
        return mAdhocCashOutInfo;
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
    public static class AdhocCashOutInfo implements Serializable
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
     * used to render UI related to the recurring cash out feature,
     * including the toggle and confirmation screen.
     */
    public static class RecurringCashOutInfo implements Serializable {
        @SerializedName("payment_batch_period")
        private PaymentBatchPeriodInfo mPaymentBatchPeriodInfo;
        /**
         * the fee charged per cash out
         */
        @SerializedName("recurring_fee")
        private PaymentAmount mRecurringFee;
        @SerializedName("help_center_article_url")
        private String mHelpCenterArticleUrl;
        @SerializedName("toggle_confirmation")
        private ToggleConfirmationInfo mToggleConfirmationInfo;
        @SerializedName("edit_disabled_dialog")
        private EditDisabledDialogInfo mEditDisabledDialogInfo;

        @NonNull
        public PaymentBatchPeriodInfo getPaymentBatchPeriodInfo() {
            return mPaymentBatchPeriodInfo;
        }

        @NonNull
        public PaymentAmount getRecurringFee() {
            return mRecurringFee;
        }


        @NonNull
        public String getHelpCenterArticleUrl() {
            return mHelpCenterArticleUrl;
        }

        @NonNull
        public ToggleConfirmationInfo getToggleConfirmationInfo() {
            return mToggleConfirmationInfo;
        }

        @Nullable
        public EditDisabledDialogInfo getEditDisabledDialogInfo() {
            return mEditDisabledDialogInfo;
        }

        public static class PaymentBatchPeriodInfo implements Serializable {
            @SerializedName("editable")
            private boolean mEditable;

            @SerializedName("days")
            private Integer mDays;

            public boolean isEditable() {
                return mEditable;
            }

            @Nullable
            public Integer getDays() {
                return mDays;
            }
        }

        public static class ToggleConfirmationInfo implements Serializable {
            @SerializedName("title_text")
            private String mTitleText;

            @SerializedName("body_text")
            private String mBodyText;

            @SerializedName("confirm_button_text")
            private String mConfirmButtonText;

            @SerializedName("cancel_button_text")
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

        public static class EditDisabledDialogInfo implements Serializable {
            @SerializedName("title_text")
            private String mTitleText;

            @SerializedName("body_text")
            private String mBodyText;

            @NonNull
            public String getTitleText() {
                return mTitleText;
            }

            @NonNull
            public String getBodyText() {
                return mBodyText;
            }
        }
    }
}
