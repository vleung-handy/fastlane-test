package com.handy.portal.model.payments;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PaymentBatches implements Serializable
{
    public NeoPaymentBatch[] getNeoPaymentBatches()
    {
        return neoPaymentBatches;
    }

    public LegacyPayment[] getLegacyPayments()
    {
        return legacyPayments;
    }

    @SerializedName("payments_batches")
    private NeoPaymentBatch[] neoPaymentBatches;

    @SerializedName("legacy_payments")
    private LegacyPayment[] legacyPayments;


    public boolean isEmpty()
    {
        return neoPaymentBatches.length == 0 && legacyPayments.length == 0;
    }

}
