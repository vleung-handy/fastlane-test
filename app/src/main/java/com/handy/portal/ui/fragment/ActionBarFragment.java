package com.handy.portal.ui.fragment;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.handy.portal.BuildConfig;
import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.core.EnvironmentModifier;
import com.handy.portal.util.UIUtils;

import javax.inject.Inject;

//TODO: eventually we should use Toolbar with support library instead of ActionBar because it is more flexible
public abstract class ActionBarFragment extends InjectedFragment
{
    @Inject
    EnvironmentModifier environmentModifier;

    private UpdateTabsCallback tabsCallback;

    abstract MainViewTab getTab();

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null)
        {
            tabsCallback = args.getParcelable(BundleKeys.UPDATE_TAB_CALLBACK);
        }

        // If not given, create one that does nothing to avoid NullPointerException
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

    @Override
    public void onResume()
    {
        super.onResume();
        if (getTab() != null)
        {
            tabsCallback.updateTabs(getTab());
        }
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
        Parcelable.Creator CREATOR = new Parcelable.Creator()
        {
            @Override
            public Object createFromParcel(Parcel source)
            {
                return null;
            }

            @Override
            public Object[] newArray(int size)
            {
                return new Object[0];
            }
        };

        void updateTabs(MainViewTab tab);
    }
}
