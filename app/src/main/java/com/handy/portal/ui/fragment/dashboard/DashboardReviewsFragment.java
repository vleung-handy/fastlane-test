package com.handy.portal.ui.fragment.dashboard;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.model.dashboard.ProviderEvaluation;
import com.handy.portal.model.dashboard.ProviderRating;
import com.handy.portal.ui.adapter.ReviewListAdapter;
import com.handy.portal.ui.fragment.ActionBarFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DashboardReviewsFragment extends ActionBarFragment
{
    @Bind(R.id.reviews_list)
    RecyclerView mReviewRecyclerView;

    private ReviewListAdapter mAdapter;
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

        ProviderEvaluation evaluation = (ProviderEvaluation) getArguments().getSerializable(BundleKeys.EVALUATION);
        if (evaluation == null || evaluation.getFiveStarRatings() == null) { return; }
        mRatings.addAll(evaluation.getFiveStarRatings());
        mAdapter.notifyDataSetChanged();
    }
}
