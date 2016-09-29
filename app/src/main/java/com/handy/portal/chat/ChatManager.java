package com.handy.portal.chat;


import com.handy.portal.data.DataManager;

import javax.inject.Inject;

public class ChatManager
{
    private org.greenrobot.eventbus.EventBus mBus;
    private final DataManager mDataManager;

    @Inject
    public ChatManager(final org.greenrobot.eventbus.EventBus bus, final DataManager dataManager)
    {
        mBus = bus;
        mDataManager = dataManager;
        mBus.register(this);
    }

//    @Subscribe
//    public void onRequestLayerAuthToken(ChatEvent.RequestLayerAuthTokenEvent event)
//    {
//        mDataManager.getLayerAuthToken(
//                event.userId,
//                event.nonce,
//                new DataManager.Callback<LayerResponseWrapper>()
//                {
//                    @Override
//                    public void onSuccess(final LayerResponseWrapper response)
//                    {
//                        mBus.post(new ChatEvent.ReceiveLayerAuthTokenSuccessEvent(response));
//                    }
//
//                    @Override
//                    public void onError(final DataManager.DataManagerError error)
//                    {
//                        mBus.post(new ChatEvent.ReceiveLayerAuthTokenErrorEvent(error));
//                    }
//                }
//        );
//    }
}
