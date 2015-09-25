package com.handy.portal.ui.fragment;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.MenuItem;

import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.util.UIUtils;

/**
 * this is a fragment with action bar support methods
 */
public class ActionBarFragment extends InjectedFragment //TODO: refine. this is a WIP
//TODO: eventually we should use Toolbar with support library instead of ActionBar because it is more flexible
{
    protected UpdateTabsCallback tabsCallback;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null)
        {
            tabsCallback = args.getParcelable(BundleKeys.UPDATE_TAB_CALLBACK);
        }

        // If not give, create one that does nothing to avoid NullPointerException
        if (tabsCallback == null)
        {
            tabsCallback = new UpdateTabsCallback()
            {
                @Override
                public int describeContents()
                {
                    return 0;
                }

                @Override
                public void writeToParcel(Parcel parcel, int i)
                {
                }

                @Override
                public void updateTabs(MainViewTab tab)
                {
                }
            };
        }
    }

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

    public interface UpdateTabsCallback extends Parcelable
    {
        void updateTabs(MainViewTab tab);
    }
}
