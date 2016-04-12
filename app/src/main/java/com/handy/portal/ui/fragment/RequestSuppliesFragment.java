package com.handy.portal.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handy.portal.R;
import com.handy.portal.constant.MainViewTab;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class RequestSuppliesFragment extends ActionBarFragment
{
    @Override
    protected MainViewTab getTab()
    {
        return MainViewTab.REQUEST_SUPPLIES;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setOptionsMenuEnabled(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_request_supplies, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setActionBar(R.string.request_supplies, false);
        setBackButtonEnabled(true);
    }
    @OnClick(R.id.request_supplies_button)
    public void onRequestSuppliesButtonClicked()
    {

    }
}
