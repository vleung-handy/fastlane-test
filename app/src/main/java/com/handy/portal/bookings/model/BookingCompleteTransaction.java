package com.handy.portal.bookings.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Type;

public final class BookingCompleteTransaction {
    @SerializedName("user_info")
    private User user;

    public final User getUser() {
        return user;
    }

    final void setUser(final User user) {
        this.user = user;
    }

    final String toJson() {
        final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .registerTypeAdapter(BookingCompleteTransaction.class,
                        new BookingCompleteTransaction()).create();

        return gson.toJson(this);
    }

    public static BookingCompleteTransaction fromJson(final String json) {
        return new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create()
                .fromJson(json, BookingCompleteTransaction.class);
    }

    static final class BookingCompleteTransactionSerializer
            implements JsonSerializer<BookingCompleteTransaction> {
        @Override
        public final JsonElement serialize(final BookingCompleteTransaction value, final Type type,
                                           final JsonSerializationContext context) {
            final JsonObject jsonObj = new JsonObject();
            jsonObj.add("user_info", context.serialize(value.getUser()));
            return jsonObj;
        }
    }


    public static final class User {
        @SerializedName("auth_token")
        private String authToken;
        @SerializedName("id")
        private String id;

        public final String getAuthToken() {
            return authToken;
        }

        public final String getId() {
            return id;
        }
    }
}
