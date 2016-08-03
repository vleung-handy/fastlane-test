package com.handy.portal.core;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Base64;

import com.google.gson.GsonBuilder;
import com.handy.portal.BuildConfig;
import com.handy.portal.action.CustomDeepLinkAction;
import com.handy.portal.bookings.BookingsModule;
import com.handy.portal.bookings.ui.fragment.SoftwareLicensesFragment;
import com.handy.portal.constant.PrefsKey;
import com.handy.portal.dashboard.fragment.DashboardFeedbackFragment;
import com.handy.portal.dashboard.fragment.DashboardReviewsFragment;
import com.handy.portal.dashboard.fragment.DashboardTiersFragment;
import com.handy.portal.dashboard.fragment.DashboardVideoLibraryFragment;
import com.handy.portal.dashboard.fragment.RatingsAndFeedbackFragment;
import com.handy.portal.dashboard.view.DashboardFeedbackView;
import com.handy.portal.dashboard.view.DashboardOptionsPerformanceView;
import com.handy.portal.dashboard.view.FiveStarRatingPercentageView;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.helpcenter.HelpModule;
import com.handy.portal.library.util.PropertiesReader;
import com.handy.portal.library.util.SystemUtils;
import com.handy.portal.location.LocationModule;
import com.handy.portal.logger.handylogger.EventLogManager;
import com.handy.portal.logger.mixpanel.Mixpanel;
import com.handy.portal.manager.ConfigManager;
import com.handy.portal.manager.LoginManager;
import com.handy.portal.manager.MainActivityFragmentNavigationHelper;
import com.handy.portal.manager.PageNavigationManager;
import com.handy.portal.manager.PrefsManager;
import com.handy.portal.manager.ProviderManager;
import com.handy.portal.manager.RegionDefinitionsManager;
import com.handy.portal.manager.StripeManager;
import com.handy.portal.manager.SystemManager;
import com.handy.portal.manager.TermsManager;
import com.handy.portal.manager.UrbanAirshipManager;
import com.handy.portal.manager.UserInterfaceUpdateManager;
import com.handy.portal.manager.WebUrlManager;
import com.handy.portal.manager.ZipClusterManager;
import com.handy.portal.notification.NotificationModule;
import com.handy.portal.onboarding.OnboardingModule;
import com.handy.portal.onboarding.ui.activity.ActivationWelcomeActivity;
import com.handy.portal.onboarding.ui.fragment.CameraPermissionsBlockerDialogFragment;
import com.handy.portal.onboarding.ui.fragment.IDVerificationFragment;
import com.handy.portal.payments.PaymentsManager;
import com.handy.portal.payments.PaymentsModule;
import com.handy.portal.receiver.HandyPushReceiver;
import com.handy.portal.retrofit.HandyRetrofitEndpoint;
import com.handy.portal.retrofit.HandyRetrofitFluidEndpoint;
import com.handy.portal.retrofit.HandyRetrofitService;
import com.handy.portal.retrofit.logevents.EventLogEndpoint;
import com.handy.portal.retrofit.logevents.EventLogService;
import com.handy.portal.retrofit.stripe.StripeRetrofitEndpoint;
import com.handy.portal.retrofit.stripe.StripeRetrofitService;
import com.handy.portal.service.DeepLinkService;
import com.handy.portal.setup.SetupManager;
import com.handy.portal.setup.SetupModule;
import com.handy.portal.ui.activity.BaseActivity;
import com.handy.portal.ui.activity.LoginActivity;
import com.handy.portal.ui.activity.MainActivity;
import com.handy.portal.ui.activity.SplashActivity;
import com.handy.portal.ui.activity.TermsActivity;
import com.handy.portal.ui.element.SupportActionView;
import com.handy.portal.ui.fragment.AccountSettingsFragment;
import com.handy.portal.ui.fragment.LoginActivityFragment;
import com.handy.portal.ui.fragment.MainActivityFragment;
import com.handy.portal.ui.fragment.ProfileUpdateFragment;
import com.handy.portal.ui.fragment.ReferAFriendFragment;
import com.handy.portal.ui.fragment.RequestSuppliesFragment;
import com.handy.portal.ui.fragment.RequestSuppliesWebViewFragment;
import com.handy.portal.ui.fragment.TermsFragment;
import com.handy.portal.updater.VersionManager;
import com.handy.portal.updater.ui.PleaseUpdateActivity;
import com.handy.portal.updater.ui.PleaseUpdateFragment;
import com.handy.portal.webview.BlockScheduleFragment;
import com.handy.portal.webview.PortalWebViewFragment;
import com.squareup.okhttp.OkHttpClient;

import org.greenrobot.eventbus.EventBus;

import java.util.Properties;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.ErrorHandler;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

@Module(injects = {
        LoginActivityFragment.class,
        LoginActivity.class,
        MainActivityFragment.class,
        BaseApplication.class,
        BaseActivity.class,
        SplashActivity.class,
        MainActivity.class,
        PleaseUpdateActivity.class,
        PleaseUpdateFragment.class,
        TermsActivity.class,
        TermsFragment.class,
        UrbanAirshipManager.class,
        DeepLinkService.class,
        MainActivityFragmentNavigationHelper.class,
        PortalWebViewFragment.class,
        BlockScheduleFragment.class,
        RequestSuppliesFragment.class,
        ProfileUpdateFragment.class,
        SupportActionView.class,
        DashboardTiersFragment.class,
        DashboardFeedbackFragment.class,
        DashboardReviewsFragment.class,
        DashboardOptionsPerformanceView.class,
        HandyPushReceiver.class,
        AccountSettingsFragment.class,
        RatingsAndFeedbackFragment.class,
        ReferAFriendFragment.class,
        FiveStarRatingPercentageView.class,
        DashboardVideoLibraryFragment.class,
        DashboardFeedbackView.class,
        RequestSuppliesWebViewFragment.class,
        ActivationWelcomeActivity.class,
        RequestSuppliesWebViewFragment.class,
        DashboardTiersFragment.class,
        SoftwareLicensesFragment.class,
        CameraPermissionsBlockerDialogFragment.class,
        IDVerificationFragment.class,
},
        includes = {
                HelpModule.class,
                NotificationModule.class,
                LocationModule.class,
                PaymentsModule.class,
                BookingsModule.class,
                OnboardingModule.class,
                SetupModule.class,
        }
)
public final class ApplicationModule
{
    private final Application application;
    private final Context context;
    private final Properties configs;

    public ApplicationModule(final Application application)
    {
        this.application = application;
        this.context = this.application.getApplicationContext();
        configs = PropertiesReader.getConfigProperties(context);
    }

    @Provides
    @Singleton
    final BuildConfigWrapper provideBuildConfigWrapper()
    {
        return new BuildConfigWrapper();
    }

    @Provides
    @Singleton
    final EnvironmentModifier provideEnvironmentModifier(PrefsManager prefsManager)
    {
        return new EnvironmentModifier(context, prefsManager);
    }

    @Provides
    @Singleton
    final UserInterfaceUpdateManager provideUserInterfaceManager(final EventBus bus)
    {
        return new UserInterfaceUpdateManager(bus);
    }

    @Provides
    @Singleton
    final HandyRetrofitEndpoint provideHandyEndpoint(final BuildConfigWrapper buildConfigWrapper, final EnvironmentModifier environmentModifier)
    {
        if (buildConfigWrapper.isDebug())
        {
            return new HandyRetrofitFluidEndpoint(context, environmentModifier);
        }
        return new HandyRetrofitEndpoint(context);
    }

    @Provides
    @Singleton
    final HandyRetrofitService provideHandyService(final BuildConfigWrapper buildConfigWrapper,
                                                   final HandyRetrofitEndpoint endpoint,
                                                   final PrefsManager prefsManager,
                                                   final EventBus bus)
    {

        final OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setReadTimeout(60, TimeUnit.SECONDS);

        final String username = configs.getProperty("api_username");
        final String password = configs.getProperty("api_password");

        final RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(endpoint)
                .setRequestInterceptor(new RequestInterceptor()
                {
                    final String auth = "Basic " + Base64.encodeToString((username + ":" + password)
                            .getBytes(), Base64.NO_WRAP);

                    @Override
                    public void intercept(RequestFacade request)
                    {
                        String authToken = prefsManager.getString(PrefsKey.AUTH_TOKEN, null);
                        if (authToken != null)
                        {
                            request.addHeader("X-Auth-Token", authToken);
                        }

                        request.addHeader("Authorization", auth);
                        request.addHeader("Accept", "application/json");
                        request.addQueryParam("client", "android");
                        request.addQueryParam("app_version", BuildConfig.VERSION_NAME);
                        request.addQueryParam("device_id", SystemUtils.getDeviceId(context));
                        request.addQueryParam("device_model", BaseApplication.getDeviceModel());
                        request.addQueryParam("os_version", Build.VERSION.RELEASE);
                        request.addQueryParam("device_carrier", getDeviceCarrier());
                        request.addQueryParam("timezone", TimeZone.getDefault().getID());
                    }
                }).setErrorHandler(new ErrorHandler()
                {
                    @Override
                    public Throwable handleError(final RetrofitError cause)
                    {
                        Response response = cause.getResponse();
                        if (response != null && response.getStatus() == 401)
                        {
                            bus.post(new HandyEvent.LogOutProvider());
                        }

                        return cause;
                    }
                }).setConverter(new GsonConverter(new GsonBuilder()
                        .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                        .create())).setClient(new OkClient(okHttpClient)).build();

        if (buildConfigWrapper.isDebug())
        {
            restAdapter.setLogLevel(RestAdapter.LogLevel.FULL);
        }

        return restAdapter.create(HandyRetrofitService.class);
    }

    //stripe
    @Provides
    @Singleton
    final StripeRetrofitEndpoint provideStripeEndpoint()
    {
        return new StripeRetrofitEndpoint(context);
    }

    @Provides
    @Singleton
    final StripeRetrofitService provideStripeService(final StripeRetrofitEndpoint endpoint) //TODO: clean up
    {
        final OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setReadTimeout(60, TimeUnit.SECONDS);

        final RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(endpoint)
                .setRequestInterceptor(new RequestInterceptor()
                {
                    @Override
                    public void intercept(RequestFacade request) { }
                }).setClient(new OkClient(okHttpClient)).build();
        return restAdapter.create(StripeRetrofitService.class);
    }

    //log events
    @Provides
    @Singleton
    final EventLogEndpoint provideLogEventsEndpoint()
    {
        return new EventLogEndpoint(context);
    }

    @Provides
    @Singleton
    final EventLogService provideLogEventsService(final EventLogEndpoint endpoint)
    {
        final OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setReadTimeout(60, TimeUnit.SECONDS);

        final RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(endpoint)
                .setRequestInterceptor(new RequestInterceptor()
                {
                    @Override
                    public void intercept(RequestFacade request) { }
                }).setClient(new OkClient(okHttpClient)).build();
        return restAdapter.create(EventLogService.class);
    }

    @Provides
    @Singleton
    final EventBus provideBus(final Mixpanel mixpanel)
    {
        return new MainBus(mixpanel);
    }

    @Provides
    @Singleton
    final Application provideApplication()
    {
        return this.application;
    }

    @Provides
    @Singleton
    final DataManager provideDataManager(final HandyRetrofitService service,
                                         final HandyRetrofitEndpoint endpoint,
                                         final StripeRetrofitService stripeService, //TODO: refactor and move somewhere else?
                                         final EventLogService eventLogService
    )
    {
        return new DataManager(service, endpoint, stripeService, eventLogService);
    }

    @Provides
    @Singleton
    final SystemManager provideSystemManager(final EventBus bus)
    {
        return new SystemManager(context, bus);
    }

    @Provides
    @Singleton
    final LoginManager provideLoginManager(final EventBus bus, final DataManager dataManager, final PrefsManager prefsManager, final Mixpanel mixpanel)
    {
        return new LoginManager(bus, dataManager, prefsManager, mixpanel);
    }

    @Provides
    @Singleton
    final ProviderManager provideProviderManager(final EventBus bus, final DataManager dataManager, final PrefsManager prefsManager)
    {
        return new ProviderManager(bus, dataManager, prefsManager);
    }

    @Provides
    @Singleton
    final ConfigManager provideConfigManager(final DataManager dataManager, final EventBus bus)
    {
        return new ConfigManager(dataManager, bus);
    }

    @Provides
    @Singleton
    final VersionManager provideVersionManager(final EventBus bus,
                                               final DataManager dataManager,
                                               final PrefsManager prefsManager,
                                               final BuildConfigWrapper buildConfigWrapper)
    {
        return new VersionManager(context, bus, dataManager, prefsManager, buildConfigWrapper);
    }

    @Provides
    @Singleton
    final TermsManager provideTermsManager(final EventBus bus,
                                           final DataManager dataManager)
    {
        return new TermsManager(bus, dataManager);
    }

    @Provides
    @Singleton
    final PrefsManager providePrefsManager()
    {
        return new PrefsManager(context);
    }

    @Provides
    @Singleton
    final Mixpanel provideMixpanel(final PrefsManager prefsManager)
    {
        return new Mixpanel(this.context, prefsManager);
    }

    @Provides
    @Singleton
    final UrbanAirshipManager provideUrbanAirshipManager(final EventBus bus,
                                                         final DataManager dataManager,
                                                         final PrefsManager prefsManager,
                                                         final Application associatedApplication,
                                                         final CustomDeepLinkAction customDeepLinkAction)
    {
        return new UrbanAirshipManager(bus, dataManager, prefsManager, associatedApplication, customDeepLinkAction);
    }

    @Provides
    final CustomDeepLinkAction provideCustomDeepLinkAction()

    {
        return new CustomDeepLinkAction();
    }

    @Provides
    @Singleton
    final MainActivityFragmentNavigationHelper provideFragmentNavigationManager(EventBus bus)
    {
        return new MainActivityFragmentNavigationHelper(bus);
    }

    @Provides
    @Singleton
    final ZipClusterManager provideZipClusterPolygonManager(final EventBus bus, final DataManager dataManager)
    {
        return new ZipClusterManager(bus, dataManager);
    }

    @Provides
    @Singleton
    final StripeManager provideStripeManager(final EventBus bus, final DataManager dataManager)
    {
        return new StripeManager(context, bus, dataManager);
    }

    @Provides
    @Singleton
    final EventLogManager provideLogEventsManager(final EventBus bus,
                                                  final DataManager dataManager,
                                                  final PrefsManager prefsManager)
    {
        return new EventLogManager(bus, dataManager, prefsManager);
    }

    @Provides
    @Singleton
    final RegionDefinitionsManager provideRegionDefinitionsManager(final EventBus bus)
    {
        return new RegionDefinitionsManager(bus);
    }

    @Provides
    @Singleton
    final WebUrlManager provideWebUrlManager(final ProviderManager providerManager,
                                             final PrefsManager prefsManager,
                                             final ConfigManager configManager,
                                             final HandyRetrofitEndpoint baseUrlEndpoint)
    {
        return new WebUrlManager(providerManager, prefsManager, configManager, baseUrlEndpoint);
    }

    @Provides
    @Singleton
    final PageNavigationManager providePageNavigationManager(final EventBus bus,
                                                             final ProviderManager providerManager,
                                                             final WebUrlManager webUrlManager,
                                                             final PaymentsManager paymentsManager,
                                                             final ConfigManager configManager
    )
    {
        return new PageNavigationManager(bus, providerManager, webUrlManager, paymentsManager, configManager);
    }

    @Provides
    @Singleton
    final SetupManager provideApplicationSetupManager(final EventBus bus,
                                                      final DataManager dataManager)
    {
        return new SetupManager(bus, dataManager);
    }

    private String getDeviceCarrier()
    {
        final TelephonyManager telephonyManager =
                ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE));
        if (telephonyManager != null)
        {
            final String networkOperatorName = telephonyManager.getNetworkOperatorName();
            if (networkOperatorName != null)
            {
                return networkOperatorName;
            }
        }
        return "";
    }
}
