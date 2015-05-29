package com.handy.portal.core;

import com.google.gson.annotations.SerializedName;

public final class BookingPriceInfo
{
    @SerializedName("hours")
    private float hours;
    @SerializedName("price")
    private float price;
    @SerializedName("discount_price")
    private float discountPrice;
    @SerializedName("bimonthly_recurring_price")
    private float biMonthlyprice;
    @SerializedName("discount_bimonthly_recurring_price")
    private float discountBiMonthlyprice;
    @SerializedName("monthly_recurring_price")
    private float monthlyPrice;
    @SerializedName("discount_monthly_recurring_price")
    private float discountMonthlyPrice;
    @SerializedName("weekly_recurring_price")
    private float weeklyPrice;
    @SerializedName("discount_weekly_recurring_price")
    private float discountWeeklyPrice;

    final float getHours()
    {
        return hours;
    }

    final float getPrice()
    {
        return price;
    }

    final float getBiMonthlyprice()
    {
        return biMonthlyprice;
    }

    final float getMonthlyPrice()
    {
        return monthlyPrice;
    }

    final float getWeeklyPrice()
    {
        return weeklyPrice;
    }

    final float getDiscountPrice()
    {
        return discountPrice;
    }

    final float getDiscountBiMonthlyprice()
    {
        return discountBiMonthlyprice;
    }

    final float getDiscountMonthlyPrice()
    {
        return discountMonthlyPrice;
    }

    final float getDiscountWeeklyPrice()
    {
        return discountWeeklyPrice;
    }
}
