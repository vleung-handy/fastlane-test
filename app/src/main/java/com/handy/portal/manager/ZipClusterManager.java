package com.handy.portal.manager;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.BookingEvent;
import com.handy.portal.model.ZipClusterPolygons;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

public class ZipClusterManager
{
    private final Bus mBus;
    private final DataManager mDataManager;

    private final Cache<String, ZipClusterPolygons> mZipClusterCache;

    @Inject
    public ZipClusterManager(final Bus bus, final DataManager dataManager)
    {
        mBus = bus;
        mBus.register(this);
        mDataManager = dataManager;
        mZipClusterCache = CacheBuilder.newBuilder()
                .maximumSize(10000)
                .expireAfterWrite(1, TimeUnit.DAYS)
                .build();
    }

    public ZipClusterPolygons getCachedPolygons(String id)
    {
        if (id == null) { return null; }

        return mZipClusterCache.getIfPresent(id);
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

        mDataManager.getZipClusterPolygons(event.zipClusterId, new DataManager.Callback<ZipClusterPolygons>()
        {
            @Override
            public void onSuccess(ZipClusterPolygons response)
            {
                mZipClusterCache.put(event.zipClusterId, response);
                mBus.post(new BookingEvent.ReceiveZipClusterPolygonsSuccess(response));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                mBus.post(new BookingEvent.ReceiveZipClusterPolygonsError(error));
            }
        });
    }
}
