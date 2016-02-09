package com.handy.portal.ui.fragment.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handy.portal.R;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.ui.fragment.ActionBarFragment;

import butterknife.ButterKnife;


public class DashboardReviewsFragment extends ActionBarFragment
{
    @Override
    protected MainViewTab getTab()
    {
        return MainViewTab.DASHBOARD_REVIEWS;
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
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_dashboard_reviews, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        setActionBarTitle(R.string.five_star_reviews);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setBackButtonEnabled(true);
    }
}
