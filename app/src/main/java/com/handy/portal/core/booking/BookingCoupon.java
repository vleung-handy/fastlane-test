package com.handy.portal.core.booking;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.handy.portal.core.BookingPriceInfo;

import java.util.ArrayList;

public final class BookingCoupon {
    @SerializedName("coupon_id") private int id;
    @SerializedName("price_table") private ArrayList<BookingPriceInfo> priceTable;

    final int getId() {
        return id;
    }

    public final ArrayList<BookingPriceInfo> getPriceTable() {
        return priceTable;
    }

    public static BookingCoupon fromJson(final String json) {
        return new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create()
                .fromJson(json, BookingCoupon.class);
    }
}
