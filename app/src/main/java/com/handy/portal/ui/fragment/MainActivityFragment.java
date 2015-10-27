package com.handy.portal.ui.fragment;

import android.os.Bundle;
import android.os.Parcel;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

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
import com.handy.portal.ui.fragment.dialog.TransientOverlayDialogFragment;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivityFragment extends InjectedFragment
{
//TODO: If we take out this entirely unused injection the app complains about: No instance field endpoint of type , to investigate in morning
    @Inject
    HandyRetrofitEndpoint handyRetrofitEndpoint;
/////////////Bad useless injection that breaks if not in?

    @InjectView(R.id.tabs)
    RadioGroup tabs;
    @InjectView(R.id.button_jobs)
    RadioButton jobsButton;
    @InjectView(R.id.button_schedule)
    RadioButton scheduleButton;
    @InjectView(R.id.button_payments)
    RadioButton paymentsButton;
    @InjectView(R.id.button_profile)
    RadioButton profileButton;
    @InjectView(R.id.button_help)
    RadioButton helpButton;
    @InjectView(R.id.loading_overlay)
    LoadingOverlayView loadingOverlayView;

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
        ButterKnife.inject(this, view);
        registerButtonListeners();
        loadingOverlayView.init();
        return view;
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState)
    {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState == null || !savedInstanceState.containsKey(BundleKeys.TAB))
        {
            mOnResumeTransitionToMainTab = true;
        }
        else
        {
            mOnResumeTransitionToMainTab = false;
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        bus.post(new HandyEvent.UpdateMainActivityFragmentActive(true));

        //Need to wait until onResume instead of onViewStateRestored to make sure bus is registered
        if (mOnResumeTransitionToMainTab)
        {
            switchToTab(MainViewTab.AVAILABLE_JOBS, false);
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        bus.post(new HandyEvent.UpdateMainActivityFragmentActive(false));
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        if (currentTab != null)
        {
            if (outState == null)
            {
                outState = new Bundle();
            }
            outState.putString(BundleKeys.TAB, currentTab.name());
        }
        super.onSaveInstanceState(outState);
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
    private void requestProcessNavigateToTab(MainViewTab targetTab, MainViewTab currentTab, Bundle arguments, TransitionStyle transitionStyle, boolean userTriggered)
    {
        bus.post(new HandyEvent.RequestProcessNavigateToTab(targetTab, currentTab, arguments, transitionStyle, userTriggered));
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
        loadingOverlayView.setOverlayVisibility(event.isVisible);
    }

//Click Listeners

    private void registerButtonListeners()
    {
        jobsButton.setOnClickListener(new TabOnClickListener(MainViewTab.AVAILABLE_JOBS));
        scheduleButton.setOnClickListener(new TabOnClickListener(MainViewTab.SCHEDULED_JOBS));
        paymentsButton.setOnClickListener(new TabOnClickListener(MainViewTab.PAYMENTS));
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
                    jobsButton.toggle();
                }
                break;
                case SCHEDULED_JOBS:
                {
                    scheduleButton.toggle();
                }
                break;
                case PAYMENTS:
                {
                    paymentsButton.toggle();
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
}
