package com.handy.portal.core;


import com.handy.portal.analytics.Mixpanel;
import com.handy.portal.data.DataManager;
import com.handy.portal.manager.BookingManager;
import com.handy.portal.manager.GoogleManager;
import com.handy.portal.manager.VersionManager;
import com.handy.portal.retrofit.HandyRetrofitEndpoint;
import com.handy.portal.retrofit.HandyRetrofitService;
import com.handy.portal.ui.activity.LoginActivity;
import com.handy.portal.ui.activity.MainActivity;
import com.handy.portal.ui.fragment.AvailableBookingsFragment;
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
        ScheduledBookingsFragment.class
}, library = true)
public class TestApplicationModule {

    @Provides
    public HandyRetrofitEndpoint provideHandyEndpoint() {
        return mock(HandyRetrofitEndpoint.class);
    }

    @Provides
    public HandyRetrofitService provideHandyService() {
        return mock(HandyRetrofitService.class);
    }

    @Provides
    public DataManager provideDataManager() {
        return mock(DataManager.class);
    }

    @Provides
    public Bus provideBus() {
        return mock(Bus.class);
    }

    @Provides
    public SecurePreferences providePrefs() {
        return mock(SecurePreferences.class);
    }

    @Provides
    public BookingManager provideBookingManager() {
        return mock(BookingManager.class);
    }

    @Provides
    public Mixpanel provideMixpanel() {
        return mock(Mixpanel.class);
    }

    @Provides
    final GoogleManager provideGoogleService() {
        return mock(GoogleManager.class);
    }

    @Provides
    final EnvironmentModifier provideEnvironmentManager() {
        return mock(EnvironmentModifier.class);
    }

    @Provides
    final BuildConfigWrapper provideBuildConfigWrapper() {
        return mock(BuildConfigWrapper.class);
    }

    @Provides
    final VersionManager provideVersionManager() {
        return mock(VersionManager.class);
    }
}
