package com.handy.portal.payments.model;

import com.google.gson.annotations.SerializedName;

/**
 * a payment review request for a booking payment transaction
 */
public class BookingPaymentTransactionReviewRequest
{
    /**
     * the id of the booking payment transaction to review
     */
    @SerializedName("booking_id")
    private String mBookingId;

    @SerializedName("booking_type")
    private String mBookingType;

    @SerializedName("machine_name")
    private String mPaymentSupportItemMachineName;

    @SerializedName("other_info")
    private String mOtherInfo;

    public BookingPaymentTransactionReviewRequest(final String bookingId,
                                                  final String bookingType,
                                                  final String paymentSupportItemMachineName,
                                                  final String otherInfo
    )
    {
        mBookingId = bookingId;
        mBookingType = bookingType;
        mPaymentSupportItemMachineName = paymentSupportItemMachineName;
        mOtherInfo = otherInfo;
    }

}
