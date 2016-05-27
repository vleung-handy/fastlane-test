package com.handy.portal.logger.mixpanel;

import android.content.Context;
import android.os.Build;

import com.google.gson.Gson;
import com.handy.portal.BuildConfig;
import com.handy.portal.constant.PrefsKey;
import com.handy.portal.core.BaseApplication;
import com.handy.portal.library.util.PropertiesReader;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.EventLog;
import com.handy.portal.logger.mixpanel.annotation.Track;
import com.handy.portal.logger.mixpanel.annotation.TrackField;
import com.handy.portal.manager.PrefsManager;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;

import javax.inject.Inject;

public class Mixpanel
{
    private static final String PROVIDER = "pro";
    private static final String ANDROID = "Android";

    private MixpanelAPI mixpanelAPI;
    private PrefsManager prefsManager;
    private Gson mGson = new Gson();

    @Inject
    public Mixpanel(final Context context, final PrefsManager prefsManager)
    {
        this.prefsManager = prefsManager;
        String mixpanelApiKey = PropertiesReader.getConfigProperties(context).getProperty("mixpanel_api_key");
        this.mixpanelAPI = MixpanelAPI.getInstance(context, mixpanelApiKey);
        setupBaseProperties();
    }

    public void onLoginSuccess()
    {
        //update our base attributes to use the updated user ID, it should have already been written to prefs
        setupBaseProperties();
    }

    private void setupBaseProperties()
    {
        final JSONObject baseProps = new JSONObject();
        String providerId = prefsManager.getString(PrefsKey.LAST_PROVIDER_ID);
        addProps(baseProps, "product_type", PROVIDER);
        addProps(baseProps, "platform", ANDROID);
        addProps(baseProps, "os_version", Build.VERSION.RELEASE);
        addProps(baseProps, "app_version", BuildConfig.VERSION_NAME);
        addProps(baseProps, "version_track", BuildConfig.FLAVOR);
        addProps(baseProps, "device_id", BaseApplication.getDeviceId());
        addProps(baseProps, "provider_id", providerId);
        mixpanelAPI.identify(providerId);
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
        else if (event instanceof LogEvent.AddLogEvent)
        {
            EventLog log = ((LogEvent.AddLogEvent) event).getLog();
            try
            {
                track(log.getEventName(), new JSONObject(mGson.toJson(log)));
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
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
                }
                catch (IllegalAccessException e)
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
