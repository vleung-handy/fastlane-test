package com.handy.portal.model.payments;

import com.google.common.collect.ObjectArrays;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PaymentBatches implements Serializable
{
    @SerializedName("payments_batches")
    private NeoPaymentBatch[] neoPaymentBatches;

    @SerializedName("legacy_payments_batches")
    private LegacyPaymentBatch[] legacyPaymentsBatchBatches;

    public NeoPaymentBatch[] getNeoPaymentBatches()
    {
        return neoPaymentBatches;
    }

    public LegacyPaymentBatch[] getLegacyPaymentsBatchBatches()
    {
        return legacyPaymentsBatchBatches;
    }

    public boolean isEmpty()
    {
        return neoPaymentBatches.length == 0 && legacyPaymentsBatchBatches.length == 0;
    }

    public PaymentBatch[] getAggregateBatchList()
    {
        //some defense against null batch lists
        if (getNeoPaymentBatches() == null
                && getLegacyPaymentsBatchBatches() == null)
        {
            return new PaymentBatch[]{};
        }
        else if (getNeoPaymentBatches() == null)
        {
            return getLegacyPaymentsBatchBatches();
        }
        else if (getLegacyPaymentsBatchBatches() == null)
        {
            return getNeoPaymentBatches();
        }
        else
        {
            return ObjectArrays.concat(getNeoPaymentBatches(), getLegacyPaymentsBatchBatches(), PaymentBatch.class);
        }

    }

    //TODO: Remove after testing
    public void clearNeoPayments()
    {
        neoPaymentBatches = new NeoPaymentBatch[]{};
    }

    public void clearLegacyPayments()
    {
        legacyPaymentsBatchBatches = new LegacyPaymentBatch[]{};
    }

}
