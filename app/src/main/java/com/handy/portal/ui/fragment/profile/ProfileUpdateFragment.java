package com.handy.portal.ui.fragment.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.handy.portal.R;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.ui.fragment.ActionBarFragment;

import butterknife.ButterKnife;

public class ProfileUpdateFragment extends ActionBarFragment
{
    @Override
    protected MainViewTab getTab()
    {
        return MainViewTab.PROFILE_UPDATE;
    }

    @Override
    public void onCreate(Bundle savedInstance)
    {
        super.onCreate(savedInstance);
        setOptionsMenuEnabled(true);
        setActionBar(R.string.edit_your_profile, false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_profile_update_provider_info, container, false);
        ButterKnife.inject(this, view);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.menu_x_back, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_exit:
                onBackButtonPressed();
                return true;
            default:
                return false;
        }
    }
}
