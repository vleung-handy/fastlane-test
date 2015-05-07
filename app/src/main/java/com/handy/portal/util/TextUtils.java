package com.handy.portal.util;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.widget.TextView;

import com.handy.portal.core.CreditCard;

import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

public final class TextUtils
{
    private static final Hashtable<String, Typeface> cache = new Hashtable<>();

    public static Typeface get(final Context c, final String name)
    {
        synchronized (cache)
        {
            if (!cache.containsKey(name))
            {
                final Typeface t = Typeface.createFromAsset(c.getAssets(),
                        String.format("fonts/%s", name));
                cache.put(name, t);
            }
            return cache.get(name);
        }
    }

    public static String formatPrice(final float price, final String currencyChar,
                                     final String currencySuffix)
    {
        final DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return (currencyChar != null ? currencyChar : "$")
                + decimalFormat.format(price)
                + (currencySuffix != null ? currencySuffix : "");
    }

    public static String formatPhone(String phone, final String prefix)
    {
        String shortFormat = "(%s) %s", longFormat = "(%s) %s-%s";
        if (prefix != null && prefix.equals("+44"))
        {
            shortFormat = "%s %s";
            longFormat = "%s %s %s";
        }

        phone = phone.replaceAll("\\D+", "");

        if (phone.length() < 4) return phone;

        else if (phone.length() >= 4 && phone.length() <= 6)
            return String.format(shortFormat, phone.substring(0, 3), phone.substring(3));

        else if (phone.length() >= 7 && phone.length() <= 10)
            return String.format(longFormat, phone.substring(0, 3), phone.substring(3, 6),
                    phone.substring(6));

        else return phone;
    }

    public static String formatAddress(final String address1, final String address2, final String city,
                                       final String state, final String zip)
    {
        return address1 + (address2 != null && address2.length() > 0 ? ", "
                + address2 + "\n" : "\n") + city + ", "
                + state + " " + (zip != null ? zip.replace(" ", "\u00A0") : null);
    }

    public static String formatDate(final Date date, final String format)
    {
        if (date == null) return null;

        final SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        final DateFormatSymbols symbols = new DateFormatSymbols();
        symbols.setAmPmStrings(new String[]{"am", "pm"});
        dateFormat.setDateFormatSymbols(symbols);
        return dateFormat.format(date);
    }

    public static String formatDecimal(final float value, final String format)
    {
        final DecimalFormat decimalFormat = new DecimalFormat(format);
        return decimalFormat.format(value);
    }

    public static String formatCreditCardNumber(final CreditCard.Type cardType, final String number)
    {
        if (number == null || number.length() < 1) return number;

        final String raw = number.replaceAll("\\D+", "");

        if (cardType == CreditCard.Type.AMEX)
        {
            if (raw.length() >= 5 && raw.length() <= 10) return String.format("%s %s",
                    raw.substring(0, 4), raw.substring(4));

            if (raw.length() >= 11) return String.format("%s %s %s",
                    raw.substring(0, 4), raw.substring(4, 10), raw.substring(10));

            return raw;
        }

        if (raw.length() >= 5 && raw.length() <= 8) return String.format("%s %s",
                raw.substring(0, 4), raw.substring(4));

        if (raw.length() >= 9 && raw.length() <= 12) return String.format("%s %s %s",
                raw.substring(0, 4), raw.substring(4, 8), raw.substring(8));

        if (raw.length() >= 13) return String.format("%s %s %s %s",
                raw.substring(0, 4), raw.substring(4, 8), raw.substring(8, 12), raw.substring(12));

        return raw;
    }

    public static String formatCreditCardExpDate(final String number)
    {
        if (number == null || number.length() < 1) return number;

        final String raw = number.replaceAll("\\D+", "");

        if (raw.length() >= 3) return String.format("%s/%s",
                raw.substring(0, 2), raw.substring(2));

        return raw;
    }

    public static String toTitleCase(final String str)
    {
        if (str == null) return null;

        boolean space = true;

        StringBuilder builder = new StringBuilder(str);
        final int len = builder.length();

        for (int i = 0; i < len; ++i)
        {
            char c = builder.charAt(i);
            if (space)
            {
                if (!Character.isWhitespace(c))
                {
                    // Convert to title case and switch out of whitespace mode.
                    builder.setCharAt(i, Character.toTitleCase(c));
                    space = false;
                }
            } else if (Character.isWhitespace(c))
            {
                space = true;
            } else
            {
                builder.setCharAt(i, Character.toLowerCase(c));
            }
        }

        return builder.toString();
    }

    public static CharSequence trim(final CharSequence s)
    {
        int start = 0;
        int end = s.length();

        while (start < end && Character.isWhitespace(s.charAt(start))) start++;
        while (end > start && Character.isWhitespace(s.charAt(end - 1))) end--;

        return s.subSequence(start, end);
    }

    public static void stripUnderlines(final TextView textView)
    {
        final Spannable s = new SpannableString(textView.getText());
        final URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);

        for (URLSpan span : spans)
        {
            final int start = s.getSpanStart(span);
            final int end = s.getSpanEnd(span);
            s.removeSpan(span);
            span = new URLSpanNoUnderline(span.getURL());
            s.setSpan(span, start, end, 0);
        }
        textView.setText(s);
    }

    private static final class URLSpanNoUnderline extends URLSpan
    {
        URLSpanNoUnderline(String url)
        {
            super(url);
        }

        @Override
        public final void updateDrawState(final TextPaint ds)
        {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }
    }
}
