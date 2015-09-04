package com.handy.portal.ui.fragment;

import android.app.ActionBar;
import android.app.Activity;
import android.view.MenuItem;

import com.handy.portal.util.UIUtils;

/**
 * this is a fragment with action bar support methods
 */
public class ActionBarFragment extends InjectedFragment //TODO: refine. this is a WIP
//TODO: eventually we should use Toolbar with support library instead of ActionBar because it is more flexible
{
    public void setActionBarVisible(boolean visible)
    {
        if (visible) getActionBar().show();
        else getActionBar().hide();
    }

    private ActionBar getActionBar()
    {
        return getActivity().getActionBar();
    }

    public void setBackButtonEnabled(boolean enabled)
    {
        getActionBar().setDisplayShowHomeEnabled(enabled);
        getActionBar().setDisplayHomeAsUpEnabled(enabled);
        getActionBar().setHomeButtonEnabled(enabled);

    }

    public void onBackButtonPressed()
    {
        Activity activity = getActivity();
        UIUtils.dismissKeyboard(activity);
        activity.onBackPressed();
    }

    public void setActionBarTitle(int resourceId)
    {
        getActionBar().setTitle(resourceId);
    }

    public void setActionBarTitle(CharSequence charSequence)
    {
        getActionBar().setTitle(charSequence);
    }

    public void setActionBar(int titleStringId, boolean backButtonEnabled)
    {
        setActionBarTitle(titleStringId);
        setBackButtonEnabled(backButtonEnabled);
        setOptionsMenuEnabled(true);
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
}
