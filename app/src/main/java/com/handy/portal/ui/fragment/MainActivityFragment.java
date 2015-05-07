package com.handy.portal.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.handy.portal.R;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends InjectedFragment
{


    @InjectView(R.id.button_available_jobs)
    Button availableJobsButton;
    @InjectView(R.id.button_scheduled_jobs)
    Button scheduledJobsButton;
    @InjectView(R.id.button_profile)
    Button profileButton;
    @InjectView(R.id.button_help)
    Button helpButton;

    private int currentTabFragmentId = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_main, container);
        ButterKnife.inject(this, view);

        registerButtonListeners();

        switchToTab(MainViewTab.AVAILABLE_BOOKINGS);
        //initView();

        return view;
    }

    private void registerButtonListeners()
    {
        scheduledJobsButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onScheduledJobsClicked(v);
            }
        });

        availableJobsButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onAvailableJobsClicked(v);
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onProfileClicked(v);
            }
        });

        helpButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onHelpClicked(v);
            }
        });
    }

    public void onAvailableJobsClicked(View clickedView)
    {
        switchToTab(MainViewTab.AVAILABLE_BOOKINGS);
    }

    public void onScheduledJobsClicked(View clickedView)
    {
        switchToTab(MainViewTab.CLAIMED_BOOKINGS);
    }

    public void onProfileClicked(View clickedView)
    {
        switchToTab(MainViewTab.PROFILE);
    }

    public void onHelpClicked(View clickedView)
    {
        switchToTab(MainViewTab.HELP);
    }

    enum MainViewTab
    {
        AVAILABLE_BOOKINGS,
        CLAIMED_BOOKINGS,
        PROFILE,
        HELP
    }

    private int getFragmentIdForTab(MainViewTab tab)
    {
        switch (tab)
        {
            case AVAILABLE_BOOKINGS:
                return R.layout.fragment_available_bookings;
            case CLAIMED_BOOKINGS:
                return R.layout.fragment_scheduled_bookings;
            case PROFILE:
                return R.layout.fragment_profile;
            case HELP:
                return R.layout.fragment_help;
        }
        return -1;
    }

    private String getParamForWebFragment(MainViewTab tab)
    {
        switch (tab)
        {
            case AVAILABLE_BOOKINGS:
                return "a";
            case CLAIMED_BOOKINGS:
                return "b";
            case PROFILE:
                return "c";
            case HELP:
                return "d";
        }
        return "";
    }

    private Class getFragmentClassForTab(MainViewTab tab)
    {
        switch (tab)
        {
            case AVAILABLE_BOOKINGS:
                return AvailableBookingsFragment.class;
            case CLAIMED_BOOKINGS:
                return ScheduledBookingsFragment.class;
            case PROFILE:
                return ProfileFragment.class;
            case HELP:
                return HelpFragment.class;
        }
        return null;
    }


    private void initView()
    {
        // Create a new Fragment to be placed in the activity layout
        AvailableBookingsFragment firstFragment = new AvailableBookingsFragment();

        //Add the fragment to the 'fragment_container' FrameLayout
        getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.main_container, firstFragment).commit();
    }

    //click on a button, get the fragment, check if different fragment, or if webview check associated id/link/param

    private void switchToTab(MainViewTab tab)
    {
        int newFragmentId = getFragmentIdForTab(tab);

        Class newFragmentClass = getFragmentClassForTab(tab);

        if (newFragmentId != currentTabFragmentId) //don't transition to same tab, ignore the clicks
        {
            //replace the existing fragment with the new fragment
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

            Fragment newFragment = null;
            try
            {
                newFragment = (Fragment) newFragmentClass.newInstance();
            } catch (Exception e)
            {
                System.err.println("Error instantiating fragment class");
            }

            currentTabFragmentId = newFragmentId;

// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.main_container, newFragment);
            transaction.addToBackStack(null);

// Commit the transaction
            transaction.commit();


        }


    }


}
