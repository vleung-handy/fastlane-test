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
    private Provider activeProvider;

    public ProviderManager(final Bus bus, final DataManager dataManager, final PrefsManager prefsManager)
    {
        this.bus = bus;
        this.dataManager = dataManager;
        this.prefsManager = prefsManager;
        bus.register(this);
    }

    @Subscribe
    public void onRequestProviderInfo(HandyEvent.RequestProviderInfo event)
    {
        dataManager.getProviderInfo(new DataManager.Callback<Provider>()
        {
            @Override
            public void onSuccess(Provider provider)//TODO: need a way to sync this and provider id received from onLoginSuccess!
            {
                setActiveProvider(provider);
                bus.post(new HandyEvent.ReceiveProviderInfoSuccess(getActiveProvider()));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                bus.post(new HandyEvent.ReceiveProviderInfoError(error));
            }
        });
    }

    private void setActiveProvider(Provider provider)
    {
        activeProvider = provider;
        prefsManager.setString(PrefsKey.LAST_PROVIDER_ID, provider.getId());
        bus.post(new HandyEvent.ProviderIdUpdated(provider.getId()));
    }

    public Provider getActiveProvider()
    {
        return activeProvider;
    }
}
