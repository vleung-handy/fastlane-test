package com.handy.portal.core;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Base64;

import com.google.gson.GsonBuilder;
import com.handy.portal.BuildConfig;
import com.handy.portal.action.CustomDeepLinkAction;
import com.handy.portal.bookings.BookingManager;
import com.handy.portal.bookings.ui.fragment.AvailableBookingsFragment;
import com.handy.portal.bookings.ui.fragment.BookingDetailsWrapperFragment;
import com.handy.portal.bookings.ui.fragment.BookingFragment;
import com.handy.portal.bookings.ui.fragment.CancellationRequestFragment;
import com.handy.portal.bookings.ui.fragment.ComplementaryBookingsFragment;
import com.handy.portal.bookings.ui.fragment.InProgressBookingFragment;
import com.handy.portal.bookings.ui.fragment.NearbyBookingsFragment;
import com.handy.portal.bookings.ui.fragment.ScheduledBookingsFragment;
import com.handy.portal.bookings.ui.fragment.SendReceiptCheckoutFragment;
import com.handy.portal.bookings.ui.fragment.dialog.ConfirmBookingCancelDialogFragment;
import com.handy.portal.bookings.ui.fragment.dialog.ConfirmBookingClaimDialogFragment;
import com.handy.portal.bookings.ui.fragment.dialog.RateBookingDialogFragment;
import com.handy.portal.constant.PrefsKey;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.helpcenter.HelpModule;
import com.handy.portal.location.LocationPingService;
import com.handy.portal.location.manager.LocationManager;
import com.handy.portal.location.scheduler.LocationScheduleService;
import com.handy.portal.location.scheduler.geofences.handler.BookingGeofenceScheduleHandler;
import com.handy.portal.location.scheduler.tracking.handler.LocationTrackingScheduleHandler;
import com.handy.portal.location.ui.LocationPermissionsBlockerDialogFragment;
import com.handy.portal.location.ui.LocationSettingsBlockerDialogFragment;
import com.handy.portal.logger.handylogger.EventLogManager;
import com.handy.portal.logger.mixpanel.Mixpanel;
import com.handy.portal.manager.ConfigManager;
import com.handy.portal.manager.GoogleManager;
import com.handy.portal.manager.LoginManager;
import com.handy.portal.manager.MainActivityFragmentNavigationHelper;
import com.handy.portal.manager.PrefsManager;
import com.handy.portal.manager.ProviderManager;
import com.handy.portal.manager.RegionDefinitionsManager;
import com.handy.portal.manager.StripeManager;
import com.handy.portal.manager.SystemManager;
import com.handy.portal.manager.TabNavigationManager;
import com.handy.portal.manager.TermsManager;
import com.handy.portal.manager.UrbanAirshipManager;
import com.handy.portal.manager.UserInterfaceUpdateManager;
import com.handy.portal.manager.WebUrlManager;
import com.handy.portal.manager.ZipClusterManager;
import com.handy.portal.notification.NotificationModule;
import com.handy.portal.payments.PaymentsManager;
import com.handy.portal.payments.ui.adapter.PaymentBatchListAdapter;
import com.handy.portal.payments.ui.element.PaymentFeeBreakdownView;
import com.handy.portal.payments.ui.element.PaymentsBatchListView;
import com.handy.portal.payments.ui.fragment.OutstandingFeesFragment;
import com.handy.portal.payments.ui.fragment.PaymentBillBlockerDialogFragment;
import com.handy.portal.payments.ui.fragment.PaymentBlockingFragment;
import com.handy.portal.payments.ui.fragment.PaymentsDetailFragment;
import com.handy.portal.payments.ui.fragment.PaymentsFragment;
import com.handy.portal.payments.ui.fragment.PaymentsUpdateBankAccountFragment;
import com.handy.portal.payments.ui.fragment.PaymentsUpdateDebitCardFragment;
import com.handy.portal.payments.ui.fragment.SelectPaymentMethodFragment;
import com.handy.portal.receiver.HandyPushReceiver;
import com.handy.portal.retrofit.HandyRetrofitEndpoint;
import com.handy.portal.retrofit.HandyRetrofitFluidEndpoint;
import com.handy.portal.retrofit.HandyRetrofitService;
import com.handy.portal.retrofit.stripe.StripeRetrofitEndpoint;
import com.handy.portal.retrofit.stripe.StripeRetrofitService;
import com.handy.portal.service.AutoCheckInService;
import com.handy.portal.service.DeepLinkService;
import com.handy.portal.ui.activity.BaseActivity;
import com.handy.portal.ui.activity.LoginActivity;
import com.handy.portal.ui.activity.MainActivity;
import com.handy.portal.ui.activity.SplashActivity;
import com.handy.portal.ui.activity.TermsActivity;
import com.handy.portal.ui.element.SupportActionView;
import com.handy.portal.ui.element.dashboard.DashboardFeedbackView;
import com.handy.portal.ui.element.dashboard.DashboardOptionsPerformanceView;
import com.handy.portal.ui.element.dashboard.FiveStarRatingPercentageView;
import com.handy.portal.ui.fragment.AccountSettingsFragment;
import com.handy.portal.ui.fragment.LoginActivityFragment;
import com.handy.portal.ui.fragment.MainActivityFragment;
import com.handy.portal.ui.fragment.ProfileUpdateFragment;
import com.handy.portal.ui.fragment.ReferAFriendFragment;
import com.handy.portal.ui.fragment.RequestSuppliesFragment;
import com.handy.portal.ui.fragment.RequestSuppliesWebViewFragment;
import com.handy.portal.ui.fragment.TermsFragment;
import com.handy.portal.ui.fragment.dashboard.DashboardFeedbackFragment;
import com.handy.portal.ui.fragment.dashboard.DashboardReviewsFragment;
import com.handy.portal.ui.fragment.dashboard.DashboardTiersFragment;
import com.handy.portal.ui.fragment.dashboard.DashboardVideoLibraryFragment;
import com.handy.portal.ui.fragment.dashboard.RatingsAndFeedbackFragment;
import com.handy.portal.updater.VersionManager;
import com.handy.portal.updater.ui.PleaseUpdateActivity;
import com.handy.portal.updater.ui.PleaseUpdateFragment;
import com.handy.portal.webview.BlockScheduleFragment;
import com.handy.portal.webview.OnboardingFragment;
import com.handy.portal.webview.PortalWebViewFragment;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.otto.Bus;

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
        BookingDetailsWrapperFragment.class,
        LoginActivityFragment.class,
        LoginActivity.class,
        ScheduledBookingsFragment.class,
        AvailableBookingsFragment.class,
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
        ComplementaryBookingsFragment.class,
        PaymentsFragment.class,
        PaymentsDetailFragment.class,
        PaymentBillBlockerDialogFragment.class,
        LocationSettingsBlockerDialogFragment.class,
        PaymentsUpdateBankAccountFragment.class,
        PaymentsUpdateDebitCardFragment.class,
        AutoCheckInService.class,
        SelectPaymentMethodFragment.class,
        PortalWebViewFragment.class,
        BlockScheduleFragment.class,
        RequestSuppliesFragment.class,
        ProfileUpdateFragment.class,
        RateBookingDialogFragment.class,
        PaymentsBatchListView.class,
        NearbyBookingsFragment.class,
        PaymentBlockingFragment.class,
        CancellationRequestFragment.class,
        SupportActionView.class,
        DashboardTiersFragment.class,
        DashboardFeedbackFragment.class,
        DashboardReviewsFragment.class,
        DashboardOptionsPerformanceView.class,
        LocationTrackingScheduleHandler.class,
        BookingGeofenceScheduleHandler.class,
        LocationScheduleService.class,
        LocationPingService.class,
        HandyPushReceiver.class,
        AccountSettingsFragment.class,
        RatingsAndFeedbackFragment.class,
        ReferAFriendFragment.class,
        FiveStarRatingPercentageView.class,
        OnboardingFragment.class,
        DashboardVideoLibraryFragment.class,
        LocationPermissionsBlockerDialogFragment.class,
        DashboardFeedbackView.class,
        SendReceiptCheckoutFragment.class,
        ConfirmBookingClaimDialogFragment.class,
        ConfirmBookingCancelDialogFragment.class,
        RequestSuppliesWebViewFragment.class,
        RequestSuppliesWebViewFragment.class,
        BookingFragment.class,
        InProgressBookingFragment.class,
        OutstandingFeesFragment.class,
        PaymentFeeBreakdownView.class,
        PaymentBatchListAdapter.class,
},
        includes = {
                HelpModule.class,
                NotificationModule.class
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
    final UserInterfaceUpdateManager provideUserInterfaceManager(final Bus bus)
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
                                                   final Bus bus)
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
                        request.addQueryParam("device_id", getDeviceId());
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

    @Provides
    @Singleton
    final Bus provideBus(final Mixpanel mixpanel)
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
                                         final StripeRetrofitService stripeService //TODO: refactor and move somewhere else?
    )
    {
        return new DataManager(service, endpoint, stripeService);
    }

    @Provides
    @Singleton
    final BookingManager provideBookingManager(final Bus bus,
                                               final DataManager dataManager)
    {
        return new BookingManager(bus, dataManager);
    }

    @Provides
    @Singleton
    final SystemManager provideSystemManager(final Bus bus)
    {
        return new SystemManager(context, bus);
    }

    @Provides
    @Singleton
    final LocationManager provideLocationManager(final Bus bus,
                                                 final DataManager dataManager,
                                                 final ProviderManager providerManager,
                                                 final PrefsManager prefsManager)
    {
        return new LocationManager(bus, dataManager, providerManager, prefsManager);
    }

    @Provides
    @Singleton
    final LoginManager provideLoginManager(final Bus bus, final DataManager dataManager, final PrefsManager prefsManager, final Mixpanel mixpanel)
    {
        return new LoginManager(bus, dataManager, prefsManager, mixpanel);
    }

    @Provides
    @Singleton
    final ProviderManager provideProviderManager(final Bus bus, final DataManager dataManager, final PrefsManager prefsManager)
    {
        return new ProviderManager(bus, dataManager, prefsManager);
    }

    @Provides
    @Singleton
    final ConfigManager provideConfigManager(final DataManager dataManager, final Bus bus)
    {
        return new ConfigManager(dataManager, bus);
    }

    @Provides
    @Singleton
    final VersionManager provideVersionManager(final Bus bus,
                                               final DataManager dataManager,
                                               final PrefsManager prefsManager,
                                               final BuildConfigWrapper buildConfigWrapper)
    {
        return new VersionManager(context, bus, dataManager, prefsManager, buildConfigWrapper);
    }

    @Provides
    @Singleton
    final TermsManager provideTermsManager(final Bus bus,
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
    final GoogleManager provideGoogleService()
    {
        return new GoogleManager(this.context);
    }

    @Provides
    @Singleton
    final UrbanAirshipManager provideUrbanAirshipManager(final Bus bus,
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
    final MainActivityFragmentNavigationHelper provideFragmentNavigationManager(Bus bus)
    {
        return new MainActivityFragmentNavigationHelper(bus);
    }

    @Provides
    @Singleton
    final PaymentsManager providePaymentsManager(Bus bus, final DataManager dataManager)
    {
        return new PaymentsManager(bus, dataManager);
    }

    @Provides
    @Singleton
    final ZipClusterManager provideZipClusterPolygonManager(final Bus bus, final DataManager dataManager)
    {
        return new ZipClusterManager(bus, dataManager);
    }

    @Provides
    @Singleton
    final StripeManager provideStripeManager(final Bus bus, final DataManager dataManager)
    {
        return new StripeManager(context, bus, dataManager);
    }

    @Provides
    @Singleton
    final EventLogManager provideLogEventsManager(final Bus bus,
                                                  final DataManager dataManager,
                                                  final PrefsManager prefsManager)
    {
        return new EventLogManager(bus, dataManager, prefsManager);
    }

    @Provides
    @Singleton
    final RegionDefinitionsManager provideRegionDefinitionsManager(final Bus bus)
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
    final TabNavigationManager provideTabNavigationManager(final Bus bus,
                                                           final ProviderManager providerManager,
                                                           final WebUrlManager webUrlManager,
                                                           final PaymentsManager paymentsManager,
                                                           final ConfigManager configManager
    )
    {
        return new TabNavigationManager(bus, providerManager, webUrlManager, paymentsManager, configManager);
    }

    private String getDeviceId()
    {
        return Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
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
