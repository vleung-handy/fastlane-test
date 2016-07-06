package com.handy.portal.data;

import com.handy.portal.constant.ProviderKey;
import com.handy.portal.model.ProviderPersonalInfo;
import com.handy.portal.model.TypeSafeMap;
import com.handy.portal.retrofit.HandyRetrofitEndpoint;
import com.handy.portal.retrofit.HandyRetrofitService;
import com.handy.portal.retrofit.logevents.EventLogService;
import com.handy.portal.retrofit.stripe.StripeRetrofitService;

public class TestDataManager extends DataManager
{
    public TestDataManager(final HandyRetrofitService service, final HandyRetrofitEndpoint endpoint, final StripeRetrofitService stripeService, final EventLogService eventLogService)
    {
        super(service, endpoint, stripeService, eventLogService);
    }

    @Override
    public void updateProviderProfile(final String providerId, final TypeSafeMap<ProviderKey> params, final Callback<ProviderPersonalInfo> cb) { }
}

