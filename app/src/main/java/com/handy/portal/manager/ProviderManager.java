package com.handy.portal.manager;

import com.handy.portal.constant.PrefsKey;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.model.Provider;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

public class ProviderManager
{
    private final Bus bus;
    private final DataManager dataManager;
    private final PrefsManager prefsManager;

    public ProviderManager(final Bus bus, final DataManager dataManager, final PrefsManager prefsManager)
    {
        this.bus = bus;
        this.dataManager = dataManager;
        this.prefsManager = prefsManager;
        bus.register(this);
    }

    @Subscribe
    public void onRequestUserInfo(HandyEvent.RequestUserInfo event)
    {
        dataManager.getProviderInfo(new DataManager.Callback<Provider>()
        {
            @Override
            public void onSuccess(Provider provider)
            {
                prefsManager.setString(PrefsKey.USER_CREDENTIALS_ID, provider.getId());
                bus.post(new HandyEvent.ReceiveUserInfoSuccess(provider));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                bus.post(new HandyEvent.ReceiveUserInfoError(error));
            }
        });
    }
}
