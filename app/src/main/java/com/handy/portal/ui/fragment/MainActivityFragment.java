package com.handy.portal.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.handy.portal.R;
import com.handy.portal.event.Event;
import com.handy.portal.ui.fragment.PortalWebViewFragment.Target;

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

    private Target currentTab = null;
    private PortalWebViewFragment webViewFragment = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_main, container);
        ButterKnife.inject(this, view);

        registerButtonListeners();
        initWebViewFragment();

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (currentTab == null)
        {
            jobsButton.setChecked(true);
            switchToTab(Target.JOBS);
        }
    }

    private void initWebViewFragment()
    {
        webViewFragment = new PortalWebViewFragment();
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container, webViewFragment)
                .disallowAddToBackStack()
                .commit();
    }

    private void registerButtonListeners()
    {
        scheduleButton.setOnClickListener(new TabOnClickListener(Target.SCHEDULE));
        jobsButton.setOnClickListener(new TabOnClickListener(Target.JOBS));
        profileButton.setOnClickListener(new TabOnClickListener(Target.PROFILE));
        helpButton.setOnClickListener(new TabOnClickListener(Target.HELP));
    }

    private class TabOnClickListener implements View.OnClickListener
    {
        private Target tab;

        TabOnClickListener(Target tab)
        {
            this.tab = tab;
        }

        @Override
        public void onClick(View view)
        {
            switchToTab(tab);
        }
    }

    private void switchToTab(Target tab)
    {
        if (currentTab != tab) //don't transition to same tab, ignore the clicks
        {
            bus.post(new Event.Navigation(tab.getValue()));
            webViewFragment.openPortalUrl(tab);
            currentTab = tab;
        }
    }
}
