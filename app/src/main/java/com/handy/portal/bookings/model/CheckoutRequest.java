package com.handy.portal.bookings.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.core.constant.LocationKey;
import com.handy.portal.core.model.LocationData;

import java.util.List;

public class CheckoutRequest {
    //The server is expecting location data at the top level of the object, otherwise would just pass location data object
    @SerializedName("latitude")
    private String mLatitude;
    @SerializedName("longitude")
    private String mLongitude;
    @SerializedName("accuracy")
    private String mAccuracy;
    @SerializedName("note_to_customer")
    private String mNoteToCustomer;
    @SerializedName("booking_instructions")
    private List<Booking.BookingInstructionUpdateRequest> mCustomerPreferences;


    public CheckoutRequest(@Nullable LocationData locationData,
                           @Nullable String noteToCustomer,
                           @Nullable List<Booking.BookingInstructionUpdateRequest> customerPreferences) {
        if (locationData != null) {
            mLatitude = locationData.getLocationMap().get(LocationKey.LATITUDE);
            mLongitude = locationData.getLocationMap().get(LocationKey.LONGITUDE);
            mAccuracy = locationData.getLocationMap().get(LocationKey.ACCURACY);
        }
        mNoteToCustomer = noteToCustomer;
        mCustomerPreferences = customerPreferences;
    }

    public String getNoteToCustomer() {
        return mNoteToCustomer;
    }

    @Nullable
    public List<Booking.BookingInstructionUpdateRequest> getCustomerPreferences() {
        return mCustomerPreferences;
    }
}
