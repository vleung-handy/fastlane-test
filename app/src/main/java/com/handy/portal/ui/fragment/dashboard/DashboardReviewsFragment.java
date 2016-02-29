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

    private List<ProviderRating> mRatings = new ArrayList<>();
    private ProviderEvaluation mEvaluation;

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

        mEvaluation = (ProviderEvaluation) getArguments().getSerializable(BundleKeys.EVALUATION);
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
        ReviewListAdapter adapter = new ReviewListAdapter(getContext(), mRatings);
        mReviewRecyclerView.setAdapter(adapter);

        if (mEvaluation == null || mEvaluation.getFiveStarRatingsWithComments() == null) { return; }
        mRatings.addAll(mEvaluation.getFiveStarRatingsWithComments());
        adapter.notifyDataSetChanged();
    }
}
