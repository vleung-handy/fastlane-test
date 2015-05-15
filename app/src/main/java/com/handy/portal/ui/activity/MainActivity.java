package com.handy.portal.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

import com.handy.portal.R;
import com.handy.portal.core.ServerParams;
import com.handy.portal.ui.fragment.PortalWebViewFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends BaseActivity
{
    @InjectView(R.id.button_jobs)
    RadioButton jobsButton;
    @InjectView(R.id.button_schedule)
    RadioButton scheduleButton;
    @InjectView(R.id.button_profile)
    RadioButton profileButton;
    @InjectView(R.id.button_help)
    RadioButton helpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this, this);

        registerButtonListeners();
        initWebViewFragment();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (currentTab == null)
        {
            jobsButton.setChecked(true);
            switchToTab(MainViewTab.JOBS);
        }
    }

    private MainViewTab currentTab = null;
    private PortalWebViewFragment webViewFragment = null;

    private void initWebViewFragment()
    {
        webViewFragment = new PortalWebViewFragment();
        getSupportFragmentManager().beginTransaction()
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

    private enum MainViewTab
    {
        JOBS(ServerParams.Targets.AVAILABLE),
        SCHEDULE(ServerParams.Targets.FUTURE),
        PROFILE(ServerParams.Targets.PROFILE),
        HELP(ServerParams.Targets.HELP);

        private String target;

        MainViewTab(String target)
        {
            this.target = target;
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
        if (currentTab != tab) //don't transition to same tab, ignore the clicks
        {
            webViewFragment.openPortalUrl(tab.getTarget());
            currentTab = tab;
        }
    }


}
