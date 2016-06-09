package com.handy.portal.manager;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.crashlytics.android.Crashlytics;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.handy.portal.constant.PrefsKey;
import com.handy.portal.constant.ProviderKey;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.ProfileEvent;
import com.handy.portal.event.ProviderDashboardEvent;
import com.handy.portal.event.ProviderSettingsEvent;
import com.handy.portal.library.util.TextUtils;
import com.handy.portal.model.Provider;
import com.handy.portal.model.ProviderPersonalInfo;
import com.handy.portal.model.ProviderProfile;
import com.handy.portal.model.ProviderSettings;
import com.handy.portal.model.SuccessWrapper;
import com.handy.portal.model.TypeSafeMap;
import com.handy.portal.model.dashboard.ProviderEvaluation;
import com.handy.portal.model.dashboard.ProviderFeedback;
import com.handy.portal.model.dashboard.ProviderRating;
import com.handy.portal.payments.PaymentEvent;
import com.handy.portal.payments.model.PaymentFlow;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ProviderManager
{
    private final Bus mBus;
    private final DataManager mDataManager;
    private final PrefsManager mPrefsManager;
    private Cache<String, Provider> mProviderCache;
    private static final String PROVIDER_CACHE_KEY = "provider";
    private Cache<String, ProviderProfile> mProviderProfileCache;
    private static final String PROVIDER_PROFILE_CACHE_KEY = "provider_profile";
    private Cache<String, ProviderSettings> mProviderSettingsCache;
    private static final String PROVIDER_SETTINGS_CACHE_KEY = "provider_settings";
    private static final String RATINGS_KEY = "ratings";

    public ProviderManager(final Bus bus, final DataManager dataManager, final PrefsManager prefsManager)
    {
        mBus = bus;
        mDataManager = dataManager;
        mPrefsManager = prefsManager;
        mProviderCache = CacheBuilder.newBuilder()
                .maximumSize(10)
                .expireAfterWrite(1, TimeUnit.DAYS)
                .build();
        mProviderProfileCache = CacheBuilder.newBuilder()
                .maximumSize(10)
                .expireAfterWrite(1, TimeUnit.DAYS)
                .build();
        mProviderSettingsCache = CacheBuilder.newBuilder()
                .maximumSize(10)
                .expireAfterWrite(1, TimeUnit.HOURS)
                .build();
        bus.register(this);
    }

    public void prefetch()
    {
        requestProviderInfo();
    }

    public void setProviderProfile(@NonNull final ProviderProfile providerProfile)
    {
        /*
            although redundant, below is needed because provider id is accessed directly from prefs everywhere
         */
        setProviderId(providerProfile.getProviderId());
        mProviderProfileCache.put(PROVIDER_PROFILE_CACHE_KEY, providerProfile);
    }

    public void setProviderId(final String providerId)
    {
        mPrefsManager.setString(PrefsKey.LAST_PROVIDER_ID, providerId);
        Crashlytics.setUserIdentifier(providerId);
        //need to update the user identifier whenever provider id is updated
    }

    @Subscribe
    public void onRequestProviderInfo(HandyEvent.RequestProviderInfo event)
    {
        Provider cachedProvider = getCachedActiveProvider();
        if (cachedProvider != null)
        {
            mBus.post(new HandyEvent.ReceiveProviderInfoSuccess(cachedProvider));
        }
        else
        {
            requestProviderInfo();
        }
    }

    @Subscribe
    public void onUpdateProviderProfile(ProfileEvent.RequestProfileUpdate event)
    {
        String providerId = mPrefsManager.getString(PrefsKey.LAST_PROVIDER_ID);
        mDataManager.updateProviderProfile(providerId, getProfileParams(event), new DataManager.Callback<ProviderPersonalInfo>()
        {
            @Override
            public void onSuccess(ProviderPersonalInfo response)
            {
                mBus.post(new ProfileEvent.ReceiveProfileUpdateSuccess(response));
                requestProviderInfo();
                requestProviderProfile();
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                mBus.post(new ProfileEvent.ReceiveProfileUpdateError(error));
            }
        });
    }

    @Subscribe
    public void onRequestProviderSettings(ProviderSettingsEvent.RequestProviderSettings event)
    {
        ProviderSettings cachedProviderSettings = getCachedProviderSettings();
        if (cachedProviderSettings != null)
        {
            mBus.post(new ProviderSettingsEvent.ReceiveProviderSettingsSuccess(cachedProviderSettings));
        }
        else
        {
            requestProviderSettings();
        }
    }

    @Subscribe
    public void onUpdateProviderSettings(ProviderSettingsEvent.RequestProviderSettingsUpdate event)
    {
        String providerId = mPrefsManager.getString(PrefsKey.LAST_PROVIDER_ID);
        mDataManager.putUpdateProviderSettings(providerId, event.getProviderSettings(), new DataManager.Callback<ProviderSettings>()
        {
            @Override
            public void onSuccess(ProviderSettings providerSettings)
            {
                mProviderSettingsCache.put(PROVIDER_SETTINGS_CACHE_KEY, providerSettings);
                mBus.post(new ProviderSettingsEvent.ReceiveProviderSettingsUpdateSuccess(providerSettings));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                mBus.post(new ProviderSettingsEvent.ReceiveProviderSettingsUpdateError(error));
            }
        });
    }

    @Subscribe
    public void onRequestPaymentFlow(PaymentEvent.RequestPaymentFlow event)
    {
        String providerId = mPrefsManager.getString(PrefsKey.LAST_PROVIDER_ID);
        mDataManager.getPaymentFlow(providerId, new DataManager.Callback<PaymentFlow>()
        {
            @Override
            public void onSuccess(PaymentFlow response)
            {
                mBus.post(new PaymentEvent.ReceivePaymentFlowSuccess(response));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                mBus.post(new PaymentEvent.ReceivePaymentFlowError(error));

            }
        });
    }

    @Subscribe
    public void onSendIncomeVerification(HandyEvent.RequestSendIncomeVerification event)
    {
        String providerId = mPrefsManager.getString(PrefsKey.LAST_PROVIDER_ID);

        mDataManager.sendIncomeVerification(providerId, new DataManager.Callback<SuccessWrapper>()
        {
            @Override
            public void onSuccess(SuccessWrapper response)
            {
                if (response.getSuccess())
                {
                    mBus.post(new HandyEvent.ReceiveSendIncomeVerificationSuccess());
                }
                else
                {
                    mBus.post(new HandyEvent.ReceiveSendIncomeVerificationError());
                }
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                mBus.post(new HandyEvent.ReceiveSendIncomeVerificationError());
            }
        });
    }

    @Subscribe
    public void onRequestProviderProfile(ProfileEvent.RequestProviderProfile event)
    {
        ProviderProfile cachedProviderProfile = null;

        if (event.useCache)
        {
            cachedProviderProfile = getCachedProviderProfile();
        }

        if (cachedProviderProfile != null)
        {
            mBus.post(new ProfileEvent.ReceiveProviderProfileSuccess(cachedProviderProfile));
        }
        else
        {
            requestProviderProfile();
        }
    }

    @Subscribe
    public void onRequestResupplyKit(ProfileEvent.RequestSendResupplyKit event)
    {
        String providerId = mPrefsManager.getString(PrefsKey.LAST_PROVIDER_ID);

        mDataManager.getResupplyKit(providerId, new DataManager.Callback<ProviderProfile>()
        {
            @Override
            public void onSuccess(ProviderProfile providerProfile)
            {
                mProviderProfileCache.put(PROVIDER_PROFILE_CACHE_KEY, providerProfile);
                mBus.post(new ProfileEvent.ReceiveSendResupplyKitSuccess(providerProfile));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                mBus.post(new ProfileEvent.ReceiveSendResupplyKitError(error));
            }
        });
    }

    @Subscribe
    public void onRequestProviderEvaluation(ProviderDashboardEvent.RequestProviderEvaluation event)
    {
        String providerId = mPrefsManager.getString(PrefsKey.LAST_PROVIDER_ID);

        mDataManager.getProviderEvaluation(providerId, new DataManager.Callback<ProviderEvaluation>()
        {
            @Override
            public void onSuccess(final ProviderEvaluation providerEvaluation)
            {
                mBus.post(new ProviderDashboardEvent.ReceiveProviderEvaluationSuccess(providerEvaluation));
            }

            @Override
            public void onError(final DataManager.DataManagerError error)
            {
                mBus.post(new ProviderDashboardEvent.ReceiveProviderEvaluationError(error));
            }
        });

    }

    @Subscribe
    public void onRequestProviderFiveStarRatings(ProviderDashboardEvent.RequestProviderFiveStarRatings event)
    {
        String providerId = mPrefsManager.getString(PrefsKey.LAST_PROVIDER_ID);

        mDataManager.getProviderFiveStarRatings(providerId, event.getMinStar(), event.getUntilBookingDate(), event.getSinceBookingDate(), new DataManager.Callback<HashMap<String, List<ProviderRating>>>()
        {
            @Override
            public void onSuccess(final HashMap<String, List<ProviderRating>> responseHash)
            {
                List<ProviderRating> providerRatings = responseHash.get(RATINGS_KEY);
                if (providerRatings != null)
                {
                    mBus.post(new ProviderDashboardEvent.ReceiveProviderFiveStarRatingsSuccess(providerRatings));
                }
            }

            @Override
            public void onError(final DataManager.DataManagerError error)
            {
                mBus.post(new ProviderDashboardEvent.ReceiveProviderFiveStarRatingsError(error));
            }
        });

    }

    @Subscribe
    public void onRequestProviderFeedback(ProviderDashboardEvent.RequestProviderFeedback event)
    {
//        String providerId = "";

        // TODO: replace with real api call before merge into develop
        List<ProviderFeedback.FeedbackTip> feedbackTips = new ArrayList<>();
        feedbackTips.add(new ProviderFeedback.FeedbackTip("Floors weren't cleaned", "General Cleanliness"));
        feedbackTips.add(new ProviderFeedback.FeedbackTip("develop a cleaning plan and follow it", "use the customer checklist as a guide"));

        List<ProviderFeedback> providerFeedback = new ArrayList<>();
        providerFeedback.add(new ProviderFeedback("Quality of service", "3 customers have indicated that they were disappointed by the quality of your cleaning.",
                feedbackTips));
        mBus.post(new ProviderDashboardEvent.ReceiveProviderFeedbackSuccess(providerFeedback));
//        mBus.post(new ProviderDashboardEvent.ReceiveProviderFeedbackError(null));
//        mDataManager.getProviderFeedback(providerId, new DataManager.Callback<List<ProviderFeedback>>()
//        {
//            @Override
//            public void onSuccess(final List<ProviderFeedback> providerFeedback)
//            {
//                mBus.post(new ProviderDashboardEvent.ReceiveProviderFeedbackSuccess(providerFeedback));
//
//            }
//
//            @Override
//            public void onError(final DataManager.DataManagerError error)
//            {
//                mBus.post(new ProviderDashboardEvent.ReceiveProviderFeedbackError(error));
//            }
//        });
    }

    @Subscribe
    public void onRequestOnboardingSupplies(final HandyEvent.RequestOnboardingSupplies event)
    {
        mDataManager.requestOnboardingSupplies(getLastProviderId(), event.getOptIn(),
                new DataManager.Callback<SuccessWrapper>()
                {
                    @Override
                    public void onSuccess(final SuccessWrapper response)
                    {
                        mBus.post(new HandyEvent.ReceiveOnboardingSuppliesSuccess());
                    }

                    @Override
                    public void onError(final DataManager.DataManagerError error)
                    {
                        mBus.post(new HandyEvent.ReceiveOnboardingSuppliesError(error));
                    }
                });
    }

    private void requestProviderInfo()
    {
        mDataManager.getProviderInfo(new DataManager.Callback<Provider>()
        {
            @Override
            public void onSuccess(Provider provider)//TODO: need a way to sync this and provider id received from onLoginSuccess!
            {
                mProviderCache.put(PROVIDER_CACHE_KEY, provider);
                setProviderId(provider.getId());
                mBus.post(new HandyEvent.ProviderIdUpdated(provider.getId()));
                mBus.post(new HandyEvent.ReceiveProviderInfoSuccess(provider));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                mBus.post(new HandyEvent.ReceiveProviderInfoError(error));
            }
        });
    }

    public void requestProviderProfile()
    {
        String providerId = mPrefsManager.getString(PrefsKey.LAST_PROVIDER_ID);

        mDataManager.getProviderProfile(providerId, new DataManager.Callback<ProviderProfile>()
        {
            @Override
            public void onSuccess(ProviderProfile providerProfile)
            {
                mProviderProfileCache.put(PROVIDER_PROFILE_CACHE_KEY, providerProfile);
                mBus.post(new ProfileEvent.ReceiveProviderProfileSuccess(providerProfile));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                mBus.post(new ProfileEvent.ReceiveProviderProfileError(error));
            }
        });
    }

    private void requestProviderSettings()
    {
        String providerId = mPrefsManager.getString(PrefsKey.LAST_PROVIDER_ID);
        mDataManager.getProviderSettings(providerId, new DataManager.Callback<ProviderSettings>()
        {
            @Override
            public void onSuccess(ProviderSettings providerSettings)
            {
                mProviderSettingsCache.put(PROVIDER_SETTINGS_CACHE_KEY, providerSettings);
                mBus.post(new ProviderSettingsEvent.ReceiveProviderSettingsSuccess(providerSettings));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                mBus.post(new ProviderSettingsEvent.ReceiveProviderSettingsError(error));
            }
        });
    }

    @Nullable
    public String getLastProviderId()
    {
        return mPrefsManager.getString(PrefsKey.LAST_PROVIDER_ID);
    }

    @Nullable
    public Provider getCachedActiveProvider()
    {
        return mProviderCache.getIfPresent(PROVIDER_CACHE_KEY);
    }

    @Nullable
    public ProviderProfile getCachedProviderProfile()
    {
        return mProviderProfileCache.getIfPresent(PROVIDER_PROFILE_CACHE_KEY);
    }

    @Nullable
    public ProviderSettings getCachedProviderSettings()
    {
        return mProviderSettingsCache.getIfPresent(PROVIDER_SETTINGS_CACHE_KEY);
    }

    private static TypeSafeMap<ProviderKey> getProfileParams(ProfileEvent.RequestProfileUpdate info)
    {
        TypeSafeMap<ProviderKey> params = new TypeSafeMap<>();

        putIfNonEmpty(params, ProviderKey.EMAIL, info.email);
        putIfNonEmpty(params, ProviderKey.PHONE, info.phone);
        putIfNonEmpty(params, ProviderKey.ADDRESS1, info.address1);
        putIfNonEmpty(params, ProviderKey.ADDRESS2, info.address2);
        putIfNonEmpty(params, ProviderKey.CITY, info.city);
        putIfNonEmpty(params, ProviderKey.STATE, info.state);
        putIfNonEmpty(params, ProviderKey.ZIPCODE, info.zipCode);

        return params;
    }

    private static void putIfNonEmpty(final TypeSafeMap<ProviderKey> params,
                                      final ProviderKey key,
                                      final String value)
    {
        if (!TextUtils.isNullOrEmpty(value))
        {
            params.put(key, value);
        }
    }
}
