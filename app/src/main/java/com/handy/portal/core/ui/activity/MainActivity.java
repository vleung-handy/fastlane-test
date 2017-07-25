package com.handy.portal.core.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;
import com.handy.portal.announcements.AnnouncementEvent;
import com.handy.portal.announcements.model.Announcement;
import com.handy.portal.bookings.BookingEvent;
import com.handy.portal.bookings.manager.BookingManager;
import com.handy.portal.bookings.ui.element.BookingMapView;
import com.handy.portal.core.EnvironmentModifier;
import com.handy.portal.core.constant.BundleKeys;
import com.handy.portal.core.constant.MainViewPage;
import com.handy.portal.core.constant.TransitionStyle;
import com.handy.portal.core.event.HandyEvent;
import com.handy.portal.core.event.NavigationEvent;
import com.handy.portal.core.manager.PageNavigationManager;
import com.handy.portal.core.manager.ProviderManager;
import com.handy.portal.core.ui.element.bookings.BookingMapProvider;
import com.handy.portal.core.ui.fragment.EditPhotoFragment;
import com.handy.portal.library.ui.layout.TabbedLayout;
import com.handy.portal.library.ui.widget.TabButton;
import com.handy.portal.library.ui.widget.TabButtonGroup;
import com.handy.portal.library.util.FragmentUtils;
import com.handy.portal.notification.NotificationUtils;
import com.handy.portal.notification.ui.fragment.NotificationBlockerDialogFragment;
import com.handy.portal.payments.PaymentEvent;
import com.handy.portal.payments.ui.fragment.dialog.PaymentBillBlockerDialogFragment;
import com.handybook.shared.layer.LayerHelper;

import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.handy.portal.core.model.ProviderPersonalInfo.ProfileImage.Type.THUMBNAIL;

public class MainActivity extends BaseActivity
        implements BookingMapProvider, LayerHelper.UnreadConversationsCountChangedListener {
    @Inject
    ProviderManager providerManager;
    @Inject
    EnvironmentModifier mEnvironmentModifier;
    @Inject
    ProviderManager mProviderManager;
    @Inject
    LayerHelper mLayerHelper;
    @Inject
    BookingManager mBookingManager;
    @Inject
    PageNavigationManager mPageNavigationManager;

    @BindView(R.id.tabs)
    TabButtonGroup mTabs;
    private TabButton mJobsButton;
    private TabButton mClientsButton;
    private TabButton mScheduleButton;
    private TabButton mMessagesButton;
    private TabButton mMoreButton;

    @BindView(R.id.loading_overlay)
    View mLoadingOverlayView;
    @BindView(R.id.content_frame)
    TabbedLayout mContentFrame;

    private BookingMapView mBookingMapView;

    private NotificationBlockerDialogFragment mNotificationBlockerDialogFragment
            = new NotificationBlockerDialogFragment();

    //    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private MainViewPage mCurrentPage = null;
    private String mCurrentTabTitle = null;

    // Other fragments will want to know to avoid re-doing things on their onCreateView
    public static boolean clearingBackStack = false;

    private Bundle mDeeplinkData;
    private boolean mDeeplinkHandled;
    private String mDeeplinkSource;
    private Integer mJobRequestsCount = null;
    private boolean mUploadProfilePictureBlockerShown = false;

    @Override
    protected boolean shouldTriggerSetup() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        setFullScreen();

        setDeeplinkData(savedInstanceState);

        if (savedInstanceState != null) {
            mCurrentPage = (MainViewPage) savedInstanceState.getSerializable(BundleKeys.PAGE);
            mCurrentTabTitle = savedInstanceState.getString(BundleKeys.TAB_TITLE);
        }

        mLayerHelper.registerUnreadConversationsCountChangedListener(this);

        registerButtonListeners();
    }

    /**
     * similar to onResume() but ensures that fragments are resumed to prevent IllegalStateException
     * see https://developer.android.com/reference/android/support/v4/app/FragmentActivity.html#onResumeFragments()
     */
    @Override
    public void onResumeFragments() {
        super.onResumeFragments();
        bus.register(this);
        //Check config params every time we resume mainactivity, may have changes which result in flow changes on open
        mConfigManager.prefetch();
        checkIfUserShouldUpdatePaymentInfo();
        checkIfNotificationIsEnabled();

        mJobRequestsCount = mBookingManager.getLastUnreadRequestsCount();
        updateClientsButtonUnreadCount();
        mBookingManager.requestProRequestedJobsCount();

        mMessagesButton.setUnreadCount((int) mLayerHelper.getUnreadConversationsCount());

        if (mCurrentPage == null) {
            switchToPage(MainViewPage.AVAILABLE_JOBS);
        }
        handleDeeplinkIfNecessary();

        bus.post(new AnnouncementEvent.ShowAnnouncementForTrigger(Announcement.TriggerContext.MAIN_FLOW_OPEN));
    }

    @Override
    public void onPause() {
        bus.unregister(this);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mLayerHelper.unregisterUnreadConversationsCountChangedListener(this);
        super.onDestroy();
    }

    @Override
    public BookingMapView getBookingMap() {
        if (mBookingMapView == null) {
            mBookingMapView = new BookingMapView(this);
            mBookingMapView.onCreate(null);
        }
        return mBookingMapView;
    }

    private void checkIfUserShouldUpdatePaymentInfo() {
        bus.post(new PaymentEvent.RequestShouldUserUpdatePaymentInfo());
    }

    public void checkIfNotificationIsEnabled() {
        if (NotificationUtils.isNotificationEnabled(this) == NotificationUtils.NOTIFICATION_DISABLED
                && !mNotificationBlockerDialogFragment.isAdded()) {
            FragmentUtils.safeLaunchDialogFragment(mNotificationBlockerDialogFragment, this,
                    NotificationBlockerDialogFragment.FRAGMENT_TAG);
        }
    }

    private void setFullScreen() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    @Subscribe
    public void onReceiveUserShouldUpdatePaymentInfo(PaymentEvent.ReceiveShouldUserUpdatePaymentInfoSuccess event) {
        //check if we need to show the payment bill blocker, we will have either soft and hard blocking (modal and blockingfragment) depending on config params
        if (event.shouldUserUpdatePaymentInfo) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            //Non-blocking modal
            if (fragmentManager.findFragmentByTag(PaymentBillBlockerDialogFragment.FRAGMENT_TAG) == null) //only show if there isn't an instance of the fragment showing already
            {
                PaymentBillBlockerDialogFragment paymentBillBlockerDialogFragment = new PaymentBillBlockerDialogFragment();
                FragmentUtils.safeLaunchDialogFragment(paymentBillBlockerDialogFragment, this, PaymentBillBlockerDialogFragment.FRAGMENT_TAG);
            }
        }
        else {
            showUploadProfilePictureBlockerIfNecessary();
        }
    }

    private void showUploadProfilePictureBlockerIfNecessary() {
        if (mProviderManager.getCachedProfileImageUrl(THUMBNAIL) == null
                && !mUploadProfilePictureBlockerShown) {
            final Bundle arguments = new Bundle();
            arguments.putSerializable(BundleKeys.NAVIGATION_SOURCE, EditPhotoFragment.Source.APP);
            mPageNavigationManager.navigateToPage(getSupportFragmentManager(),
                    MainViewPage.PROFILE_PICTURE, arguments, null, true);
            mUploadProfilePictureBlockerShown = true;
        }
    }

    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        setDeeplinkData(savedInstanceState);
    }

    private void handleDeeplinkIfNecessary() {
        if (!mDeeplinkHandled) {
            mPageNavigationManager.handleNonUriDerivedDeeplinkDataBundle(
                    getSupportFragmentManager(), mDeeplinkData, mDeeplinkSource);
        }
        mDeeplinkHandled = true;
    }

    private void setDeeplinkData(final Bundle savedInstanceState) {
        if (savedInstanceState == null || !(mDeeplinkHandled = savedInstanceState.getBoolean(BundleKeys.DEEPLINK_HANDLED))) {
            final Intent intent = getIntent();
            mDeeplinkData = intent.getBundleExtra(BundleKeys.DEEPLINK_DATA);
            mDeeplinkSource = intent.getStringExtra(BundleKeys.DEEPLINK_SOURCE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        try {
            if (outState == null) {
                outState = new Bundle();
            }
            outState.putBoolean(BundleKeys.DEEPLINK_HANDLED, mDeeplinkHandled);
            outState.putSerializable(BundleKeys.PAGE, mCurrentPage);
            outState.putString(BundleKeys.TAB_TITLE, mTabs.getCurrentlySelectedTab().getTitle());

            super.onSaveInstanceState(outState);
        }
        catch (IllegalArgumentException e) {
            // Non fatal
            Crashlytics.logException(e);
        }
    }

    @Subscribe
    public void onSetNavigationTabVisibility(NavigationEvent.SetNavigationTabVisibility event) {
        setTabVisibility(event.isVisible);
    }

    private void setTabVisibility(boolean isVisible) {
        if (mContentFrame != null) {
            mContentFrame.setAutoHideShowTabs(isVisible);
        }

        if (mTabs != null) {
            mTabs.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        }
    }

    @Subscribe
    public void onSwapFragment(NavigationEvent.SwapFragmentEvent event) {
        bus.post(new HandyEvent.Navigation(event.targetPage.toString().toLowerCase()));
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        setTabVisibility(true);
        mPageNavigationManager.navigateToPage(getSupportFragmentManager(), event.targetPage,
                event.arguments, event.transitionStyle, event.addToBackStack);
        clearOnBackPressedListenerStack();
    }

    @Override
    public void onBackPressed() {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        super.onBackPressed();
    }

    @Subscribe
    public void onShowLoadingOverlay(HandyEvent.SetLoadingOverlayVisibility event) {
        mLoadingOverlayView.setVisibility(event.isVisible ? View.VISIBLE : View.GONE);
    }

    @Subscribe
    public void onLogOutProvider(HandyEvent.LogOutProvider event) {
        logOutProvider();
        Toast.makeText(this, R.string.handy_account_no_longer_active, Toast.LENGTH_SHORT).show();
    }

    @Subscribe
    public void updateSelectedTabButton(NavigationEvent.SelectPage event) {
        if (event.page == null) { return; }
        mCurrentPage = event.page;
        switch (event.page) {
            case AVAILABLE_JOBS: {
                mJobsButton.toggle();
            }
            break;
            case CLIENTS: {
                mClientsButton.toggle();
            }
            break;
            case SEND_RECEIPT_CHECKOUT:
            case SCHEDULED_JOBS: {
                mScheduleButton.toggle();
            }
            break;
            case MESSAGES: {
                mMessagesButton.toggle();
            }
            break;
            case PAYMENTS: {
                mMoreButton.toggle();
            }
            break;
            case ACCOUNT_SETTINGS: {
                mMoreButton.toggle();
            }
            break;
        }
    }

    @Subscribe
    public void onReceiveProRequestedJobsCountSuccess(
            final BookingEvent.ReceiveProRequestedJobsCountSuccess event) {
        mJobRequestsCount = event.getCount();
        updateClientsButtonUnreadCount();
    }

    @Override
    public void onUnreadConversationsCountChanged(final long count) {
        mMessagesButton.setUnreadCount((int) mLayerHelper.getUnreadConversationsCount());
    }

    private void updateClientsButtonUnreadCount() {
        if (mJobRequestsCount != null) {
            mClientsButton.setUnreadCount(mJobRequestsCount);
        }
    }

//Click Listeners

    private void registerButtonListeners() {
        registerBottomNavListeners();
    }

    private void registerBottomNavListeners() {
        mJobsButton = new TabButton(this)
                .init(R.string.tab_claim, R.drawable.ic_menu_search);
        mJobsButton.setId(R.id.tab_nav_available);
        mClientsButton = new TabButton(this).init(R.string.tab_clients,
                R.drawable.ic_menu_clients);
        mClientsButton.setId(R.id.tab_nav_clients);
        mScheduleButton = new TabButton(this)
                .init(R.string.tab_schedule, R.drawable.ic_menu_schedule);
        mScheduleButton.setId(R.id.tab_nav_schedule);
        mMessagesButton = new TabButton(this)
                .init(R.string.tab_messages, R.drawable.ic_menu_messages);
        mMessagesButton.setId(R.id.tab_nav_messages);
        mMoreButton = new TabButton(this)
                .init(R.string.tab_more, R.drawable.ic_menu_more);
        mMoreButton.setId(R.id.tab_nav_item_more);
        mTabs.setTabs(mJobsButton, mScheduleButton, mClientsButton, mMessagesButton, mMoreButton);
        mTabs.selected(mCurrentTabTitle);

        mJobsButton.setOnClickListener(
                new TabOnClickListener(mJobsButton, MainViewPage.AVAILABLE_JOBS));
        mScheduleButton.setOnClickListener(
                new TabOnClickListener(mScheduleButton, MainViewPage.SCHEDULED_JOBS));
        mClientsButton.setOnClickListener(
                new TabOnClickListener(mClientsButton, MainViewPage.CLIENTS));
        mMessagesButton.setOnClickListener(
                new TabOnClickListener(mMessagesButton, MainViewPage.MESSAGES));
        mMoreButton.setOnClickListener(
                new TabOnClickListener(mMoreButton, MainViewPage.MORE_ITEMS));
    }

    private void switchToPage(@NonNull MainViewPage page) {
        switchToPage(page, new Bundle(), TransitionStyle.NATIVE_TO_NATIVE);
    }

    private void switchToPage(@NonNull MainViewPage targetPage, @NonNull Bundle argumentsBundle,
                              @NonNull TransitionStyle overrideTransitionStyle) {
        mPageNavigationManager.navigateToPage(getSupportFragmentManager(), targetPage,
                argumentsBundle, overrideTransitionStyle, false);
    }

    // Fragment swapping and related
    private void clearFragmentBackStack() {
        clearingBackStack = true;
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        supportFragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE); //clears out the whole stack
        clearingBackStack = false;
    }

    // TODO: consider move log out logic somewhere else
    @SuppressWarnings("deprecation")
    private void logOutProvider() {
        //want to remain in the current environment after user is logged out
        EnvironmentModifier.Environment currentEnvironment = mEnvironmentModifier.getEnvironment();
        String currentEnvironmentPrefix = mEnvironmentModifier.getEnvironmentPrefix();

        mPrefsManager.clear();
        clearFragmentBackStack();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        }
        else {
            CookieSyncManager.createInstance(this);
            CookieManager.getInstance().removeAllCookie();
            CookieSyncManager.getInstance().sync();
        }

        if (mLayerHelper.getLayerClient().isAuthenticated()) {
            mLayerHelper.deauthenticate();
        }

        mEnvironmentModifier.setEnvironment(currentEnvironment, currentEnvironmentPrefix, null);

        bus.post(new HandyEvent.UserLoggedOut());
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    // Inner classes
    private class TabOnClickListener implements View.OnClickListener {
        private TabButton mTabButton;
        private MainViewPage mPage;

        TabOnClickListener(@Nullable final TabButton tabButton, final MainViewPage page) {
            mTabButton = tabButton;
            mPage = page;
        }

        @Override
        public void onClick(View view) {
            if (mTabButton != null) {
                mTabButton.toggle();
            }
            if (mPage != mCurrentPage) {
                switchToPage(mPage);
            }
        }
    }
}
