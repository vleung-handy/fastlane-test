package com.handy.portal.payments.model;

public class DebitCardInfo {
    private String mCardNumber;
    private String mExpMonth;
    private String mExpYear;
    private String mCvc;
    private String mCurrencyCode;

    public String getCardNumber() {
        return mCardNumber;
    }

    public void setCardNumber(String cardNumber) {
        mCardNumber = cardNumber;
    }

    public String getExpMonth() {
        return mExpMonth;
    }

    public void setExpMonth(String expMonth) {
        mExpMonth = expMonth;
    }

    public String getExpYear() {
        return mExpYear;
    }

    public void setExpYear(String expYear) {
        mExpYear = expYear;
    }

    public String getCvc() {
        return mCvc;
    }

    public void setCvc(String cvc) {
        mCvc = cvc;
    }

    public String getCurrencyCode() {
        return mCurrencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        mCurrencyCode = currencyCode;
    }
}
