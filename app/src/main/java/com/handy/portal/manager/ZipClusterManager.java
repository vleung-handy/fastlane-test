package com.handy.portal.manager;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.handy.portal.bookings.BookingEvent;
import com.handy.portal.data.DataManager;
import com.handy.portal.model.ZipClusterPolygons;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

public class ZipClusterManager
{
    private final EventBus mBus;
    private final DataManager mDataManager;

    private final Cache<String, ZipClusterPolygons> mZipClusterCache;

    //store ids of clusters we are in the process of requesting so we don't request the same cluster multiple times
    private final HashSet<String> mRequestInProgressClusters;

    @Inject
    public ZipClusterManager(final EventBus bus, final DataManager dataManager)
    {
        mBus = bus;
        mBus.register(this);
        mDataManager = dataManager;
        mZipClusterCache = CacheBuilder.newBuilder()
                .maximumSize(10000)
                .expireAfterWrite(1, TimeUnit.DAYS)
                .build();
        mRequestInProgressClusters = new HashSet<>();
    }

    @Subscribe
    public void onRequestZipClusterPolygon(final BookingEvent.RequestZipClusterPolygons event)
    {
        ZipClusterPolygons zipClusterPolygons = mZipClusterCache.getIfPresent(event.zipClusterId);
        if (zipClusterPolygons != null)
        {
            mBus.post(new BookingEvent.ReceiveZipClusterPolygonsSuccess(zipClusterPolygons));
            return;
        }

        //ignore cluster ids already being requested
        if (!mRequestInProgressClusters.contains(event.zipClusterId))
        {
            mRequestInProgressClusters.add(event.zipClusterId);
            mDataManager.getZipClusterPolygons(event.zipClusterId, new DataManager.Callback<ZipClusterPolygons>()
            {
                @Override
                public void onSuccess(ZipClusterPolygons response)
                {
                    mRequestInProgressClusters.remove(event.zipClusterId);
                    mZipClusterCache.put(event.zipClusterId, response);
                    mBus.post(new BookingEvent.ReceiveZipClusterPolygonsSuccess(response));
                }

                @Override
                public void onError(DataManager.DataManagerError error)
                {
                    mRequestInProgressClusters.remove(event.zipClusterId);
                    mBus.post(new BookingEvent.ReceiveZipClusterPolygonsError(error));
                }
            });
        }
    }
}
