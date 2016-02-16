package com.handy.portal.ui.fragment.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.ProviderDashboardEvent;
import com.handy.portal.model.dashboard.ProviderFeedback;
import com.handy.portal.ui.element.dashboard.DashboardFeedbackView;
import com.handy.portal.ui.fragment.ActionBarFragment;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class DashboardFeedbackFragment extends ActionBarFragment
{
    @Bind(R.id.layout_dashboard_feedback)
    LinearLayout mFeedbackLayout;
    @Bind(R.id.fetch_error_view)
    View mFetchErrorView;
    @Bind(R.id.fetch_error_text)
    TextView mFetchErrorTextView;

    private List<ProviderFeedback> mProviderFeedback = new ArrayList<>();

    @Override
    protected MainViewTab getTab()
    {
        return MainViewTab.DASHBOARD_REVIEWS;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setActionBarTitle(R.string.feedback);
        setOptionsMenuEnabled(true);
        setBackButtonEnabled(true);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        getProviderFeedback();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_dashboard_feedback, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    @Subscribe
    public void onReceiveProviderFeedbackSuccess(ProviderDashboardEvent.ReceiveProviderFeedbackSuccess event)
    {
        mFetchErrorView.setVisibility(View.GONE);
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));

        mProviderFeedback = event.providerFeedback;
        for (ProviderFeedback feedback : mProviderFeedback)
        {
            mFeedbackLayout.addView(new DashboardFeedbackView(getContext(), feedback));
        }
    }

    @Subscribe
    public void onReceiveProviderFeedbackFailure(ProviderDashboardEvent.ReceiveProviderFeedbackError event)
    {
        if (event.error != null && event.error.getType() == DataManager.DataManagerError.Type.NETWORK)
        {
            mFetchErrorTextView.setText(R.string.error_fetching_connectivity_issue);
        }
        else
        {
            mFetchErrorTextView.setText(R.string.error_dashboard_feedback);
        }
        mFetchErrorView.setVisibility(View.VISIBLE);
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
    }

    @OnClick(R.id.try_again_button)
    public void getProviderFeedback(){
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        bus.post(new ProviderDashboardEvent.RequestProviderFeedback());
    }
}
