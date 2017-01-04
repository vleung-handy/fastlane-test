package com.handy.portal.dashboard.fragment;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.handy.portal.R;
import com.handy.portal.RobolectricGradleTestWrapper;
import com.handy.portal.TestUtils;
import com.handy.portal.core.ui.activity.MainActivity;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;


public class RatingsAndFeedbackFragmentTest extends RobolectricGradleTestWrapper
{
    private RatingsAndFeedbackFragment mFragment;

    @Before
    public void setUp() throws Exception
    {
        mFragment = new RatingsAndFeedbackFragment();
        SupportFragmentTestUtil.startFragment(mFragment, MainActivity.class);
    }

    @Test
    public void testTitleIsCorrect() throws Exception
    {
        assertEquals(mFragment.getString(R.string.ratings_and_feedback),
                ((AppCompatActivity) mFragment.getActivity()).getSupportActionBar().getTitle());
    }

    @Test
    public void testTierNav()
    {
        mFragment.mDashboardOptionsPerformanceView.findViewById(R.id.tier_option).performClick();
        Fragment currentFragment = TestUtils.getScreenFragment(mFragment.getFragmentManager());
        assertThat(currentFragment, instanceOf(DashboardTiersFragment.class));
    }

    @Test
    public void testFeedbackNav()
    {
        mFragment.mDashboardOptionsPerformanceView.findViewById(R.id.feedback_option).performClick();
        Fragment currentFragment = TestUtils.getScreenFragment(mFragment.getFragmentManager());
        assertThat(currentFragment, instanceOf(DashboardFeedbackFragment.class));
    }

    @Test
    public void testReviewsNav()
    {
        mFragment.mDashboardOptionsPerformanceView.findViewById(R.id.reviews_option).performClick();
        Fragment currentFragment = TestUtils.getScreenFragment(mFragment.getFragmentManager());
        assertThat(currentFragment, instanceOf(DashboardReviewsFragment.class));
    }
}
