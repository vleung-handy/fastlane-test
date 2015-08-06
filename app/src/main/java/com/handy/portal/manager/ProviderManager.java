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
    private long timestampCacheInitializedMs;
    private long CACHE_TIME_MS = 600000; //invalidate contact info after an hour (currently can only change it via emailing us)
    //TODO: implement a more formal cache management system?

    public ProviderManager(final Bus bus, final DataManager dataManager, final PrefsManager prefsManager)
    {
        this.bus = bus;
        this.dataManager = dataManager;
        this.prefsManager = prefsManager;
        bus.register(this);
    }

    private void setCacheData(Provider provider){
        timestampCacheInitializedMs = System.currentTimeMillis();
        activeProvider = provider;
        prefsManager.setString(PrefsKey.LAST_PROVIDER_ID, provider.getId());//TODO: don't need this if we can make sure it is same as provider id from login

    }

    private boolean isCacheValid(){
        if(activeProvider == null || System.currentTimeMillis() - timestampCacheInitializedMs > CACHE_TIME_MS){
            return false;
        }
        return true;
    }

    @Subscribe
    public void onRequestUserInfo(HandyEvent.RequestProviderInfo event)
    {
        if(!isCacheValid()){
            dataManager.getProviderInfo(new DataManager.Callback<Provider>()
            {
                @Override
                public void onSuccess(Provider provider)//TODO: need a way to sync this and provider id received from onLoginSuccess!
                {
                    setCacheData(provider);
                    bus.post(new HandyEvent.ReceiveProviderInfoSuccess(getCachedActiveProvider()));
                }

                @Override
                public void onError(DataManager.DataManagerError error)
                {
                    bus.post(new HandyEvent.ReceiveProviderInfoError(error));
                }
            });
        }else{
            bus.post(new HandyEvent.ReceiveProviderInfoSuccess(getCachedActiveProvider()));
        }

    }

    public Provider getCachedActiveProvider()
    {
        return activeProvider;
    }
}
