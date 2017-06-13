package com.handy.portal.payments.model;

import android.support.annotation.Nullable;

import com.crashlytics.android.Crashlytics;

public enum PaymentMethodType {
    DIRECTDEPOSIT,
    DEBITCARD,;

    @Nullable
    public static PaymentMethodType fromString(String value)
    {
        try
        {
            return PaymentMethodType.valueOf(value);
        }
        catch (IllegalArgumentException | NullPointerException e)
        {
            Crashlytics.logException(new Exception("Unrecognized payment method type " + value));
        }
        return null;
    }
}
