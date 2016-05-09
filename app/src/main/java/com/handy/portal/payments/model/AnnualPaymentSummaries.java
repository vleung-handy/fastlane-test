package com.handy.portal.payments.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AnnualPaymentSummaries implements Serializable //unused for now
{
    @SerializedName("annual_summaries")
    private AnnualPaymentSummary annualPaymentSummaries[]; //assuming from most recent to least recent, with first entry being current year


    public static class AnnualPaymentSummary implements Serializable
    {
        @SerializedName("year")
        private int year;

        @SerializedName("completed_jobs")
        private int numCompletedJobs;

        @SerializedName("net_earnings")
        private PaymentInfo netEarnings;

        public int getYear()
        {
            return year;
        }

        public int getNumCompletedJobs()
        {
            return numCompletedJobs;
        }

        public PaymentInfo getNetEarnings()
        {
            return netEarnings;
        }
    }

    public AnnualPaymentSummary[] getAnnualPaymentSummaries()
    {
        return annualPaymentSummaries;
    }

    public boolean isEmpty()
    {
        return annualPaymentSummaries == null || annualPaymentSummaries.length == 0;
    }

    public AnnualPaymentSummary getMostRecentYearSummary()
    {
        return isEmpty() ? null : annualPaymentSummaries[0];
    }
}
