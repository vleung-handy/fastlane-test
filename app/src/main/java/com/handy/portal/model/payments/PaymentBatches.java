package com.handy.portal.model.payments;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PaymentBatches implements Serializable
{
    public NeoPaymentBatch[] getNeoPaymentBatches()
    {
        return neoPaymentBatches;
    }

    public LegacyPaymentBatch[] getLegacyPaymentsBatchBatches()
    {
        return legacyPaymentsBatchBatches;
    }

    @SerializedName("payments_batches")
    private NeoPaymentBatch[] neoPaymentBatches;

    @SerializedName("legacy_payments_batches")
    private LegacyPaymentBatch[] legacyPaymentsBatchBatches;


    public boolean isEmpty()
    {
        return neoPaymentBatches.length == 0 && legacyPaymentsBatchBatches.length == 0;
    }

}
