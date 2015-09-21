package com.handy.portal.model.payments;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by vleung on 9/16/15.
 */
public class PaymentBatch implements Serializable
{
    public final Date getOldestDate()
    {
        if(this instanceof NeoPaymentBatch)
        {
            return ((NeoPaymentBatch) this).getStartDate();
        }
        else if(this instanceof LegacyPaymentBatch)
        {
            return ((LegacyPaymentBatch) this).getDate();
        }
        return null;
    }
}
