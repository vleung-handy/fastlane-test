package com.handy.portal.core;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.util.Base64;

import com.google.gson.GsonBuilder;
import com.handy.portal.BuildConfig;
import com.handy.portal.data.BaseDataManager;
import com.handy.portal.data.BaseDataManagerErrorHandler;
import com.handy.portal.data.DataManager;
import com.handy.portal.data.DataManagerErrorHandler;
import com.handy.portal.data.HandyRetrofitEndpoint;
import com.handy.portal.data.HandyRetrofitFluidEndpoint;
import com.handy.portal.data.HandyRetrofitService;
import com.handy.portal.data.Mixpanel;
import com.handy.portal.data.PropertiesReader;
import com.handy.portal.data.SecurePreferences;
import com.handy.portal.ui.activity.BaseActivity;
import com.handy.portal.ui.activity.LoginActivity;
import com.handy.portal.ui.activity.MainActivity;
import com.handy.portal.ui.activity.SplashActivity;
import com.handy.portal.ui.fragment.AvailableBookingsFragment;
import com.handy.portal.ui.fragment.HelpFragment;
import com.handy.portal.ui.fragment.LoginActivityFragment;
import com.handy.portal.ui.fragment.MainActivityFragment;
import com.handy.portal.ui.fragment.PortalWebViewFragment;
import com.handy.portal.ui.fragment.ProfileFragment;
import com.handy.portal.ui.fragment.ScheduledBookingsFragment;
import com.handy.portal.data.EnvironmentSwitcher;
import com.handy.portal.util.FlavorUtils;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.otto.Bus;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

@Module(injects = {
        LoginActivityFragment.class,
        LoginActivity.class,
        PortalWebViewFragment.class,
        ScheduledBookingsFragment.class,
        ProfileFragment.class,
        HelpFragment.class,
        AvailableBookingsFragment.class,
        PortalWebViewClient.class,
        MainActivityFragment.class,
        BaseApplication.class,
        BaseActivity.class,
        MainActivity.class,
        SplashActivity.class
})
public final class ApplicationModule
{
    private final Context context;
    private final Properties configs;

    public ApplicationModule(final Context context)
    {
        this.context = context.getApplicationContext();
        configs = PropertiesReader.getConfigProperties(context);
    }

    @Provides
    @Singleton
    final EnvironmentSwitcher provideEnvironmentSwitcher()
    {
        return new EnvironmentSwitcher();
    }

    @Provides
    @Singleton
    final HandyRetrofitEndpoint provideHandyEnpoint(final EnvironmentSwitcher environmentSwitcher)
    {
        if (FlavorUtils.isStageFlavor())
        {
            return new HandyRetrofitFluidEndpoint(context, environmentSwitcher);
        }
        return new HandyRetrofitEndpoint(context);
    }

    @Provides
    @Singleton
    final HandyRetrofitService provideHandyService(
            final HandyRetrofitEndpoint endpoint, final UserManager userManager)
    {

        final OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setReadTimeout(10, TimeUnit.SECONDS);

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
                        request.addHeader("Authorization", auth);
                        request.addHeader("Accept", "application/json");
                        request.addQueryParam("client", "android");
                        request.addQueryParam("app_version", BuildConfig.VERSION_NAME);
                        request.addQueryParam("apiver", "1");
                        request.addQueryParam("app_device_id", getDeviceId());
                        request.addQueryParam("app_device_model", getDeviceName());
                        request.addQueryParam("app_device_os", Build.VERSION.RELEASE);

                        final User user = userManager.getCurrentUser();
                        if (user != null) request.addQueryParam("app_user_id", user.getId());
                    }
                }).setConverter(new GsonConverter(new GsonBuilder()
                        .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                        .setExclusionStrategies(BookingRequest.getExclusionStrategy())
                        .registerTypeAdapter(BookingRequest.class,
                                new BookingRequest.BookingRequestSerializer())
                        .setExclusionStrategies(BookingQuote.getExclusionStrategy())
                        .registerTypeAdapter(BookingQuote.class,
                                new BookingQuote.BookingQuoteSerializer())
                        .setExclusionStrategies(BookingPostInfo.getExclusionStrategy())
                        .registerTypeAdapter(BookingPostInfo.class,
                                new BookingPostInfo.BookingPostInfoSerializer())
                        .setExclusionStrategies(BookingTransaction.getExclusionStrategy())
                        .registerTypeAdapter(BookingTransaction.class,
                                new BookingTransaction.BookingTransactionSerializer())
                        .setExclusionStrategies(User.getExclusionStrategy())
                        .registerTypeAdapter(User.class, new User.UserSerializer())
                        .create())).setClient(new OkClient(okHttpClient)).build();

        if (!BuildConfig.FLAVOR.equals(BaseApplication.FLAVOR_PROD)
                || BuildConfig.BUILD_TYPE.equals("debug"))
            restAdapter.setLogLevel(RestAdapter.LogLevel.FULL);

        return restAdapter.create(HandyRetrofitService.class);
    }

    @Provides
    @Singleton
    final DataManager provideDataManager(final HandyRetrofitService service,
                                         final HandyRetrofitEndpoint endpoint,
                                         final Bus bus,
                                         final SecurePreferences prefs)
    {
        return new BaseDataManager(service, endpoint, bus, prefs);
    }

    @Provides
    final DataManagerErrorHandler provideDataManagerErrorHandler()
    {
        return new BaseDataManagerErrorHandler();
    }

    @Provides
    @Singleton
    final Bus provideBus()
    {
        return new MainBus();
    }

    @Provides
    @Singleton
    final SecurePreferences providePrefs()
    {
        return new SecurePreferences(context, null,
                configs.getProperty("secure_prefs_key"), true);
    }

    @Provides
    @Singleton
    final BookingManager provideBookingManager(final Bus bus,
                                               final SecurePreferences prefs,
                                               final DataManager dataManager)
    {
        return new BookingManager(bus, prefs, dataManager);
    }

    @Provides
    @Singleton
    final UserManager provideUserManager(final Bus bus,
                                         final SecurePreferences prefs)
    {
        return new UserManager(bus, prefs);
    }


    @Provides
    @Singleton
    final LoginManager provideLoginManager(final Bus bus,
                                           final DataManager dataManager)
    {
        return new LoginManager(bus, dataManager);
    }

//    @Provides final ReactiveLocationProvider provideReactiveLocationProvider() {
//        return new ReactiveLocationProvider(context);
//    }

    @Provides
    @Singleton
    final Mixpanel provideMixpanel(final UserManager userManager,
                                   final BookingManager bookingManager,
                                   final Bus bus)
    {
        return new Mixpanel(context, userManager, bookingManager, bus);
    }

    @Provides
    @Singleton
    final NavigationManager provideNavigationManager(final UserManager userManager,
                                                     final DataManager dataManager,
                                                     final DataManagerErrorHandler dataManagerErrorHandler)
    {
        return new NavigationManager(this.context, userManager, dataManager, dataManagerErrorHandler);
    }

    @Provides
    @Singleton
    final GoogleService provideGoogleService()
    {
        return new GoogleService(this.context);
    }


    private String getDeviceId()
    {
        return Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    private String getDeviceName()
    {
        final String manufacturer = Build.MANUFACTURER;
        final String model = Build.MODEL;

        if (model.startsWith(manufacturer)) return model;
        else return manufacturer + " " + model;
    }
}
