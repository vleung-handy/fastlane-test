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
import com.handy.portal.util.TextUtils;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

public final class BookingRequest extends Observable
{
    @SerializedName("service_id")
    private int serviceId;
    @SerializedName("uniq")
    private String uniq;
    @SerializedName("zipcode")
    private String zipCode;
    @SerializedName("email")
    private String email;
    @SerializedName("user_id")
    private String userId;
    @SerializedName("service_attributes")
    private HashMap<String, String> options;
    @SerializedName("date_start")
    private Date startDate;
    @SerializedName("entered_code")
    private String promoCode;
    @SerializedName("_android_promo_type")
    private PromoCode.Type promoType;

    public final int getServiceId()
    {
        return serviceId;
    }

    public final void setServiceId(final int serviceId)
    {
        this.serviceId = serviceId;
        triggerObservers();
    }

    public final String getUniq()
    {
        return uniq;
    }

    public final void setUniq(final String uniq)
    {
        this.uniq = uniq;
        triggerObservers();
    }

    public final String getZipCode()
    {
        return zipCode;
    }

    public final void setZipCode(final String zipCode)
    {
        this.zipCode = zipCode;
        triggerObservers();
    }

    public final String getEmail()
    {
        return email;
    }

    public final void setEmail(final String email)
    {
        this.email = email;
        triggerObservers();
    }

    final String getUserId()
    {
        return userId;
    }

    public final void setUserId(final String userId)
    {
        this.userId = userId;
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

    public final HashMap<String, String> getOptions()
    {
        if (options == null) options = new HashMap<>();
        return options;
    }

    public final void setOptions(final HashMap<String, String> options)
    {
        this.options = options;
        triggerObservers();
    }

    public final String getPromoCode()
    {
        return promoCode;
    }

    public final void setPromoCode(final String promoCode)
    {
        this.promoCode = promoCode;
        triggerObservers();
    }

    public final PromoCode.Type getPromoType()
    {
        return promoType;
    }

    public final void setPromoType(final PromoCode.Type promoType)
    {
        this.promoType = promoType;
        triggerObservers();
    }

    private void triggerObservers()
    {
        setChanged();
        notifyObservers();
    }

    final String toJson()
    {
        final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm")
                .setExclusionStrategies(getExclusionStrategy())
                .registerTypeAdapter(BookingRequest.class, new BookingRequestSerializer()).create();

        return gson.toJson(this);
    }

    static BookingRequest fromJson(final String json)
    {
        return new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm").create()
                .fromJson(json, BookingRequest.class);
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

    static final class BookingRequestSerializer implements JsonSerializer<BookingRequest>
    {
        @Override
        public final JsonElement serialize(final BookingRequest value, final Type type,
                                           final JsonSerializationContext context)
        {
            final JsonObject jsonObj = new JsonObject();
            jsonObj.add("service_id", context.serialize(value.getServiceId()));
            jsonObj.add("uniq", context.serialize(value.getUniq()));
            jsonObj.add("zipcode", context.serialize(value.getZipCode()));
            jsonObj.add("email", context.serialize(value.getEmail()));
            jsonObj.add("user_id", context.serialize(value.getUserId()));
            jsonObj.add("service_attributes", context.serialize(value.getOptions()));

            jsonObj.add("date_start", context.serialize(TextUtils.formatDate(value.getStartDate(),
                    "yyyy-MM-dd'T'HH:mm")));

            if (value.getPromoCode() != null)
            {
                jsonObj.add("entered_code", context.serialize(value.getPromoCode()));
                jsonObj.add("_android_promo_type", context.serialize(value.getPromoType()));
            }

            jsonObj.add("mobile", context.serialize(1));
            return jsonObj;
        }
    }
}
