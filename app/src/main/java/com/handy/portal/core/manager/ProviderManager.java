package com.handy.portal.core.manager;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.crashlytics.android.Crashlytics;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.handy.portal.core.constant.PrefsKey;
import com.handy.portal.core.constant.ProviderKey;
import com.handy.portal.core.event.HandyEvent;
import com.handy.portal.core.event.ProfileEvent;
import com.handy.portal.core.event.ProviderDashboardEvent;
import com.handy.portal.core.event.ProviderSettingsEvent;
import com.handy.portal.core.model.ProviderPersonalInfo;
import com.handy.portal.core.model.ProviderPersonalInfo.ProfileImage.Type;
import com.handy.portal.core.model.ProviderProfile;
import com.handy.portal.core.model.ProviderProfileResponse;
import com.handy.portal.core.model.ProviderSettings;
import com.handy.portal.core.model.SuccessWrapper;
import com.handy.portal.core.model.TypeSafeMap;
import com.handy.portal.dashboard.model.ProviderEvaluation;
import com.handy.portal.dashboard.model.ProviderFeedback;
import com.handy.portal.dashboard.model.ProviderRating;
import com.handy.portal.data.DataManager;
import com.handy.portal.library.util.TextUtils;
import com.handy.portal.payments.PaymentEvent;
import com.handy.portal.payments.model.PaymentFlow;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ProviderManager
{
    private final EventBus mBus;
    private final DataManager mDataManager;
    private final PrefsManager mPrefsManager;
    private Cache<String, ProviderProfile> mProviderProfileCache;
    private static final String PROVIDER_PROFILE_CACHE_KEY = "provider_profile";
    private Cache<String, ProviderSettings> mProviderSettingsCache;
    private static final String PROVIDER_SETTINGS_CACHE_KEY = "provider_settings";
    private static final String RATINGS_KEY = "ratings";

    public ProviderManager(final EventBus bus, final DataManager dataManager, final PrefsManager prefsManager)
    {
        mBus = bus;
        mDataManager = dataManager;
        mPrefsManager = prefsManager;
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
        mPrefsManager.setSecureString(PrefsKey.LAST_PROVIDER_ID, providerId);
        mBus.post(new HandyEvent.ProviderIdUpdated(providerId));
        Crashlytics.setUserIdentifier(providerId);
        //need to update the user identifier whenever provider id is updated
    }

    @Subscribe
    public void onUpdateProviderProfile(ProfileEvent.RequestProfileUpdate event)
    {
        String providerId = mPrefsManager.getSecureString(PrefsKey.LAST_PROVIDER_ID);
        mDataManager.updateProviderProfile(providerId, getProfileParams(event), new DataManager.Callback<ProviderProfileResponse>()
        {
            @Override
            public void onSuccess(ProviderProfileResponse response)
            {
                ProviderProfile providerProfile = response.getProviderProfile();
                mProviderProfileCache.put(PROVIDER_PROFILE_CACHE_KEY, providerProfile);
                mBus.post(new ProfileEvent.ReceiveProfileUpdateSuccess(
                        providerProfile.getProviderPersonalInfo()));
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
        String providerId = mPrefsManager.getSecureString(PrefsKey.LAST_PROVIDER_ID);
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
        String providerId = mPrefsManager.getSecureString(PrefsKey.LAST_PROVIDER_ID);
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
        String providerId = mPrefsManager.getSecureString(PrefsKey.LAST_PROVIDER_ID);

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
        String providerId = mPrefsManager.getSecureString(PrefsKey.LAST_PROVIDER_ID);

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
        String providerId = mPrefsManager.getSecureString(PrefsKey.LAST_PROVIDER_ID);

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
        String providerId = mPrefsManager.getSecureString(PrefsKey.LAST_PROVIDER_ID);

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

    @Subscribe
    public void onRequestIdVerificationStart(final ProviderSettingsEvent.RequestIdVerificationStart event)
    {
        mDataManager.beforeStartIdVerification(event.getBeforeIdVerificationStartUrl(),
                new DataManager.Callback<HashMap<String, String>>()
                {
                    @Override
                    public void onSuccess(final HashMap<String, String> response)
                    { }

                    @Override
                    public void onError(final DataManager.DataManagerError error)
                    { }
                });
    }

    @Subscribe
    public void onRequestIdVerificationFinish(final ProviderSettingsEvent.RequestIdVerificationFinish event)
    {
        mDataManager.finishIdVerification(event.getAfterIdVerificationFinishUrl(),
                event.getScanReference(), event.getStatus(), new DataManager.Callback<HashMap<String, String>>()
                {
                    @Override
                    public void onSuccess(final HashMap<String, String> response)
                    { }

                    @Override
                    public void onError(final DataManager.DataManagerError error)
                    { }
                });
    }

    @Subscribe
    public void onRequestPhotoUploadUrl(final ProfileEvent.RequestPhotoUploadUrl event)
    {
        mDataManager.requestPhotoUploadUrl(getLastProviderId(), event.getImageMimeType(),
                new DataManager.Callback<HashMap<String, String>>()
                {
                    @Override
                    public void onSuccess(final HashMap<String, String> response)
                    {
                        final String uploadUrl = response.get("upload_url");
                        mBus.post(new ProfileEvent.ReceivePhotoUploadUrlSuccess(uploadUrl));
                    }

                    @Override
                    public void onError(final DataManager.DataManagerError error)
                    {
                        mBus.post(new ProfileEvent.ReceivePhotoUploadUrlError(error));
                    }
                });
    }

    public void requestProviderProfile()
    {
        String providerId = mPrefsManager.getSecureString(PrefsKey.LAST_PROVIDER_ID);

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
        String providerId = mPrefsManager.getSecureString(PrefsKey.LAST_PROVIDER_ID);
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
        return mPrefsManager.getSecureString(PrefsKey.LAST_PROVIDER_ID);
    }

    @Nullable
    public ProviderProfile getCachedProviderProfile()
    {
        return mProviderProfileCache.getIfPresent(PROVIDER_PROFILE_CACHE_KEY);
    }

    @Nullable
    public String getCachedProfileImageUrl(@NonNull final Type imageType)
    {
        final ProviderProfile profile = getCachedProviderProfile();
        if (profile != null && profile.getProviderPersonalInfo() != null)
        {
            final ProviderPersonalInfo providerPersonalInfo = profile.getProviderPersonalInfo();
            final ProviderPersonalInfo.ProfileImage profileImage =
                    providerPersonalInfo.getProfileImage(imageType);
            if (profileImage != null)
            {
                return profileImage.getUrl();
            }
            else
            {
                // Fall back to original size
                final ProviderPersonalInfo.ProfileImage originalProfileImage =
                        providerPersonalInfo.getProfileImage(Type.ORIGINAL);
                if (originalProfileImage != null)
                {
                    return originalProfileImage.getUrl();
                }
            }
        }
        return null;
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
