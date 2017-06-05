package com.handy.portal.availability.manager;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.handy.portal.availability.AvailabilityEvent;
import com.handy.portal.availability.model.Availability;
import com.handy.portal.core.manager.ProviderManager;
import com.handy.portal.data.DataManager;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;
import java.util.HashMap;

import javax.inject.Inject;

public class AvailabilityManager {

    private final EventBus mBus;
    private final DataManager mDataManager;
    private final ProviderManager mProviderManager;

    private Availability.Wrapper.WeekRanges mWeekRangesWrapper;
    private HashMap<Date, Availability.AdhocTimeline> mUpdatedTimelines;

    @Inject
    public AvailabilityManager(
            final EventBus bus,
            final DataManager dataManager,
            final ProviderManager providerManager
    ) {
        mBus = bus;
        mDataManager = dataManager;
        mProviderManager = providerManager;
        mUpdatedTimelines = new HashMap<>();
    }

    public void getAvailability(
            final boolean useCacheIfPresent,
            @Nullable final DataManager.Callback<Void> callback
    ) {
        if (useCacheIfPresent && isReady()) {
            if (callback != null) {
                callback.onSuccess(null);
            }
        }
        else {
            mDataManager.getAvailability(
                    mProviderManager.getLastProviderId(),
                    new DataManager.Callback<Availability.Wrapper.WeekRanges>() {
                        @Override
                        public void onSuccess(
                                final Availability.Wrapper.WeekRanges weekRangesWrapper
                        ) {
                            invalidateData();
                            mWeekRangesWrapper = weekRangesWrapper;
                            if (callback != null) {
                                callback.onSuccess(null);
                            }
                        }

                        @Override
                        public void onError(final DataManager.DataManagerError error) {
                            if (callback != null) {
                                callback.onError(error);
                            }
                        }
                    }
            );
        }
    }

    public void saveAvailability(
            final Availability.Wrapper.AdhocTimelines timelinesWrapper,
            @Nullable final DataManager.Callback<Void> callback
    ) {
        mDataManager.saveAvailability(
                mProviderManager.getLastProviderId(),
                timelinesWrapper,
                new DataManager.Callback<Void>() {
                    @Override
                    public void onSuccess(final Void response) {
                        for (final Availability.AdhocTimeline timeline : timelinesWrapper.get()) {
                            mUpdatedTimelines.put(timeline.getDate(), timeline);
                            mBus.post(new AvailabilityEvent.AdhocTimelineUpdated(timeline));
                        }
                        if (callback != null) {
                            callback.onSuccess(response);
                        }
                    }

                    @Override
                    public void onError(final DataManager.DataManagerError error) {
                        if (callback != null) {
                            callback.onError(error);
                        }
                    }
                }
        );
    }

    public void getAvailabilityTemplate(
            @Nullable final DataManager.Callback<Availability.Wrapper.TemplateTimelines> callback
    ) {
        mDataManager.getAvailabilityTemplate(mProviderManager.getLastProviderId(), callback);
    }

    public void saveAvailabilityTemplate(
            final Availability.Wrapper.TemplateTimelines timelinesWrapper,
            @Nullable final DataManager.Callback<Void> callback
    ) {
        mDataManager.saveAvailabilityTemplate(
                mProviderManager.getLastProviderId(),
                timelinesWrapper,
                new DataManager.Callback<Void>() {
                    @Override
                    public void onSuccess(final Void response) {
                        // Refresh availability cache
                        getAvailability(false, new DataManager.Callback<Void>() {
                            @Override
                            public void onSuccess(final Void response) {
                                finalizeSaveAvailabilityTemplate(timelinesWrapper, callback);
                            }

                            @Override
                            public void onError(final DataManager.DataManagerError error) {
                                // Since we're only trying to refresh availability cache here, it's
                                // totally ok if it errors. We'll consider this a success because
                                // because the original request succeeded.
                                finalizeSaveAvailabilityTemplate(timelinesWrapper, callback);
                            }
                        });
                    }

                    @Override
                    public void onError(final DataManager.DataManagerError error) {
                        if (callback != null) {
                            callback.onError(error);
                        }
                    }
                }
        );
    }

    private void finalizeSaveAvailabilityTemplate(
            final Availability.Wrapper.TemplateTimelines timelinesWrapper,
            @Nullable final DataManager.Callback<Void> callback
    ) {
        for (final Availability.TemplateTimeline timeline : timelinesWrapper.get()) {
            mBus.post(new AvailabilityEvent.TemplateTimelineUpdated(timeline));
        }
        if (callback != null) {
            callback.onSuccess(null);
        }
    }

    @Nullable
    public Availability.AdhocTimeline getTimelineForDate(@NonNull final Date date) {
        Availability.AdhocTimeline timeline = mUpdatedTimelines.get(date);
        if (mWeekRangesWrapper != null && timeline == null) {
            timeline = mWeekRangesWrapper.getTimelineForDate(date);
        }
        return timeline;
    }

    public boolean hasAvailableHours() {
        for (final Availability.Range range : mWeekRangesWrapper.get()) {
            for (final Date date : range.dates()) {
                final Availability.AdhocTimeline timeline = getTimelineForDate(date);
                if (timeline != null && timeline.hasIntervals()) {
                    return true;
                }
            }
        }
        return false;
    }

    // Determines whether the manager has information about the given date.
    public boolean covers(final Date date) {
        return mWeekRangesWrapper.covers(date);
    }

    public Availability.Range getCurrentWeekRange() {
        return mWeekRangesWrapper.get().get(0); // server will always return 2 or more weeks
    }

    public Availability.Range getNextWeekRange() {
        return mWeekRangesWrapper.get().get(1); // server will always return 2 or more weeks
    }

    public void invalidateData() {
        mWeekRangesWrapper = null;
        mUpdatedTimelines.clear();
    }

    public boolean isReady() {
        return mWeekRangesWrapper != null;
    }
}
