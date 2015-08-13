package com.handy.portal.core;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.util.Base64;

import com.google.gson.GsonBuilder;
import com.handy.portal.BuildConfig;
import com.handy.portal.action.CustomDeepLinkAction;
import com.handy.portal.analytics.Mixpanel;
import com.handy.portal.constant.PrefsKey;
import com.handy.portal.data.BaseDataManager;
import com.handy.portal.data.DataManager;
import com.handy.portal.manager.BookingManager;
import com.handy.portal.manager.ConfigManager;
import com.handy.portal.manager.GoogleManager;
import com.handy.portal.manager.HelpManager;
import com.handy.portal.manager.LoginManager;
import com.handy.portal.manager.PrefsManager;
import com.handy.portal.manager.ProviderManager;
import com.handy.portal.manager.TermsManager;
import com.handy.portal.manager.UrbanAirshipManager;
import com.handy.portal.manager.VersionManager;
import com.handy.portal.retrofit.HandyRetrofitEndpoint;
import com.handy.portal.retrofit.HandyRetrofitFluidEndpoint;
import com.handy.portal.retrofit.HandyRetrofitService;
import com.handy.portal.ui.activity.BaseActivity;
import com.handy.portal.ui.activity.LoginActivity;
import com.handy.portal.ui.activity.MainActivity;
import com.handy.portal.ui.activity.PleaseUpdateActivity;
import com.handy.portal.ui.activity.SplashActivity;
import com.handy.portal.ui.activity.TermsActivity;
import com.handy.portal.ui.constructor.SupportActionViewConstructor;
import com.handy.portal.ui.fragment.AvailableBookingsFragment;
import com.handy.portal.ui.fragment.BookingDetailsFragment;
import com.handy.portal.ui.fragment.HelpContactFragment;
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
import java.util.TimeZone;
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
        AvailableBookingsFragment.class,
        PortalWebViewClient.class,
        MainActivityFragment.class,
        BaseApplication.class,
        BaseActivity.class,
        SplashActivity.class,
        MainActivity.class,
        PleaseUpdateActivity.class,
        PleaseUpdateFragment.class,
        TermsActivity.class,
        TermsFragment.class,
        HelpFragment.class,
        HelpContactFragment.class,
        SupportActionViewConstructor.class,
        UrbanAirshipManager.class,
})
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
    final EnvironmentModifier provideEnvironmentModifier(final BuildConfigWrapper buildConfigWrapper)
    {
        return new EnvironmentModifier(context, buildConfigWrapper);
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
                                                   final PrefsManager prefsManager)
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
                            request.addQueryParam("auth_token", authToken);
                        }

                        request.addHeader("Authorization", auth);
                        request.addQueryParam("client", "android");
                        request.addQueryParam("app_version", BuildConfig.VERSION_NAME);
                        request.addQueryParam("apiver", "1");
                        request.addQueryParam("app_device_id", getDeviceId());
                        request.addQueryParam("app_device_model", getDeviceName());
                        request.addQueryParam("app_device_os", Build.VERSION.RELEASE);
                        request.addQueryParam("timezone", TimeZone.getDefault().getID());
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
                                         final PrefsManager prefsManager
    )
    {
        return new BaseDataManager(service, endpoint, prefsManager);
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
    final ConfigManager provideConfigManager(final DataManager dataManager)
    {
        return new ConfigManager(dataManager);
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
    final PrefsManager providePrefsManager(final SecurePreferences prefs)
    {
        return new PrefsManager(prefs);
    }

    @Provides
    @Singleton
    final HelpManager provideHelpManager(final Bus bus,
                                               final DataManager dataManager)
    {
        return new HelpManager(bus, dataManager);
    }

    @Provides
    @Singleton
    final ApplicationOnResumeWatcher provideApplicationOnResumeWatcher(final Bus bus)
    {
        return new ApplicationOnResumeWatcher(bus);
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
