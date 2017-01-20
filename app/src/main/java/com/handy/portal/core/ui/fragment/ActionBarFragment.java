package com.handy.portal.core.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.handy.portal.BuildConfig;
import com.handy.portal.R;
import com.handy.portal.core.EnvironmentModifier;
import com.handy.portal.core.constant.MainViewPage;
import com.handy.portal.core.event.HandyEvent;
import com.handy.portal.core.event.NavigationEvent;
import com.handy.portal.library.ui.fragment.InjectedFragment;
import com.handy.portal.library.util.EnvironmentUtils;
import com.handy.portal.library.util.UIUtils;

import javax.inject.Inject;

public abstract class ActionBarFragment extends InjectedFragment
{
    @Inject
    EnvironmentModifier environmentModifier;
    private Menu mMenu;

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        setActionBarVisible(true);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        bus.post(new NavigationEvent.SelectPage(getAppPage()));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        if (BuildConfig.DEBUG)
        {
            inflater.inflate(R.menu.menu_main, menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);
        final MenuItem environmentModifierMenuItem = menu.findItem(R.id.action_settings);
        if (environmentModifierMenuItem != null)
        {
            setEnvironmentMenuItemTitle(environmentModifierMenuItem);
            environmentModifierMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener()
            {
                @Override
                public boolean onMenuItemClick(MenuItem item)
                {
                    EnvironmentUtils.showEnvironmentModifierDialog(environmentModifier, getActivity(), new EnvironmentModifier.OnEnvironmentChangedListener()
                    {
                        @Override
                        public void onEnvironmentChanged(String newEnvironmentPrefix)
                        {
                            setEnvironmentMenuItemTitle(environmentModifierMenuItem);
                            bus.post(new HandyEvent.LogOutProvider());
                        }
                    });
                    return true;
                }
            });
        }
        mMenu = menu;
    }

    public void setEnvironmentMenuItemTitle(final MenuItem environmentMenuItemTitle)
    {
        String title;
        final EnvironmentModifier.Environment environment = environmentModifier.getEnvironment();
        if (environment == EnvironmentModifier.Environment.Q)
        {
            title = environmentModifier.getEnvironmentPrefix().toUpperCase();
        }
        else
        {
            title = environment.name().toUpperCase();
        }
        environmentMenuItemTitle.setTitle(title);
    }

    public void setActionBarVisible(boolean visible)
    {
        ActionBar actionBar = getActionBar();
        if (actionBar != null)
        {
            if (visible) { actionBar.show(); }
            else { actionBar.hide(); }
        }
    }

    protected ActionBar getActionBar()
    {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    public void setBackButtonEnabled(boolean enabled)
    {
        ActionBar actionBar = getActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayShowHomeEnabled(enabled);
            actionBar.setDisplayHomeAsUpEnabled(enabled);
            actionBar.setHomeButtonEnabled(enabled);
        }
    }

    public void onBackButtonPressed()
    {
        Activity activity = getActivity();
        UIUtils.dismissKeyboard(activity);
        activity.onBackPressed();
    }

    public void setActionBarTitle(int resourceId)
    {
        ActionBar actionBar = getActionBar();
        if (actionBar != null)
        { actionBar.setTitle(resourceId); }
    }

    public void setActionBarTitle(CharSequence charSequence)
    {
        ActionBar actionBar = getActionBar();
        if (actionBar != null)
        { actionBar.setTitle(charSequence); }
    }

    public void setActionBar(String titleString, boolean backButtonEnabled)
    {
        setActionBarTitle(titleString);
        setBackButtonEnabled(backButtonEnabled);
        setOptionsMenuEnabled(true);
    }

    public void setActionBar(int titleStringId, boolean backButtonEnabled)
    {
        setActionBar(getResources().getString(titleStringId), backButtonEnabled);
    }

    public void setOptionsMenuEnabled(boolean enabled)
    {
        setHasOptionsMenu(enabled);
        setMenuVisibility(enabled);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) //default support for back button
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackButtonPressed();
                return true;
            default:
                return false;
        }
    }

    protected MainViewPage getAppPage() { return null; }

    protected Menu getMenu()
    {
        return mMenu;
    }
}
