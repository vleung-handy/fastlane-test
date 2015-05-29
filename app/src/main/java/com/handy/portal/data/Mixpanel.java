package com.handy.portal.data;

import android.content.Context;

import com.handy.portal.BuildConfig;
import com.handy.portal.event.Event;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import javax.inject.Inject;

public class Mixpanel
{
    private MixpanelAPI mixpanel;

    @Inject
    public Mixpanel(final Context context, final Bus bus)
    {
        String mixpanelApiKey = PropertiesReader.getConfigProperties(context).getProperty("mixpanel_api_key");
        this.mixpanel = MixpanelAPI.getInstance(context, mixpanelApiKey);
        setupBaseProperties();
        bus.register(this);
    }

    private void setupBaseProperties() {
        final JSONObject baseProps = new JSONObject();
        addProps(baseProps, "device", "android");
        addProps(baseProps, "app version", BuildConfig.VERSION_NAME);
        addProps(baseProps, "app flavor", BuildConfig.FLAVOR);
        mixpanel.registerSuperProperties(baseProps);
    }

    public void flush()
    {
        mixpanel.flush();
    }

    public void track(String eventName) {
        mixpanel.track(eventName, null);
    }

    @Subscribe
    public void onRequestPinCode(Event.RequestPinCodeEvent event) {
        mixpanel.track("portal login submitted - phone number", null);
    }

    @Subscribe
    public void onRequestLoginEvent(Event.RequestLoginEvent event) {
        mixpanel.track("portal login submitted - pin code", null);
    }

    @Subscribe
    public void onNavigation(Event.Navigation event) {
        final JSONObject props = new JSONObject();
        addProps(props, "page", event.page);
        mixpanel.track("portal navigation", props);
    }

    @Subscribe
    public void onLoginError(Event.LoginError event)
    {
        final JSONObject props = new JSONObject();
        addProps(props, "source", event.source);
        mixpanel.track("portal login error", props);
    }

    public void trackEventAppOpened(final boolean newOpen)
    {
        String event_name = "provider portal";
        final JSONObject props = new JSONObject();
        addProps(props, "new_open", newOpen);
        mixpanel.track(event_name, props);
    }

    private void addProps(final JSONObject object, final String key, final Object value)
    {
        try
        {
            object.put(key, value);
        } catch (final JSONException e)
        {
            throw new RuntimeException(e);
        }
    }

    private void addProps(final JSONObject object, final HashMap<String, Object> props)
    {
        try
        {
            for (final String key : props.keySet()) object.put(key, props.get(key));
        } catch (final JSONException e)
        {
            throw new RuntimeException(e);
        }
    }

}
