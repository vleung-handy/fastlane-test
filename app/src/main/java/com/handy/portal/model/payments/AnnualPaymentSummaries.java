package com.handy.portal.model.payments;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.model.PaymentInfo;

import java.io.Serializable;

public class AnnualPaymentSummaries implements Serializable //unused for now
{
    @SerializedName("annual_summaries")
    private AnnualPaymentSummary annualPaymentSummaries[];

    public static class AnnualPaymentSummary implements Serializable{
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
}
