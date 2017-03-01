package com.handy.portal.payments.model;

import com.google.gson.annotations.SerializedName;

/**
 * a payment review request for a booking payment transaction
 */
public class BookingPaymentReviewRequest {
    /**
     * the id of the booking payment transaction to review
     */
    @SerializedName("booking_id")
    private String mBookingId;

    @SerializedName("booking_type")
    private String mBookingType;

    @SerializedName("machine_name")
    private String mPaymentSupportItemMachineName;

    /**
     * text inputted by the user when the “other” option is selected
     * (currently unsupported, so just sending null for now)
     */
    @SerializedName("other_info")
    private String mOtherInfo;

    public BookingPaymentReviewRequest(final String bookingId,
                                       final String bookingType,
                                       final String paymentSupportItemMachineName,
                                       final String otherInfo
    ) {
        mBookingId = bookingId;
        mBookingType = bookingType;
        mPaymentSupportItemMachineName = paymentSupportItemMachineName;
        mOtherInfo = otherInfo;
    }

}
