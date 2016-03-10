package com.handy.portal.logger.handylogger;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.handy.portal.constant.PrefsKey;
import com.handy.portal.data.DataManager;
import com.handy.portal.logger.handylogger.model.Event;
import com.handy.portal.logger.handylogger.model.EventLogBundle;
import com.handy.portal.logger.handylogger.model.EventLogResponse;
import com.handy.portal.manager.PrefsManager;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

public class EventLogManager
{
    private static final String SENT_TIMESTAMP_SECS_KEY = "event_bundle_sent_timestamp";
    private static final int MAX_NUM_PER_BUNDLE = 50;
    private static final Gson GSON = new Gson();

    private static List<Event> sLogs = new ArrayList<>();
    private final Bus mBus;
    private final DataManager mDataManager;
    private final PrefsManager mPrefsManager;

    @Inject
    public EventLogManager(final Bus bus, final DataManager dataManager,
                           final PrefsManager prefsManager)
    {
        mBus = bus;
        mBus.register(this);
        mDataManager = dataManager;
        mPrefsManager = prefsManager;
    }

    @Subscribe
    public synchronized void addLog(@NonNull LogEvent.AddLogEvent event)
    {
        Crashlytics.log(event.getLog().getEventName());
        sLogs.add(new Event(event.getLog()));
        if (sLogs.size() >= MAX_NUM_PER_BUNDLE)
        {
            saveLogs(null);
            sendLogs(null);
        }
    }

    @Subscribe
    public synchronized void sendLogs(@Nullable final LogEvent.SendLogsEvent event)
    {
        final List<String> jsonBundleStrings = loadSavedEventBundles();
        if (jsonBundleStrings.size() == 0) { return; }
        for (final String bundleString : jsonBundleStrings)
        {
            final JsonObject eventLogBundle = GSON.fromJson(bundleString, JsonObject.class);
            eventLogBundle.addProperty(SENT_TIMESTAMP_SECS_KEY, System.currentTimeMillis() / 1000);
            mDataManager.postLogs(eventLogBundle, new DataManager.Callback<EventLogResponse>()
            {
                @Override
                public void onSuccess(EventLogResponse response)
                {
                    jsonBundleStrings.remove(bundleString);
                    saveToPreference(jsonBundleStrings);
                }

                @Override
                public void onError(DataManager.DataManagerError error) {}
            });
        }
    }

    @Subscribe
    public synchronized void saveLogs(@Nullable LogEvent.SaveLogsEvent event)
    {
        if (sLogs.size() > 0)
        {
            List<String> eventLogBundles = loadSavedEventBundles();
            eventLogBundles.add(GSON.toJson(new EventLogBundle(getProviderId(), sLogs)));
            saveToPreference(eventLogBundles);
            sLogs = new ArrayList<>();
        }
    }

    private List<String> loadSavedEventBundles()
    {
        String json = mPrefsManager.getString(PrefsKey.EVENT_LOG_BUNDLES, "");
        String[] bundles = GSON.fromJson(json, String[].class);
        if (bundles != null)
        {
            return new ArrayList<>(Arrays.asList(bundles));
        }
        else
        {
            return new ArrayList<>();
        }
    }

    private void saveToPreference(List<String> eventLogBundles)
    {
        String json = GSON.toJson(eventLogBundles);
        mPrefsManager.setString(PrefsKey.EVENT_LOG_BUNDLES, json);
    }

    private int getProviderId()
    {
        return Integer.parseInt(mPrefsManager.getString(PrefsKey.LAST_PROVIDER_ID, "0"));
    }
}
