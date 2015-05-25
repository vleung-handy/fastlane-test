package com.handy.portal.core;


import com.handy.portal.data.DataManager;
import com.handy.portal.data.DataManagerErrorHandler;
import com.handy.portal.data.EnvironmentManager;
import com.handy.portal.data.FlavorManager;
import com.handy.portal.data.HandyRetrofitEndpoint;
import com.handy.portal.data.HandyRetrofitService;
import com.handy.portal.data.Mixpanel;
import com.handy.portal.data.SecurePreferences;
import com.handy.portal.ui.activity.LoginActivity;
import com.handy.portal.ui.activity.MainActivity;
import com.handy.portal.ui.fragment.LoginActivityFragment;
import com.handy.portal.ui.fragment.MainActivityFragment;
import com.handy.portal.ui.fragment.PleaseUpdateFragment;
import com.handy.portal.ui.fragment.PortalWebViewFragment;
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
        PleaseUpdateFragment.class
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
    public DataManagerErrorHandler provideDataManagerErrorHandler() {
        return mock(DataManagerErrorHandler.class);
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
    public UserManager provideUserManager() {
        return mock(UserManager.class);
    }

    @Provides
    public Mixpanel provideMixpanel() {
        return mock(Mixpanel.class);
    }

    @Provides
    final GoogleService provideGoogleService() {
        return mock(GoogleService.class);
    }

    @Provides
    final NavigationManager provideNavigationManager() {
        return mock(NavigationManager.class);
    }

    @Provides
    final EnvironmentManager provideEnvironmentManager() {
        return mock(EnvironmentManager.class);
    }

    @Provides
    final FlavorManager provideFlavorManager() {
        return mock(FlavorManager.class);
    }
}
