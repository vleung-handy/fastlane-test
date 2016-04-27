package com.handy.portal.util;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class CurrencyUtils
{
    private static final String DEFAULT_CURRENCY_SYMBOL = "$";

    public static String formatPrice(final double price, final String currencyChar)
    {
        String currencySymbol = currencyChar != null ? currencyChar : DEFAULT_CURRENCY_SYMBOL;
        String sign = price < 0 ? "-" : "";
        final DecimalFormat decimalFormat = new DecimalFormat("#,##0");
        decimalFormat.setRoundingMode(RoundingMode.DOWN);
        return sign + currencySymbol + decimalFormat.format(Math.abs(price));
    }

    public static String formatPriceWithCents(final int priceCents, final String currencyChar)
    {
        String dollarText = formatPrice(priceCents * 0.01, currencyChar);
        String centsText = formatCents(priceCents);

        return dollarText + centsText;
    }

    public static String formatCents(final int price)
    {
        int centsValue = Math.abs(price) % 100;
        return new DecimalFormat(".00").format(centsValue * 0.01);
    }

    public static String formatPriceWithoutCents(final int priceCents, final String currencyChar)
    {
        return formatPrice(priceCents * 0.01, currencyChar);
    }
}
