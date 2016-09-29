package com.handy.portal.chat;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import com.layer.sdk.LayerClient;
import com.layer.sdk.exceptions.LayerException;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;


/**
 */
public class LayerAuthenticationProvider implements AuthenticationProvider<LayerAuthenticationProvider.Credentials>
{
    private static final String TAG = LayerAuthenticationProvider.class.getName();

    private final SharedPreferences mPreferences;
    private final EventBus mEventBus;
    private Callback mCallback;

    @Inject
    public LayerAuthenticationProvider(Context context, EventBus bus)
    {
        mPreferences = context.getSharedPreferences(LayerAuthenticationProvider.class.getSimpleName(), Context.MODE_PRIVATE);
        mEventBus = bus;
    }

    @Override
    public AuthenticationProvider<Credentials> setCredentials(Credentials credentials)
    {
        if (credentials == null)
        {
            mPreferences.edit().clear().commit();
            return this;
        }
        mPreferences.edit()
                .putString("appId", credentials.getLayerAppId())
                .putString("name", credentials.getUserName())
                .commit();
        return this;
    }

    @Override
    public boolean hasCredentials()
    {
        return mPreferences.contains("appId");
    }

    @Override
    public AuthenticationProvider<Credentials> setCallback(Callback callback)
    {
        mCallback = callback;
        return this;
    }

    @Override
    public void onAuthenticated(LayerClient layerClient, String userId)
    {
        Log.d(TAG, "Authenticated with Layer, user ID: " + userId);
        layerClient.connect();
        if (mCallback != null)
        {
            mCallback.onSuccess(this, userId);
        }
        else
        {
            Log.d(TAG, "onAuthenticated: callback is null");
        }
    }

    @Override
    public void onDeauthenticated(LayerClient layerClient)
    {
        Log.d(TAG, "Deauthenticated with Layer");
    }

    @Override
    public void onAuthenticationChallenge(LayerClient layerClient, String nonce)
    {
        Log.d(TAG, "Received challenge: " + nonce);
        respondToChallenge(nonce);
    }

    @Override
    public void onAuthenticationError(LayerClient layerClient, LayerException e)
    {
        String error = "Failed to authenticate with Layer: " + e.getMessage();
        Log.e(TAG, error, e);
        if (mCallback != null)
        {
            mCallback.onError(this, error);
        }
    }

    @Override
    public boolean routeLogin(LayerClient layerClient, String layerAppId)
    {

        if ((layerClient != null) && layerClient.isAuthenticated())
        {
            // The LayerClient is authenticated: no action required.
            Log.d(TAG, "No authentication routing required");
            return false;
        }

        return true;
    }

    private void respondToChallenge(String nonce)
    {
        Log.d(TAG, "respondToChallenge: ");
        mEventBus.post(new ChatEvent.RequestLayerAuthTokenEvent(mPreferences.getString("name", null)
                , nonce));
    }

    public static class Credentials
    {
        private final String mLayerAppId;
        private final String mUserName;

        public Credentials(Uri layerAppId, String userName)
        {
            this(layerAppId == null ? null : layerAppId.getLastPathSegment(), userName);
        }

        public Credentials(String layerAppId, String userName)
        {
            mLayerAppId = layerAppId == null ? null : (layerAppId.contains("/") ? layerAppId.substring(layerAppId.lastIndexOf("/") + 1) : layerAppId);
            mUserName = userName;
        }

        public String getUserName()
        {
            return mUserName;
        }

        public String getLayerAppId()
        {
            return mLayerAppId;
        }
    }
}
