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
import com.handy.portal.ui.fragment.LoginActivityFragment;
import com.squareup.otto.Bus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static org.mockito.Mockito.mock;

@Module(injects = {
        TestBaseApplication.class,
        LoginActivityFragment.class,
        LoginActivity.class
}, library = true)
public class TestApplicationModule {

    @Provides
    @Singleton
    public HandyRetrofitEndpoint provideHandyEndpoint() {
        return mock(HandyRetrofitEndpoint.class);
    }

    @Provides
    @Singleton
    public HandyRetrofitService provideHandyService() {
        return mock(HandyRetrofitService.class);
    }

    @Provides
    @Singleton
    public DataManager provideDataManager() {
        return mock(DataManager.class);
    }

    @Provides
    @Singleton
    public DataManagerErrorHandler provideDataManagerErrorHandler() {
        return mock(DataManagerErrorHandler.class);
    }

    @Provides
    @Singleton
    public Bus provideBus() {
        return mock(Bus.class);
    }

    @Provides
    @Singleton
    public SecurePreferences providePrefs() {
        return mock(SecurePreferences.class);
    }

    @Provides
    @Singleton
    public BookingManager provideBookingManager() {
        return mock(BookingManager.class);
    }

    @Provides
    @Singleton
    public UserManager provideUserManager() {
        return mock(UserManager.class);
    }

    @Provides
    @Singleton
    public Mixpanel provideMixpanel() {
        return mock(Mixpanel.class);
    }

    @Provides
    @Singleton
    final GoogleService provideGoogleService() {
        return mock(GoogleService.class);
    }

    @Provides
    @Singleton
    final NavigationManager provideNavigationManager() {
        return mock(NavigationManager.class);
    }

    @Provides
    @Singleton
    final EnvironmentManager provideEnvironmentManager() {
        return mock(EnvironmentManager.class);
    }

    @Provides
    @Singleton
    final FlavorManager provideFlavorManager() {
        return mock(FlavorManager.class);
    }
}
