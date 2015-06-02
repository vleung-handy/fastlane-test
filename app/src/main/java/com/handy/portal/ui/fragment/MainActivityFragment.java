package com.handy.portal.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.handy.portal.R;
import com.handy.portal.consts.BundleKeys;
import com.handy.portal.consts.MainViewTab;
import com.handy.portal.consts.TransitionAnimationIndex;
import com.handy.portal.consts.TransitionStyle;
import com.handy.portal.core.SwapFragmentArguments;
import com.handy.portal.event.Event;
import com.handy.portal.ui.element.TransitionOverlayView;
import com.handy.portal.ui.fragment.PortalWebViewFragment.Target;
import com.squareup.otto.Subscribe;

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

    private MainViewTab currentTab = null;
    private PortalWebViewFragment webViewFragment = null;

    public MainActivityFragment()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_main, container);
        ButterKnife.inject(this, view);

        registerButtonListeners();

        jobsButton.setChecked(true);
        switchToTab(MainViewTab.JOBS);

        transitionOverlayView.init();

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (currentTab == null)
        {
            jobsButton.setChecked(true);
            switchToTab(MainViewTab.JOBS);
        }
    }

    //Listeners
    @Subscribe
    public void onNavigateToTabEvent(Event.NavigateToTabEvent event)
    {
        switchToTab(event.targetTab, event.arguments, event.transitionStyleOverride);
    }

    private void initWebViewFragment(Target urlTarget)
    {
        webViewFragment = new PortalWebViewFragment();

        //pass along the target
        Bundle arguments = new Bundle();
        arguments.putString(BundleKeys.TARGET_URL, urlTarget.getValue());

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
        if (currentTab != targetTab) //don't transition to same tab, ignore the clicks
        {
            //analytics event
            String analyticsPageData = "";
            if (targetTab.isNativeTab())
            {
                analyticsPageData = targetTab.getClassType().toString();
            } else
            {
                analyticsPageData = targetTab.getTarget().getValue();
            }
            bus.post(new Event.Navigation(analyticsPageData));

            if (targetTab.isNativeTab())
            {
                webViewFragment = null; //clear this out explicitly otherwise we keep a pointer to a bad fragment once it gets swapped out

                SwapFragmentArguments swapFragmentArguments = new SwapFragmentArguments();

                if (currentTab != null)
                {
                    swapFragmentArguments.transitionAnimationIds = currentTab.getTransitionAnimationIds(targetTab);
                    if (overrideTransitionStyle != null)
                    {
                        int[] overrideAnimationIds = overrideTransitionStyle.getAnimationsIds();

                        if (swapFragmentArguments.transitionAnimationIds == null)
                        {
                            swapFragmentArguments.transitionAnimationIds = new int[2];
                        }

                        if (overrideAnimationIds[TransitionAnimationIndex.INCOMING] != 0)
                        {
                            swapFragmentArguments.transitionAnimationIds[TransitionAnimationIndex.INCOMING]
                                    = overrideAnimationIds[TransitionAnimationIndex.INCOMING];
                        }
                        if (overrideAnimationIds[TransitionAnimationIndex.OUTGOING] != 0)
                        {
                            swapFragmentArguments.transitionAnimationIds[TransitionAnimationIndex.OUTGOING]
                                    = overrideAnimationIds[TransitionAnimationIndex.OUTGOING];
                        }
                    }
                    swapFragmentArguments.showOverlay = currentTab.setupOverlay(targetTab, transitionOverlayView, overrideTransitionStyle);
                }

                swapFragmentArguments.targetClassType = targetTab.getClassType();
                swapFragmentArguments.argumentsBundle = argumentsBundle;
                swapFragmentArguments.addToBackStack = true;

                swapFragment(swapFragmentArguments);

            } else
            {
                if (webViewFragment == null)
                {
                    initWebViewFragment(currentTab.getTarget());
                } else
                {
                    webViewFragment.openPortalUrl(currentTab.getTarget());
                }
            }

            currentTab = targetTab;
        }
    }

    private void swapFragment(SwapFragmentArguments swapArguments)
    {
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
                System.err.println("Error instantiating fragment class : " + e);
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
        if (swapArguments.transitionAnimationIds != null)
        {
            transaction.setCustomAnimations(
                    swapArguments.transitionAnimationIds[TransitionAnimationIndex.INCOMING],
                    swapArguments.transitionAnimationIds[TransitionAnimationIndex.OUTGOING]
            );
        }

        //Runs async, covers the transition
        if (swapArguments.showOverlay)
        {
            transitionOverlayView.showThenHideOverlay();
        }

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.main_container, newFragment);

        if (swapArguments.addToBackStack)
        {
            transaction.addToBackStack(null);
        } else
        {
            transaction.disallowAddToBackStack();
        }

        // Commit the transaction
        transaction.commit();

    }



}



