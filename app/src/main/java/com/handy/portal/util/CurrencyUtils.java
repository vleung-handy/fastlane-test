package com.handy.portal.util;

public class CurrencyUtils
{
    private static final String DEFAULT_CURRENCY_SYMBOL = "$";

    public static String formatPrice(final int price, final String currencyChar)
    {
        return (price < 0 ? "-" : "") + (currencyChar != null ? currencyChar : DEFAULT_CURRENCY_SYMBOL) + Math.abs(price);
    }

    public static int centsToDollars(int cents)
    {
        //truncate the cents for display purposes
        return cents/100;
    }
}
