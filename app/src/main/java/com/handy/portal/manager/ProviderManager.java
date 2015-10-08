package com.handy.portal.manager;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.handy.portal.constant.PrefsKey;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.PaymentEvents;
import com.handy.portal.model.Provider;
import com.handy.portal.model.ProviderProfile;
import com.handy.portal.model.SuccessWrapper;
import com.handy.portal.model.payments.PaymentFlowResponse;
import com.squareup.otto.Bus;
import com.squareup.otto.Produce;
import com.squareup.otto.Subscribe;

import java.util.concurrent.TimeUnit;

public class ProviderManager
{
    private final Bus bus;
    private final DataManager dataManager;
    private final PrefsManager prefsManager;
    private Cache<String, Provider> providerCache;
    private static final String PROVIDER_CACHE_KEY = "provider";

    private ProviderProfile providerProfile;

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
    public void onRequestPaymentFlow(PaymentEvents.RequestPaymentFlow event)
    {
        String providerId = prefsManager.getString(PrefsKey.LAST_PROVIDER_ID);
        dataManager.getPaymentFlow(providerId, new DataManager.Callback<PaymentFlowResponse>()
        {
            @Override
            public void onSuccess(PaymentFlowResponse response)
            {
                bus.post(new PaymentEvents.ReceivePaymentFlowSuccess(response));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                bus.post(new PaymentEvents.ReceivePaymentFlowError(error));

            }
        });
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

    @Produce
    public HandyEvent.ReceiveProviderProfileSuccess produceProviderProfile()
    {
        if (providerProfile != null)
        {
            return new HandyEvent.ReceiveProviderProfileSuccess(providerProfile);
        }
        return null;
    }

    @Subscribe
    public void onRequestProviderProfile(HandyEvent.RequestProviderProfile event)
    {
        String providerId = prefsManager.getString(PrefsKey.LAST_PROVIDER_ID);

        dataManager.getProviderProfile(providerId, new DataManager.Callback<ProviderProfile>()
        {
            @Override
            public void onSuccess(ProviderProfile providerProfile)
            {
                ProviderManager.this.providerProfile = providerProfile;
                bus.post(new HandyEvent.ReceiveProviderProfileSuccess(providerProfile));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                bus.post(new HandyEvent.ReceiveProviderProfileError());
            }
        });
    }

    @Subscribe
    public void onRequestResupplyKit(HandyEvent.RequestSendResupplyKit event)
    {
        String providerId = prefsManager.getString(PrefsKey.LAST_PROVIDER_ID);

        dataManager.getResupplyKit(providerId, new DataManager.Callback<SuccessWrapper>()
        {
            @Override
            public void onSuccess(SuccessWrapper response)
            {
                if (response.getSuccess())
                {
                    bus.post(new HandyEvent.ReceiveSendResupplyKitSuccess());
                }
                else
                {
                    bus.post(new HandyEvent.ReceiveSendResupplyKitError());
                }
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                bus.post(new HandyEvent.ReceiveSendResupplyKitError());
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
