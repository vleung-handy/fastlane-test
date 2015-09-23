package com.handy.portal.model.payments;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.util.CurrencyUtils;

import java.util.Date;

public class NeoPaymentBatch extends PaymentBatch
{
    /**
     * assuming all amounts are passed in as cents
     */
    @SerializedName("batch_id")
    private int batchId;

    @SerializedName("start_date")
    private Date startDate;

    @SerializedName("end_date")
    private Date endDate;

    @SerializedName("status")
    private String status;

    @SerializedName("currency_symbol")
    private String currencySymbol;

    @SerializedName("num_completed_jobs")
    private int numCompletedJobs;

    @SerializedName("num_withholdings")
    private int numWithholdings;

    @SerializedName("net_earnings_total_amount")
    private int netEarningsTotalAmount;


    @SerializedName("gross_earnings_total_amount")
    private int grossEarningsTotalAmount;

    @SerializedName("withholdings_total_amount")
    private int withholdingsTotalAmount;

    @SerializedName("remaining_withholding_amount")
    private int remainingWithholdingAmount;

    @SerializedName("payment_groups")
    private PaymentGroup paymentGroups[];

    public enum Status{
        FAILED("FAILED"),
        PENDING("PENDING"),
        PAID("PAID");

        private final String name;
        private Status(final String name)
        {
            this.name = name;
        }
        @Override
        public String toString()
        {
            return this.name;
        }
    }

    public int getNumWithholdings()
    {
        return numWithholdings;
    }

    public int getGrossEarningsTotalAmount()
    {
        return grossEarningsTotalAmount;
    }

    public int getWithholdingsTotalAmount()
    {
        return withholdingsTotalAmount;
    }

    public int getNumCompletedJobs()
    {
        return numCompletedJobs;
    }

    public int getBatchId()
    {
        return batchId;
    }

    public Date getStartDate()
    {
        return startDate;
    }

    public Date getEndDate()
    {
        return endDate;
    }

    public String getStatus()
    {
        return status;
    }

    public String getCurrencySymbol()
    {
        return currencySymbol;
    }

    public int getNetEarningsTotalAmount()
    {
        return netEarningsTotalAmount;
    }

    public int getRemainingWithholdingDollarAmount()
    {
        return CurrencyUtils.centsToDollars(remainingWithholdingAmount);
    }

    public int getRemainingWithholdingAmount()
    {
        return remainingWithholdingAmount;
    }

    public PaymentGroup[] getPaymentGroups()
    {
        return paymentGroups;
    }

    public void setPaymentGroups(PaymentGroup[] paymentGroups) //setter is needed for filtering the payment groups to remove empty groups (later, server will remove them)
    {
        this.paymentGroups = paymentGroups;
    }

    public int getTotalAmountDollars()
    {
        return CurrencyUtils.centsToDollars(netEarningsTotalAmount);
    }
}
