package com.handy.portal.manager;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.handy.portal.constant.PrefsKey;
import com.handy.portal.core.BaseApplication;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.LogEvent;
import com.handy.portal.model.TypedJsonString;
import com.handy.portal.model.logs.EventLog;
import com.handy.portal.model.logs.EventLogBundle;
import com.handy.portal.model.logs.EventLogResponse;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

public class EventLogManager
{
    private static final int MAX_NUM_PER_BUNDLE = 100;
    private static final Gson GSON = new Gson();

    private static List<EventLog> sLogs = new ArrayList<>();
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
    public void addLog(@NonNull LogEvent.AddLogEvent event)
    {
        sLogs.add(event.getLog());
        if (sLogs.size() > MAX_NUM_PER_BUNDLE)
        {
            saveLogs(null);
            sendLogs(null);
        }
    }

    @Subscribe
    public void sendLogs(@Nullable final LogEvent.SendLogsEvent event)
    {
        final List<String> bundles = loadSavedEventBundles();
        if (bundles.size() == 0) { return; }
        for (final String bundle : bundles)
        {
            mDataManager.postLogs(new TypedJsonString(bundle), new DataManager.Callback<EventLogResponse>()
            {
                @Override
                public void onSuccess(EventLogResponse response)
                {
                    bundles.remove(bundle);
                    saveToPreference(bundles);
                }

                @Override
                public void onError(DataManager.DataManagerError error) { }
            });
        }
    }

    @Subscribe
    public void saveLogs(@Nullable LogEvent.SaveLogsEvent event)
    {
        if (sLogs.size() > 0)
        {
            List<String> eventLogBundles = loadSavedEventBundles();
            String bundleId = createBundleId();
            eventLogBundles.add(GSON.toJson(new EventLogBundle(bundleId, sLogs)));
            saveToPreference(eventLogBundles);
            sLogs = new ArrayList<>();
        }
    }

    private String createBundleId()
    {
        return System.currentTimeMillis() + "+" + BaseApplication.getDeviceId();
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
}
