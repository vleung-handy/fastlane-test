package com.handy.portal.manager;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.help.HelpNode;
import com.handy.portal.help.HelpNodeWrapper;
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

        this.helpNodeCache = CacheBuilder.newBuilder()
                .weakKeys()
                .maximumSize(10000)
                .expireAfterWrite(1, TimeUnit.HOURS)
                .build();

        System.out.println("Constructed our help manager");
    }

    @Subscribe
    public void onRequestHelpNodeDetails(HandyEvent.RequestHelpNode event)
    {
        String nodeId = event.nodeId;
        String bookingId = event.bookingId;

        if(helpNodeCache == null)
        {
            System.err.println("Our help node cache didn't get fired up?");
        }

        final HelpNode helpNode = helpNodeCache.getIfPresent(nodeId);
        if (helpNode != null)
        {
            bus.post(new HandyEvent.ReceiveHelpNodeSuccess(helpNode));
        }
        else
        {
            dataManager.getHelpInfo(nodeId, bookingId, new DataManager.Callback<HelpNodeWrapper>()
            {
                @Override
                public void onSuccess(HelpNodeWrapper helpNodeWrapper)
                {
                    HelpNode helpNode = helpNodeWrapper.getHelpNode();
                    bus.post(new HandyEvent.ReceiveHelpNodeSuccess(helpNode));
                }

                @Override
                public void onError(DataManager.DataManagerError error)
                {
                    bus.post(new HandyEvent.ReceiveHelpNodeError(error));

                    if(error.getType() != DataManager.DataManagerError.Type.NETWORK)
                    {
                        //TODO: should we invalidate cache?
                    }
                }
            });
        }

    }

    @Subscribe
    public void onRequestHelpBookingNodeDetails(HandyEvent.RequestHelpBookingNode event)
    {
        String nodeId = event.nodeId;
        String bookingId = event.bookingId;

        final HelpNode helpNode = helpNodeCache.getIfPresent(nodeId);
        if (helpNode != null)
        {
            bus.post(new HandyEvent.ReceiveHelpBookingNodeSuccess(helpNode));
        }
        else
        {
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

                    if (error.getType() != DataManager.DataManagerError.Type.NETWORK)
                    {
                        //TODO: should we invalidate cache?
                    }
                }
            });
        }
    }
}
