package com.handy.portal.manager;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.handy.portal.constant.PrefsKey;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.model.Provider;
import com.handy.portal.model.SuccessWrapper;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.concurrent.TimeUnit;

public class ProviderManager
{
    private final Bus bus;
    private final DataManager dataManager;
    private final PrefsManager prefsManager;
    private Cache<String, Provider> providerCache;
    private static final String PROVIDER_CACHE_KEY = "provider";

    public ProviderManager(final Bus bus, final DataManager dataManager, final PrefsManager prefsManager)
    {
        this.bus = bus;
        this.dataManager = dataManager;
        this.prefsManager = prefsManager;
        this.providerCache = CacheBuilder.newBuilder()
            .maximumSize(10)
            .expireAfterWrite(1, TimeUnit.DAYS)
            .build();
        bus.register(this);
    }

    public void prefetch()
    {
        requestProviderInfo();
    }

    @Subscribe
    public void onRequestProviderInfo(HandyEvent.RequestProviderInfo event)
    {
        Provider cachedProvider = getCachedActiveProvider();
        if (cachedProvider != null)
        {
            bus.post(new HandyEvent.ReceiveProviderInfoSuccess(cachedProvider));
        }
        else
        {
            requestProviderInfo();
        }
    }

    @Subscribe
    public void onSendIncomeVerification(HandyEvent.SendIncomeVerification event)
    {
        Provider cachedProvider = getCachedActiveProvider();

        if (cachedProvider != null)
        {
            dataManager.getSendIncomeVerification(cachedProvider.getId(), new DataManager.Callback<SuccessWrapper>()
            {
                @Override
                public void onSuccess(SuccessWrapper response)
                {
                    if (response.getSuccess())
                    {
                        bus.post(new HandyEvent.SendIncomeVerificationSuccess());
                    }
                    else
                    {
                        bus.post(new HandyEvent.SendIncomeVerificationError());
                    }
                }

                @Override
                public void onError(DataManager.DataManagerError error)
                {
                    bus.post(new HandyEvent.SendIncomeVerificationError());
                }
            });
        }
        else
        {
            bus.post(new HandyEvent.SendIncomeVerificationError());
        }
    }

    private void requestProviderInfo()
    {
        dataManager.getProviderInfo(new DataManager.Callback<Provider>()
        {
            @Override
            public void onSuccess(Provider provider)//TODO: need a way to sync this and provider id received from onLoginSuccess!
            {
                providerCache.put(PROVIDER_CACHE_KEY, provider);
                prefsManager.setString(PrefsKey.LAST_PROVIDER_ID, provider.getId());
                bus.post(new HandyEvent.ProviderIdUpdated(provider.getId()));
                bus.post(new HandyEvent.ReceiveProviderInfoSuccess(provider));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                bus.post(new HandyEvent.ReceiveProviderInfoError(error));
            }
        });
    }

    public Provider getCachedActiveProvider()
    {
        return providerCache.getIfPresent(PROVIDER_CACHE_KEY);
    }
}
