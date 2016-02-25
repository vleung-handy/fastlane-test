package com.handy.portal.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handy.portal.R;
import com.handy.portal.constant.MainViewTab;

import butterknife.ButterKnife;

public class ReferAFriendFragment extends ActionBarFragment
{

    @Override
    protected MainViewTab getTab()
    {
        return MainViewTab.REFER_A_FRIEND;
    }

    @Nullable
    @Override
    public View onCreateView(
            final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState
    )
    {
        final View view = inflater.inflate(R.layout.fragment_refer_a_friend, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setActionBar(R.string.refer_a_friend, false);
    }
}
