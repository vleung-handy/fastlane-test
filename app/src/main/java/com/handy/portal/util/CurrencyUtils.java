package com.handy.portal.util;

/**
 * Created by vleung on 9/21/15.
 */
public class CurrencyUtils
{
    private static final String DEFAULT_CURRENCY_SYMBOL = "$";

    public static String formatPrice(final int price, final String currencyChar)
    {
        return (price < 0 ? "-" : "") + (currencyChar != null ? currencyChar : DEFAULT_CURRENCY_SYMBOL) + Math.abs(price);
    }

    public static int centsToDollars(int cents)
    {
        return cents/100;
    }
}
