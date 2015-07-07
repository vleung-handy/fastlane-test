package com.handy.portal.analytics;

import android.content.Context;

import com.handy.portal.BuildConfig;
import com.handy.portal.annotation.Track;
import com.handy.portal.annotation.TrackField;
import com.handy.portal.constant.PrefsKey;
import com.handy.portal.core.PropertiesReader;
import com.handy.portal.manager.PrefsManager;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;

import javax.inject.Inject;

public class Mixpanel
{
    private MixpanelAPI mixpanelAPI;

    @Inject
    PrefsManager prefsManager;

    @Inject
    public Mixpanel(final Context context)
    {
        String mixpanelApiKey = PropertiesReader.getConfigProperties(context).getProperty("mixpanel_api_key");
        this.mixpanelAPI = MixpanelAPI.getInstance(context, mixpanelApiKey);
        setupBaseProperties();
    }

    private void setupBaseProperties()
    {
        final JSONObject baseProps = new JSONObject();
        addProps(baseProps, "device", "android");
        addProps(baseProps, "app version", BuildConfig.VERSION_NAME);
        addProps(baseProps, "app flavor", BuildConfig.FLAVOR);
        if(prefsManager != null)
        {
            addProps(baseProps, "user_id", prefsManager.getString(PrefsKey.USER_CREDENTIALS_ID_KEY));
        }
        mixpanelAPI.registerSuperProperties(baseProps);
    }

    public void flush()
    {
        mixpanelAPI.flush();
    }

    public void track(String eventName)
    {
        track(eventName, null);
    }

    public void trackEventAppOpened(final boolean newOpen)
    {
        String eventName = "provider portal";
        final JSONObject props = new JSONObject();
        addProps(props, "new_open", newOpen);
        mixpanelAPI.track(eventName, props);
    }

    public void trackEvent(Object event)
    {
        Class eventClass = event.getClass();
        if (eventClass.isAnnotationPresent(Track.class))
        {
            Track annotation = (Track) eventClass.getAnnotation(Track.class);
            String message = annotation.value();

            getItemsToTrack(eventClass);

            track(message, getItemsToTrack(event));
        }
    }

    private void track(String eventName, JSONObject object)
    {
        mixpanelAPI.track(eventName, object);
    }

    private JSONObject getItemsToTrack(Object event)
    {
        Class eventClass = event.getClass();
        JSONObject object = new JSONObject();
        for (Field field : eventClass.getDeclaredFields())
        {
            if (field.isAnnotationPresent(TrackField.class))
            {
                TrackField annotation = field.getAnnotation(TrackField.class);
                String key = annotation.value();
                Object value = null;

                field.setAccessible(true);
                try
                {
                    value = field.get(event);
                } catch (IllegalAccessException e)
                {
                    e.printStackTrace();
                }

                addProps(object, key, value);
            }
        }
        return object;
    }

    private static void addProps(final JSONObject object, final String key, final Object value)
    {
        try
        {
            object.put(key, value);
        }
        catch (final JSONException e)
        {
            throw new RuntimeException(e);
        }
    }

}
