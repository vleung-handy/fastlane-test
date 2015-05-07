package com.handy.portal.core;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.SerializedName;
import com.handy.portal.core.booking.BookingOption;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

public final class BookingQuote extends Observable
{
    @SerializedName("id")
    private int bookingId;
    @SerializedName("service_id")
    private int serviceId;
    @SerializedName("user_id")
    private String userId;
    @SerializedName("hrs")
    private float hours;
    @SerializedName("date_start")
    private Date startDate;
    @SerializedName("zipcode")
    private String zipCode;
    @SerializedName("currency_char")
    private String currencyChar;
    @SerializedName("currency_suffix")
    private String currencySuffix;
    @SerializedName("hourly_amount")
    private float hourlyAmount;
    @SerializedName("price_table")
    private ArrayList<BookingPriceInfo> priceTable;
    @SerializedName("dynamic_options")
    private ArrayList<PeakPriceInfo> surgePriceTable;
    @SerializedName("stripe_key")
    private String stripeKey;
    @SerializedName("phone_country_prefix")
    private String phonePrefix;
    @SerializedName("special_extras_options")
    private BookingOption extrasOptions;

    private HashMap<Float, BookingPriceInfo> priceTableMap;
    private ArrayList<ArrayList<PeakPriceInfo>> peakPriceTable;

    public final int getBookingId()
    {
        return bookingId;
    }

    final void setBookingId(final int bookingId)
    {
        this.bookingId = bookingId;
        triggerObservers();
    }

    public final int getServiceId()
    {
        return serviceId;
    }

    final void setServiceId(final int serviceId)
    {
        this.serviceId = serviceId;
        triggerObservers();
    }

    public final String getUserId()
    {
        return userId;
    }

    final void setUserId(final String userId)
    {
        this.userId = userId;
        triggerObservers();
    }

    public float getHours()
    {
        return hours;
    }

    public void setHours(float hours)
    {
        this.hours = hours;
        triggerObservers();
    }

    public final Date getStartDate()
    {
        return startDate;
    }

    public final void setStartDate(final Date startDate)
    {
        this.startDate = startDate;
        triggerObservers();
    }

    public final String getZipCode()
    {
        return zipCode;
    }

    final void setZipCode(final String zipCode)
    {
        this.zipCode = zipCode;
        triggerObservers();
    }

    public final String getCurrencyChar()
    {
        return currencyChar;
    }

    final void setCurrencyChar(final String currencyChar)
    {
        this.currencyChar = currencyChar;
        triggerObservers();
    }

    public final String getCurrencySuffix()
    {
        return currencySuffix;
    }

    final void setCurrencySuffix(final String currencySuffix)
    {
        this.currencySuffix = currencySuffix;
        triggerObservers();
    }

    public final float getHourlyAmount()
    {
        return hourlyAmount;
    }

    final void setHourlyAmount(final float hourlyAmount)
    {
        this.hourlyAmount = hourlyAmount;
    }

    public final ArrayList<BookingPriceInfo> getPriceTable()
    {
        return priceTable;
    }

    public final void setPriceTable(final ArrayList<BookingPriceInfo> priceTable)
    {
        this.priceTable = priceTable;
        buildPriceMap();
        triggerObservers();
    }

    final HashMap<Float, BookingPriceInfo> getPriceTableMap()
    {
        if (priceTableMap == null || priceTable.isEmpty()) buildPriceMap();
        return priceTableMap;
    }

    public final ArrayList<PeakPriceInfo> getSurgePriceTable()
    {
        return surgePriceTable;
    }

    public final void setSurgePriceTable(final ArrayList<PeakPriceInfo> surgePriceTable)
    {
        this.surgePriceTable = surgePriceTable;
        buildPeakPriceTable();
        triggerObservers();
    }

    public final ArrayList<ArrayList<PeakPriceInfo>> getPeakPriceTable()
    {
        if (peakPriceTable == null || peakPriceTable.isEmpty()) buildPeakPriceTable();
        return peakPriceTable;
    }

    final boolean hasRecurring()
    {
        final BookingPriceInfo info = this.priceTable.get(0);
        return !(info.getBiMonthlyprice() <= 0 && info.getMonthlyPrice() <= 0
                && info.getWeeklyPrice() <= 0);
    }

    public final float[] getPricing(final float hours, final int freq)
    {
        final BookingPriceInfo info = this.getPriceTableMap().get(hours);

        switch (freq)
        {
            case 1:
                return new float[]{info.getWeeklyPrice(), info.getDiscountWeeklyPrice()};

            case 2:
                return new float[]{info.getBiMonthlyprice(), info.getDiscountBiMonthlyprice()};

            case 4:
                return new float[]{info.getMonthlyPrice(), info.getDiscountMonthlyPrice()};

            default:
                return new float[]{info.getPrice(), info.getDiscountPrice()};
        }
    }

    public final String getPhonePrefix()
    {
        return phonePrefix;
    }

    final void setPhonePrefix(final String phonePrefix)
    {
        this.phonePrefix = phonePrefix;
    }

    public final String getStripeKey()
    {
        return stripeKey;
    }

    final void setStripeKey(final String stripeKey)
    {
        this.stripeKey = stripeKey;
        triggerObservers();
    }

    public final BookingOption getExtrasOptions()
    {
        return extrasOptions;
    }

    public final void setExtrasOptions(final BookingOption extrasOptions)
    {
        this.extrasOptions = extrasOptions;
        triggerObservers();
    }

    private void triggerObservers()
    {
        setChanged();
        notifyObservers();
    }

    private void buildPriceMap()
    {
        priceTableMap = new HashMap<>();

        if (this.priceTable == null) return;

        for (final BookingPriceInfo info : this.priceTable)
            priceTableMap.put(info.getHours(), info);
    }

    private void buildPeakPriceTable()
    {
        if (this.surgePriceTable == null) return;

        final HashMap<Date, ArrayList<PeakPriceInfo>> peakPriceMap = new HashMap<>();

        for (final PeakPriceInfo info : this.surgePriceTable)
        {
            final Calendar dateCal = Calendar.getInstance();
            dateCal.setTime(info.getDate());

            final Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_MONTH, dateCal.get(Calendar.DAY_OF_MONTH));
            cal.set(Calendar.MONTH, dateCal.get(Calendar.MONTH));
            cal.set(Calendar.YEAR, dateCal.get(Calendar.YEAR));
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            final Date date = cal.getTime();
            ArrayList<PeakPriceInfo> priceList;

            if ((priceList = peakPriceMap.get(date)) != null)
            {
                priceList.add(info);
            } else
            {
                priceList = new ArrayList<>();
                priceList.add(info);
                peakPriceMap.put(date, priceList);
            }
        }

        final ArrayList<ArrayList<PeakPriceInfo>> table = new ArrayList<>();
        final ArrayList<Date> keys = new ArrayList<>(peakPriceMap.keySet());

        Collections.sort(keys, new Comparator<Date>()
        {
            @Override
            public int compare(final Date lhs, final Date rhs)
            {
                return (int) (lhs.getTime() - rhs.getTime());
            }
        });

        for (final Date d : keys)
        {
            final ArrayList<PeakPriceInfo> list = peakPriceMap.get(d);
            table.add(list);
        }
        peakPriceTable = table;
    }

    final String toJson()
    {
        final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .setExclusionStrategies(getExclusionStrategy())
                .registerTypeAdapter(BookingQuote.class, new BookingQuoteSerializer()).create();

        return gson.toJson(this);
    }

    public static BookingQuote fromJson(final String json)
    {
        return new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create()
                .fromJson(json, BookingQuote.class);
    }

    static ExclusionStrategy getExclusionStrategy()
    {
        return new ExclusionStrategy()
        {
            @Override
            public boolean shouldSkipField(final FieldAttributes f)
            {
                return false;
            }

            @Override
            public boolean shouldSkipClass(final Class<?> clazz)
            {
                return clazz.equals(Observer.class);
            }
        };
    }

    static final class BookingQuoteSerializer implements JsonSerializer<BookingQuote>
    {
        @Override
        public final JsonElement serialize(final BookingQuote value, final Type type,
                                           final JsonSerializationContext context)
        {
            final JsonObject jsonObj = new JsonObject();
            jsonObj.add("id", context.serialize(value.getBookingId()));
            jsonObj.add("service_id", context.serialize(value.getServiceId()));
            jsonObj.add("user_id", context.serialize(value.getUserId()));
            jsonObj.add("hrs", context.serialize(value.getHours()));
            jsonObj.add("date_start", context.serialize(value.getStartDate()));
            jsonObj.add("zipcode", context.serialize(value.getZipCode()));
            jsonObj.add("currency_char", context.serialize(value.getCurrencyChar()));
            jsonObj.add("currency_suffix", context.serialize(value.getCurrencySuffix()));
            jsonObj.add("phone_country_prefix", context.serialize(value.getPhonePrefix()));
            jsonObj.add("hourly_amount", context.serialize(value.getHourlyAmount()));
            jsonObj.add("price_table", context.serialize(value.getPriceTable()));
            jsonObj.add("dynamic_options", context.serialize(value.getSurgePriceTable()));
            jsonObj.add("stripe_key", context.serialize(value.getStripeKey()));
            jsonObj.add("special_extras_options", context.serialize(value.getExtrasOptions()));
            return jsonObj;
        }
    }

    public static final class PeakPriceInfo implements Serializable
    {
        @SerializedName("date")
        private Date date;
        @SerializedName("price")
        private float price;
        @SerializedName("type")
        private String type;

        public final Date getDate()
        {
            return date;
        }

        public final String getType()
        {
            return type;
        }

        public final float getPrice()
        {
            return price;
        }
    }
}
