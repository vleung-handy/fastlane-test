package com.handy.portal.ui.fragment.dashboard;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.event.ProviderDashboardEvent;
import com.handy.portal.model.dashboard.ProviderEvaluation;
import com.handy.portal.model.dashboard.ProviderRating;
import com.handy.portal.ui.adapter.ReviewListAdapter;
import com.handy.portal.ui.fragment.ActionBarFragment;
import com.handy.portal.ui.listener.EndlessRecyclerViewScrollListener;
import com.handy.portal.util.DateTimeUtils;
import com.handy.portal.util.Utils;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DashboardReviewsFragment extends ActionBarFragment
{
    @Bind(R.id.reviews_list)
    RecyclerView mReviewRecyclerView;
    @Bind(R.id.no_result_view)
    ViewGroup mNoResultView;
    @Bind(R.id.no_result_text)
    TextView mNoResultText;

    private static int MIN_STAR = 5;
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

        setOptionsMenuEnabled(true);
        setBackButtonEnabled(true);

        mReviewRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mReviewRecyclerView.setLayoutManager(linearLayoutManager);
        ReviewListAdapter adapter = new ReviewListAdapter(mRatings);
        mReviewRecyclerView.setAdapter(adapter);
        mReviewRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager)
        {
            @Override
            public void onLoadMore(int page, int totalItemsCount)
            {
                if (mEvaluation != null &&
                        mEvaluation.getFiveStarRatingsWithComments() != null &&
                        !mEvaluation.getFiveStarRatingsWithComments().isEmpty())
                {

                    Date toBookingDate = ((ReviewListAdapter) mReviewRecyclerView.getAdapter()).getToBookingDate();
                    String toBookingDateString = DateTimeUtils.formatIso8601(toBookingDate);
                    Log.d("to_booking_date", toBookingDateString);
                    bus.post(new ProviderDashboardEvent.RequestProviderFiveStarRatings(MIN_STAR, toBookingDateString));
                }
            }
        });

        if (mEvaluation == null || mEvaluation.getFiveStarRatingsWithComments() == null
                || mEvaluation.getFiveStarRatingsWithComments().isEmpty())
        {
            mNoResultView.setVisibility(View.VISIBLE);
            mNoResultText.setText(R.string.no_reviews);
        }
        else
        {
            mNoResultView.setVisibility(View.GONE);
            mRatings.addAll(mEvaluation.getFiveStarRatingsWithComments());
            adapter.notifyDataSetChanged();
        }
    }

    @Subscribe
    public void onReceiveProviderFiveStarRatingsSuccess(ProviderDashboardEvent.ReceiveProviderFiveStarRatingsSuccess event)
    {
        for (ProviderRating providerRating : event.getProviderRatings())
        {
            mRatings.add(providerRating);
        }

        mReviewRecyclerView.getAdapter().notifyDataSetChanged();
    }
}
