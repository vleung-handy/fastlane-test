package com.handy.portal.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.constant.PrefsKey;
import com.handy.portal.constant.TransitionStyle;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.LogEvent;
import com.handy.portal.manager.ConfigManager;
import com.handy.portal.manager.PrefsManager;
import com.handy.portal.model.ConfigurationResponse;
import com.handy.portal.model.SwapFragmentArguments;
import com.handy.portal.ui.activity.BaseActivity;
import com.handy.portal.ui.activity.LoginActivity;
import com.handy.portal.ui.fragment.dialog.TransientOverlayDialogFragment;
import com.handy.portal.util.DeeplinkMapper;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivityFragment extends InjectedFragment
{
    @Inject
    PrefsManager mPrefsManager;
    @Inject
    ConfigManager mConfigManager;
    /////////////Bad useless injection that breaks if not in?

    @Bind(R.id.tabs)
    RadioGroup tabs;
    @Bind(R.id.button_jobs)
    RadioButton mJobsButton;
    @Bind(R.id.button_schedule)
    RadioButton mScheduleButton;
    @Bind(R.id.button_notifications)
    RadioButton mNotificationsButton;
    @Bind(R.id.button_more)
    RadioButton mButtonMore;
    @Bind(R.id.loading_overlay)
    View mLoadingOverlayView;
    @Bind(R.id.nav_link_my_profile)
    RadioButton mNavLinkMyProfile;
    @Bind(R.id.nav_link_payments)
    RadioButton mNavLinkPayments;
    @Bind(R.id.nav_link_edit_payment_method)
    RadioButton mNavLinkEditPaymentMethod;
    @Bind(R.id.nav_link_help)
    RadioButton mNavLinkHelp;
    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @Bind(R.id.navigation_drawer)
    LinearLayout mNavigationDrawer;
    @Bind(R.id.nav_tray_links)
    RadioGroup mNavTrayLinks;
    @Bind(R.id.navigation_header)
    TextView mNavigationHeader;

    //What tab are we currently displaying
    private MainViewTab currentTab = null;

    //Are we currently clearing out the backstack?
    // Other fragments will want to know to avoid re-doing things on their onCreateView
    public static boolean clearingBackStack = false;

    private boolean mOnResumeTransitionToMainTab; //need to catch and hold until onResume so we can catch the response from the bus

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_main, container);
        ButterKnife.bind(this, view);
        registerButtonListeners();
        return view;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        bus.post(new HandyEvent.UpdateMainActivityFragmentActive(true));
        if (currentTab == null)
        {
            switchToTab(MainViewTab.AVAILABLE_JOBS, false);
        }
        handleDeeplink();
    }

    private void handleDeeplink()
    {
        final Bundle deeplinkData =
                getActivity().getIntent().getBundleExtra(BundleKeys.DEEPLINK_DATA);
        if (deeplinkData != null)
        {
            final String deeplink = deeplinkData.getString(BundleKeys.DEEPLINK);
            if (deeplink != null)
            {
                final MainViewTab targetTab = DeeplinkMapper.getTabForDeeplink(deeplink);
                if (targetTab != null)
                {
                    switchToTab(targetTab, deeplinkData, false);
                }
            }
            ((BaseActivity) getActivity()).setDeeplinkHandled();
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        bus.post(new HandyEvent.UpdateMainActivityFragmentActive(false));
    }

//Event Listeners

    @Subscribe
    public void onNavigateToTabEvent(HandyEvent.NavigateToTab event)
    {
        //Catch this event then throw one to have the manager do the processing
        //We need to bother catching it here because we need to know the current tab of this fragment
        requestProcessNavigateToTab(event.targetTab, this.currentTab, event.arguments, event.transitionStyleOverride, false);
    }

    //Ask the managers to do all the argument processing and post back a SwapFragmentNavigation event
    private void requestProcessNavigateToTab(
            MainViewTab targetTab, MainViewTab currentTab, Bundle arguments,
            TransitionStyle transitionStyle, boolean userTriggered)
    {
        bus.post(new HandyEvent.RequestProcessNavigateToTab(targetTab, currentTab, arguments,
                transitionStyle, userTriggered));
        bus.post(new LogEvent.AddLogEvent(
                mEventLogFactory.createNavigationLog(targetTab.name().toLowerCase())));
    }

    @Subscribe
    public void onSwapFragmentNavigation(HandyEvent.SwapFragmentNavigation event)
    {
        SwapFragmentArguments swapFragmentArguments = event.swapFragmentArguments;

        //Add our fragment specific callback to update tab buttons
        addUpdateTabCallback(swapFragmentArguments);
        //Track in analytics
        trackSwitchToTab(swapFragmentArguments.targetTab);
        //Swap the fragments
        swapFragment(swapFragmentArguments);
        //Update the tab button display
        updateSelectedTabButton(swapFragmentArguments.targetTab);
        //Clear the back pressed listeners from the old fragment(s)
        ((BaseActivity) getActivity()).clearOnBackPressedListenerStack();
        //Set correct currentTab
        currentTab = swapFragmentArguments.targetTab;
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

//Click Listeners

    private void registerButtonListeners()
    {
        registerBottomNavListeners();
        registerNavDrawerListeners();
    }

    private void registerBottomNavListeners()
    {
        mJobsButton.setOnClickListener(new TabOnClickListener(MainViewTab.AVAILABLE_JOBS));
        mScheduleButton.setOnClickListener(new TabOnClickListener(MainViewTab.SCHEDULED_JOBS));
        mButtonMore.setOnClickListener(new MoreButtonOnClickListener());
        tabs.setOnCheckedChangeListener(new BottomNavOnCheckedChangeListener());

        if (getConfigurationResponse() != null && getConfigurationResponse().shouldShowNotificationMenuButton())
        {
            mNotificationsButton.setOnClickListener(new TabOnClickListener(MainViewTab.NOTIFICATIONS));
            mNotificationsButton.setVisibility(View.VISIBLE);
        }
    }

    private void registerNavDrawerListeners()
    {
        mNavLinkMyProfile.setOnClickListener(new NavDrawerOnClickListener(MainViewTab.PROFILE, null));
        mNavLinkPayments.setOnClickListener(new NavDrawerOnClickListener(MainViewTab.PAYMENTS, null));
        mNavLinkEditPaymentMethod.setOnClickListener(new NavDrawerOnClickListener(MainViewTab.SELECT_PAYMENT_METHOD, TransitionStyle.SLIDE_UP));
        mNavLinkHelp.setOnClickListener(new NavDrawerOnClickListener(MainViewTab.HELP, null));
    }

    private class TabOnClickListener implements View.OnClickListener
    {
        private MainViewTab mTab;

        TabOnClickListener(MainViewTab tab)
        {
            mTab = tab;
        }

        @Override
        public void onClick(View view)
        {
            switchToTab(mTab, true);
        }
    }


    private class NavDrawerOnClickListener extends TabOnClickListener
    {
        private MainViewTab mTab;
        private TransitionStyle mTransitionStyle;

        NavDrawerOnClickListener(MainViewTab tab, TransitionStyle transitionStyleOverride)
        {
            super(tab);
            mTab = tab;
            mTransitionStyle = transitionStyleOverride;
        }

        @Override
        public void onClick(View view)
        {
            if (mTransitionStyle != null)
            {
                switchToTab(mTab, null, mTransitionStyle, false);
            }
            else
            {
                switchToTab(mTab, true);
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


    private class BottomNavOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener
    {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int radioButtonId)
        {
            if (radioButtonId == mButtonMore.getId() && currentTab != null)
            {
                updateSelectedTabButton(currentTab);
            }
        }
    }

    private void switchToTab(MainViewTab tab, boolean userTriggered)
    {
        switchToTab(tab, null, userTriggered);
    }

    private void switchToTab(MainViewTab targetTab, Bundle argumentsBundle, boolean userTriggered)
    {
        switchToTab(targetTab, argumentsBundle, null, userTriggered);
    }

    private void switchToTab(MainViewTab targetTab, Bundle argumentsBundle, TransitionStyle overrideTransitionStyle, boolean userTriggered)
    {
        requestProcessNavigateToTab(targetTab, currentTab, argumentsBundle, overrideTransitionStyle, userTriggered);
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
    private void trackSwitchToTab(MainViewTab targetTab)
    {
        bus.post(new HandyEvent.Navigation(targetTab.toString().toLowerCase()));
    }

    private void addUpdateTabCallback(SwapFragmentArguments swapFragmentArguments)
    {
        swapFragmentArguments.argumentsBundle.putParcelable(BundleKeys.UPDATE_TAB_CALLBACK, new ActionBarFragment.UpdateTabsCallback()
        {
            @Override
            public int describeContents() { return 0; }

            @Override
            public void writeToParcel(Parcel parcel, int i) { }

            @Override
            public void updateTabs(MainViewTab tab)
            {
                if (tabs != null) { updateSelectedTabButton(tab); }
            }
        });
    }

    //Update the visuals to show the correct selected button
    private void updateSelectedTabButton(MainViewTab targetTab)
    {
        //Somewhat ugly mapping right now, is there a more elegant way to do this? Tabs as a model should not know about their buttons
        if (targetTab != MainViewTab.DETAILS)
        {
            switch (targetTab)
            {
                case AVAILABLE_JOBS:
                case BLOCK_PRO_AVAILABLE_JOBS_WEBVIEW:
                {
                    mJobsButton.toggle();
                    mNavTrayLinks.clearCheck();
                }
                break;
                case SCHEDULED_JOBS:
                {
                    mScheduleButton.toggle();
                    mNavTrayLinks.clearCheck();
                }
                break;
                case NOTIFICATIONS:
                {
                    mNotificationsButton.toggle();
                    mNavTrayLinks.clearCheck();
                }
                break;
                case PAYMENTS:
                {
                    mButtonMore.toggle();
                    mNavLinkPayments.toggle();
                }
                break;
                case PROFILE:
                {
                    mButtonMore.toggle();
                    mNavLinkMyProfile.toggle();
                }
                break;
                case HELP:
                {
                    mButtonMore.toggle();
                    mNavLinkHelp.toggle();
                }
                break;
                case SELECT_PAYMENT_METHOD:
                {
                    mButtonMore.toggle();
                    mNavLinkEditPaymentMethod.toggle();
                }
                break;
            }
        }
    }

    private void swapFragment(SwapFragmentArguments swapArguments)
    {
        if (swapArguments.clearBackStack)
        {
            clearFragmentBackStack();
        }

        //replace the existing fragment with the new fragment
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

        Fragment newFragment = null;
        if (swapArguments.targetClassType != null)
        {
            try
            {
                newFragment = (Fragment) swapArguments.targetClassType.newInstance();
            }
            catch (Exception e)
            {
                Crashlytics.logException(new RuntimeException("Error instantiating fragment class", e));
                return;
            }
        }

        if (swapArguments.overrideFragment != null)
        {
            newFragment = swapArguments.overrideFragment;
        }

        if (newFragment != null && swapArguments.argumentsBundle != null)
        {
            newFragment.setArguments(swapArguments.argumentsBundle);
        }

        //Animate the transition, animations must come before the .replace call
        if (swapArguments.transitionStyle != null)
        {
            transaction.setCustomAnimations(
                    swapArguments.transitionStyle.getIncomingAnimId(),
                    swapArguments.transitionStyle.getOutgoingAnimId(),
                    swapArguments.transitionStyle.getPopIncomingAnimId(),
                    swapArguments.transitionStyle.getPopOutgoingAnimId()
            );

            //Runs async, covers the transition
            if (swapArguments.transitionStyle.shouldShowOverlay())
            {
                TransientOverlayDialogFragment overlayDialogFragment = TransientOverlayDialogFragment
                        .newInstance(R.anim.overlay_fade_in_then_out, R.drawable.ic_success_circle, swapArguments.transitionStyle.getOverlayStringId());
                overlayDialogFragment.show(getFragmentManager(), "overlay dialog fragment");
            }
        }

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.main_container, newFragment);

        if (swapArguments.addToBackStack)
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

    private void logOutProvider()
    {
        mPrefsManager.setString(PrefsKey.AUTH_TOKEN, null);
        mPrefsManager.setString(PrefsKey.LAST_PROVIDER_ID, null);
        clearFragmentBackStack();
        CookieManager.getInstance().removeAllCookie();
        startActivity(new Intent(getActivity(), LoginActivity.class));
        getActivity().finish();
    }

}
