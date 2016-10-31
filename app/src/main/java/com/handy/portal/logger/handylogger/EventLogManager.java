package com.handy.portal.logger.handylogger;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.handy.portal.constant.PrefsKey;
import com.handy.portal.core.BaseApplication;
import com.handy.portal.data.DataManager;
import com.handy.portal.library.util.PropertiesReader;
import com.handy.portal.logger.handylogger.model.Event;
import com.handy.portal.logger.handylogger.model.EventLogBundle;
import com.handy.portal.logger.handylogger.model.EventLogResponse;
import com.handy.portal.logger.handylogger.model.EventSuperProperties;
import com.handy.portal.logger.handylogger.model.EventSuperPropertiesBase;
import com.handy.portal.logger.handylogger.model.Session;
import com.handy.portal.manager.FileManager;
import com.handy.portal.manager.PrefsManager;
import com.handy.portal.manager.ProviderManager;
import com.handy.portal.model.ProviderPersonalInfo;
import com.handy.portal.model.ProviderProfile;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

public class EventLogManager
{

    private static final String KEY_SENT_TIMESTAMP_SECS = "event_bundle_sent_timestamp";
    private static final int UPLOAD_TIMER_DELAY_MS = 60000; //1 min
    private static final int UPLOAD_TIMER_DELAY_NO_INTERNET_MS = 15 * UPLOAD_TIMER_DELAY_MS; //15 min
    private static final String TAG = EventLogManager.class.getSimpleName();
    private static final int DEFAULT_USER_ID = -1;
    static final int MAX_EVENTS_PER_BUNDLE = 50;
    private static final Gson GSON = new Gson();

    private static EventLogBundle sCurrentEventLogBundle;
    private final EventBus mBus;
    private final DataManager mDataManager;
    private final FileManager mFileManager;
    private final PrefsManager mPrefsManager;
    private MixpanelAPI mMixpanel;
    private Session mSession;
    //Used just for mixed panel
    private ProviderManager mProviderManager;
    private boolean mIsProviderLoggedIn; // This is used for updating mixpanel super property

    //Counter for the number of logs being sent, so when we get a response we can subtract from this number
    // until all the logs are finished
    private int mSendingLogsCount;
    //Timer to be used for sending logs
    private Timer mUploadLogTimer;

    @Inject
    public EventLogManager(final EventBus bus, final DataManager dataManager, final FileManager fileManager, final PrefsManager prefsManager, ProviderManager providerManager)
    {
        mBus = bus;
        mBus.register(this);
        mDataManager = dataManager;
        mFileManager = fileManager;
        mPrefsManager = prefsManager;
        mProviderManager = providerManager;
        //Send logs on initialization
        savePrefsToLogsOnInitialization();

        initMixPanel();

        //Session
        mSession = Session.getInstance(prefsManager);
    }

    /**
     * @param event
     */
    @Subscribe
    public synchronized void addLog(@NonNull LogEvent.AddLogEvent event)
    {
        mSession.incrementEventCount(mPrefsManager);
        Event eventLog = new Event(event.getLog(), mSession.getId(), mSession.getEventCount());

        //log the payload to Crashlytics too
        //Note: Should always log regardless of flavor/variant

        //Create upload timer when we get a new log and there isn't a timer currently
        if (mUploadLogTimer == null) { setUploadTimer(); }

        //log the payload to Crashlytics too, useful for follow steps for debugging when crash
        try
        {
            //putting in try/catch block just in case GSON.toJson throws an exception
            //Get the log only to log
            JSONObject eventLogJson = new JSONObject(GSON.toJson(event.getLog()));
            String logString = event.getLog().getEventName() + ": " + eventLogJson.toString();
            Crashlytics.log(logString);

            //Mixpanel tracking info in NOR-1016
            addMixPanelProperties(eventLogJson, eventLog);
            mMixpanel.track(eventLog.getEventType(), eventLogJson);
        }
        catch (JsonParseException | JSONException e)
        {
            Crashlytics.logException(e);
        }

        //If event log bundle is null or we've hit the max num per bundle then we create a new bundle
        if (sCurrentEventLogBundle == null || sCurrentEventLogBundle.size() >= MAX_EVENTS_PER_BUNDLE)
        {
            //Create new event log bundle and add it to the List
            sCurrentEventLogBundle = new EventLogBundle(
                    getProviderId(),
                    new ArrayList<Event>()
            );
            synchronized (BundlesWrapper.class)
            {
                BundlesWrapper.BUNDLES.add(sCurrentEventLogBundle);
            }
        }
        //Prefix event_type with app_lib_
        eventLog.setEventType("app_lib_" + eventLog.getEventType());
        sCurrentEventLogBundle.addEvent(eventLog);

        //Save the EventLogBundle to preferences always
        synchronized (BundlesWrapper.class)
        {
            saveToPreference(PrefsKey.EVENT_LOG_BUNDLES, BundlesWrapper.BUNDLES);
        }

    }

    /**
     * @param prefsKey
     * @return The list of Strings if returned, otherwise, null if nothing was saved in that pref
     * previously
     */

    private String loadSavedEventLogBundles(String prefsKey)
    {
        synchronized (mPrefsManager)
        {
            return mPrefsManager.getString(prefsKey, null);
        }
    }

    /**
     * Save the List of EventLogBundles to the prefsKey
     *
     * @param prefsKey
     * @param eventLogBundles
     */
    private void saveToPreference(String prefsKey, List<EventLogBundle> eventLogBundles)
    {
        try
        {
            mPrefsManager.setString(prefsKey, GSON.toJson(eventLogBundles));
        }
        catch (JsonParseException e)
        {
            //If there's an JsonParseException then clear the eventLogBundles because invalid json
            synchronized (BundlesWrapper.class)
            {
                BundlesWrapper.BUNDLES.clear();
            }
        }
    }

    private void removePreference(String prefsKey)
    {
        synchronized (mPrefsManager)
        {
            mPrefsManager.removeValue(prefsKey);
        }
    }

    private int getProviderId()
    {
        String providerId = mProviderManager.getLastProviderId();
        return !TextUtils.isEmpty(providerId) && TextUtils.isDigitsOnly(providerId)
                ? Integer.parseInt(providerId) : DEFAULT_USER_ID;
    }

    //************************************* handle all saving/sending of logs **********************
    private void setUploadTimer()
    {
        if (mUploadLogTimer != null)
        {
            mUploadLogTimer.cancel();
            mUploadLogTimer = null;
        }

        mUploadLogTimer = new Timer();
        mUploadLogTimer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                sendLogsFromPreference();
            }
            //Check network connection and set timer delay appropriately
        }, hasNetworkConnection() ? UPLOAD_TIMER_DELAY_MS : UPLOAD_TIMER_DELAY_NO_INTERNET_MS);
    }

    private void savePrefsToLogsOnInitialization()
    {
        String[] logPrefsKey = {PrefsKey.EVENT_LOG_BUNDLES_TO_SEND, PrefsKey.EVENT_LOG_BUNDLES};
        boolean hasNewLog = false;

        for (String prefKey : logPrefsKey)
        {
            //Check if there was logs that were to be sent but were never saved to file system
            String logBundles = loadSavedEventLogBundles(prefKey);

            if (!TextUtils.isEmpty(logBundles))
            {
                hasNewLog = true;
                //Save the previous ones to file system
                saveLogsToFileSystem(logBundles);
            }
        }

        if (hasNewLog)
        { setUploadTimer(); }
    }

    /**
     * Should be triggered from the timer
     */
    @VisibleForTesting
    void sendLogsFromPreference()
    {
        String logBundles = loadSavedEventLogBundles(PrefsKey.EVENT_LOG_BUNDLES);

        if (!TextUtils.isEmpty(logBundles))
        {
            //Save the current EventLogBundle to preferences always
            synchronized (BundlesWrapper.class)
            {
                //Save the EventLogBundle to preferences always
                saveToPreference(
                        PrefsKey.EVENT_LOG_BUNDLES_TO_SEND,
                        BundlesWrapper.BUNDLES
                );
                BundlesWrapper.BUNDLES.clear();
            }
            sCurrentEventLogBundle = null;
            //delete the old one immediately
            removePreference(PrefsKey.EVENT_LOG_BUNDLES);
        }

        // We need to retrieve the logs previously and save them into the preference as send log key
        // clear out the existing variables for the log manager
        final String prefBundleString = loadSavedEventLogBundles(PrefsKey.EVENT_LOG_BUNDLES_TO_SEND);

        //This means nothing was stored previously in prefs
        if (!TextUtils.isEmpty(prefBundleString))
        {
            //Save this to the file system and remove from original preference
            if (!TextUtils.isEmpty(prefBundleString))
            {
                //Saving the current logs to file system
                saveLogsToFileSystem(prefBundleString);
            }
        }

        sendLogs();
    }

    private synchronized void saveLogsToFileSystem(final String prefBundleString)
    {
        JsonObject[] eventLogBundles = GSON.fromJson(
                prefBundleString,
                JsonObject[].class
        );
        for (JsonObject logBundleJson : eventLogBundles)
        {
            String eventBundleId = logBundleJson.get(EventLogBundle.KEY_EVENT_BUNDLE_ID)
                    .getAsString();
            boolean fileSaved = mFileManager.saveLogFile(eventBundleId, logBundleJson.toString());
            // If the file didn't save then we log an exception
            if (!fileSaved)
            {
                Crashlytics.logException(new Exception("Failed to save log to file system: "
                        + logBundleJson.toString()));
            }
        }

        //Remove preference the preference since it either saved or failed
        removePreference(PrefsKey.EVENT_LOG_BUNDLES_TO_SEND);
    }

    /**
     * handles sending the logs
     */
    private void sendLogs()
    {
        //This is jsut in case there's an invalid json file for some reason
        File invalidFile = null;

        try
        {
            File[] files = mFileManager.getLogFileList();
            if (files == null)
            {
                //Log exception
                Crashlytics.logException(new Exception("Log Files list returns null. Should not happen"));
                //Just return. next log event will trigger timer
                return;
            }

            mSendingLogsCount = files.length;
            for (final File file : files)
            {
                invalidFile = file;
                //Get each event log bundle
                JsonObject eventLogBundle = GSON.fromJson(
                        mFileManager.readFile(file),
                        JsonObject.class
                );

                if (eventLogBundle == null)
                {
                    mFileManager.deleteLogFile(invalidFile.getName());
                    continue;
                }

                //Add the sent timestamp value
                eventLogBundle.addProperty(KEY_SENT_TIMESTAMP_SECS, System.currentTimeMillis() / 1000);

                //Upload logs
                mDataManager.postLogs(
                        eventLogBundle,
                        new DataManager.Callback<EventLogResponse>()
                        {
                            @Override
                            public void onSuccess(EventLogResponse response)
                            {
                                mFileManager.deleteLogFile(response.getBundleId());
                                finishUpload();
                            }

                            @Override
                            public void onError(DataManager.DataManagerError error)
                            {
                                finishUpload();
                            }

                            private void finishUpload()
                            {
                                //If uploads are finished
                                if (--mSendingLogsCount == 0)
                                {
                                    //If there are currently logs, set timer, else clear old timer
                                    if (!BundlesWrapper.BUNDLES.isEmpty()
                                            || mFileManager.getLogFileList().length > 0)
                                    {
                                        setUploadTimer();
                                    }
                                    else
                                    {
                                        mUploadLogTimer.cancel();
                                        mUploadLogTimer = null;
                                    }
                                }
                            }
                        }
                );
            }
        }
        catch (JsonSyntaxException e)
        {
            Crashlytics.logException(e);
            Log.e(TAG, e.getMessage());
            //If there's json exception it means logs aren't valid and clear it out
            if (invalidFile != null)
            {
                mFileManager.deleteLogFile(invalidFile.getName());
            }
            //reset log count
            mSendingLogsCount = 0;
        }
    }

    private void addMixPanelProperties(JSONObject eventLogJson, Event event) throws JSONException
    {
        addMixPanelUserSuperProperty();

        //Mixpanel tracking info in NOR-1016
        eventLogJson.put("event_context", event.getEventContext());
        eventLogJson.put("session_event_count", event.getSessionEventCount());
        eventLogJson.put("session_id", event.getSessionId());
        eventLogJson.put("platform", "android");
        eventLogJson.put("client", "android");
        eventLogJson.put("mobile", 1);

        ProviderProfile provider = mProviderManager.getCachedProviderProfile();
        if (provider != null)
        {
            ProviderPersonalInfo info = provider.getProviderPersonalInfo();
            if (info != null)
            {
                eventLogJson.put("email", info.getEmail());
                eventLogJson.put("name", info.getFirstName() + " " + info.getLastName());
            }
            eventLogJson.put("provider_id", provider.getProviderId());
            eventLogJson.put("provider_logged_in", 1);
        }
        else
        {
            eventLogJson.put("provider_logged_in", 0);
        }
    }

    private void initMixPanel()
    {
        //Set up mix panel
        String mixpanelApiKey = PropertiesReader.getConfigProperties(BaseApplication.getContext()).getProperty("mixpanel_api_key");
        mMixpanel = MixpanelAPI.getInstance(BaseApplication.getContext(), mixpanelApiKey);

        //Set up super properties for mix panel
        JSONObject superProperties = null;
        try
        {
            superProperties = new JSONObject(GSON.toJson(new EventSuperPropertiesBase()));
        }
        catch (JSONException e)
        {
            Crashlytics.logException(e);
        }

        if (mProviderManager.getCachedProviderProfile() != null)
        {
            mIsProviderLoggedIn = true;
            //Only set this on initialization. Setting it after initialization will break mixpanel
            mMixpanel.identify(String.valueOf(getProviderId()));
        }

        if (superProperties != null) { mMixpanel.registerSuperProperties(superProperties); }
    }

    private void addMixPanelUserSuperProperty()
    {

        //If user is not logged in, check if he's logged in
        if (!mIsProviderLoggedIn)
        {
            //If logged in add user id to super properties
            if (mProviderManager.getCachedProviderProfile() != null)
            {
                try
                {
                    JSONObject userIdJson = new JSONObject();
                    userIdJson.put(EventSuperProperties.PROVIDER_ID, getProviderId());
                    mMixpanel.registerSuperProperties(userIdJson);
                    mIsProviderLoggedIn = true;
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean hasNetworkConnection()
    {
        ConnectivityManager cm =
                (ConnectivityManager) BaseApplication.getContext()
                        .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private static class BundlesWrapper
    {
        static final List<EventLogBundle> BUNDLES = new ArrayList<>();
    }
}
