package com.handy.portal.payments.model;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.bookings.model.Booking;

import java.io.Serializable;

public class BookingTransactions implements Serializable {
    @SerializedName("booking")
    private Booking mBooking;
    @SerializedName("transactions")
    private Transaction[] mTransactions;
    @SerializedName("net_earnings")
    private int mNetEarnings;
    @SerializedName("currency_symbol")
    private String mCurrencySymbol;
    @SerializedName("payment_support_items")
    private PaymentSupportItem mPaymentSupportItems[];

    public PaymentSupportItem[] getPaymentSupportItems() {
        return mPaymentSupportItems;
    }

    public BookingTransactions() { }

    public BookingTransactions(final Booking booking, final Transaction[] transactions,
                               final int netEarnings, final String currencySymbol) {
        mBooking = booking;
        mTransactions = transactions;
        mNetEarnings = netEarnings;
        mCurrencySymbol = currencySymbol;
    }

    public Booking getBooking() {
        return mBooking;
    }

    public Transaction[] getTransactions() {
        return mTransactions;
    }

    public int getNetEarnings() {
        return mNetEarnings;
    }

    public String getCurrencySymbol() {
        return mCurrencySymbol;
    }
}
