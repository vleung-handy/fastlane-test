package com.handy.portal.ui.fragment.dashboard;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.ProviderDashboardEvent;
import com.handy.portal.model.dashboard.ProviderRating;
import com.handy.portal.ui.adapter.ReviewListAdapter;
import com.handy.portal.ui.fragment.ActionBarFragment;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class DashboardReviewsFragment extends ActionBarFragment
{
    @Bind(R.id.fetch_error_view)
    View mFetchErrorView;
    @Bind(R.id.fetch_error_text)
    TextView mFetchErrorTextView;
    @Bind(R.id.reviews_list)
    RecyclerView mReviewRecyclerView;

    private RecyclerView.Adapter mAdapter;
    private List<ProviderRating> mRatings = new ArrayList<>();

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
        setBackButtonEnabled(true);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        getProviderReviews();
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

        mReviewRecyclerView.setHasFixedSize(true);
        mReviewRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new ReviewListAdapter(getContext(), mRatings);

        mReviewRecyclerView.setAdapter(mAdapter);
    }

    @Subscribe
    public void onReceiveProviderFiveStarRatingsSuccess(ProviderDashboardEvent.ReceiveProviderFiveStarRatingsSuccess event)
    {
        mReviewRecyclerView.setVisibility(View.VISIBLE);
        mFetchErrorView.setVisibility(View.GONE);
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));

        mRatings.addAll(event.getProviderRatings());
        mAdapter.notifyDataSetChanged();
    }

    @Subscribe
    public void onReceiveProviderFiveStarRatingsFailure(ProviderDashboardEvent.ReceiveProviderFiveStarRatingsError event)
    {
        mReviewRecyclerView.setVisibility(View.GONE);
        mFetchErrorView.setVisibility(View.VISIBLE);
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));

        if (event.error != null && event.error.getType() == DataManager.DataManagerError.Type.NETWORK)
        {
            mFetchErrorTextView.setText(R.string.error_fetching_connectivity_issue);
        }
        else
        {
            mFetchErrorTextView.setText(R.string.error_dashboard_reviews);
        }
    }

    @OnClick(R.id.try_again_button)
    public void getProviderReviews()
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        bus.post(new ProviderDashboardEvent.RequestProviderFiveStarRatings());
    }
}
