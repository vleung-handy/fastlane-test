package com.handy.portal.core;

import android.app.Application;

import com.handy.portal.bookings.BookingManager;
import com.handy.portal.bookings.ui.fragment.AvailableBookingsFragment;
import com.handy.portal.bookings.ui.fragment.ScheduledBookingsFragment;
import com.handy.portal.bookings.ui.fragment.SendReceiptCheckoutFragment;
import com.handy.portal.bookings.ui.fragment.SendReceiptCheckoutFragmentTest;
import com.handy.portal.data.DataManager;
import com.handy.portal.data.TestDataManager;
import com.handy.portal.location.ui.LocationSettingsBlockerDialogFragment;
import com.handy.portal.logger.handylogger.EventLogManager;
import com.handy.portal.logger.mixpanel.Mixpanel;
import com.handy.portal.manager.ConfigManager;
import com.handy.portal.manager.GoogleManager;
import com.handy.portal.manager.LoginManager;
import com.handy.portal.manager.PrefsManager;
import com.handy.portal.manager.ProviderManager;
import com.handy.portal.manager.StripeManager;
import com.handy.portal.manager.SystemManager;
import com.handy.portal.manager.TermsManager;
import com.handy.portal.manager.UrbanAirshipManager;
import com.handy.portal.manager.UserInterfaceUpdateManager;
import com.handy.portal.model.Provider;
import com.handy.portal.payments.ui.adapter.PaymentBatchListAdapter;
import com.handy.portal.payments.ui.element.PaymentsBatchListView;
import com.handy.portal.payments.ui.fragment.PaymentsDetailFragment;
import com.handy.portal.payments.ui.fragment.PaymentsFragment;
import com.handy.portal.payments.ui.fragment.PaymentsFragmentTest;
import com.handy.portal.retrofit.HandyRetrofitEndpoint;
import com.handy.portal.retrofit.HandyRetrofitService;
import com.handy.portal.ui.activity.BaseActivity;
import com.handy.portal.ui.activity.LoginActivity;
import com.handy.portal.ui.activity.MainActivity;
import com.handy.portal.ui.activity.TestActivity;
import com.handy.portal.ui.element.SupportActionView;
import com.handy.portal.ui.fragment.LoginActivityFragment;
import com.handy.portal.ui.fragment.MainActivityFragment;
import com.handy.portal.ui.fragment.MainActivityFragmentTest;
import com.handy.portal.ui.fragment.ProfileUpdateFragment;
import com.handy.portal.updater.VersionManager;
import com.handy.portal.updater.ui.PleaseUpdateFragment;
import com.securepreferences.SecurePreferences;
import org.greenrobot.eventbus.EventBus;

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
        ProfileUpdateFragment.class,
        PleaseUpdateFragment.class,
        AvailableBookingsFragment.class,
        ScheduledBookingsFragment.class,
        UrbanAirshipManager.class,
        PaymentsFragment.class,
        PaymentsDetailFragment.class,
        TestActivity.class,
        PaymentsBatchListView.class,
        SupportActionView.class,
        PaymentsFragmentTest.class,
        MainActivityFragmentTest.class,
        LocationSettingsBlockerDialogFragment.class,
        SendReceiptCheckoutFragment.class,
        SendReceiptCheckoutFragmentTest.class,
        PaymentBatchListAdapter.class,
        BaseActivity.SetupHandler.class,
}, library = true)
public class TestApplicationModule
{
    private final Application mApplication;

    public TestApplicationModule(final Application application)
    {
        mApplication = application;
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
        when(environmentModifier.getEnvironmentPrefix()).thenReturn("ms");
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
    @Singleton
    final DataManager provideDataManager()
    {
        return mock(TestDataManager.class);
    }

    @Provides
    @Singleton
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
        Provider provider = mock(Provider.class);
        when(provider.getId()).thenReturn("444");
        ProviderManager providerManager = mock(ProviderManager.class);
        when(providerManager.getCachedActiveProvider()).thenReturn(provider);
        return providerManager;
    }

    @Provides
    @Singleton
    final ConfigManager provideConfigManager()
    {
        return mock(ConfigManager.class);
    }

    @Provides
    final SystemManager provideSytemManager()
    {
        return mock(SystemManager.class);
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
        return mock(PrefsManager.class);
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
    final UserInterfaceUpdateManager provideUserInterfaceUpdateManager()
    {
        return mock(UserInterfaceUpdateManager.class);
    }

    @Provides
    final UrbanAirshipManager providerUrbanAirshipManager(final Bus bus, final DataManager dataManager, final PrefsManager prefsManager, final Application associatedApplication)
    {
        return mock(UrbanAirshipManager.class);
    }
}
