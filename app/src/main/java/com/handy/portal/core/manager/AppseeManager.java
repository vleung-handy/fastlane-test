package com.handy.portal.core.manager;

import android.support.annotation.NonNull;
import android.view.View;

import com.appsee.Appsee;
import com.crashlytics.android.Crashlytics;
import com.google.common.base.Strings;
import com.handy.portal.core.event.HandyEvent;
import com.handy.portal.core.model.ConfigurationResponse;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * wrapping Appsee in a manager in case we want more control over it ex. easily toggle it on for
 * specific users only via configs, or catch exceptions
 */
public class AppseeManager
{
    private final String mAppSeeApiKey;
    private ConfigManager mConfigManager;
    private ProviderManager mProviderManager;
    private FileManager mFileManager;

    /**
     * we won't enable appsee recording if device doesn't have at least this much storage space
     */
    private static final long LOW_STORAGE_SPACE_THRESHOLD_MEGABYTES = 500; //500mb

    public AppseeManager(
            @NonNull final String appseeApiKey,
            @NonNull final ConfigManager configurationManager,
            @NonNull final ProviderManager providerManager,
            @NonNull final FileManager fileManager,
            @NonNull final EventBus eventBus
            )
    {
        mAppSeeApiKey = appseeApiKey;
        mConfigManager = configurationManager;
        mFileManager = fileManager;
        mProviderManager = providerManager;
        eventBus.register(this);
    }

    /**
     * Appsee appears to use default internal storage directory for video storage
     *
     * this method should not cause a crash as all exceptions are caught
     * @return
     */
    private boolean isEnoughSpaceAvailableForRecording()
    {
        long freeSpaceBytes = mFileManager.getInternalStorageDirectoryFreeSpaceBytes();
        if(freeSpaceBytes < 0)
        {
            addAppseeEvent("unable to get files directory free space");
        }
        long freeSpaceMegabytes = freeSpaceBytes/1000000;
        return freeSpaceMegabytes >= LOW_STORAGE_SPACE_THRESHOLD_MEGABYTES;
    }

    private void addAppseeEvent(String eventText)
    {
        try
        {
            //although Appsee.addEvent() already has a try/catch, there can be an exception in the catch
            Appsee.addEvent(eventText);
        }
        catch (Exception e)
        {
            Crashlytics.logException(e);
        }
    }

    /**
     * @return whether Appsee.start() can be called
     */
    public boolean isAppseeEnabled()
    {
        ConfigurationResponse configuration = mConfigManager.getConfigurationResponse();
        boolean isEnoughStorageSpaceAvailableForRecording = isEnoughSpaceAvailableForRecording();
        if(!isEnoughStorageSpaceAvailableForRecording)
        {
            String logErrorMessage = "not enabling Appsee - low disk space (<" + LOW_STORAGE_SPACE_THRESHOLD_MEGABYTES + "mb)";
            Crashlytics.logException(new Exception(logErrorMessage));
            addAppseeEvent(logErrorMessage); //log in Appsee so we don't get confused when no recording
        }
        /*
        need to enable if user not logged in because we can't get config response then
        business always wants pre-login flows to be recorded
         */
        return (configuration != null
                && configuration.isAppseeAnalyticsEnabled()
                && isEnoughStorageSpaceAvailableForRecording)
                || !isUserLoggedIn();
    }

    /**
     * TODO this is not a sure way of knowing that provider is logged in
     * they can have this in their prefs but not be authenticated
     * not putting in preferences manager because of this uncertainty
     * @return
     */
    private boolean isUserLoggedIn()
    {
        return !Strings.isNullOrEmpty(mProviderManager.getLastProviderId());
    }

    /**
     * starts recording the screen if Appsee is enabled
     * (based on resulting videos, starting recording again after already started will have no effect)
     * else stops recording the screen
     *
     * according to docs, should ONLY be called from Activity.onCreate() or Activity.onResume()
     */
    public void startOrStopRecordingAsNecessary()
    {
        if (isAppseeEnabled())
        {
            //start appsee recording
            startRecording();
            updateUserId();
            return;
        }

        stopRecording();
        //in case configuration changed from recording enabled -> disabled

    }

    /**
     * should call after recording start, and when active provider changes
     *
     * TODO is this harmless if recording isn't started? also can it be called any time in lifecycle?
     */
    private void updateUserId()
    {
        //set user id if present
//        Appsee.setUserId(mProviderManager.getLastProviderId());
        //TODO uncomment when legal says this is OK
        //TODO verify on dashboard that this shows up correctly

    }

    /**
     * start recording the screen
     * does nothing if recording was already started
     */
    private void startRecording()
    {
        try
        {
            //although Appsee.start() already has a try/catch, there can be an exception in the catch
            Appsee.start(mAppSeeApiKey);
        }
        catch (Exception e)
        {
            Crashlytics.logException(e);
        }
    }

    /**
     * stops recording the screen
     * does nothing if recording was not started
     */
    private void stopRecording()
    {
        try
        {
            //although Appsee.stop() already has a try/catch, there can be an exception in the catch
            Appsee.stop();
        }
        catch (Exception e)
        {
            Crashlytics.logException(e);
        }
    }

    /**
     * marks the given views as sensitive so that they are blocked in the Appsee recordings
     * NOTE: looks like all edit text views are blocked out of the recording by default
     * <p>
     * does nothing if recording was not started
     *
     * @param views
     */
    public static void markViewsAsSensitive(View... views)
    {
        for (View view : views)
        {
            try
            {
                Appsee.markViewAsSensitive(view);
            }
            catch (Exception e)
            {
                Crashlytics.logException(e);
            }
        }
    }

    @Subscribe
    public void onProviderIdUpdated(HandyEvent.ProviderIdUpdated event)
    {
        updateUserId();
    }
}
