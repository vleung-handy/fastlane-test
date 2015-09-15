package com.handy.portal.model.payments;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by vleung on 9/14/15.
 */
public class PaymentGroup implements Serializable
{
    public enum MachineName{
        completed_jobs, withholdings
    }

    @SerializedName("label")
    private String label;

    @SerializedName("machine_name")
    private String machineName;

    @SerializedName("amount")
    private Integer amount;

    @SerializedName("payments")
    private Payment[] payments;

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
        return getAmount()/100;
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
