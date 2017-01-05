package com.handy.portal.dashboard.fragment;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.handy.portal.R;
import com.handy.portal.RobolectricGradleTestWrapper;
import com.handy.portal.core.constant.BundleKeys;
import com.handy.portal.core.ui.activity.MainActivity;
import com.handy.portal.data.TestDataManager;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class DashboardReviewsFragmentTest extends RobolectricGradleTestWrapper
{
    private DashboardReviewsFragment mFragment;

    @Before
    public void setUp() throws Exception
    {
        mFragment = new DashboardReviewsFragment();
    }

    @Test
    public void shouldShowTitleAndReviews() throws Exception
    {
        Bundle args = new Bundle();
        args.putSerializable(BundleKeys.PROVIDER_EVALUATION, TestDataManager.createProviderEvaluation());
        mFragment.setArguments(args);
        SupportFragmentTestUtil.startFragment(mFragment, MainActivity.class);

        assertEquals(mFragment.getString(R.string.five_star_reviews),
                ((AppCompatActivity) mFragment.getActivity()).getSupportActionBar().getTitle());
        assertThat(mFragment.mReviewRecyclerView.getAdapter().getItemCount(), greaterThan(0));
        assertEquals(mFragment.mNoResultView.getVisibility(), View.GONE);
    }

    @Test
    public void shouldShowNoResultViewIfNoRating() throws Exception
    {
        Bundle args = new Bundle();
        args.putSerializable(BundleKeys.PROVIDER_EVALUATION, TestDataManager.createProviderEvaluation());
        mFragment.setArguments(args);
        SupportFragmentTestUtil.startFragment(mFragment, MainActivity.class);

        assertEquals(mFragment.getString(R.string.five_star_reviews),
                ((AppCompatActivity) mFragment.getActivity()).getSupportActionBar().getTitle());
        assertThat(mFragment.mReviewRecyclerView.getAdapter().getItemCount(), greaterThan(0));
        assertEquals(mFragment.mNoResultView.getVisibility(), View.GONE);
    }
}
