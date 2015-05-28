package com.handy.portal.data;

import android.content.Context;

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

    private final static String EVENT_PREFIX = "android_portal_";

    @Inject
    public Mixpanel(final Context context, final Bus bus)
    {
        String mixpanelApiKey = PropertiesReader.getConfigProperties(context).getProperty("mixpanel_api_key");
        this.mixpanel = MixpanelAPI.getInstance(context, mixpanelApiKey);
        bus.register(this);
    }

    public void flush()
    {
        mixpanel.flush();
    }

    @Subscribe
    public void onLoginSuccess(Event.LoginSuccess event)
    {
        trackSimpleBusEvent(event);
    }

    @Subscribe
    public void onLoginSuccess(Event.LoginError event)
    {
        trackSimpleBusEvent(event);
    }

    public void trackEventAppOpened(final boolean newOpen)
    {
        String event_name = "app_open";
        final JSONObject props = new JSONObject();
        addProps(props, "new_open", newOpen);
        mixpanel.track(EVENT_PREFIX + event_name, props);
    }

//    public void trackEventLaundryAdded(final LaundryEventSource source)
//    {
//        final JSONObject props = new JSONObject();
//        addProps(props, "source", source.getValue());
//        mixpanel.track("submit add laundry confirm page", props);
//    }

    private void trackSimpleBusEvent(Event event) {
        String event_name = EVENT_PREFIX + event.getClass().getSimpleName();
        mixpanel.track(event_name, null);
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
