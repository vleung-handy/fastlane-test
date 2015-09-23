package com.handy.portal.util;

import java.text.DecimalFormat;

public class CurrencyUtils
{
    private static final String DEFAULT_CURRENCY_SYMBOL = "$";

    public static String formatPrice(final int price, final String currencyChar)
    {
        String priceText = new DecimalFormat("#,##0").format(Math.abs(price));
        return (price < 0 ? "-" : "") + (currencyChar != null ? currencyChar : DEFAULT_CURRENCY_SYMBOL) + priceText;
    }

    public static String formatPriceWithCents(final int priceCents, final String currencyChar)
    {
        int absolutePriceCents = Math.abs(priceCents);
        int dollarValue = absolutePriceCents / 100;
        double centsValue = absolutePriceCents % 100;

        String dollarText = formatPrice(dollarValue, currencyChar);
        String centsText = formatCents(centsValue);

        return dollarText + centsText;
    }

    public static String formatCents(double cents)
    {
        return new DecimalFormat(".00").format(cents * 0.01);
    }

    public static int centsToDollars(int cents)
    {
        //truncate the cents for display purposes
        return cents/100;
    }
}
