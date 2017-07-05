package com.handy.portal.library.util;

import android.support.annotation.NonNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class CurrencyUtils {
    private static final String DEFAULT_CURRENCY_SYMBOL = "$";

    @Deprecated
    public static String formatPrice(final double price, final String currencyChar) {
        String currencySymbol = currencyChar != null ? currencyChar : DEFAULT_CURRENCY_SYMBOL;
        String sign = price < 0 ? "-" : "";
        final DecimalFormat decimalFormat = new DecimalFormat("#,##0");
        decimalFormat.setRoundingMode(RoundingMode.DOWN);
        return sign + currencySymbol + decimalFormat.format(Math.abs(price));
    }

    public static String formatPrice(final int priceCents,
                                     @NonNull final String currencySymbol,
                                     boolean shouldDisplayEmptyDecimals) {
        DecimalFormat decimalFormat;
        if (!shouldDisplayEmptyDecimals && priceCents % 100 == 0) {
            decimalFormat = new DecimalFormat();
            decimalFormat.setMaximumFractionDigits(0);
        }
        else {
            decimalFormat = new DecimalFormat("0.00");
        }
        decimalFormat.setPositivePrefix(currencySymbol);
        decimalFormat.setNegativePrefix("-" + currencySymbol);
        return decimalFormat.format(new BigDecimal(priceCents).movePointLeft(2));
    }

    @Deprecated
    public static String formatPriceWithCents(final int priceCents, final String currencyChar) {
        String dollarText = formatPrice(priceCents * 0.01, currencyChar);
        String centsText = formatCents(priceCents);

        return dollarText + centsText;
    }

    @Deprecated
    public static String formatCents(final int price) {
        int centsValue = Math.abs(price) % 100;
        return new DecimalFormat(".00").format(centsValue * 0.01);
    }

    @Deprecated
    public static String formatPriceWithoutCents(final int priceCents, final String currencyChar) {
        return formatPrice(priceCents * 0.01, currencyChar);
    }
}
