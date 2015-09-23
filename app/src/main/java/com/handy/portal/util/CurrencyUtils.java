package com.handy.portal.util;

import java.text.DecimalFormat;

public class CurrencyUtils
{
    private static final String DEFAULT_CURRENCY_SYMBOL = "$";

    public static String formatPrice(final int price, final String currencyChar)
    {
        return (price < 0 ? "-" : "") + (currencyChar != null ? currencyChar : DEFAULT_CURRENCY_SYMBOL) + Math.abs(price);
    }

    public static String formatPriceWithCents(final int priceCents, final String currencyChar)
    {
        int absolutePriceCents = Math.abs(priceCents);
        int dollarValue = absolutePriceCents / 100;
        double centsValue = (absolutePriceCents % 100) * 0.01;

        String dollarText = (currencyChar != null ? currencyChar : DEFAULT_CURRENCY_SYMBOL) + dollarValue;
        String centsText = new DecimalFormat(".00").format(centsValue);

        return (priceCents < 0 ? "-" : "") + dollarText + centsText;
    }

    public static int centsToDollars(int cents)
    {
        //truncate the cents for display purposes
        return cents/100;
    }
}
