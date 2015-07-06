package com.handy.portal.core;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.util.Base64;

import com.google.gson.GsonBuilder;
import com.handy.portal.BuildConfig;
import com.handy.portal.data.BaseDataManager;
import com.handy.portal.data.BaseDataManagerErrorHandler;
import com.handy.portal.data.BuildConfigWrapper;
import com.handy.portal.data.DataManager;
import com.handy.portal.data.DataManagerErrorHandler;
import com.handy.portal.data.EnvironmentManager;
import com.handy.portal.data.HandyRetrofitEndpoint;
import com.handy.portal.data.HandyRetrofitFluidEndpoint;
import com.handy.portal.data.HandyRetrofitService;
import com.handy.portal.data.Mixpanel;
import com.handy.portal.data.PropertiesReader;
import com.handy.portal.ui.activity.BaseActivity;
import com.handy.portal.ui.activity.LoginActivity;
import com.handy.portal.ui.activity.MainActivity;
import com.handy.portal.ui.activity.PleaseUpdateActivity;
import com.handy.portal.ui.activity.SplashActivity;
import com.handy.portal.ui.activity.TermsActivity;
import com.handy.portal.ui.fragment.AvailableBookingsFragment;
import com.handy.portal.ui.fragment.BookingDetailsFragment;
import com.handy.portal.ui.fragment.HelpFragment;
import com.handy.portal.ui.fragment.LoginActivityFragment;
import com.handy.portal.ui.fragment.MainActivityFragment;
import com.handy.portal.ui.fragment.PleaseUpdateFragment;
import com.handy.portal.ui.fragment.PortalWebViewFragment;
import com.handy.portal.ui.fragment.ProfileFragment;
import com.handy.portal.ui.fragment.ScheduledBookingsFragment;
import com.handy.portal.ui.fragment.TermsFragment;
import com.securepreferences.SecurePreferences;
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
        BookingDetailsFragment.class,
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
        SplashActivity.class,
        PleaseUpdateActivity.class,
        PleaseUpdateFragment.class,
        TermsActivity.class,
        TermsFragment.class
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
    final BuildConfigWrapper provideBuildConfigWrapper()
    {
        return new BuildConfigWrapper();
    }

    @Provides
    @Singleton
    final EnvironmentManager provideEnvironmentManager()
    {
        return new EnvironmentManager();
    }

    @Provides
    @Singleton
    final HandyRetrofitEndpoint provideHandyEndpoint(final BuildConfigWrapper buildConfigWrapper, final EnvironmentManager environmentManager)
    {
        if (buildConfigWrapper.isDebug())
        {
            return new HandyRetrofitFluidEndpoint(context, environmentManager);
        }
        return new HandyRetrofitEndpoint(context);
    }

    @Provides
    @Singleton
    final HandyRetrofitService provideHandyService(final BuildConfigWrapper buildConfigWrapper,
                                                   final HandyRetrofitEndpoint endpoint)
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
                        request.addQueryParam("client", "android");
                        request.addQueryParam("app_version", BuildConfig.VERSION_NAME);
                        request.addQueryParam("apiver", "1");
                        request.addQueryParam("app_device_id", getDeviceId());
                        request.addQueryParam("app_device_model", getDeviceName());
                        request.addQueryParam("app_device_os", Build.VERSION.RELEASE);
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

    @Provides
    @Singleton
    final DataManager provideDataManager(final HandyRetrofitService service,
                                         final HandyRetrofitEndpoint endpoint,
                                         final SecurePreferences prefs
    )
    {
        return new BaseDataManager(service, endpoint, prefs);
    }

    @Provides
    final DataManagerErrorHandler provideDataManagerErrorHandler()
    {
        return new BaseDataManagerErrorHandler();
    }

    @Provides
    @Singleton
    final Bus provideBus(final Mixpanel mixpanel)
    {
        return new MainBus(mixpanel);
    }

    @Provides
    @Singleton
    final SecurePreferences providePrefs()
    {
        return new SecurePreferences(context,
                configs.getProperty("secure_prefs_key"), "prefs.xml");
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
    final LoginManager provideLoginManager(final Bus bus, final SecurePreferences prefs, final DataManager dataManager)
    {
        return new LoginManager(bus, prefs, dataManager);
    }

    @Provides
    @Singleton
    final ConfigManager provideConfigManager(final DataManager dataManager)
    {
        return new ConfigManager(dataManager);
    }

    @Provides
    @Singleton
    final VersionManager provideVersionManager(final Bus bus,
                                               final DataManager dataManager,
                                               final BuildConfigWrapper buildConfigWrapper)
    {
        return new VersionManager(context, bus, dataManager, buildConfigWrapper);
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
    final ApplicationOnResumeWatcher provideApplicationOnResumeWatcher(final Bus bus)
    {
        return new ApplicationOnResumeWatcher(bus);
    }

//    @Provides final ReactiveLocationProvider provideReactiveLocationProvider() {
//        return new ReactiveLocationProvider(context);
//    }

    @Provides
    @Singleton
    final Mixpanel provideMixpanel()
    {
        return new Mixpanel(context);
    }

    @Provides
    @Singleton
    final NavigationManager provideNavigationManager(final DataManager dataManager,
                                                     final DataManagerErrorHandler dataManagerErrorHandler)
    {
        return new NavigationManager(this.context, dataManager, dataManagerErrorHandler);
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

        if (model.startsWith(manufacturer))
        {
            return model;
        }
        else
        {
            return manufacturer + " " + model;
        }
    }
}
