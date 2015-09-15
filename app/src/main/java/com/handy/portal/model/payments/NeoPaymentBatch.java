package com.handy.portal.model.payments;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class NeoPaymentBatch extends PaymentBatch
{
    /**
     * assuming all amounts are passed in as cents
     * will parse to dollar amounts
     */
    @SerializedName("batch_id")
    private int batchId;

    @SerializedName("start_date")
    private Date startDate;

    @SerializedName("end_date")
    private Date endDate;

    @SerializedName("status")
    private String status;

    /**protocol for paymentinfo will be changed to use PaymentInfo object**/
    @SerializedName("currency_symbol")
    private String currencySymbol;


    public int getWithholdings()
    {
        return withholdings;
    }

    @SerializedName("completed_jobs")
    private int completedJobs;

    @SerializedName("withholdings")
    private int withholdings;

    @SerializedName("total_amount")
    private int totalAmount;


    @SerializedName("earnings_total_amount")
    private int earningsTotalAmount;

    @SerializedName("withholdings_total_amount")
    private int withholdingsTotalAmount;

    @SerializedName("remaining_withholding_amount")
    private int remainingWithholdingAmount;

    @SerializedName("payment_groups")
    private PaymentGroup paymentGroups[];

    public int getEarningsTotalAmount()
    {
        return earningsTotalAmount;
    }

    public int getWithholdingsTotalAmount()
    {
        return withholdingsTotalAmount;
    }
    public int getCompletedJobs()
    {
        return completedJobs;
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

    public int getTotalAmount()
    {
        return totalAmount;
    }

    public int getRemainingWithholdingDollarAmount()
    {
        return remainingWithholdingAmount/100;
    }
    public int getRemainingWithholdingAmount()
    {
        return remainingWithholdingAmount;
    }

    public PaymentGroup[] getPaymentGroups()
    {
        return paymentGroups;
    }



    public int getTotalAmountDollars()
    {
        return totalAmount /100;
    }
}
