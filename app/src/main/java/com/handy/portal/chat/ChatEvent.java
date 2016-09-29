package com.handy.portal.chat;

import android.support.annotation.NonNull;

import com.handy.portal.data.DataManager;
import com.handy.portal.event.HandyEvent;

public abstract class ChatEvent
{
    public static class RequestLayerAuthTokenEvent extends HandyEvent.RequestEvent
    {
        @NonNull
        public String userId;
        @NonNull
        public String nonce;

        public RequestLayerAuthTokenEvent(@NonNull final String userId, @NonNull final String nonce)
        {
            this.userId = userId;
            this.nonce = nonce;
        }
    }


    public static class ReceiveLayerAuthTokenSuccessEvent extends HandyEvent.ReceiveSuccessEvent
    {
        public boolean success;
        public String identityToken;

        public ReceiveLayerAuthTokenSuccessEvent(LayerResponseWrapper response)
        {
            this.success = response.getSuccess();
            this.identityToken = response.getIdentityToken();
        }
    }


    public static class ReceiveLayerAuthTokenErrorEvent extends HandyEvent.ReceiveErrorEvent
    {
        public ReceiveLayerAuthTokenErrorEvent(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }
}
