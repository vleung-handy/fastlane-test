package com.handy.portal.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.handy.portal.BuildConfig;
import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.core.EnvironmentModifier;
import com.handy.portal.util.UIUtils;

import java.io.Serializable;

import javax.inject.Inject;

public abstract class ActionBarFragment extends InjectedFragment
{
    @Inject
    EnvironmentModifier environmentModifier;
    private UpdateTabsCallback tabsCallback;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null)
        {
            tabsCallback = (UpdateTabsCallback) args.getSerializable(BundleKeys.UPDATE_TAB_CALLBACK);
        }

        // If not given, create one that does nothing to avoid NullPointerException
        if (tabsCallback == null)
        {
            tabsCallback = new UpdateTabsCallback()
            {
                @Override
                public void updateTabs(MainViewTab tab) { }
            };
        }
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        setActionBarVisible(true);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        tabsCallback.updateTabs(getTab());
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
            environmentModifierMenuItem.setTitle(environmentModifier.getEnvironmentPrefix().toUpperCase());
            environmentModifierMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener()
            {
                @Override
                public boolean onMenuItemClick(MenuItem item)
                {
                    UIUtils.createEnvironmentModifierDialog(environmentModifier, getActivity(), new EnvironmentModifier.OnEnvironmentChangedListener()
                    {
                        @Override
                        public void onEnvironmentChanged(String newEnvironmentPrefix)
                        {
                            environmentModifierMenuItem.setTitle(newEnvironmentPrefix.toUpperCase());
                        }
                    }).show();
                    return true;
                }
            });
        }
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

    private ActionBar getActionBar()
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

    public void invalidateOptionsMenu()
    {
        getActivity().invalidateOptionsMenu();
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

    protected MainViewTab getTab() { return null; }

    public interface UpdateTabsCallback extends Serializable
    {
        void updateTabs(@Nullable MainViewTab tab);
    }
}
