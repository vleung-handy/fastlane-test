package com.handy.portal.payments.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PaymentSupportItem implements Serializable
{
    /**
     * e.g. "There's an incorrect fee"
     */
    @SerializedName("display_name")
    private String mDisplayName;

    /**
     * @see MachineName
     */
    @SerializedName("machine_name")
    private String mMachineName;

    public static class MachineName
    {
        public static final String MISSING_DEPOSIT = "missing_deposit";
        public static final String INCORRECT_AMOUNT = "incorrect_amount";
        public static final String INCORRECT_FEE = "incorrect_fee";
        public static final String OTHER = "other";
    }

    /**
     * TODO temporarily existing for test only
     * @param displayName
     * @param machineName
     */
    public PaymentSupportItem(String displayName, String machineName)
    {
        mMachineName = machineName;
        mDisplayName = displayName;
    }
    public String getDisplayName()
    {
        return mDisplayName;
    }

    public String getMachineName()
    {
        return mMachineName;
    }
}
