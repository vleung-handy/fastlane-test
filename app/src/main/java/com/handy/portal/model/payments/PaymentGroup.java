package com.handy.portal.model.payments;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.util.CurrencyUtils;

import java.io.Serializable;

public class PaymentGroup implements Serializable
{
    @SerializedName("label")
    private String label;

    @SerializedName("machine_name")
    private String machineName;

    @SerializedName("amount")
    private Integer amount;

    @SerializedName("payments")
    private Payment[] payments;

    public enum MachineName{
        completed_jobs, withholdings
    }

    public String getLabel()
    {
        return label;
    }

    public String getMachineName()
    {
        return machineName;
    }

    public Integer getDollarAmount()
    {
        return CurrencyUtils.centsToDollars(getAmount());
    }
    public Integer getAmount()
    {
        return amount;
    }

    public Payment[] getPayments()
    {
        return payments;
    }
}
