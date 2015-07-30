package com.handy.portal.manager;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.model.HelpNode;
import com.handy.portal.model.HelpNodeWrapper;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

public class HelpManager
{
    private final Bus bus;
    private final DataManager dataManager;

    // will change type when we want access to bookings for a specific day, right now, we're just dumping all
    private final Cache<String, HelpNode> helpNodeCache;

    @Inject
    public HelpManager(final Bus bus, final DataManager dataManager)
    {
        this.bus = bus;
        this.bus.register(this);
        this.dataManager = dataManager;

        //TODO: we don't currently have a way to query to see if a node is changed so we rely on our cache decaying every day
        this.helpNodeCache = CacheBuilder.newBuilder()
                .maximumSize(10000)
                .expireAfterWrite(1, TimeUnit.DAYS)
                .build();
    }

    @Subscribe
    public void onRequestHelpNodeDetails(HandyEvent.RequestHelpNode event)
    {
        String nodeId = event.nodeId;
        String bookingId = event.bookingId;

        //TODO: Currently we send null to request root on the server, this is a bit hacky and does not allow us to cache the root node which is silly

        if (nodeId != null) //nulls will crash our cache on the getIfPresentCall
        {
            final HelpNode cachedHelpNode = helpNodeCache.getIfPresent(nodeId);
            if (cachedHelpNode != null)
            {
                bus.post(new HandyEvent.ReceiveHelpNodeSuccess(cachedHelpNode));
                return;
            }
        }

        dataManager.getHelpInfo(nodeId, bookingId, new DataManager.Callback<HelpNodeWrapper>()
        {
            @Override
            public void onSuccess(HelpNodeWrapper helpNodeWrapper)
            {
                HelpNode helpNode = helpNodeWrapper.getHelpNode();
                helpNodeCache.put(Integer.toString(helpNode.getId()), helpNode);
                //don't cache the child nodes, they look like full data but don't have their children
                bus.post(new HandyEvent.ReceiveHelpNodeSuccess(helpNode));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                bus.post(new HandyEvent.ReceiveHelpNodeError(error));
            }
        });
    }

    @Subscribe
    public void onRequestHelpBookingNodeDetails(HandyEvent.RequestHelpBookingNode event)
    {
        String nodeId = event.nodeId;
        String bookingId = event.bookingId;

        //TODO: Currently we send null to request root on the server, this is a bit hacky and does not allow us to cache the root node which is silly

        if (nodeId != null) //nulls will crash our cache on the getIfPresentCall
        {
            final HelpNode cachedHelpNode = helpNodeCache.getIfPresent(nodeId);
            if (cachedHelpNode != null)
            {
                bus.post(new HandyEvent.ReceiveHelpBookingNodeSuccess(cachedHelpNode));
                return;
            }
        }

        dataManager.getHelpBookingsInfo(nodeId, bookingId, new DataManager.Callback<HelpNodeWrapper>()
        {
            @Override
            public void onSuccess(HelpNodeWrapper helpNodeWrapper)
            {
                HelpNode helpNode = helpNodeWrapper.getHelpNode();
                bus.post(new HandyEvent.ReceiveHelpBookingNodeSuccess(helpNode));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                bus.post(new HandyEvent.ReceiveHelpBookingNodeError(error));
            }
        });

    }
}
