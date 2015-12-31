package com.handy.portal.core;

import android.app.Application;

import com.handy.portal.analytics.Mixpanel;
import com.handy.portal.data.DataManager;
import com.handy.portal.manager.BookingManager;
import com.handy.portal.manager.ConfigManager;
import com.handy.portal.manager.EventLogManager;
import com.handy.portal.manager.GoogleManager;
import com.handy.portal.manager.LoginManager;
import com.handy.portal.manager.PrefsManager;
import com.handy.portal.manager.ProviderManager;
import com.handy.portal.manager.StripeManager;
import com.handy.portal.manager.TermsManager;
import com.handy.portal.manager.UrbanAirshipManager;
import com.handy.portal.manager.VersionManager;
import com.handy.portal.model.logs.EventLogFactory;
import com.handy.portal.retrofit.HandyRetrofitEndpoint;
import com.handy.portal.retrofit.HandyRetrofitService;
import com.handy.portal.ui.activity.LoginActivity;
import com.handy.portal.ui.activity.MainActivity;
import com.handy.portal.ui.activity.TestActivity;
import com.handy.portal.ui.element.payments.PaymentsBatchListView;
import com.handy.portal.ui.element.profile.ManagementToolsView;
import com.handy.portal.ui.fragment.AvailableBookingsFragment;
import com.handy.portal.ui.fragment.BookingDetailsFragment;
import com.handy.portal.ui.fragment.HelpContactFragment;
import com.handy.portal.ui.fragment.HelpFragment;
import com.handy.portal.ui.fragment.LoginActivityFragment;
import com.handy.portal.ui.fragment.MainActivityFragment;
import com.handy.portal.ui.fragment.PleaseUpdateFragment;
import com.handy.portal.ui.fragment.ScheduledBookingsFragment;
import com.handy.portal.ui.fragment.payments.PaymentsDetailFragment;
import com.handy.portal.ui.fragment.payments.PaymentsFragment;
import com.handy.portal.ui.fragment.profile.ProfileFragment;
import com.securepreferences.SecurePreferences;
import com.squareup.otto.Bus;

import org.robolectric.shadows.ShadowPreferenceManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Module(injects = {
        TestBaseApplication.class,
        LoginActivityFragment.class,
        LoginActivity.class,
        MainActivityFragment.class,
        MainActivity.class,
        ProfileFragment.class,
        PleaseUpdateFragment.class,
        AvailableBookingsFragment.class,
        ScheduledBookingsFragment.class,
        BookingDetailsFragment.class,
        HelpFragment.class,
        HelpContactFragment.class,
        UrbanAirshipManager.class,
        PaymentsFragment.class,
        PaymentsDetailFragment.class,
        TestActivity.class,
        PaymentsBatchListView.class,
        ManagementToolsView.class,
}, library = true)
public class TestApplicationModule
{
    private final Application application;

    public TestApplicationModule(final Application application)
    {
        this.application = application;
    }

    @Provides
    final BuildConfigWrapper provideBuildConfigWrapper()
    {
        return mock(BuildConfigWrapper.class);
    }

    @Provides
    final EnvironmentModifier provideEnvironmentModifier()
    {
        EnvironmentModifier environmentModifier = mock(EnvironmentModifier.class);
        when(environmentModifier.getEnvironmentPrefix()).thenReturn("s");
        return environmentModifier;
    }

    @Provides
    final HandyRetrofitEndpoint provideHandyEndpoint()
    {
        return mock(HandyRetrofitEndpoint.class);
    }

    @Provides
    final HandyRetrofitService provideHandyService()
    {
        return mock(HandyRetrofitService.class);
    }

    @Provides
    final DataManager provideDataManager()
    {
        return mock(DataManager.class);
    }

    @Provides
    final Bus provideBus()
    {
        return mock(Bus.class);
    }

    @Provides
    final Application provideApplication()
    {
        return mock(Application.class);
    }

    @Provides
    final SecurePreferences providePrefs()
    {
        return mock(SecurePreferences.class);
    }

    @Provides
    final BookingManager provideBookingManager()
    {
        return mock(BookingManager.class);
    }

    @Provides
    final LoginManager provideLoginManager()
    {
        return mock(LoginManager.class);
    }

    @Provides
    final ProviderManager provideProviderManager()
    {
        return mock(ProviderManager.class);
    }

    @Provides
    final ConfigManager provideConfigManager()
    {
        return mock(ConfigManager.class);
    }

    @Provides
    final VersionManager provideVersionManager()
    {
        return mock(VersionManager.class);
    }

    @Provides
    final TermsManager provideTermsManager()
    {
        return mock(TermsManager.class);
    }

    @Provides
    @Singleton
    final PrefsManager providePrefsManager()
    {
        return new PrefsManager(ShadowPreferenceManager.getDefaultSharedPreferences(application));
    }

    @Provides
    final EventLogManager eventLogManager()
    {
        return mock(EventLogManager.class);
    }

    @Provides
    final StripeManager provideStripeManager()
    {
        return mock(StripeManager.class);
    }

    @Provides
    final ApplicationOnResumeWatcher provideApplicationOnResumeWatcher()
    {
        return mock(ApplicationOnResumeWatcher.class);
    }

    @Provides
    final Mixpanel provideMixpanel()
    {
        return mock(Mixpanel.class);
    }

    @Provides
    final GoogleManager provideGoogleService()
    {
        return mock(GoogleManager.class);
    }

    @Provides
    final UrbanAirshipManager providerUrbanAirshipManager(final Bus bus, final DataManager dataManager, final PrefsManager prefsManager, final Application associatedApplication)
    {
        return mock(UrbanAirshipManager.class);
    }

    @Provides
    final EventLogFactory provideEventLogFactory() { return mock(EventLogFactory.class); }

}
