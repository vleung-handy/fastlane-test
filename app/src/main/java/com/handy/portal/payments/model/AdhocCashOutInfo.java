package com.handy.portal.payments.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

public class AdhocCashOutInfo implements Serializable {
    @SerializedName("date_start")
    private Date mDateStart;
    @SerializedName("date_end")
    private Date mDateEnd;
    @SerializedName("net_earnings")
    private Integer mNetEarningsCents;
    @SerializedName("cash_out_fee")
    private Integer mCashOutFeeCents;
    @SerializedName("expected_payment")
    private Integer mExpectedPaymentCents;
    @SerializedName("currency")
    private String mCurrencyCode;
    @SerializedName("currency_symbol")
    private String mCurrencySymbol;
    @SerializedName("payment_method_info")
    private PaymentMethodInfo mPaymentMethodInfo;
    @SerializedName("help_center_article_url")
    private String mHelpCenterArticleUrl;

    //success
    @SerializedName("success")
    private Boolean mSuccess;
    @SerializedName("message")
    private String mErrorMessage;

    public String getHelpCenterArticleUrl() {
        return mHelpCenterArticleUrl;
    }

    public Boolean getSuccess() {
        return mSuccess;
    }

    public String getErrorMessage() {
        return mErrorMessage;
    }

    @Nullable
    public PaymentMethodInfo getPaymentMethodInfo() {
        return mPaymentMethodInfo;
    }

    public Date getDateStart() {
        return mDateStart;
    }

    public Date getDateEnd() {
        return mDateEnd;
    }

    public Integer getNetEarningsCents() {
        return mNetEarningsCents;
    }

    public Integer getCashOutFeeCents() {
        return mCashOutFeeCents;
    }

    public Integer getExpectedPaymentCents() {
        return mExpectedPaymentCents;
    }

    public String getCurrencyCode() {
        return mCurrencyCode;
    }

    public String getCurrencySymbol() {
        return mCurrencySymbol;
    }

    public static class PaymentMethodInfo implements Serializable {
        @SerializedName("last_four")
        private String mLast4Digits;

        @SerializedName("type")
        private String mType;

        public String getLast4Digits() {
            return mLast4Digits;
        }

        @Nullable
        public PaymentMethodType getType() {
            return PaymentMethodType.fromString(mType);
        }
    }

}
