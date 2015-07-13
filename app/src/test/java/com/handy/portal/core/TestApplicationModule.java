package com.handy.portal.core;


import com.handy.portal.analytics.Mixpanel;
import com.handy.portal.data.DataManager;
import com.handy.portal.manager.BookingManager;
import com.handy.portal.manager.ConfigManager;
import com.handy.portal.manager.GoogleManager;
import com.handy.portal.manager.LoginManager;
import com.handy.portal.manager.PrefsManager;
import com.handy.portal.manager.TermsManager;
import com.handy.portal.manager.VersionManager;
import com.handy.portal.retrofit.HandyRetrofitEndpoint;
import com.handy.portal.retrofit.HandyRetrofitService;
import com.handy.portal.ui.activity.LoginActivity;
import com.handy.portal.ui.activity.MainActivity;
import com.handy.portal.ui.fragment.AvailableBookingsFragment;
import com.handy.portal.ui.fragment.BookingDetailsFragment;
import com.handy.portal.ui.fragment.BookingDetailsFragmentTest;
import com.handy.portal.ui.fragment.LoginActivityFragment;
import com.handy.portal.ui.fragment.MainActivityFragment;
import com.handy.portal.ui.fragment.PleaseUpdateFragment;
import com.handy.portal.ui.fragment.PortalWebViewFragment;
import com.handy.portal.ui.fragment.ScheduledBookingsFragment;
import com.securepreferences.SecurePreferences;
import com.squareup.otto.Bus;

import dagger.Module;
import dagger.Provides;

import static org.mockito.Mockito.mock;

@Module(injects = {
        TestBaseApplication.class,
        LoginActivityFragment.class,
        LoginActivity.class,
        MainActivityFragment.class,
        MainActivity.class,
        PortalWebViewFragment.class,
        PleaseUpdateFragment.class,
        AvailableBookingsFragment.class,
        ScheduledBookingsFragment.class,
        BookingDetailsFragment.class
}, library = true)
public class TestApplicationModule
{

    @Provides
    final BuildConfigWrapper provideBuildConfigWrapper()
    {
        return mock(BuildConfigWrapper.class);
    }

    @Provides
    final EnvironmentModifier provideEnvironmentModifier()
    {
        return mock(EnvironmentModifier.class);
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
    final PrefsManager providePrefsManager()
    {
        return mock(PrefsManager.class);
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
}
