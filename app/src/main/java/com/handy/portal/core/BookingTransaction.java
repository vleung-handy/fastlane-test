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

import java.lang.reflect.Type;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

public final class BookingTransaction extends Observable {
    @SerializedName("booking_id") private int bookingId;
    @SerializedName("user_id") private String userId;
    @SerializedName("service_id") private int serviceId;
    @SerializedName("first_name") private String firstName;
    @SerializedName("last_name") private String lastName;
    @SerializedName("address1") private String address1;
    @SerializedName("address2") private String address2;
    @SerializedName("phone") private String phone;
    @SerializedName("zipcode") private String zipCode;
    @SerializedName("email") private String email;
    @SerializedName("hrs") private float hours;
    @SerializedName("updated_recurring_freq") private int recurringFrequency;
    @SerializedName("extra_cleaning_text") private String extraCleaningText;
    @SerializedName("extra_hours") private float extraHours;
    @SerializedName("date_start") private Date startDate;
    @SerializedName("auth_token") private String authToken;
    @SerializedName("stripe_token") private String stripeToken;
    @SerializedName("_android_promo_applied") private String promoApplied;

    public final int getBookingId() {
        return bookingId;
    }

    public final void setBookingId(final int bookingId) {
        this.bookingId = bookingId;
        triggerObservers();
    }

    final String getUserId() {
        return userId;
    }

    public final void setUserId(final String userId) {
        this.userId = userId;
        triggerObservers();
    }

    final int getServiceId() {
        return serviceId;
    }

    public final void setServiceId(final int serviceId) {
        this.serviceId = serviceId;
        triggerObservers();
    }

    final String getFirstName() {
        return firstName;
    }

    public final void setFirstName(final String firstName) {
        this.firstName = firstName;
        triggerObservers();
    }

    final String getLastName() {
        return lastName;
    }

    public final void setLastName(final String lastName) {
        this.lastName = lastName;
        triggerObservers();
    }

    final String getAddress1() {
        return address1;
    }

    public final void setAddress1(final String address1) {
        this.address1 = address1;
        triggerObservers();
    }

    final String getAddress2() {
        return address2;
    }

    public final void setAddress2(final String address2) {
        this.address2 = address2;
        triggerObservers();
    }

    final String getPhone() {
        return phone;
    }

    public final void setPhone(final String phone) {
        this.phone = phone;
        triggerObservers();
    }

    final String getZipCode() {
        return zipCode;
    }

    public final void setZipCode(final String zipCode) {
        this.zipCode = zipCode;
        triggerObservers();
    }

    final String getEmail() {
        return email;
    }

    public final void setEmail(final String email) {
        this.email = email;
        triggerObservers();
    }

    public final float getHours() {
        return hours;
    }

    public final void setHours(final float hours) {
        this.hours = hours;
        triggerObservers();
    }

    public final int getRecurringFrequency() {
        return recurringFrequency;
    }

    public final void setRecurringFrequency(final int recurringFrequency) {
        this.recurringFrequency = recurringFrequency;
        triggerObservers();
    }

    public final String getExtraCleaningText() {
        return extraCleaningText;
    }

    public final void setExtraCleaningText(final String extraCleaningText) {
        this.extraCleaningText = extraCleaningText;
        triggerObservers();
    }

    public final float getExtraHours() {
        return extraHours;
    }

    public final void setExtraHours(final float extraHours) {
        this.extraHours = extraHours;
        triggerObservers();
    }

    public final Date getStartDate() {
        return startDate;
    }

    public final void setStartDate(final Date startDate) {
        this.startDate = startDate;
        triggerObservers();
    }

    final String getAuthToken() {
        return authToken;
    }

    public final void setAuthToken(final String authToken) {
        this.authToken = authToken;
        triggerObservers();
    }

    final String getStripeToken() {
        return stripeToken;
    }

    public final void setStripeToken(final String stripeToken) {
        this.stripeToken = stripeToken;
        triggerObservers();
    }

    public final String promoApplied() {
        return promoApplied;
    }

    public final void setPromoApplied(final String promoApplied) {
        this.promoApplied = promoApplied;
        triggerObservers();
    }

    private void triggerObservers() {
        setChanged();
        notifyObservers();
    }

    final String toJson() {
        final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .setExclusionStrategies(getExclusionStrategy())
                .registerTypeAdapter(BookingTransaction.class,
                        new BookingTransactionSerializer()).create();

        return gson.toJson(this);
    }

    static BookingTransaction fromJson(final String json) {
        return new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create()
                .fromJson(json, BookingTransaction.class);
    }

    static ExclusionStrategy getExclusionStrategy() {
        return new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(final FieldAttributes f) {
                return false;
            }

            @Override
            public boolean shouldSkipClass(final Class<?> clazz) {
                return clazz.equals(Observer.class);
            }
        };
    }

    static final class BookingTransactionSerializer implements JsonSerializer<BookingTransaction> {
        @Override
        public final JsonElement serialize(final BookingTransaction value, final Type type,
                                           final JsonSerializationContext context) {
            final JsonObject jsonObj = new JsonObject();
            jsonObj.add("booking_id", context.serialize(value.getBookingId()));
            jsonObj.add("user_id", context.serialize(value.getUserId()));
            jsonObj.add("service_id", context.serialize(value.getServiceId()));
            jsonObj.add("first_name", context.serialize(value.getFirstName()));
            jsonObj.add("last_name", context.serialize(value.getLastName()));
            jsonObj.add("address1", context.serialize(value.getAddress1()));
            jsonObj.add("address2", context.serialize(value.getAddress2()));
            jsonObj.add("phone", context.serialize(value.getPhone()));
            jsonObj.add("zipcode", context.serialize(value.getZipCode()));
            jsonObj.add("email", context.serialize(value.getEmail()));
            jsonObj.add("hrs", context.serialize(value.getHours()));
            jsonObj.add("date_start", context.serialize(value.getStartDate()));
            jsonObj.add("auth_token", context.serialize(value.getAuthToken()));
            jsonObj.add("stripe_token", context.serialize(value.getStripeToken()));
            jsonObj.add("extra_cleaning_text", context.serialize(value.getExtraCleaningText()));
            jsonObj.add("mobile", context.serialize(1));
            jsonObj.add("_android_promo_applied", context.serialize(value.promoApplied()));

            final int recur = value.getRecurringFrequency();

            if (recur > 0) jsonObj.add("updated_recurring_freq",
                    context.serialize(Integer.toString(recur)));

            final float extraHours = value.getExtraHours();
            if (extraHours > 0) jsonObj.add("extra_hours", context.serialize(extraHours));

            return jsonObj;
        }
    }
}
