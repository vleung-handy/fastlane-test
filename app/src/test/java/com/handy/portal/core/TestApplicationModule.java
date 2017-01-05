package com.handy.portal.core;

import android.app.Application;
import android.content.Context;

import com.handy.portal.bookings.manager.BookingManager;
import com.handy.portal.bookings.ui.fragment.AvailableBookingsFragment;
import com.handy.portal.bookings.ui.fragment.BookingDetailsWrapperFragment;
import com.handy.portal.bookings.ui.fragment.BookingFragment;
import com.handy.portal.bookings.ui.fragment.InProgressBookingFragment;
import com.handy.portal.bookings.ui.fragment.ScheduledBookingsFragment;
import com.handy.portal.bookings.ui.fragment.SendReceiptCheckoutFragment;
import com.handy.portal.bookings.ui.fragment.SendReceiptCheckoutFragmentTest;
import com.handy.portal.clients.ui.fragment.ProRequestedJobsFragment;
import com.handy.portal.clients.ui.fragment.dialog.SwapBookingClaimDialogFragment;
import com.handy.portal.core.manager.AppseeManager;
import com.handy.portal.core.manager.ConfigManager;
import com.handy.portal.core.manager.FileManager;
import com.handy.portal.core.manager.LoginManager;
import com.handy.portal.core.manager.PageNavigationManager;
import com.handy.portal.core.manager.PrefsManager;
import com.handy.portal.core.manager.ProviderManager;
import com.handy.portal.core.manager.StripeManager;
import com.handy.portal.core.manager.SystemManager;
import com.handy.portal.core.manager.TermsManager;
import com.handy.portal.core.manager.UrbanAirshipManager;
import com.handy.portal.core.manager.UserInterfaceUpdateManager;
import com.handy.portal.core.ui.activity.BaseActivity;
import com.handy.portal.core.ui.activity.LoginActivity;
import com.handy.portal.core.ui.activity.MainActivity;
import com.handy.portal.core.ui.activity.SplashActivity;
import com.handy.portal.core.ui.activity.TestActivity;
import com.handy.portal.core.ui.element.SupportActionView;
import com.handy.portal.core.ui.fragment.AccountSettingsFragment;
import com.handy.portal.core.ui.fragment.LoginActivityFragment;
import com.handy.portal.core.ui.fragment.LoginSltFragment;
import com.handy.portal.core.ui.fragment.MainActivityTest;
import com.handy.portal.core.ui.fragment.MainActivityTest2;
import com.handy.portal.core.ui.fragment.ProfileUpdateFragment;
import com.handy.portal.core.ui.fragment.ReferAFriendFragment;
import com.handy.portal.dashboard.fragment.DashboardFeedbackFragment;
import com.handy.portal.dashboard.fragment.DashboardReviewsFragment;
import com.handy.portal.dashboard.fragment.DashboardTiersFragment;
import com.handy.portal.dashboard.fragment.DashboardVideoLibraryFragment;
import com.handy.portal.dashboard.fragment.RatingsAndFeedbackFragment;
import com.handy.portal.dashboard.view.DashboardFeedbackView;
import com.handy.portal.dashboard.view.DashboardOptionsPerformanceView;
import com.handy.portal.data.DataManager;
import com.handy.portal.data.TestDataManager;
import com.handy.portal.helpcenter.ui.fragment.HelpWebViewFragment;
import com.handy.portal.location.ui.LocationSettingsBlockerDialogFragment;
import com.handy.portal.logger.handylogger.EventLogManager;
import com.handy.portal.logger.handylogger.EventLogManagerTest;
import com.handy.portal.notification.ui.fragment.NotificationsFragment;
import com.handy.portal.onboarding.ui.activity.OnboardingFlowActivity;
import com.handy.portal.onboarding.ui.activity.OnboardingSubflowActivity;
import com.handy.portal.onboarding.ui.fragment.IDVerificationFragment;
import com.handy.portal.onboarding.ui.fragment.NewPurchaseSuppliesFragment;
import com.handy.portal.onboarding.ui.fragment.OnboardingStatusFragment;
import com.handy.portal.onboarding.ui.fragment.PurchaseSuppliesConfirmationFragment;
import com.handy.portal.onboarding.ui.fragment.PurchaseSuppliesFragment;
import com.handy.portal.onboarding.ui.fragment.ScheduleBuilderFragment;
import com.handy.portal.onboarding.ui.fragment.ScheduleConfirmationFragment;
import com.handy.portal.onboarding.ui.fragment.ScheduleConfirmationFragmentTest;
import com.handy.portal.onboarding.ui.fragment.SchedulePreferencesFragment;
import com.handy.portal.onboarding.ui.fragment.SchedulePreferencesFragmentTest;
import com.handy.portal.payments.PaymentsManager;
import com.handy.portal.payments.ui.adapter.PaymentBatchListAdapter;
import com.handy.portal.payments.ui.element.PaymentsBatchListView;
import com.handy.portal.payments.ui.fragment.PaymentBlockingFragment;
import com.handy.portal.payments.ui.fragment.PaymentsDetailFragment;
import com.handy.portal.payments.ui.fragment.PaymentsFragment;
import com.handy.portal.payments.ui.fragment.PaymentsFragmentTest;
import com.handy.portal.retrofit.DynamicEndpoint;
import com.handy.portal.retrofit.DynamicEndpointService;
import com.handy.portal.retrofit.HandyRetrofitEndpoint;
import com.handy.portal.retrofit.HandyRetrofitService;
import com.handy.portal.retrofit.stripe.StripeRetrofitService;
import com.handy.portal.setup.SetupManager;
import com.handy.portal.setup.step.AcceptTermsStep;
import com.handy.portal.setup.step.SetConfigurationStep;
import com.handy.portal.setup.step.SetProviderProfileStep;
import com.handy.portal.updater.VersionManager;
import com.handy.portal.updater.ui.PleaseUpdateFragment;
import com.handy.portal.webview.BlockScheduleFragment;
import com.handy.portal.webview.PortalWebViewClient;
import com.handybook.shared.layer.LayerHelper;
import com.securepreferences.SecurePreferences;

import org.greenrobot.eventbus.EventBus;
import org.mockito.Answers;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@Module(injects = {
        TestBaseApplication.class,
        LoginActivityFragment.class,
        LoginActivity.class,
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
        MainActivityTest.class,
        MainActivityTest2.class,
        LocationSettingsBlockerDialogFragment.class,
        SendReceiptCheckoutFragment.class,
        SendReceiptCheckoutFragmentTest.class,
        PaymentBatchListAdapter.class,
        BaseActivity.SetupHandler.class,
        BookingDetailsWrapperFragment.class,
        BookingFragment.class,
        InProgressBookingFragment.class,
        RatingsAndFeedbackFragment.class,
        DashboardOptionsPerformanceView.class,
        DashboardFeedbackFragment.class,
        DashboardFeedbackView.class,
        DashboardReviewsFragment.class,
        DashboardTiersFragment.class,
        ProRequestedJobsFragment.class,
        NotificationsFragment.class,
        ReferAFriendFragment.class,
        AccountSettingsFragment.class,
        DashboardVideoLibraryFragment.class,
        HelpWebViewFragment.class,
        SwapBookingClaimDialogFragment.class,
        OnboardingFlowActivity.class,
        OnboardingSubflowActivity.class,
        OnboardingStatusFragment.class,
        IDVerificationFragment.class,
        SchedulePreferencesFragment.class,
        ScheduleBuilderFragment.class,
        PurchaseSuppliesFragment.class,
        PurchaseSuppliesConfirmationFragment.class,
        NewPurchaseSuppliesFragment.class,
        ScheduleConfirmationFragment.class,
        EventLogManagerTest.class,
        LoginActivity.class,
        LoginSltFragment.class,
        SplashActivity.class,
        SchedulePreferencesFragmentTest.class,
        ScheduleConfirmationFragmentTest.class,
        AcceptTermsStep.class,
        SetConfigurationStep.class,
        SetProviderProfileStep.class,
        PortalWebViewClient.class,
        BlockScheduleFragment.class,
        PaymentBlockingFragment.class,

}, library = true)
public class TestApplicationModule
{
    private final Application mApplication;

    public TestApplicationModule(final Application application)
    {
        mApplication = application;
    }

    @Provides
    @Singleton
    final Context provideContext() { return mApplication.getApplicationContext(); }

    @Provides
    @Singleton
    final AppseeManager provideAppseeManager()
    {
        return mock(AppseeManager.class);
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
        when(environmentModifier.getEnvironment()).thenReturn(EnvironmentModifier.Environment.S);
        return environmentModifier;
    }

    @Provides
    @Singleton
    final HandyRetrofitEndpoint provideHandyEndpoint()
    {
        return new HandyRetrofitEndpoint(mApplication);
    }

    @Provides
    final HandyRetrofitService provideHandyService()
    {
        return mock(HandyRetrofitService.class);
    }

    @Provides
    @Singleton
    final DataManager provideDataManager(final HandyRetrofitEndpoint endpoint)
    {
        return new TestDataManager(
                mock(HandyRetrofitService.class),
                endpoint,
                mock(StripeRetrofitService.class),
                mock(DynamicEndpoint.class),
                mock(DynamicEndpointService.class));
    }

    @Provides
    @Singleton
    final EventBus provideBus()
    {
        return spy(new EventBus());
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
    @Singleton
    final PageNavigationManager providePageNavigationManager(final EventBus bus,
                                                             final PaymentsManager paymentsManager,
                                                             final ConfigManager configManager
    )
    {
        return new PageNavigationManager(bus, paymentsManager, configManager);
    }

    @Provides
    @Singleton
    final BookingManager provideBookingManager(final EventBus bus,
                                               final DataManager dataManager)
    {
        return spy(new BookingManager(bus, dataManager));
    }

    @Provides
    @Singleton
    final LoginManager provideLoginManager(final EventBus bus, final DataManager dataManager, final PrefsManager prefsManager)
    {
        return new LoginManager(bus, dataManager, prefsManager);
    }

    @Provides
    @Singleton
    final SetupManager provideApplicationSetupManager(final EventBus bus, final DataManager dataManager)
    {
        return new SetupManager(bus, dataManager);
    }

    @Provides
    final ProviderManager provideProviderManager()
    {
        ProviderManager providerManager = mock(ProviderManager.class);
        when(providerManager.getLastProviderId()).thenReturn("444");
        return providerManager;
    }

    @Provides
    @Singleton
    final ConfigManager provideConfigManager(final DataManager dataManager, final EventBus bus)
    {
        return spy(new ConfigManager(dataManager, bus));
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
        return new PrefsManager(mApplication.getApplicationContext());
    }

    @Provides
    @Singleton
    final EventLogManager provideLogEventsManager(
            final EventBus bus,
            final DataManager dataManager,
            final FileManager fileManager,
            final PrefsManager prefsManager,
            final ProviderManager providerManager
    )
    {
        return spy(new EventLogManager(mApplication, bus, dataManager, fileManager, prefsManager, providerManager));
    }

    @Provides
    @Singleton
    final FileManager provideFileManager()
    {
        return new FileManager(mApplication);
    }

    @Provides
    final StripeManager provideStripeManager()
    {
        return mock(StripeManager.class);
    }

    @Provides
    final UserInterfaceUpdateManager provideUserInterfaceUpdateManager()
    {
        return mock(UserInterfaceUpdateManager.class);
    }

    @Provides
    final UrbanAirshipManager providerUrbanAirshipManager(final EventBus bus, final DataManager dataManager, final PrefsManager prefsManager, final Application associatedApplication)
    {
        return mock(UrbanAirshipManager.class);
    }

    @Provides
    final RestAdapter provideRestAdapter()
    {
        return mock(RestAdapter.class);
    }

    @Provides
    final LayerHelper provideLayerHelper()
    {
        return mock(LayerHelper.class, Answers.RETURNS_DEEP_STUBS.get());
    }

    @Provides
    @Singleton
    final PaymentsManager providePaymentsManager(EventBus bus, final DataManager dataManager)
    {
        return spy(new PaymentsManager(bus, dataManager));
    }
}
