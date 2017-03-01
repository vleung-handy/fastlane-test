package com.handy.portal.bookings.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ChatOptions implements Serializable {

    @SerializedName("direct_to_in_app_chat")
    private boolean mDirectToInAppChat;

    public boolean isDirectToInAppChat() {
        return mDirectToInAppChat;
    }
}
