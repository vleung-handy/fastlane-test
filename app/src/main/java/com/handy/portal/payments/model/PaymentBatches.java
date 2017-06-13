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

    @SerializedName("cash_out")
    private CashOutInfo mCashOutInfo;

    @Nullable
    public CashOutInfo getCashOutInfo() {
        return mCashOutInfo;
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

    public static class CashOutInfo implements Serializable
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
}
