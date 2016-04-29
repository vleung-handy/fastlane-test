package com.handy.portal.payments.model;

import com.google.gson.annotations.SerializedName;

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

    @SerializedName("expected_deposit_date")
    private Date expectedDepositDate;

    @SerializedName("status")
    private String status;

    @SerializedName("currency_symbol")
    private String currencySymbol;

    @SerializedName("num_completed_jobs")
    private int numCompletedJobs;

    @SerializedName("num_withholdings")
    private int numFees;

    @SerializedName("net_earnings_total_amount")
    private int netEarningsTotalAmount;


    @SerializedName("gross_earnings_total_amount")
    private int grossEarningsTotalAmount;

    @SerializedName("withholdings_total_amount")
    private int feesTotalAmount;

    @SerializedName("remaining_withholding_amount")
    private int remainingFeeAmount;

    @SerializedName("payment_groups")
    private PaymentGroup paymentGroups[];

    public enum Status{
        FAILED("Failed"),
        PENDING("Pending"),
        IN_REVIEW("In Review"),
        PAID("Paid");

        private final String name;
        Status(final String name)
        {
            this.name = name;
        }
        @Override
        public String toString()
        {
            return this.name;
        }
    }

    public int getNumFees()
    {
        return numFees;
    }

    public int getGrossEarningsTotalAmount()
    {
        return grossEarningsTotalAmount;
    }

    public int getFeesTotalAmount()
    {
        return feesTotalAmount;
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

    public Date getExpectedDepositDate()
    {
        return expectedDepositDate;
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

    public int getRemainingFeeAmount()
    {
        return remainingFeeAmount;
    }

    public PaymentGroup[] getPaymentGroups()
    {
        return paymentGroups;
    }

    public void setPaymentGroups(PaymentGroup[] paymentGroups) //setter is needed for filtering the payment groups to remove empty groups (later, server will remove them)
    {
        this.paymentGroups = paymentGroups;
    }

    // Used to make calling of the date for both NeoPaymentBatch and LegacyPaymentBatch objects
    // easier.
    @Override
    public Date getEffectiveDate()
    {
        return getEndDate();
    }
}
