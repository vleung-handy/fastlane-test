package com.handy.portal.core;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.handy.portal.BuildConfig;
import com.handy.portal.availability.AvailabilityModule;
import com.handy.portal.bookings.BookingsModule;
import com.handy.portal.clients.ClientsModule;
import com.handy.portal.core.constant.PrefsKey;
import com.handy.portal.core.event.HandyEvent;
import com.handy.portal.core.manager.AppseeManager;
import com.handy.portal.core.manager.ConfigManager;
import com.handy.portal.core.manager.FileManager;
import com.handy.portal.core.manager.LoginManager;
import com.handy.portal.core.manager.PageNavigationManager;
import com.handy.portal.core.manager.PrefsManager;
import com.handy.portal.core.manager.ProviderManager;
import com.handy.portal.core.manager.RegionDefinitionsManager;
import com.handy.portal.core.manager.StripeManager;
import com.handy.portal.core.manager.SystemManager;
import com.handy.portal.core.manager.UrbanAirshipManager;
import com.handy.portal.core.manager.UserInterfaceUpdateManager;
import com.handy.portal.core.manager.ZipClusterManager;
import com.handy.portal.data.DataManager;
import com.handy.portal.deeplink.CustomDeepLinkAction;
import com.handy.portal.helpcenter.HelpModule;
import com.handy.portal.library.util.PropertiesReader;
import com.handy.portal.library.util.SystemUtils;
import com.handy.portal.location.LocationModule;
import com.handy.portal.logger.handylogger.EventLogManager;
import com.handy.portal.notification.NotificationModule;
import com.handy.portal.onboarding.OnboardingModule;
import com.handy.portal.payments.PaymentsModule;
import com.handy.portal.retrofit.DynamicEndpoint;
import com.handy.portal.retrofit.DynamicEndpointService;
import com.handy.portal.retrofit.HandyRetrofit2Service;
import com.handy.portal.retrofit.HandyRetrofitEndpoint;
import com.handy.portal.retrofit.HandyRetrofitFluidEndpoint;
import com.handy.portal.retrofit.HandyRetrofitService;
import com.handy.portal.retrofit.stripe.StripeRetrofitEndpoint;
import com.handy.portal.retrofit.stripe.StripeRetrofitService;
import com.handy.portal.setup.SetupManager;
import com.handy.portal.setup.SetupModule;
import com.handy.portal.terms.TermsModule;
import com.handy.portal.updater.AppUpdaterModule;
import com.squareup.okhttp.CertificatePinner;
import com.squareup.okhttp.OkHttpClient;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.Properties;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit.ErrorHandler;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.handy.portal.library.util.DateTimeUtils.UNIVERSAL_DATE_FORMAT;

@Module(
        injects = {
                CoreTestApplication.class,
        },
        includes = {
                InjectionModule.class,
                HelpModule.class,
                NotificationModule.class,
                LocationModule.class,
                PaymentsModule.class,
                BookingsModule.class,
                OnboardingModule.class,
                SetupModule.class,
                ClientsModule.class,
                TermsModule.class,
                AppUpdaterModule.class,
                AvailabilityModule.class,
        }
)
public class CoreTestApplicationModule {
    private final Application application;
    private final Context context;
    private final Properties configs;

    public CoreTestApplicationModule(final Application application) {
        this.application = application;
        this.context = this.application.getApplicationContext();
        configs = PropertiesReader.getConfigProperties(context);
    }

    @Provides
    @Singleton
    final Context provideContext() { return context; }

    @Provides
    @Singleton
    final AppseeManager provideAppseeManager(final ConfigManager configManager,
                                             final ProviderManager providerManager,
                                             final FileManager fileManager,
                                             final EventBus eventBus) {
        String appseeApiKey = configs.getProperty("appsee_api_key");
        return new AppseeManager(appseeApiKey, configManager, providerManager, fileManager, eventBus);
    }

    @Provides
    @Singleton
    final BuildConfigWrapper provideBuildConfigWrapper() {
        return new BuildConfigWrapper();
    }

    @Provides
    @Singleton
    final EnvironmentModifier provideEnvironmentModifier(PrefsManager prefsManager) {
        return new EnvironmentModifier(context, prefsManager);
    }

    @Provides
    @Singleton
    final UserInterfaceUpdateManager provideUserInterfaceManager(final EventBus bus) {
        return new UserInterfaceUpdateManager(bus);
    }

    @Provides
    @Singleton
    final HandyRetrofitEndpoint provideHandyEndpoint(final BuildConfigWrapper buildConfigWrapper, final EnvironmentModifier environmentModifier) {
        if (buildConfigWrapper.isDebug()) {
            return new HandyRetrofitFluidEndpoint(context, environmentModifier);
        }
        return new HandyRetrofitEndpoint(context);
    }

    @Provides
    @Singleton
    final RestAdapter provideRestAdapter(final BuildConfigWrapper buildConfigWrapper,
                                         final HandyRetrofitEndpoint endpoint,
                                         final PrefsManager prefsManager,
                                         final EventBus bus) {
        final OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setReadTimeout(60, TimeUnit.SECONDS);
        if (!BuildConfig.DEBUG) {
            okHttpClient.setCertificatePinner(new CertificatePinner.Builder()
                    .add(configs.getProperty("hostname"),
                            "sha1/tbHJQrYmt+5isj5s44sk794iYFc=",
                            "sha1/SXxoaOSEzPC6BgGmxAt/EAcsajw=",
                            "sha1/blhOM3W9V/bVQhsWAcLYwPU6n24=",
                            "sha1/T5x9IXmcrQ7YuQxXnxoCmeeQ84c=")
                    .build());
        }

        final String username = configs.getProperty("api_username");
        final String password = configs.getProperty("api_password");

        final RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(endpoint)
                .setRequestInterceptor(new RequestInterceptor() {
                    final String auth = "Basic " + Base64.encodeToString((username + ":" + password)
                            .getBytes(), Base64.NO_WRAP);

                    @Override
                    public void intercept(RequestFacade request) {
                        String authToken = prefsManager.getSecureString(PrefsKey.AUTH_TOKEN, null);
                        if (authToken != null) {
                            request.addHeader("X-Auth-Token", authToken);
                        }

                        request.addHeader("Authorization", auth);
                        request.addHeader("Accept", "application/json");
                        request.addQueryParam("client", "android");
                        request.addQueryParam("app_version", BuildConfig.VERSION_NAME);
                        request.addQueryParam("device_id", SystemUtils.getDeviceId(context));
                        request.addQueryParam("device_model", SystemUtils.getDeviceModel());
                        request.addQueryParam("os_version", Build.VERSION.RELEASE);
                        request.addQueryParam("device_carrier", getDeviceCarrier());
                        request.addQueryParam("timezone", TimeZone.getDefault().getID());
                    }
                }).setErrorHandler(new ErrorHandler() {
                    @Override
                    public Throwable handleError(final RetrofitError cause) {
                        Response response = cause.getResponse();
                        if (response != null && response.getStatus() == 401) {
                            bus.post(new HandyEvent.LogOutProvider());
                        }

                        return cause;
                    }
                }).setConverter(new GsonConverter(new GsonBuilder()
                        .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                        .create())).setClient(new OkClient(okHttpClient)).build();

        if (buildConfigWrapper.isDebug()) {
            restAdapter.setLogLevel(RestAdapter.LogLevel.FULL);
        }
        return restAdapter;
    }

    @Provides
    @Singleton
    final HandyRetrofitService provideHandyService(final RestAdapter restAdapter) {
        return restAdapter.create(HandyRetrofitService.class);
    }

    @Provides
    @Singleton
    final HandyRetrofit2Service provideRetrofit2Service(
            final HandyRetrofitEndpoint endpoint, final PrefsManager prefsManager) {
        final String username = configs.getProperty("api_username");
        final String password = configs.getProperty("api_password");
        final String auth = "Basic " +
                Base64.encodeToString((username + ":" + password).getBytes(), Base64.NO_WRAP);

        HttpLoggingInterceptor httpInterceptor = new HttpLoggingInterceptor();
        if (BuildConfig.DEBUG) {
            httpInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        }
        okhttp3.OkHttpClient.Builder httpClientBuilder = new okhttp3.OkHttpClient
                .Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {

                        Request original = chain.request();
                        HttpUrl originalHttpUrl = original.url();

                        okhttp3.HttpUrl.Builder urlBuilder = originalHttpUrl
                                .newBuilder()
                                .addQueryParameter("client", "android")
                                .addQueryParameter("app_version", BuildConfig.VERSION_NAME)
                                .addQueryParameter("device_id", SystemUtils.getDeviceId(context))
                                .addQueryParameter("device_model", SystemUtils.getDeviceModel())
                                .addQueryParameter("os_version", Build.VERSION.RELEASE)
                                .addQueryParameter("device_carrier", getDeviceCarrier())
                                .addQueryParameter("timezone", TimeZone.getDefault().getID());

                        Request.Builder requestBuilder = original
                                .newBuilder()
                                .addHeader("Authorization", auth)
                                .addHeader("Accept", "application/json")
                                .url(urlBuilder.build());

                        String authToken = prefsManager.getSecureString(PrefsKey.AUTH_TOKEN, null);
                        if (authToken != null) {
                            requestBuilder.addHeader("X-Auth-Token", authToken);
                        }
                        Request request = requestBuilder.build();
                        return chain.proceed(request);
                    }
                })
                .addInterceptor(httpInterceptor);

        if (!BuildConfig.DEBUG) {
            httpClientBuilder.certificatePinner(
                    new okhttp3.CertificatePinner.Builder().add(
                            configs.getProperty("hostname"),
                            "sha1/tbHJQrYmt+5isj5s44sk794iYFc=",
                            "sha1/SXxoaOSEzPC6BgGmxAt/EAcsajw=",
                            "sha1/blhOM3W9V/bVQhsWAcLYwPU6n24=",
                            "sha1/T5x9IXmcrQ7YuQxXnxoCmeeQ84c="
                    ).build());
        }

        Gson gson = new GsonBuilder()
                .setDateFormat(UNIVERSAL_DATE_FORMAT)
                .create();

        Retrofit retrofit = new Retrofit
                .Builder()
                .baseUrl(endpoint.getUrl())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClientBuilder.build())
                .build();

        return retrofit.create(HandyRetrofit2Service.class);
    }

    //stripe
    @Provides
    @Singleton
    final StripeRetrofitEndpoint provideStripeEndpoint() {
        return new StripeRetrofitEndpoint(context);
    }

    @Provides
    @Singleton
    final FileManager provideFileManager() {
        return new FileManager(context);
    }

    @Provides
    @Singleton
    final StripeRetrofitService provideStripeService(final StripeRetrofitEndpoint endpoint) //TODO: clean up
    {
        final OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setReadTimeout(60, TimeUnit.SECONDS);

        final RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(endpoint)
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) { }
                }).setClient(new OkClient(okHttpClient)).build();
        return restAdapter.create(StripeRetrofitService.class);
    }

    @Provides
    @Singleton
    final DynamicEndpoint provideDynamicEndpoint() {
        return new DynamicEndpoint();
    }

    @Provides
    @Singleton
    final DynamicEndpointService provideDynamicEndpointService(
            final DynamicEndpoint endpoint,
            final PrefsManager prefsManager) {
        final OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setReadTimeout(60, TimeUnit.SECONDS);
        okHttpClient.setWriteTimeout(90, TimeUnit.SECONDS);

        final RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(endpoint)
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        String authToken = prefsManager.getSecureString(PrefsKey.AUTH_TOKEN, null);
                        if (authToken != null) {
                            request.addHeader("X-Auth-Token", authToken);
                        }
                    }
                }).setClient(new OkClient(okHttpClient)).build();
        if (BuildConfig.DEBUG) {
            restAdapter.setLogLevel(RestAdapter.LogLevel.FULL);
        }
        return restAdapter.create(DynamicEndpointService.class);
    }

    @Provides
    @Singleton
    final EventBus provideBus() {
        return new MainBus();
    }

    @Provides
    @Singleton
    final Application provideApplication() {
        return this.application;
    }

    @Provides
    @Singleton
    final DataManager provideDataManager(final HandyRetrofitService service,
                                         final HandyRetrofit2Service service2,
                                         final HandyRetrofitEndpoint endpoint,
                                         final StripeRetrofitService stripeService, //TODO: refactor and move somewhere else?
                                         final DynamicEndpoint dynamicEndpoint,
                                         final DynamicEndpointService dynamicEndpointService
    ) {
        return new DataManager(service, service2, endpoint, stripeService, dynamicEndpoint, dynamicEndpointService);
    }

    @Provides
    @Singleton
    final SystemManager provideSystemManager(final EventBus bus) {
        return new SystemManager(context, bus);
    }

    @Provides
    @Singleton
    final LoginManager provideLoginManager(final EventBus bus, final DataManager dataManager, final PrefsManager prefsManager) {
        return new LoginManager(bus, dataManager, prefsManager);
    }

    @Provides
    @Singleton
    final ProviderManager provideProviderManager(final EventBus bus, final DataManager dataManager, final PrefsManager prefsManager) {
        return new ProviderManager(bus, dataManager, prefsManager);
    }

    @Provides
    @Singleton
    final ConfigManager provideConfigManager(final DataManager dataManager, final EventBus bus) {
        return new CoreTestConfigManager(dataManager, bus);
    }

    @Provides
    @Singleton
    final PrefsManager providePrefsManager() {
        return new PrefsManager(context);
    }

    @Provides
    @Singleton
    final UrbanAirshipManager provideUrbanAirshipManager(final EventBus bus,
                                                         final DataManager dataManager,
                                                         final PrefsManager prefsManager,
                                                         final Application associatedApplication,
                                                         final CustomDeepLinkAction customDeepLinkAction) {
        return new UrbanAirshipManager(bus, dataManager, prefsManager, associatedApplication, customDeepLinkAction);
    }

    @Provides
    final CustomDeepLinkAction provideCustomDeepLinkAction()

    {
        return new CustomDeepLinkAction();
    }

    @Provides
    @Singleton
    final ZipClusterManager provideZipClusterPolygonManager(final EventBus bus, final DataManager dataManager) {
        return new ZipClusterManager(bus, dataManager);
    }

    @Provides
    @Singleton
    final StripeManager provideStripeManager(final EventBus bus, final DataManager dataManager) {
        return new StripeManager(context, bus, dataManager);
    }

    @Provides
    @Singleton
    final EventLogManager provideLogEventsManager(final EventBus bus,
                                                  final DataManager dataManager,
                                                  final FileManager fileManager,
                                                  final PrefsManager prefsManager,
                                                  final ProviderManager providerManager) {
        return new EventLogManager(
                context, bus, dataManager, fileManager, prefsManager, providerManager);
    }

    @Provides
    @Singleton
    final RegionDefinitionsManager provideRegionDefinitionsManager(final EventBus bus) {
        return new RegionDefinitionsManager(bus);
    }

    @Provides
    @Singleton
    final PageNavigationManager providePageNavigationManager(final EventBus bus) {
        return new PageNavigationManager(bus);
    }

    @Provides
    @Singleton
    final SetupManager provideApplicationSetupManager(final EventBus bus,
                                                      final DataManager dataManager) {
        return new SetupManager(bus, dataManager);
    }

    private String getDeviceCarrier() {
        final TelephonyManager telephonyManager =
                ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE));
        if (telephonyManager != null) {
            final String networkOperatorName = telephonyManager.getNetworkOperatorName();
            if (networkOperatorName != null) {
                return networkOperatorName;
            }
        }
        return "";
    }
}
