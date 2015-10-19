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
import com.handy.portal.ui.activity.BaseActivity;
import com.handy.portal.ui.element.LoadingOverlayView;
import com.handy.portal.ui.fragment.dialog.TransientOverlayDialogFragment;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivityFragment extends InjectedFragment
{
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

    private MainViewTab currentTab = null;

    public static boolean clearingBackStack = false;

    @Override
    public void onResume()
    {
        super.onResume();
        bus.post(new HandyEvent.UpdateMainActivityFragmentActive(true));
    }

    @Override
    public void onPause()
    {
        super.onPause();
        bus.post(new HandyEvent.UpdateMainActivityFragmentActive(false));
    }

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
            switchToTab(MainViewTab.AVAILABLE_JOBS, false);
        }
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
        trackSwitchToTab(targetTab);

        SwapFragmentArguments swapFragmentArguments = new SwapFragmentArguments();

        //don't use transition if don't have anything to transition from
        if (currentTab != null)
        {
            swapFragmentArguments.transitionStyle = (overrideTransitionStyle != null ? overrideTransitionStyle : currentTab.getDefaultTransitionStyle(targetTab));
        }

        swapFragmentArguments.targetClassType = targetTab.getClassType();

        if (argumentsBundle == null)
        {
            argumentsBundle = new Bundle();
        }
        argumentsBundle.putParcelable(BundleKeys.UPDATE_TAB_CALLBACK, new ActionBarFragment.UpdateTabsCallback()
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

        if (targetTab == MainViewTab.DETAILS)
        {
            argumentsBundle.putSerializable(BundleKeys.TAB, currentTab);
        }

        swapFragmentArguments.argumentsBundle = argumentsBundle;

        if (userTriggered)
        {
            swapFragmentArguments.addToBackStack = false;
        }
        else
        {
            swapFragmentArguments.addToBackStack |= targetTab == MainViewTab.COMPLEMENTARY_JOBS;
            swapFragmentArguments.addToBackStack |= targetTab == MainViewTab.SELECT_PAYMENT_METHOD;
            swapFragmentArguments.addToBackStack |= targetTab == MainViewTab.UPDATE_BANK_ACCOUNT;
            swapFragmentArguments.addToBackStack |= targetTab == MainViewTab.UPDATE_DEBIT_CARD;
            swapFragmentArguments.addToBackStack |= targetTab == MainViewTab.DETAILS;
            swapFragmentArguments.addToBackStack |= targetTab == MainViewTab.PAYMENTS_DETAIL;
            swapFragmentArguments.addToBackStack |= targetTab == MainViewTab.HELP_CONTACT;
            swapFragmentArguments.addToBackStack |= currentTab == MainViewTab.DETAILS && targetTab == MainViewTab.HELP;
            swapFragmentArguments.addToBackStack |= currentTab == MainViewTab.HELP && targetTab == MainViewTab.HELP;
            swapFragmentArguments.addToBackStack |= currentTab == MainViewTab.PAYMENTS && targetTab == MainViewTab.HELP;
        }

        swapFragmentArguments.clearBackStack = !swapFragmentArguments.addToBackStack;

        swapFragment(swapFragmentArguments);

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
                case AVAILABLE_JOBS:
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
