package com.handy.portal.library.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CurrencyUtilsTest {
    private static final String TEST_CURRENCY_SYMBOL = "$";

    @Test
    public void formatPriceWithEmptyDecimals() {
        int priceCents = 1200;
        String priceString = CurrencyUtils.formatPrice(priceCents, TEST_CURRENCY_SYMBOL, true);
        assertEquals("$12.00", priceString);

        priceCents = -1200;
        priceString = CurrencyUtils.formatPrice(priceCents, TEST_CURRENCY_SYMBOL, true);
        assertEquals("-$12.00", priceString);

        priceCents = 0;
        priceString = CurrencyUtils.formatPrice(priceCents, TEST_CURRENCY_SYMBOL, true);
        assertEquals("$0.00", priceString);
    }

    @Test
    public void formatPriceWithoutEmptyDecimals()
    {
        int priceCents = 1200;
        String priceString = CurrencyUtils.formatPrice(priceCents, TEST_CURRENCY_SYMBOL, false);
        assertEquals("$12", priceString);

        priceCents = -1250;
        priceString = CurrencyUtils.formatPrice(priceCents, TEST_CURRENCY_SYMBOL, false);
        assertEquals("-$12.50", priceString);

        priceCents = 0;
        priceString = CurrencyUtils.formatPrice(priceCents, TEST_CURRENCY_SYMBOL, false);
        assertEquals("$0", priceString);
    }


}
