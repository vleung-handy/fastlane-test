package com.handy.portal.helpcenter.manager;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.helpcenter.model.HelpNode;
import com.handy.portal.helpcenter.model.HelpNodeWrapper;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

public class HelpManager
{
    private final Bus bus;
    private final DataManager dataManager;

    private static final String PAYMENTS_NODE_ID_KEY = "payments_node_id";
    private static final String ROOT_NODE_ID_KEY = "root_node_id";

    // will change type when we want access to bookings for a specific day, right now, we're just dumping all
    private final Cache<String, HelpNode> helpNodeCache;
    private final Cache<String, String> helpNodeIdCache;

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
        this.helpNodeIdCache = CacheBuilder.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(1, TimeUnit.DAYS)
            .build();
    }

    @Subscribe
    public void onRequestHelpNodeDetails(HandyEvent.RequestHelpNode event)
    {
        final boolean requestingRootNote = event.nodeId == null;
        String nodeId = requestingRootNote ? helpNodeIdCache.getIfPresent(ROOT_NODE_ID_KEY) : event.nodeId;
        String bookingId = event.bookingId;

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
                if (requestingRootNote)
                {
                    helpNodeIdCache.put(ROOT_NODE_ID_KEY, Integer.toString(helpNode.getId()));
                }
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

        // We don't not cache help node for booking
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

    @Subscribe
    public void onRequestHelpPaymentsNode(HandyEvent.RequestHelpPaymentsNode event)
    {
        String cachedPaymentsSupportId = helpNodeIdCache.getIfPresent(PAYMENTS_NODE_ID_KEY);
        if (cachedPaymentsSupportId != null) //nulls will crash our cache on the getIfPresentCall
        {
            final HelpNode cachedHelpNode = helpNodeCache.getIfPresent(cachedPaymentsSupportId);
            if (cachedHelpNode != null)
            {
                bus.post(new HandyEvent.ReceiveHelpPaymentsNodeSuccess(cachedHelpNode));
                return;
            }
        }
        dataManager.getHelpPaymentsInfo(new DataManager.Callback<HelpNodeWrapper>()
        {
            @Override
            public void onSuccess(HelpNodeWrapper helpNodeWrapper)
            {
                HelpNode helpNode = helpNodeWrapper.getHelpNode();
                String cachedPaymentsSupportId = Integer.toString(helpNode.getId());
                helpNodeCache.put(cachedPaymentsSupportId, helpNode);
                helpNodeIdCache.put(PAYMENTS_NODE_ID_KEY, cachedPaymentsSupportId);
                bus.post(new HandyEvent.ReceiveHelpPaymentsNodeSuccess(helpNode));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                bus.post(new HandyEvent.ReceiveHelpPaymentsNodeError(error));
            }
        });
    }
}
