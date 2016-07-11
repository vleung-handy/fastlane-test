package com.handy.portal.ui.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.BuildConfig;
import com.handy.portal.R;
import com.handy.portal.bookings.BookingEvent;
import com.handy.portal.bookings.model.BookingsWrapper;
import com.handy.portal.bookings.ui.fragment.ProRequestedJobsFragment;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewPage;
import com.handy.portal.constant.TransitionStyle;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.NavigationEvent;
import com.handy.portal.event.NotificationEvent;
import com.handy.portal.library.ui.fragment.InjectedFragment;
import com.handy.portal.library.ui.fragment.dialog.TransientOverlayDialogFragment;
import com.handy.portal.library.ui.layout.TabbedLayout;
import com.handy.portal.library.ui.widget.TabButton;
import com.handy.portal.library.ui.widget.TabButtonGroup;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.DeeplinkLog;
import com.handy.portal.logger.handylogger.model.SideMenuLog;
import com.handy.portal.manager.ConfigManager;
import com.handy.portal.manager.PrefsManager;
import com.handy.portal.model.ConfigurationResponse;
import com.handy.portal.ui.activity.BaseActivity;
import com.handy.portal.ui.activity.LoginActivity;
import com.handy.portal.util.DeeplinkMapper;

import org.greenrobot.eventbus.Subscribe;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivityFragment extends InjectedFragment
{
    @Inject
    PrefsManager mPrefsManager;
    @Inject
    ConfigManager mConfigManager;
    /////////////Bad useless injection that breaks if not in?

    @BindView(R.id.tabs)
    TabButtonGroup mTabs;
    private TabButton mJobsButton;
    private TabButton mRequestsButton;
    private TabButton mScheduleButton;
    private TabButton mAlertsButton;
    private TabButton mButtonMore;

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
    RelativeLayout mNavigationDrawer;
    @BindView(R.id.nav_tray_links)
    RadioGroup mNavTrayLinks;
    @BindView(R.id.navigation_header)
    TextView mNavigationHeader;
    @BindView(R.id.content_frame)
    TabbedLayout mContentFrame;
    @BindView(R.id.build_version_text)
    TextView mBuildVersionText;

    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private MainViewPage currentPage = null;

    //Are we currently clearing out the backstack?
    // Other fragments will want to know to avoid re-doing things on their onCreateView
    public static boolean clearingBackStack = false;

    private Bundle mDeeplinkData;
    private boolean mDeeplinkHandled;
    private String mDeeplinkSource;

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setDeeplinkData(savedInstanceState);

        mActionBarDrawerToggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        {
            @Override
            public void onDrawerOpened(final View drawerView)
            {
                super.onDrawerOpened(drawerView);
                bus.post(new LogEvent.AddLogEvent(new SideMenuLog.Opened()));
                setDrawerActive(true);
            }

            @Override
            public void onDrawerClosed(final View drawerView)
            {
                super.onDrawerClosed(drawerView);
                bus.post(new LogEvent.AddLogEvent(new SideMenuLog.Closed()));
                setDrawerActive(false);
            }
        };
    }

    @Override
    public void onViewStateRestored(final Bundle savedInstanceState)
    {
        super.onViewStateRestored(savedInstanceState);
        setDeeplinkData(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_main, container);
        ButterKnife.bind(this, view);
        registerButtonListeners();
        mBuildVersionText.setText(BuildConfig.VERSION_NAME);
        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        initProRequestedJobs();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        bus.register(this);

        bus.post(new NotificationEvent.RequestUnreadCount());
        bus.post(new HandyEvent.UpdateMainActivityFragmentActive(true));
        if (currentPage == null)
        {
            switchToPage(MainViewPage.AVAILABLE_JOBS, false);
        }
        handleDeeplink();
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
    }

    @Subscribe
    public void onReceiveUnreadCountSuccess(NotificationEvent.ReceiveUnreadCountSuccess event)
    {
        mAlertsButton.setUnreadCount(event.getUnreadCount());
    }

    private void setDeeplinkData(final Bundle savedInstanceState)
    {
        if (savedInstanceState == null || !(mDeeplinkHandled = savedInstanceState.getBoolean(BundleKeys.DEEPLINK_HANDLED)))
        {
            final Intent intent = getActivity().getIntent();
            mDeeplinkData = intent.getBundleExtra(BundleKeys.DEEPLINK_DATA);
            mDeeplinkSource = intent.getStringExtra(BundleKeys.DEEPLINK_SOURCE);
        }
    }

    private void handleDeeplink()
    {
        if (mDeeplinkData != null && !mDeeplinkHandled)
        {
            final String deeplink = mDeeplinkData.getString(BundleKeys.DEEPLINK);
            if (deeplink != null)
            {
                final MainViewPage targetPage = DeeplinkMapper.getPageForDeeplink(deeplink);
                if (targetPage != null)
                {
                    bus.post(new LogEvent.AddLogEvent(new DeeplinkLog.Processed(
                            mDeeplinkSource,
                            mDeeplinkData
                    )));
                    switchToPage(targetPage, mDeeplinkData, false);
                }
                else
                {
                    // Unable to find a matching page for deeplink, so ignore it.
                    bus.post(new LogEvent.AddLogEvent(new DeeplinkLog.Ignored(
                            mDeeplinkSource,
                            DeeplinkLog.Ignored.Reason.UNRECOGNIZED,
                            mDeeplinkData
                    )));
                }
            }
        }
        mDeeplinkHandled = true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        try
        {
            if (outState == null)
            {
                outState = new Bundle();
            }
            outState.putBoolean(BundleKeys.DEEPLINK_HANDLED, mDeeplinkHandled);
            super.onSaveInstanceState(outState);
        }
        catch (IllegalArgumentException e)
        {
            // Non fatal
            Crashlytics.logException(e);
        }
    }

    @Override
    public void onPause()
    {
        bus.post(new HandyEvent.UpdateMainActivityFragmentActive(false));
        mDrawerLayout.removeDrawerListener(mActionBarDrawerToggle);
        bus.unregister(this);
        super.onPause();
    }

//Event Listeners

    @Subscribe
    public void onSetNavigationTabVisibility(NavigationEvent.SetNavigationTabVisibility event)
    {
        setTabVisibility(event.isVisible);
    }

    private void setTabVisibility(boolean isVisible)
    {
        if (mContentFrame != null)
        {
            mContentFrame.setAutoHideShowTabs(isVisible);
        }

        if (mTabs != null)
        {
            mTabs.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        }
    }

    @Subscribe
    public void onSetNavigationDrawerActive(NavigationEvent.SetNavigationDrawerActive event)
    {
        setDrawerActive(event.isActive);
    }

    private void setDrawerActive(boolean isActive)
    {
        if (mDrawerLayout != null)
        {
            mDrawerLayout.setDrawerLockMode(isActive ? DrawerLayout.LOCK_MODE_UNLOCKED : DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

    @Subscribe
    public void onSwapFragment(NavigationEvent.SwapFragmentEvent event)
    {
        trackSwitchToPage(event.targetPage);
        setTabVisibility(true);
        setDrawerActive(false);
        swapFragment(event);
        ((BaseActivity) getActivity()).clearOnBackPressedListenerStack();
        currentPage = event.targetPage;
    }

    @Subscribe
    public void onShowLoadingOverlay(HandyEvent.SetLoadingOverlayVisibility event)
    {
        mLoadingOverlayView.setVisibility(event.isVisible ? View.VISIBLE : View.GONE);
    }

    @Subscribe
    public void onReceiveProviderInfoSuccess(HandyEvent.ReceiveProviderInfoSuccess event)
    {
        mNavigationHeader.setText(event.provider.getFullName());
    }

    @Subscribe
    public void onLogOutProvider(HandyEvent.LogOutProvider event)
    {
        logOutProvider();
        showToast(R.string.handy_account_no_longer_active);
    }

    @Subscribe
    public void updateSelectedTabButton(NavigationEvent.SelectPage event)
    {
        if (event.page == null) { return; }
        switch (event.page)
        {
            case AVAILABLE_JOBS:
            case BLOCK_PRO_WEBVIEW:
            {
                mJobsButton.toggle();
                mNavTrayLinks.clearCheck();
            }
            break;
            case REQUESTED_JOBS:
            {
                mRequestsButton.toggle();
                mNavTrayLinks.clearCheck();
            }
            break;
            case SEND_RECEIPT_CHECKOUT:
            case SCHEDULED_JOBS:
            {
                mScheduleButton.toggle();
                mNavTrayLinks.clearCheck();
            }
            break;
            case NOTIFICATIONS:
            {
                mAlertsButton.toggle();
                mNavTrayLinks.clearCheck();
            }
            break;
            case PAYMENTS:
            {
                mButtonMore.toggle();
                mNavLinkPayments.toggle();
            }
            break;
            case YOUTUBE_PLAYER:
            case DASHBOARD:
            {
                mButtonMore.toggle();
                mNavLinkRatingsAndFeedback.toggle();
            }
            break;
            case REFER_A_FRIEND:
            {
                mButtonMore.toggle();
                mNavLinkReferAFriend.toggle();
            }
            break;
            case ACCOUNT_SETTINGS:
            {
                mButtonMore.toggle();
                mNavAccountSettings.toggle();
            }
            break;
            case DASHBOARD_VIDEO_LIBRARY:
            {
                mButtonMore.toggle();
                mNavLinkVideoLibrary.toggle();
            }
            break;
            case HELP_WEBVIEW:
            {
                mButtonMore.toggle();
                mNavLinkHelp.toggle();
            }
            break;
        }
    }

    @Subscribe
    public void onReceiveProRequestedJobsSuccess(BookingEvent.ReceiveProRequestedJobsSuccess event)
    {
        List<BookingsWrapper> proRequestedJobsList = event.getProRequestedJobs();
        if (mRequestsButton != null && proRequestedJobsList != null)
        {
            //Show and update count
            int countOfRequestedJobs = 0;
            for (BookingsWrapper wrapper : event.getProRequestedJobs())
            {
                countOfRequestedJobs += wrapper.getBookings().size();
            }

            //If no unclaimed jobs don't show icon and count
            if (countOfRequestedJobs > 0)
            {
                mRequestsButton.setUnreadCount(countOfRequestedJobs);
            }
        }
    }

//Click Listeners

    private void registerButtonListeners()
    {
        registerBottomNavListeners();
        registerNavDrawerListeners();
    }

    private void registerBottomNavListeners()
    {
        mJobsButton = new TabButton(getContext())
                .init(R.string.tab_claim, R.drawable.ic_menu_search);
        mRequestsButton = new TabButton(getContext()).init(R.string.tab_requests,
                R.drawable.ic_menu_requests);
        mRequestsButton.setId(R.id.tab_nav_pro_requested_jobs);
        mScheduleButton = new TabButton(getContext())
                .init(R.string.tab_schedule, R.drawable.ic_menu_schedule);
        mAlertsButton = new TabButton(getContext())
                .init(R.string.tab_alerts, R.drawable.ic_menu_alerts);
        mButtonMore = new TabButton(getContext())
                .init(R.string.tab_more, R.drawable.ic_menu_more);
        mButtonMore.setId(R.id.tab_nav_item_more);
        mTabs.setTabs(mJobsButton, mRequestsButton, mScheduleButton, mAlertsButton, mButtonMore);

        mJobsButton.setOnClickListener(
                new TabOnClickListener(mJobsButton, MainViewPage.AVAILABLE_JOBS));
        mRequestsButton.setOnClickListener(new TabOnClickListener(mRequestsButton, MainViewPage.REQUESTED_JOBS));
        mScheduleButton.setOnClickListener(
                new TabOnClickListener(mScheduleButton, MainViewPage.SCHEDULED_JOBS));
        mButtonMore.setOnClickListener(new MoreButtonOnClickListener());

        if (getConfigurationResponse() != null && getConfigurationResponse().shouldShowNotificationMenuButton())
        {
            mAlertsButton.setOnClickListener(
                    new TabOnClickListener(mAlertsButton, MainViewPage.NOTIFICATIONS));
            mAlertsButton.setVisibility(View.VISIBLE);
        }
        else
        {
            mAlertsButton.setVisibility(View.GONE);
        }
    }

    private void registerNavDrawerListeners()
    {
        mNavLinkPayments.setOnClickListener(new NavDrawerOnClickListener(MainViewPage.PAYMENTS, null));
        mNavLinkRatingsAndFeedback.setOnClickListener(new NavDrawerOnClickListener(MainViewPage.DASHBOARD, null));
        mNavLinkReferAFriend.setOnClickListener(new NavDrawerOnClickListener(MainViewPage.REFER_A_FRIEND, null));
        mNavAccountSettings.setOnClickListener(new NavDrawerOnClickListener(MainViewPage.ACCOUNT_SETTINGS, null));
        mNavLinkVideoLibrary.setOnClickListener(new NavDrawerOnClickListener(MainViewPage.DASHBOARD_VIDEO_LIBRARY, null));
        mNavLinkHelp.setOnClickListener(new NavDrawerOnClickListener(MainViewPage.HELP_WEBVIEW, null));
    }

    private class TabOnClickListener implements View.OnClickListener
    {
        private TabButton mTabButton;
        private MainViewPage mPage;

        TabOnClickListener(@Nullable final TabButton tabButton, final MainViewPage page)
        {
            mTabButton = tabButton;
            mPage = page;
        }

        @Override
        public void onClick(View view)
        {
            if (mTabButton != null)
            {
                mTabButton.toggle();
            }
            if (mPage != currentPage)
            {
                switchToPage(mPage, true);
            }
        }
    }


    private class NavDrawerOnClickListener extends TabOnClickListener
    {
        private MainViewPage mPage;
        private TransitionStyle mTransitionStyle;

        NavDrawerOnClickListener(
                final MainViewPage mPage,
                final TransitionStyle transitionStyleOverride
        )
        {
            super(null, mPage);
            this.mPage = mPage;
            mTransitionStyle = transitionStyleOverride;
        }

        @Override
        public void onClick(View view)
        {
            bus.post(new LogEvent.AddLogEvent(new SideMenuLog.ItemSelected(mPage.name().toLowerCase())));
            mButtonMore.toggle();
            if (mTransitionStyle != null)
            {
                switchToPage(mPage, new Bundle(), mTransitionStyle, false);
            }
            else
            {
                switchToPage(mPage, true);
            }

            mDrawerLayout.closeDrawers();
        }
    }


    private class MoreButtonOnClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View view)
        {
            mDrawerLayout.openDrawer(mNavigationDrawer);
        }
    }

    private void switchToPage(@NonNull MainViewPage page, boolean userTriggered)
    {
        switchToPage(page, new Bundle(), TransitionStyle.NATIVE_TO_NATIVE, userTriggered);
    }

    private void switchToPage(@NonNull MainViewPage targetPage, @NonNull Bundle argumentsBundle, boolean userTriggered)
    {
        switchToPage(targetPage, argumentsBundle, TransitionStyle.NATIVE_TO_NATIVE, userTriggered);
    }

    private void switchToPage(@NonNull MainViewPage targetPage, @NonNull Bundle argumentsBundle,
                              @NonNull TransitionStyle overrideTransitionStyle, boolean userTriggered)
    {
        bus.post(new NavigationEvent.NavigateToPage(targetPage, argumentsBundle, overrideTransitionStyle, false));
    }

///Fragment swapping and related

    private void clearFragmentBackStack()
    {
        clearingBackStack = true;
        FragmentManager supportFragmentManager = getActivity().getSupportFragmentManager();
        supportFragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE); //clears out the whole stack
        clearingBackStack = false;
    }

    //analytics event
    private void trackSwitchToPage(MainViewPage targetPage)
    {
        bus.post(new HandyEvent.Navigation(targetPage.toString().toLowerCase()));
    }

    private void swapFragment(NavigationEvent.SwapFragmentEvent swapFragmentEvent)
    {
        if (!swapFragmentEvent.addToBackStack)
        {
            clearFragmentBackStack();
        }

        //replace the existing fragment with the new fragment
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

        Fragment newFragment = null;
        if (swapFragmentEvent.targetPage != null)
        {
            try
            {
                newFragment = (Fragment) swapFragmentEvent.targetPage.getClassType().newInstance();
            }
            catch (Exception e)
            {
                Crashlytics.logException(new RuntimeException("Error instantiating fragment class", e));
                return;
            }
        }

        if (newFragment != null && swapFragmentEvent.arguments != null)
        {
            newFragment.setArguments(swapFragmentEvent.arguments);
        }

        //Animate the transition, animations must come before the .replace call
        if (swapFragmentEvent.transitionStyle != null)
        {
            transaction.setCustomAnimations(
                    swapFragmentEvent.transitionStyle.getIncomingAnimId(),
                    swapFragmentEvent.transitionStyle.getOutgoingAnimId(),
                    swapFragmentEvent.transitionStyle.getPopIncomingAnimId(),
                    swapFragmentEvent.transitionStyle.getPopOutgoingAnimId()
            );

            //Runs async, covers the transition
            if (swapFragmentEvent.transitionStyle.shouldShowOverlay())
            {
                TransientOverlayDialogFragment overlayDialogFragment = TransientOverlayDialogFragment
                        .newInstance(R.anim.overlay_fade_in_then_out, R.drawable.ic_success_circle, swapFragmentEvent.transitionStyle.getOverlayStringId());
                overlayDialogFragment.show(getFragmentManager(), "overlay dialog fragment");
            }
        }

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.main_container, newFragment);

        if (swapFragmentEvent.addToBackStack)
        {
            transaction.addToBackStack(null);
        }
        else
        {
            transaction.disallowAddToBackStack();
        }

        // Commit the transaction
        transaction.commit();
    }

    private ConfigurationResponse getConfigurationResponse()
    {
        return mConfigManager.getConfigurationResponse();
    }

    @SuppressWarnings("deprecation")
    private void logOutProvider()
    {
        mPrefsManager.clear();
        clearFragmentBackStack();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        }
        else
        {
            CookieSyncManager.createInstance(getActivity());
            CookieManager.getInstance().removeAllCookie();
            CookieSyncManager.getInstance().sync();
        }
        startActivity(new Intent(getActivity(), LoginActivity.class));
        getActivity().finish();
    }

    private void initProRequestedJobs()
    {
        if (mConfigManager.getConfigurationResponse() != null
                && mConfigManager.getConfigurationResponse().isPendingRequestsInboxEnabled())
        {
            requestRequestedAvailableJobs();
            mRequestsButton.setVisibility(View.VISIBLE);
        }
        else
        {
            mRequestsButton.setVisibility(View.GONE);
        }
    }

    private void requestRequestedAvailableJobs()
    {
        //TODO: Days should be behind a config param, just using a static const until then
        List<Date> datesForBookings = DateTimeUtils.getDateWithoutTimeList(new Date(), ProRequestedJobsFragment.REQUESTED_JOBS_NUM_DAYS_IN_ADVANCE);
        bus.post(new BookingEvent.RequestProRequestedJobs(datesForBookings, true));
    }
}
