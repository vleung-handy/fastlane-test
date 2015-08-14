package com.handy.portal.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.constant.TransitionStyle;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.model.SwapFragmentArguments;
import com.handy.portal.retrofit.HandyRetrofitEndpoint;
import com.handy.portal.ui.activity.BaseActivity;
import com.handy.portal.ui.element.LoadingOverlayView;
import com.handy.portal.ui.element.TransitionOverlayView;
import com.squareup.otto.Subscribe;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivityFragment extends InjectedFragment
{
    @InjectView(R.id.button_jobs)
    RadioButton jobsButton;
    @InjectView(R.id.button_schedule)
    RadioButton scheduleButton;
    @InjectView(R.id.button_profile)
    RadioButton profileButton;
    @InjectView(R.id.button_help)
    RadioButton helpButton;
    @InjectView(R.id.transition_overlay)
    TransitionOverlayView transitionOverlayView;
    @InjectView(R.id.loading_overlay)
    LoadingOverlayView loadingOverlayView;

    @Inject
    HandyRetrofitEndpoint endpoint;

    private MainViewTab currentTab = null;
    private PortalWebViewFragment webViewFragment = null;

    public static boolean clearingBackStack = false;


    public static final String URI_HOST_DEEPLINK = "deeplink";
    public static final String URI_PATH_AVAILABLE_JOBS = "/available_jobs";
    public static final String URI_PATH_BOOKING_DETAILS = "/booking_details";


    @Override
    public void onResume()
    {
        super.onResume();

        //deep link push notification may have come in
        checkForDeepLinkIntent();
    }

    private void checkForDeepLinkIntent()
    {
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.getData() != null)
        {
            Uri intentUri = intent.getData();
            if(intentUri.getHost().equals(URI_HOST_DEEPLINK))
            {
                openDeepLink(intent.getData());
            }
        }
    }

    //TODO: Is it possible to shift the logic that manages our fragment stack and deep links to relevant services to get them out of this fragment?
    private void openDeepLink(Uri deepLink)
    {
        if (deepLink == null || !deepLink.getHost().equals(URI_HOST_DEEPLINK))
        {
            return;
        }

        String path = deepLink.getPath();

        /* Debugging help while testing UA, take out before we merge to dev
        System.out.println("See uri : " + deepLink);
        System.out.println("See path : " + path);
        System.out.println("See query : " + deepLink.getQuery());
        System.out.println("See host : " + deepLink.getHost());
        */

        switch(path)
        {
            case URI_PATH_AVAILABLE_JOBS:
            {
                switchToTab(MainViewTab.JOBS, true);
            }
            break;

            case URI_PATH_BOOKING_DETAILS:
            {
                Bundle bundle = new Bundle();
                String bookingId = deepLink.getQuery();
                bundle.putString(BundleKeys.BOOKING_ID, bookingId);
                switchToTab(MainViewTab.DETAILS, bundle, true);
            }
            break;

        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_main, container);
        ButterKnife.inject(this, view);
        registerButtonListeners();
        registerBackStackListener();
        transitionOverlayView.init();
        loadingOverlayView.init();

        /*
            below logic is needed to workaround a bug in android 4.4 that cause webview artifacts to show.
            setting this at the help view or webview level does not fully work (complex webview pages didn't load)
        */
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT)
        {
            view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        return view;
    }

    private void registerBackStackListener()
    {
        getActivity().getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener()
        {
            @Override
            public void onBackStackChanged()
            {
                // traverse the fragment stack from top to bottom and activate the first relevant tab
                List<Fragment> fragments = getActivity().getSupportFragmentManager().getFragments();
                for (int i = fragments.size() - 1; i >= 0; i--)
                {
                    if (updateSelectedTabButton(fragments.get(i)))
                    {
                        break;
                    }
                }
            }
        });
    }

    private boolean updateSelectedTabButton(Fragment fragment)
    {
        return selectTabIfFragmentMatches(fragment, AvailableBookingsFragment.class, jobsButton) ||
               selectTabIfFragmentMatches(fragment, ScheduledBookingsFragment.class, scheduleButton) ||
               selectTabIfFragmentMatches(fragment, PortalWebViewFragment.class, profileButton) ||
               selectTabIfFragmentMatches(fragment, HelpFragment.class, helpButton);
    }

    private boolean selectTabIfFragmentMatches(Fragment fragment, Class<? extends Fragment> fragmentClass, RadioButton tab)
    {
        if (fragmentClass.isInstance(fragment))
        {
            tab.setChecked(true);
            return true;
        }
        return false;
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState)
    {
        super.onViewStateRestored(savedInstanceState);

        String tab = MainViewTab.JOBS.name();
        if (savedInstanceState != null && savedInstanceState.containsKey(BundleKeys.TAB))
        {
            tab = savedInstanceState.getString(BundleKeys.TAB);
        }

        switchToTab(MainViewTab.valueOf(tab), false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        if (outState == null)
        {
            outState = new Bundle();
        }
        outState.putString(BundleKeys.TAB, currentTab.name());
        super.onSaveInstanceState(outState);
    }

    //Listeners
    @Subscribe
    public void onNavigateToTabEvent(HandyEvent.NavigateToTab event)
    {
        switchToTab(event.targetTab, event.arguments, event.transitionStyleOverride, false);
    }

    @Subscribe
    public void onShowLoadingOverlay(HandyEvent.SetLoadingOverlayVisibility event)
    {
        loadingOverlayView.setOverlayVisibility(event.isVisible);
    }

    private void registerButtonListeners()
    {
        scheduleButton.setOnClickListener(new TabOnClickListener(MainViewTab.SCHEDULE));
        jobsButton.setOnClickListener(new TabOnClickListener(MainViewTab.JOBS));
        profileButton.setOnClickListener(new TabOnClickListener(MainViewTab.PROFILE));
        helpButton.setOnClickListener(new TabOnClickListener(MainViewTab.HELP));
    }

    private class TabOnClickListener implements View.OnClickListener
    {
        private MainViewTab tab;

        TabOnClickListener(MainViewTab tab)
        {
            this.tab = tab;
        }

        @Override
        public void onClick(View view)
        {
            switchToTab(tab, true);
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
        trackSwitchToTab(targetTab);

        SwapFragmentArguments swapFragmentArguments = new SwapFragmentArguments();

        if (targetTab.isNativeTab())
        {
            webViewFragment = null; //clear this out explicitly otherwise we keep a pointer to a bad fragment once it gets swapped out

            //don't use transition if don't have anything to transition from
            if (currentTab != null)
            {
                swapFragmentArguments.transitionStyle = (overrideTransitionStyle != null ? overrideTransitionStyle : currentTab.getDefaultTransitionStyle(targetTab));
            }

            swapFragmentArguments.targetClassType = targetTab.getClassType();
            swapFragmentArguments.argumentsBundle = argumentsBundle;

            if (userTriggered)
            {
                swapFragmentArguments.addToBackStack = false;
            }
            else
            {
                swapFragmentArguments.addToBackStack |= targetTab == MainViewTab.DETAILS;
                swapFragmentArguments.addToBackStack |= targetTab == MainViewTab.HELP_CONTACT;
                swapFragmentArguments.addToBackStack |= currentTab == MainViewTab.DETAILS && targetTab == MainViewTab.HELP;
                swapFragmentArguments.addToBackStack |= currentTab == MainViewTab.HELP && targetTab == MainViewTab.HELP;
            }

            swapFragmentArguments.clearBackStack = !swapFragmentArguments.addToBackStack;

            swapFragment(swapFragmentArguments);
        }
        else
        {
            String url;
            if (argumentsBundle != null && argumentsBundle.containsKey(BundleKeys.TARGET_URL))
            {
                url = argumentsBundle.getString(BundleKeys.TARGET_URL);
            }
            else
            {
                url = endpoint.getBaseUrl() + "/portal/home?goto=" + targetTab.getTarget().getValue();
            }

            if (webViewFragment == null)
            {
                webViewFragment = new PortalWebViewFragment();

                //pass along the target
                Bundle arguments = new Bundle();
                arguments.putString(BundleKeys.TARGET_URL, url);

                swapFragmentArguments.argumentsBundle = arguments;
                swapFragmentArguments.overrideFragment = webViewFragment;
                swapFragmentArguments.clearBackStack = true;

                swapFragment(swapFragmentArguments);
            }
            else
            {
                //don't need to do any fragment swapping just open the new url
                webViewFragment.openPortalUrl(url);
            }
        }

        updateSelectedTabButton(targetTab);

        ((BaseActivity) getActivity()).clearOnBackPressedListenerStack();

        currentTab = targetTab;
    }

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

    //Update the visuals to show the correct selected button
    private void updateSelectedTabButton(MainViewTab targetTab)
    {
        //Somewhat ugly mapping right now, is there a more elegant way to do this? Tabs as a model should not know about their buttons
        if (targetTab != MainViewTab.DETAILS)
        {
            switch (targetTab)
            {
                case JOBS:
                {
                    jobsButton.toggle();
                }
                break;
                case SCHEDULE:
                {
                    scheduleButton.toggle();
                }
                break;
                case PROFILE:
                {
                    profileButton.toggle();
                }
                break;
                case HELP:
                {
                    helpButton.toggle();
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
            } catch (Exception e)
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
            transaction.setCustomAnimations(swapArguments.transitionStyle.getIncomingAnimId(), swapArguments.transitionStyle.getOutgoingAnimId());

            //Runs async, covers the transition
            if (swapArguments.transitionStyle.shouldShowOverlay())
            {
                transitionOverlayView.setupOverlay(swapArguments.transitionStyle);
                transitionOverlayView.showThenHideOverlay();
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
}
