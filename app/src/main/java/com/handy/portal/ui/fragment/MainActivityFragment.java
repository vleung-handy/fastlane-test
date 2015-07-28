package com.handy.portal.ui.fragment;

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

    public static boolean clearingBackStack;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_main, container);
        ButterKnife.inject(this, view);
        registerButtonListeners();
        transitionOverlayView.init();
        loadingOverlayView.init();

        return view;
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
        switchToTab(MainViewTab.valueOf(tab));
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
        switchToTab(event.targetTab, event.arguments, event.transitionStyleOverride);
    }

    @Subscribe
    public void onShowLoadingOverlay(HandyEvent.SetLoadingOverlayVisibility event)
    {
        loadingOverlayView.setOverlayVisibility(event.isVisible);
    }

    private void initWebViewFragment(String url)
    {
        webViewFragment = new PortalWebViewFragment();

        //pass along the target
        Bundle arguments = new Bundle();
        arguments.putString(BundleKeys.TARGET_URL, url);

        SwapFragmentArguments swapFragmentArguments = new SwapFragmentArguments();
        swapFragmentArguments.argumentsBundle = arguments;
        swapFragmentArguments.overrideFragment = webViewFragment;

        swapFragment(swapFragmentArguments);
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
            switchToTab(tab);
        }
    }

    private void switchToTab(MainViewTab tab)
    {
        switchToTab(tab, null);
    }

    private void switchToTab(MainViewTab targetTab, Bundle argumentsBundle)
    {
        switchToTab(targetTab, argumentsBundle, null);
    }

    private void switchToTab(MainViewTab targetTab, Bundle argumentsBundle, TransitionStyle overrideTransitionStyle)
    {
        trackSwitchToTab(targetTab);

        updateSelectedTabButton(targetTab);

        if (targetTab.isNativeTab())
        {
            webViewFragment = null; //clear this out explicitly otherwise we keep a pointer to a bad fragment once it gets swapped out

            SwapFragmentArguments swapFragmentArguments = new SwapFragmentArguments();

            //don't use transition if don't have anything to transition from
            if (currentTab != null)
            {
                swapFragmentArguments.transitionStyle = (overrideTransitionStyle != null ? overrideTransitionStyle : currentTab.getDefaultTransitionStyle(targetTab));
            }

            swapFragmentArguments.targetClassType = targetTab.getClassType();
            swapFragmentArguments.argumentsBundle = argumentsBundle;
            swapFragmentArguments.addToBackStack = (targetTab == MainViewTab.DETAILS);
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
                initWebViewFragment(url);
            }
            else
            {
                webViewFragment.openPortalUrl(url);
            }
        }

        if (targetTab != MainViewTab.DETAILS)
        {
            ((BaseActivity) getActivity()).clearOnBackPressedListenerStack();
            currentTab = targetTab;
        }
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
        String analyticsPageData;
        if (targetTab.isNativeTab())
        {
            analyticsPageData = targetTab.getClassType().toString();
        }
        else
        {
            analyticsPageData = targetTab.getTarget().getValue();
        }
        bus.post(new HandyEvent.Navigation(analyticsPageData));
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
        clearFragmentBackStack();

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

        if(swapArguments.addToBackStack)
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
