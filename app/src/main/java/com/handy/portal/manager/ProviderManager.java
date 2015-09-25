package com.handy.portal.manager;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.handy.portal.constant.PrefsKey;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.model.Provider;
import com.handy.portal.model.SuccessWrapper;
import com.handy.portal.util.DateTimeUtils;
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

    //TODO: use a formal/common system?
    private long timestampPromptedUserUpdatePaymentInfoMs = 0;
    private final long intervalPromptUpdatePaymentInfoMs = DateTimeUtils.MILLISECONDS_IN_HOUR;

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
    public void onRequestShouldUserUpdatePaymentInfo(HandyEvent.RequestShouldUserUpdatePaymentInfo event)
    {
        //TODO: this is for testing only. replace with real network call

        if(System.currentTimeMillis() - timestampPromptedUserUpdatePaymentInfoMs > intervalPromptUpdatePaymentInfoMs)
        {
            timestampPromptedUserUpdatePaymentInfoMs = System.currentTimeMillis();
            bus.post(new HandyEvent.ReceiveShouldUserUpdatePaymentInfo(true));
        }
        else
        {
            bus.post(new HandyEvent.ReceiveShouldUserUpdatePaymentInfo(false));
        }
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
    public void onSendIncomeVerification(HandyEvent.RequestSendIncomeVerification event)
    {
        String providerId = prefsManager.getString(PrefsKey.LAST_PROVIDER_ID);

        dataManager.sendIncomeVerification(providerId, new DataManager.Callback<SuccessWrapper>()
        {
            @Override
            public void onSuccess(SuccessWrapper response)
            {
                if (response.getSuccess())
                {
                    bus.post(new HandyEvent.ReceiveSendIncomeVerificationSuccess());
                }
                else
                {
                    bus.post(new HandyEvent.ReceiveSendIncomeVerificationError());
                }
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                bus.post(new HandyEvent.ReceiveSendIncomeVerificationError());
            }
        });
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
