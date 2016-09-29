package com.handy.portal.chat;

import android.util.Log;

import com.layer.sdk.LayerClient;

/**
 * Created by jtse on 9/22/16.
 */
public class LayerHelper {

    private static final String TAG = LayerHelper.class.getName();

    private LayerClient mLayerClient;
    private AuthenticationProvider mLayerAuthProvider;
    private String mAppId;

    public LayerHelper(final LayerClient layerClient, final AuthenticationProvider layerAuthProvider, final String appId) {
        mLayerClient = layerClient;
        mLayerAuthProvider = layerAuthProvider;
        mAppId = appId;
    }

    /**
     * Authenticates with the AuthenticationProvider and Layer, returning asynchronous results to
     * the provided callback.
     *
     * @param credentials Credentials associated with the current AuthenticationProvider.
     * @param callback    Callback to receive authentication results.
     */
    @SuppressWarnings("unchecked")
    public void authenticate(Object credentials, AuthenticationProvider.Callback callback) {
        if (mLayerClient == null) return;
        if (mAppId == null) return;
        mLayerAuthProvider
                .setCredentials(credentials)
                .setCallback(callback);

        Log.d(TAG, "authenticate: authenticating with client:" + mLayerClient.toString() + " auth provider:"
                + mLayerAuthProvider.toString());

        mLayerClient.authenticate();
    }

    /**
     * Deauthenticates with Layer and clears cached AuthenticationProvider credentials.
     *
     * @param callback Callback to receive deauthentication success and failure.
     */
    public void deauthenticate(final com.layer.atlas.util.Util.DeauthenticationCallback callback) {
        Log.d(TAG, "deauthenticate: with client:" + mLayerClient.toString() + "  authProvider: ");
        com.layer.atlas.util.Util.deauthenticate(mLayerClient, new com.layer.atlas.util.Util.DeauthenticationCallback() {
            @Override
            @SuppressWarnings("unchecked")
            public void onDeauthenticationSuccess(LayerClient client) {
                Log.d(TAG, "onDeauthenticationSuccess: ");
                mLayerAuthProvider.setCredentials(null);
                callback.onDeauthenticationSuccess(client);
            }

            @Override
            public void onDeauthenticationFailed(LayerClient client, String reason) {
                Log.d(TAG, "onDeauthenticationFailed: ");
                callback.onDeauthenticationFailed(client, reason);
            }
        });
    }

}
