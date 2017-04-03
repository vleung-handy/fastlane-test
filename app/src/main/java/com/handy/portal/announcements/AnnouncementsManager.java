package com.handy.portal.announcements;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.crashlytics.android.Crashlytics;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.handy.portal.announcements.model.Announcement;
import com.handy.portal.announcements.model.AnnouncementShownRecord;
import com.handy.portal.announcements.model.AnnouncementsWrapper;
import com.handy.portal.announcements.model.CurrentAnnouncementsRequest;
import com.handy.portal.core.constant.PrefsKey;
import com.handy.portal.core.event.HandyEvent;
import com.handy.portal.core.manager.ConfigManager;
import com.handy.portal.core.manager.PrefsManager;
import com.handy.portal.data.DataManager;
import com.handy.portal.library.util.IOUtils;

import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import static com.stripe.net.APIResource.GSON;

/**
 * responsible for getting announcements from the server
 * and keeping track of ones that the user has already seen
 */
public class AnnouncementsManager {
    private final Context mContext;
    private final PrefsManager mPrefsManager;
    private final DataManager mDataManager;
    private final ConfigManager mConfigManager;

    /**
     * used for serialization/deserialization of the internal announcement records in prefs
     */
    private static final Type INTERNAL_ANNOUNCEMENT_RECORD_TYPE = new TypeToken<LinkedHashMap<String, AnnouncementShownRecord>>() {}.getType();
    /**
     * this is hacky, but we agreed to do this as a quick-fix to prevent records from accumulating
     * <p>
     * represents the max num of announcement records to keep in prefs and send to server
     * there will never be more than this amount configured in dash
     */
    private static final int MAX_NUM_ANNOUNCEMENT_RECORDS = 20;
    /**
     * represents how long the current announcements should be cached for in memory
     */
    private static long ANNOUNCEMENTS_CACHE_DURATION_MS = TimeUnit.HOURS.toMillis(3);

    /**
     * the last time that the current announcements were refreshed
     */
    private long mAnnouncementsLastRefreshedTimeMs = 0;

    /**
     * map of announcement trigger context to list of announcements to display
     */
    private HashMap<Announcement.TriggerContext, List<Announcement>> mTriggerContextToAnnouncementListMap;

    @Inject
    public AnnouncementsManager(
            final Context context,
            final DataManager dataManager,
            final PrefsManager prefsManager,
            final ConfigManager configManager
    ) {
        mContext = context;
        mDataManager = dataManager;
        mPrefsManager = prefsManager;
        mConfigManager = configManager;
    }

    /**
     * invalidate cached announcements
     */
    public void invalidateCachedAnnouncements() {
        mTriggerContextToAnnouncementListMap = null;
    }

    /**
     * gets announcements from the server
     */
    private void getAnnouncements(@NonNull CurrentAnnouncementsRequest currentAnnouncementsRequest, DataManager.Callback<AnnouncementsWrapper> callback) {
        //todo revert
        mDataManager.getCurrentAnnouncements(currentAnnouncementsRequest, callback);
//        try {
//            String json = IOUtils.loadJSONFromAsset(mContext, "test_announcements.json");
//            AnnouncementsWrapper announcementsWrapper = new GsonBuilder().create().fromJson(json, AnnouncementsWrapper.class);
//            callback.onSuccess(announcementsWrapper);
//            return;
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//        }
//        callback.onError(new DataManager.DataManagerError(DataManager.DataManagerError.Type.CLIENT));

    }

    /**
     * refresh the announcements cache by re-fetching from the server
     */
    public void updateCachedAnnouncements(@Nullable final DataManager.Callback<HashMap<Announcement.TriggerContext, List<Announcement>>> callback) {
        try {
            if (!areAnnouncementsEnabled()) {
                if (callback != null) {
                    callback.onSuccess(null);
                }
                return;
            }
            getAnnouncements(getCurrentAnnouncementsRequest(), new DataManager.Callback<AnnouncementsWrapper>() {
                @Override
                public void onSuccess(final AnnouncementsWrapper response) {
                    //clear the announcements cache
                    if (mTriggerContextToAnnouncementListMap == null) {
                        mTriggerContextToAnnouncementListMap = new HashMap<>();
                    }
                    else {
                        mTriggerContextToAnnouncementListMap.clear();
                    }

                    //group the announcements by trigger context
                    for (Announcement announcement : response.getAnnouncements()) {
                        Announcement.TriggerContext triggerContext
                                = announcement.getTriggerContext();
                        if (mTriggerContextToAnnouncementListMap.get(triggerContext) == null) {
                            mTriggerContextToAnnouncementListMap.put(triggerContext, new LinkedList<Announcement>());
                        }

                        //put this announcement in the bucket associated with its trigger context
                        mTriggerContextToAnnouncementListMap.get(triggerContext).add(announcement);
                    }

                    //update the announcements refreshed time
                    mAnnouncementsLastRefreshedTimeMs = System.currentTimeMillis();

                    if (callback != null) {
                        callback.onSuccess(mTriggerContextToAnnouncementListMap);
                    }
                }

                @Override
                public void onError(final DataManager.DataManagerError error) {
                    if (callback != null) {
                        callback.onError(error);
                    }
                }
            });

        }
        catch (Exception e) {
            e.printStackTrace();
            if (callback != null) {
                callback.onError(new DataManager.DataManagerError(DataManager.DataManagerError.Type.CLIENT, e.getMessage()));
            }
        }
    }

    /**
     * @return true if the announcements feature is enabled
     */
    private boolean areAnnouncementsEnabled() {
        return mConfigManager.getConfigurationResponse() != null
                && mConfigManager.getConfigurationResponse().areAnnouncementsEnabled();
    }

    /**
     * @return true if the in-memory announcements cache is valid
     */
    private boolean isAnnouncementCacheValid() {
        return mTriggerContextToAnnouncementListMap != null
                && (System.currentTimeMillis() - mAnnouncementsLastRefreshedTimeMs < ANNOUNCEMENTS_CACHE_DURATION_MS)
                && areAnnouncementsEnabled();
    }

    /**
     * passes a list of announcements in the given callback's onSuccess(), for the given trigger context
     */
    public void getAnnouncementsForTriggerContext(@Nullable final Announcement.TriggerContext triggerContext,
                                                  @NonNull final DataManager.Callback<List<Announcement>> callback) {
        if (isAnnouncementCacheValid()) {
            //announcements are cached
            callback.onSuccess(mTriggerContextToAnnouncementListMap.get(triggerContext));
        }
        else {
            //if announcements not cached
            updateCachedAnnouncements(new DataManager.Callback<HashMap<Announcement.TriggerContext, List<Announcement>>>() {
                @Override
                public void onSuccess(final HashMap<Announcement.TriggerContext, List<Announcement>> response) {
                    callback.onSuccess(response.get(triggerContext));
                }

                @Override
                public void onError(final DataManager.DataManagerError error) {
                    callback.onError(error);
                }
            });
        }
    }

    /**
     * marks the given announcement as shown in local prefs
     *
     * @param announcement
     */
    public void markAnnouncementAsShown(@NonNull Announcement announcement) {
        long epochTimestampSeconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        AnnouncementShownRecord announcementShownRecord
                = new AnnouncementShownRecord(announcement.getId(), epochTimestampSeconds);

        //get current records from prefs
        LinkedHashMap<String, AnnouncementShownRecord>
                idToRecordMap = getInternalAnnouncementShownRecords();
        if (idToRecordMap == null) {
            idToRecordMap = new LinkedHashMap<>();
        }
        if (idToRecordMap.get(announcement.getId()) != null) {
            //entry already exists. remove it before inserting so that we can preserve the time order
            idToRecordMap.remove(announcement.getId());
        }
        idToRecordMap.put(announcement.getId(), announcementShownRecord);

        /*
        ensure that we only have the N most recent records
        the linked hashmap is ordered by record timestamp, from oldest to newest
         */
        Iterator<Map.Entry<String, AnnouncementShownRecord>> iterator = idToRecordMap.entrySet().iterator();
        while (iterator.hasNext()) {
            if (idToRecordMap.size() <= MAX_NUM_ANNOUNCEMENT_RECORDS) {
                //we are already below the records limit. no need to remove any more
                break;
            }

            //remove the oldest record
            iterator.next();
            iterator.remove();
        }
        /*
        write records to prefs

        note: not ideal to write the entire records set, but we are only keeping a limited amount,
        and implementation time is limited so it is somewhat hacky
         */
        String json = GSON.toJson(idToRecordMap, INTERNAL_ANNOUNCEMENT_RECORD_TYPE);
        mPrefsManager.setString(PrefsKey.ANNOUNCEMENT_RECORDS, json);
    }

    /**
     * @return ordered map of announcements records user has already seen, from local prefs
     */
    @Nullable
    private LinkedHashMap<String, AnnouncementShownRecord> getInternalAnnouncementShownRecords() {
        String json = mPrefsManager.getString(PrefsKey.ANNOUNCEMENT_RECORDS, null);
        try {
            return GSON.fromJson(json, INTERNAL_ANNOUNCEMENT_RECORD_TYPE);
        }
        catch (Exception e) {
            Crashlytics.logException(e);
        }
        return null;
    }

    /**
     * this will be sent to the server
     *
     * @return a model representing a request for current announcements from the server
     */
    @NonNull
    private CurrentAnnouncementsRequest getCurrentAnnouncementsRequest() {
        HashMap<String, AnnouncementShownRecord> idToRecordMap = getInternalAnnouncementShownRecords();
        if (idToRecordMap == null) { return new CurrentAnnouncementsRequest(null); }
        return new CurrentAnnouncementsRequest(new LinkedList<>(idToRecordMap.values()));
    }


    /**
     * logged-in user may have different announcements
     */
    @Subscribe
    public void onUserLoggedIn(HandyEvent.ReceiveLoginSuccess event)
    {
        invalidateCachedAnnouncements();

        //prefetch the announcements so we can show them without delay when needed
        updateCachedAnnouncements(null);
    }

    /**
     * clear all announcements including those in prefs when the user is logged out
     *
     * @param event
     */
    @Subscribe
    public void onUserLoggedOut(HandyEvent.UserLoggedOut event) {
        clear();
    }

    /**
     * clear announcements data from prefs and memory
     */
    private void clear() {
        mTriggerContextToAnnouncementListMap = null;
        mPrefsManager.setString(PrefsKey.ANNOUNCEMENT_RECORDS, null);
    }
}
