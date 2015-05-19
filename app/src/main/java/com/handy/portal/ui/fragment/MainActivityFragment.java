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
import com.handy.portal.core.ServerParams;
import com.handy.portal.event.Event;
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

    private MainViewTab currentTab = null;
    private PortalWebViewFragment webViewFragment = null;

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
        switchToTab(event.targetTab, event.arguments);
    }

    private void initWebViewFragment(String urlTarget)
    {
        webViewFragment = new PortalWebViewFragment();

        //pass along the target
        Bundle arguments = new Bundle();
        arguments.putString(BundleKeys.TARGET_URL, urlTarget);
        webViewFragment.setArguments(arguments);

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container, webViewFragment)
                .disallowAddToBackStack()
                .commit();
    }

    private void registerButtonListeners()
    {
        scheduleButton.setOnClickListener(new TabOnClickListener(MainViewTab.SCHEDULE));
        jobsButton.setOnClickListener(new TabOnClickListener(MainViewTab.JOBS));
        profileButton.setOnClickListener(new TabOnClickListener(MainViewTab.PROFILE));
        helpButton.setOnClickListener(new TabOnClickListener(MainViewTab.HELP));
    }

    public enum MainViewTab
    {
        JOBS(ServerParams.Targets.AVAILABLE, AvailableBookingsFragment.class),
        SCHEDULE(ServerParams.Targets.FUTURE, ScheduledBookingsFragment.class),
        PROFILE(ServerParams.Targets.PROFILE, null),
        HELP(ServerParams.Targets.HELP, null),
        DETAILS(ServerParams.Targets.DETAILS, BookingDetailsFragment.class),
        ;

        private String target;
        private Class classType;

        MainViewTab(String target, Class classType)
        {
            this.target = target;
            this.classType = classType;
        }

        public String getTarget()
        {
            return target;
        }
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

    private void switchToTab(MainViewTab tab, Bundle argumentsBundle)
    {
        if (currentTab != tab) //don't transition to same tab, ignore the clicks
        {
            currentTab = tab;
            if(isNativeTab(currentTab))
            {
                swapFragment(currentTab.classType, argumentsBundle);
                webViewFragment = null; //clear this out explicitly otherwise we keep a pointer to a bad fragment once it gets swapped out
            }
            else
            {
                if(webViewFragment == null)
                {
                    initWebViewFragment(currentTab.getTarget());
                }
                else
                {
                    webViewFragment.openPortalUrl(currentTab.getTarget());
                }
            }
        }
    }

    private void swapFragment(Class targetClassType)
    {
        swapFragment(targetClassType, null);
    }

    private void swapFragment(Class targetClassType, Bundle argumentsBundle)
    {
        //replace the existing fragment with the new fragment
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

        Fragment newFragment = null;
        try
        {
            newFragment = (Fragment) targetClassType.newInstance();
        }
        catch (Exception e)
        {
            System.err.println("Error instantiating fragment class : " + e);
        }

        newFragment.setArguments(argumentsBundle);

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.main_container, newFragment);

        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

    //Eventually all tabs will be native tabs
    private boolean isNativeTab(MainViewTab tab)
    {
        return(tab.classType != null);
    }

}
