package com.handy.portal.payments.model;

import java.io.Serializable;
import java.util.Date;

public abstract class PaymentBatch implements Serializable {
    public abstract Date getEffectiveDate();
}
