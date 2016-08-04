package com.handy.portal.layer;

import android.os.AsyncTask;
import android.util.Log;

import com.layer.sdk.LayerClient;
import com.layer.sdk.exceptions.LayerException;
import com.layer.sdk.listeners.LayerAuthenticationListener;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

/**
 */
public class MyAuthenticationListener implements LayerAuthenticationListener
{
    private static final String TAG = "MyAuthenticationList";
    LayerFragment mFragment;

    public MyAuthenticationListener(LayerFragment activity)
    {
        mFragment = activity;
    }


    @Override
    public void onAuthenticated(LayerClient client, String arg1)
    {
        System.out.println("Authentication successful");
        Log.d(TAG, "onAuthenticated() called with: " + "client = [" + client + "], arg1 = [" + arg1 + "]");
        mFragment.authenticated();
    }

    @Override
    public void onAuthenticationChallenge(final LayerClient client, final String nonce)
    {
        Log.d(TAG, "onAuthenticationChallenge() called with: " + "client = [" + client + "], nonce = [" + nonce + "]");

        final String mUserId = mFragment.getMyId();

        Log.d(TAG, "onAuthenticationChallenge: with user id " + mUserId);

        //Note: This Layer Authentication Service is for TESTING PURPOSES ONLY
        //When going into production, you will need to create your own web service
        //Check out https://developer.layer.com/docs/guides#authentication for guidance
        (new AsyncTask<Void, Void, Void>()
        {
            @Override
            protected Void doInBackground(Void... params)
            {
                try
                {
                    HttpPost post = new HttpPost("https://layer-identity-provider.herokuapp" +
                            ".com/identity_tokens");
                    post.setHeader("Content-Type", "application/json");
                    post.setHeader("Accept", "application/json");

                    JSONObject json = new JSONObject()
                            .put("app_id", client.getAppId())
                            .put("user_id", mUserId)
                            .put("nonce", nonce);
                    post.setEntity(new StringEntity(json.toString()));

                    HttpResponse response = (new DefaultHttpClient()).execute(post);
                    String eit = (new JSONObject(EntityUtils.toString(response.getEntity())))
                            .optString("identity_token");

                    Log.d(TAG, "onAuthenticationChallenge: got back from server:" + eit);
                    client.answerAuthenticationChallenge(eit);

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                return null;
            }
        }).execute();
    }

    @Override
    public void onAuthenticationError(LayerClient layerClient, LayerException e)
    {
        // TODO Auto-generated method stub
        System.out.println("There was an error authenticating");
        Log.d(TAG, "onAuthenticationError() called with: " + "layerClient = [" + layerClient + "], e = [" + e + "]");
    }

    @Override
    public void onDeauthenticated(LayerClient client)
    {
        // TODO Auto-generated method stub
        Log.d(TAG, "onDeauthenticated() called with: " + "client = [" + client + "]");
    }
}
