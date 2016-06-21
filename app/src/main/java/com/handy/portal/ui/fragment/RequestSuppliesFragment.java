package com.handy.portal.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handy.portal.R;
import com.handy.portal.constant.MainViewPage;
import com.handy.portal.event.NavigationEvent;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class RequestSuppliesFragment extends ActionBarFragment
{
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
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        setActionBar(R.string.purchase_cleaning_supplies, false);
        setBackButtonEnabled(true);
    }

    @OnClick(R.id.request_supplies_text)
    public void onRequestSuppliesButtonClicked()
    {
        bus.post(new NavigationEvent.NavigateToPage(MainViewPage.REQUEST_SUPPLIES_WEB_VIEW, true));
    }
}
