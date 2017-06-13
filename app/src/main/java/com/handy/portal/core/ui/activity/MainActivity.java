package com.handy.portal.core.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;
import com.handy.portal.announcements.AnnouncementEvent;
import com.handy.portal.announcements.model.Announcement;
import com.handy.portal.bookings.BookingEvent;
import com.handy.portal.bookings.manager.BookingManager;
import com.handy.portal.bookings.ui.element.BookingMapView;
import com.handy.portal.core.EnvironmentModifier;
import com.handy.portal.core.MainContentFragmentHolder;
import com.handy.portal.core.constant.BundleKeys;
import com.handy.portal.core.constant.MainViewPage;
import com.handy.portal.core.constant.RequestCode;
import com.handy.portal.core.constant.TransitionStyle;
import com.handy.portal.core.event.HandyEvent;
import com.handy.portal.core.event.NavigationEvent;
import com.handy.portal.core.event.NotificationEvent;
import com.handy.portal.core.event.ProfileEvent;
import com.handy.portal.core.manager.AppseeManager;
import com.handy.portal.core.manager.PageNavigationManager;
import com.handy.portal.core.manager.ProviderManager;
import com.handy.portal.core.model.ConfigurationResponse;
import com.handy.portal.core.model.ProviderProfile;
import com.handy.portal.core.ui.element.bookings.BookingMapProvider;
import com.handy.portal.core.ui.fragment.EditPhotoFragment;
import com.handy.portal.library.ui.fragment.dialog.TransientOverlayDialogFragment;
import com.handy.portal.library.ui.layout.TabbedLayout;
import com.handy.portal.library.ui.widget.TabButton;
import com.handy.portal.library.ui.widget.TabButtonGroup;
import com.handy.portal.library.util.FragmentUtils;
import com.handy.portal.library.util.ShareUtils;
import com.handy.portal.library.util.SystemUtils;
import com.handy.portal.library.util.Utils;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.ProfileLog;
import com.handy.portal.logger.handylogger.model.SideMenuLog;
import com.handy.portal.notification.NotificationUtils;
import com.handy.portal.notification.ui.fragment.NotificationBlockerDialogFragment;
import com.handy.portal.payments.PaymentEvent;
import com.handy.portal.payments.ui.fragment.dialog.PaymentBillBlockerDialogFragment;
import com.handybook.shared.layer.LayerHelper;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.handy.portal.core.model.ProviderPersonalInfo.ProfileImage.Type.THUMBNAIL;

//TODO: should move some of this logic out of here
public class MainActivity extends BaseActivity
        implements BookingMapProvider, LayerHelper.UnreadConversationsCountChangedListener, MainContentFragmentHolder {
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
    private TabButton mAlertsButton;
    private TabButton mMoreButton;

    @BindView(R.id.loading_overlay)
    View mLoadingOverlayView;
    @BindView(R.id.nav_link_payments)
    RadioButton mNavLinkPayments;
    @BindView(R.id.nav_link_ratings_and_feedback)
    RadioButton mNavLinkRatingsAndFeedback;
    @BindView(R.id.nav_link_refer_a_friend)
    RadioButton mNavLinkReferAFriend;
    @BindView(R.id.nav_link_account_settings)
    RadioButton mNavAccountSettings;
    @BindView(R.id.nav_link_video_library)
    RadioButton mNavLinkVideoLibrary;
    @BindView(R.id.nav_link_help)
    RadioButton mNavLinkHelp;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.navigation_drawer)
    ViewGroup mNavigationDrawer;
    @BindView(R.id.nav_tray_links)
    RadioGroup mNavTrayLinks;
    @BindView(R.id.navigation_header_pro_name)
    TextView mNavigationHeaderProName;
    @BindView(R.id.content_frame)
    TabbedLayout mContentFrame;
    @BindView(R.id.provider_image)
    ImageView mProImage;
    @BindView(R.id.navigation_header_cta_button)
    Button mNavigationHeaderCtaButton;

    private BookingMapView mBookingMapView;

    private NotificationBlockerDialogFragment mNotificationBlockerDialogFragment
            = new NotificationBlockerDialogFragment();

    private ActionBarDrawerToggle mActionBarDrawerToggle;
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

        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(final View drawerView) {
                super.onDrawerOpened(drawerView);
                bus.post(new LogEvent.AddLogEvent(new SideMenuLog.Opened()));
                setDrawerActive(true);
            }

            @Override
            public void onDrawerClosed(final View drawerView) {
                super.onDrawerClosed(drawerView);
                bus.post(new LogEvent.AddLogEvent(new SideMenuLog.Closed()));
                setDrawerActive(false);
            }
        };

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

        if (mClientsButton != null && mClientsButton.getVisibility() == View.VISIBLE) {
            mJobRequestsCount = mBookingManager.getLastUnreadRequestsCount();
            updateClientsButtonUnreadCount();
            mBookingManager.requestProRequestedJobsCount();
        }
        if (mAlertsButton != null && mAlertsButton.getVisibility() == View.VISIBLE) {
            bus.post(new NotificationEvent.RequestUnreadCount());
        }
        if (mCurrentPage == null) {
            switchToPage(MainViewPage.AVAILABLE_JOBS);
        }
        handleDeeplinkIfNecessary();

        handleDrawerOpen();
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);

        AppseeManager.markViewsAsSensitive(mNavigationHeaderProName, mProImage);

        initProName();
        initProImage(null);
        initNavigationHeaderCtaButton();

        bus.post(new AnnouncementEvent.ShowAnnouncementForTrigger(Announcement.TriggerContext.MAIN_FLOW_OPEN));
    }

    @Override
    public void onPause() {
        mDrawerLayout.removeDrawerListener(mActionBarDrawerToggle);
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
        final ConfigurationResponse configuration = mConfigManager.getConfigurationResponse();
        if (configuration != null
                && configuration.isProfilePictureUploadEnabled()
                && mProviderManager.getCachedProfileImageUrl(THUMBNAIL) == null
                && !mUploadProfilePictureBlockerShown) {
            final Bundle arguments = new Bundle();
            arguments.putSerializable(BundleKeys.NAVIGATION_SOURCE, EditPhotoFragment.Source.APP);
            bus.post(new NavigationEvent.NavigateToPage(MainViewPage.PROFILE_PICTURE, arguments, true));
            mUploadProfilePictureBlockerShown = true;
        }
    }

    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        setDeeplinkData(savedInstanceState);
    }

    private void initProName() {
        final ProviderProfile providerProfile = mProviderManager.getCachedProviderProfile();
        if (providerProfile != null && providerProfile.getProviderPersonalInfo() != null) {
            mNavigationHeaderProName.setText(providerProfile.getProviderPersonalInfo().getFullName());
        }
    }

    @Subscribe
    public void initProImage(final ProfileEvent.ProfilePhotoUpdated event) {
        mProImage.setVisibility(View.VISIBLE);
        final String profilePhotoUrl = mProviderManager.getCachedProfileImageUrl(THUMBNAIL);
        Picasso.with(this)
                .load(profilePhotoUrl)
                .placeholder(R.drawable.img_pro_placeholder)
                .noFade()
                .into(mProImage);
    }

    private void initNavigationHeaderCtaButton() {
        if (shouldEnableProfileShareButton()) {
            mNavigationHeaderCtaButton.setText(R.string.share_profile);
        }
        else {
            mNavigationHeaderCtaButton.setText(R.string.edit_profile);
        }
    }

    /**
     * For the time being, since the "more" menu is still in the form of a drawer menu,
     * it is possible that coming from a deeplink, we want the drawer to be opened. Detect and
     * handle that here.
     */
    private void handleDrawerOpen() {
        if (mDeeplinkData != null && mDeeplinkData.getBoolean(BundleKeys.DRAWER_OPEN)) {
            mDrawerLayout.openDrawer(mNavigationDrawer);
        }
    }

    private void handleDeeplinkIfNecessary() {
        if (!mDeeplinkHandled) {
            mPageNavigationManager.handleNonUriDerivedDeeplinkDataBundle(mDeeplinkData, mDeeplinkSource);
        }
        mDeeplinkHandled = true;
    }

    @Subscribe
    public void onReceiveUnreadCountSuccess(NotificationEvent.ReceiveUnreadCountSuccess event) {
        if (mAlertsButton != null) {
            mAlertsButton.setUnreadCount(event.getUnreadCount());
        }
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

    @OnClick(R.id.navigation_header_cta_button)
    public void onNavigationHeaderCtaClicked() {
        if (shouldEnableProfileShareButton()) {
            bus.post(new LogEvent.AddLogEvent(new ProfileLog.ProfileShareClicked()));

            final Intent dummyIntent = new Intent();
            dummyIntent.setAction(Intent.ACTION_SEND);
            dummyIntent.setType("text/plain");

            final Intent activityPickerIntent = new Intent();
            activityPickerIntent.setAction(Intent.ACTION_PICK_ACTIVITY);
            activityPickerIntent.putExtra(Intent.EXTRA_TITLE, getString(R.string.share_with));
            activityPickerIntent.putExtra(Intent.EXTRA_INTENT, dummyIntent);
            startActivityForResult(activityPickerIntent, RequestCode.PICK_ACTIVITY);
        }
        else {
            bus.post(new NavigationEvent.NavigateToPage(MainViewPage.PROFILE_UPDATE, true));
        }
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
        if (requestCode == RequestCode.PICK_ACTIVITY
                && resultCode == Activity.RESULT_OK
                && intent != null) {
            final ProviderProfile providerProfile = mProviderManager.getCachedProviderProfile();

            final String channel = ShareUtils.getChannelFromIntent(this, intent);
            if (channel.equalsIgnoreCase(ShareUtils.CHANNEL_TWITTER)) {
                intent.putExtra(Intent.EXTRA_TEXT, getString(
                        R.string.profile_share_twitter_text_formatted,
                        providerProfile.getReferralInfo().getProfileUrl())
                );
            }
            else {
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.book_my_service));
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.profile_share_text_formatted,
                        providerProfile.getProviderPersonalInfo().getFirstName(),
                        providerProfile.getReferralInfo().getProfileUrl()));
            }
            Utils.safeLaunchIntent(intent, this);

            final String appName = SystemUtils.getAppNameFromIntent(this, intent);
            bus.post(new LogEvent.AddLogEvent(new ProfileLog.ProfileShareSubmitted(
                    appName, channel)));
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    private boolean shouldEnableProfileShareButton() {
        final ConfigurationResponse configuration = mConfigManager.getConfigurationResponse();
        final ProviderProfile providerProfile = mProviderManager.getCachedProviderProfile();
        return configuration != null
                && configuration.isProfileShareEnabled()
                && providerProfile != null
                && providerProfile.getReferralInfo() != null
                && !TextUtils.isEmpty(providerProfile.getReferralInfo().getProfileUrl());
    }

    @OnClick(R.id.provider_image)
    public void onProfileImageClicked() {
        bus.post(new NavigationEvent.NavigateToPage(MainViewPage.PROFILE_UPDATE, true));
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

    private void setDrawerActive(boolean isActive) {
        if (mDrawerLayout != null) {
            mDrawerLayout.setDrawerLockMode(isActive ? DrawerLayout.LOCK_MODE_UNLOCKED : DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

    @Subscribe
    public void onSwapFragment(NavigationEvent.SwapFragmentEvent event) {
        bus.post(new HandyEvent.Navigation(event.targetPage.toString().toLowerCase()));
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        setTabVisibility(true);
        setDrawerActive(false);
        swapFragment(event);
        clearOnBackPressedListenerStack();
        mCurrentPage = event.targetPage;
    }

    @Override
    public void onBackPressed() {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        if (mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
            mDrawerLayout.closeDrawers();
        }
        else {
            super.onBackPressed();
        }
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
        switch (event.page) {
            case AVAILABLE_JOBS: {
                mJobsButton.toggle();
                mNavTrayLinks.clearCheck();
            }
            break;
            case CLIENTS: {
                mClientsButton.toggle();
                mNavTrayLinks.clearCheck();
            }
            break;
            case SEND_RECEIPT_CHECKOUT:
            case SCHEDULED_JOBS: {
                mScheduleButton.toggle();
                mNavTrayLinks.clearCheck();
            }
            break;
            case NOTIFICATIONS: {
                mAlertsButton.toggle();
                mNavTrayLinks.clearCheck();
            }
            break;
            case PAYMENTS: {
                mMoreButton.toggle();
                mNavLinkPayments.toggle();
            }
            break;
            case YOUTUBE_PLAYER:
            case DASHBOARD: {
                mMoreButton.toggle();
                mNavLinkRatingsAndFeedback.toggle();
            }
            break;
            case REFER_A_FRIEND: {
                mMoreButton.toggle();
                mNavLinkReferAFriend.toggle();
            }
            break;
            case ACCOUNT_SETTINGS: {
                mMoreButton.toggle();
                mNavAccountSettings.toggle();
            }
            break;
            case DASHBOARD_VIDEO_LIBRARY: {
                mMoreButton.toggle();
                mNavLinkVideoLibrary.toggle();
            }
            case PROFILE_UPDATE: {
                mMoreButton.toggle();
                mNavAccountSettings.toggle();
            }
            break;
            case PROFILE_PICTURE: {
                mMoreButton.toggle();
                mNavAccountSettings.toggle();
            }
            break;
            case HELP_WEBVIEW: {
                mMoreButton.toggle();
                mNavLinkHelp.toggle();
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
        updateClientsButtonUnreadCount();
    }

    private void updateClientsButtonUnreadCount() {
        if (mClientsButton != null && mJobRequestsCount != null) {
            int clientsButtonUnreadCount = mJobRequestsCount;
            clientsButtonUnreadCount += mLayerHelper.getUnreadConversationsCount();
            mClientsButton.setUnreadCount(clientsButtonUnreadCount);
        }
    }

//Click Listeners

    private void registerButtonListeners() {
        registerBottomNavListeners();
        registerNavDrawerListeners();
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
        mAlertsButton = new TabButton(this)
                .init(R.string.tab_alerts, R.drawable.ic_menu_alerts);
        mAlertsButton.setId(R.id.tab_nav_alert);
        mMoreButton = new TabButton(this)
                .init(R.string.tab_more, R.drawable.ic_menu_more);
        mMoreButton.setId(R.id.tab_nav_item_more);
        mTabs.setTabs(mJobsButton, mScheduleButton, mClientsButton, mAlertsButton, mMoreButton);
        mTabs.selected(mCurrentTabTitle);

        mJobsButton.setOnClickListener(
                new TabOnClickListener(mJobsButton, MainViewPage.AVAILABLE_JOBS));
        mScheduleButton.setOnClickListener(
                new TabOnClickListener(mScheduleButton, MainViewPage.SCHEDULED_JOBS));
        mClientsButton.setOnClickListener(
                new TabOnClickListener(mClientsButton, MainViewPage.CLIENTS));
        mAlertsButton.setOnClickListener(
                new TabOnClickListener(mAlertsButton, MainViewPage.NOTIFICATIONS));

        ConfigurationResponse config = mConfigManager.getConfigurationResponse();
        if (config != null && config.isMoreFullTabEnabled()) {
            mMoreButton.setOnClickListener(new TabOnClickListener(mMoreButton, MainViewPage.MORE_ITEMS));
        }
        else {
            mMoreButton.setOnClickListener(new MoreButtonOnClickListener());
        }
    }

    private void registerNavDrawerListeners() {
        mNavLinkPayments.setOnClickListener(new NavDrawerOnClickListener(MainViewPage.PAYMENTS, null));
        mNavLinkRatingsAndFeedback.setOnClickListener(new NavDrawerOnClickListener(MainViewPage.DASHBOARD, null));
        mNavLinkReferAFriend.setOnClickListener(new NavDrawerOnClickListener(MainViewPage.REFER_A_FRIEND, null));
        mNavAccountSettings.setOnClickListener(new NavDrawerOnClickListener(MainViewPage.ACCOUNT_SETTINGS, null));
        mNavLinkVideoLibrary.setOnClickListener(new NavDrawerOnClickListener(MainViewPage.DASHBOARD_VIDEO_LIBRARY, null));
        mNavLinkHelp.setOnClickListener(new NavDrawerOnClickListener(MainViewPage.HELP_WEBVIEW, null));
    }

    private void switchToPage(@NonNull MainViewPage page) {
        switchToPage(page, new Bundle(), TransitionStyle.NATIVE_TO_NATIVE);
    }

    private void switchToPage(@NonNull MainViewPage targetPage, @NonNull Bundle argumentsBundle,
                              @NonNull TransitionStyle overrideTransitionStyle) {
        bus.post(new NavigationEvent.NavigateToPage(targetPage, argumentsBundle, overrideTransitionStyle, false));
    }

// Fragment swapping and related

    private void clearFragmentBackStack() {
        clearingBackStack = true;
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        supportFragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE); //clears out the whole stack
        clearingBackStack = false;
    }

    private void swapFragment(NavigationEvent.SwapFragmentEvent swapFragmentEvent) {
        Fragment newFragment = null;
        if (swapFragmentEvent.targetPage != null) {
            try {
                newFragment = (Fragment) swapFragmentEvent.targetPage.getClassType().newInstance();
            }
            catch (Exception e) {
                Crashlytics.logException(new RuntimeException("Error instantiating fragment class", e));
                return;
            }
        }
        swapFragment(newFragment,
                swapFragmentEvent.getReturnFragment(),
                swapFragmentEvent.getActivityRequestCode(),
                swapFragmentEvent.arguments,
                swapFragmentEvent.transitionStyle,
                swapFragmentEvent.addToBackStack);
    }

    private void swapFragment(@Nullable Fragment newFragment,
                              @Nullable Fragment targetFragment,
                              int activityRequestCode,
                              @Nullable Bundle arguments,
                              @Nullable TransitionStyle transitionStyle,
                              boolean addToBackStack) {
        if (!addToBackStack) {
            clearFragmentBackStack();
        }

        //replace the existing fragment with the new fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (newFragment != null && arguments != null) {
            newFragment.setArguments(arguments);
            if (targetFragment != null) {
                newFragment.setTargetFragment(targetFragment, activityRequestCode);
            }
        }

        //Animate the transition, animations must come before the .replace call
        if (transitionStyle != null) {
            transaction.setCustomAnimations(
                    transitionStyle.getIncomingAnimId(),
                    transitionStyle.getOutgoingAnimId(),
                    transitionStyle.getPopIncomingAnimId(),
                    transitionStyle.getPopOutgoingAnimId()
            );

            //Runs async, covers the transition
            if (transitionStyle.shouldShowOverlay()) {
                TransientOverlayDialogFragment overlayDialogFragment = TransientOverlayDialogFragment
                        .newInstance(R.anim.overlay_fade_in_then_out, R.drawable.ic_success_circle, transitionStyle.getOverlayStringId());
                overlayDialogFragment.show(getSupportFragmentManager(), "overlay dialog fragment");
            }
        }

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.main_container, newFragment);

        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        else {
            transaction.disallowAddToBackStack();
        }

        // Commit the transaction
        transaction.commit();
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

    //todo will refactor navigation in this class later; this is just being used to support the interface that the select payment method screen expects
    @Override
    public void replaceMainContentFragment(@NonNull final Fragment replacementFragment, final boolean addToBackStack) {
        swapFragment(replacementFragment, null, 0, replacementFragment.getArguments(), TransitionStyle.NATIVE_TO_NATIVE, addToBackStack);
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


    private class NavDrawerOnClickListener extends TabOnClickListener {
        private MainViewPage mPage;
        private TransitionStyle mTransitionStyle;

        NavDrawerOnClickListener(
                final MainViewPage mPage,
                final TransitionStyle transitionStyleOverride
        ) {
            super(null, mPage);
            this.mPage = mPage;
            mTransitionStyle = transitionStyleOverride;
        }

        @Override
        public void onClick(View view) {
            bus.post(new LogEvent.AddLogEvent(new SideMenuLog.ItemSelected(mPage.name().toLowerCase())));
            mMoreButton.toggle();
            if (mTransitionStyle != null) {
                switchToPage(mPage, new Bundle(), mTransitionStyle);
            }
            else {
                switchToPage(mPage);
            }

            mDrawerLayout.closeDrawers();
        }
    }


    private class MoreButtonOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            mDrawerLayout.openDrawer(mNavigationDrawer);
        }
    }
}
