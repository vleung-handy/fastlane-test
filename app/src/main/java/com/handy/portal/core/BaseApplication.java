package com.handy.portal.core;

import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.handy.portal.BuildConfig;
import com.handy.portal.R;
import com.handy.portal.announcements.AnnouncementsManager;
import com.handy.portal.bookings.manager.BookingManager;
import com.handy.portal.bookings.manager.BookingModalsManager;
import com.handy.portal.core.event.HandyEvent;
import com.handy.portal.core.manager.ConfigManager;
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
import com.handy.portal.library.util.FontUtils;
import com.handy.portal.location.manager.LocationManager;
import com.handy.portal.location.manager.LocationScheduleUpdateManager;
import com.handy.portal.logger.handylogger.EventLogManager;
import com.handy.portal.notification.NotificationMessageManager;
import com.handy.portal.payments.PaymentsManager;
import com.handy.portal.retrofit.HandyRetrofitEndpoint;
import com.handy.portal.setup.SetupManager;
import com.handy.portal.terms.TermsManager;
import com.handy.portal.updater.VersionManager;
import com.handybook.shared.core.HandyLibrary;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import dagger.ObjectGraph;
import io.fabric.sdk.android.Fabric;
import retrofit.RestAdapter;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class BaseApplication extends MultiDexApplication {
    private ObjectGraph mGraph;

    //We are injecting all of our event bus listening managers in BaseApplication to start them up for event listening
    @Inject
    DataManager dataManager;
    @Inject
    HandyRetrofitEndpoint handyRetrofitEndpoint;
    @Inject
    BookingManager bookingManager;
    @Inject
    BookingModalsManager bookingModalsManager;
    @Inject
    LoginManager loginManager;
    @Inject
    ProviderManager providerManager;
    @Inject
    VersionManager versionManager;
    @Inject
    TermsManager termsManager;
    @Inject
    ConfigManager configManager;
    @Inject
    PrefsManager prefsManager;
    @Inject
    PaymentsManager paymentsManager;
    @Inject
    ZipClusterManager zipClusterManager;
    @Inject
    StripeManager stripeManager;
    @Inject
    EventLogManager logEventsManager;
    @Inject
    RegionDefinitionsManager regionDefinitionsManager;
    @Inject
    UrbanAirshipManager urbanAirshipManager;
    @Inject
    PageNavigationManager mPageNavigationManager;
    @Inject
    NotificationMessageManager notificationMessageManager;
    @Inject
    LocationManager locationManager;
    @Inject
    SystemManager systemManager;
    @Inject
    UserInterfaceUpdateManager userInterfaceUpdateManager;
    @Inject
    SetupManager setupManager;
    @Inject
    LocationScheduleUpdateManager mLocationScheduleUpdateManager;
    @Inject
    RestAdapter mRestAdapter;
    @Inject
    AnnouncementsManager mAnnouncementsManager;

    @Inject
    EventBus bus;

    @Override
    public void onCreate() {
        super.onCreate();
        mGraph = createObjectGraph();
        inject(this);
        HandyLibrary.init(mRestAdapter, this, !BuildConfig.DEBUG);

        startCrashlytics();
        //Start UA
        bus.post(new HandyEvent.StartUrbanAirship());

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(FontUtils.CIRCULAR_BOOK)
                .setFontAttrId(R.attr.fontPath)
                .build());
    }

    protected void startCrashlytics() {
        Crashlytics crashlytics = new Crashlytics.Builder().core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build()).build();
        Fabric.with(this, crashlytics);
    }

    protected ObjectGraph createObjectGraph() {
        return ObjectGraph.create(new ApplicationModule(this));
    }

    public final void inject(final Object object) {
        mGraph.inject(object);
    }
}
