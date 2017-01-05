package com.handy.portal.dashboard.fragment;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.handy.portal.R;
import com.handy.portal.RobolectricGradleTestWrapper;
import com.handy.portal.core.constant.BundleKeys;
import com.handy.portal.core.ui.activity.MainActivity;
import com.handy.portal.data.TestDataManager;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import static org.junit.Assert.assertEquals;

public class DashboardTiersFragmentTest extends RobolectricGradleTestWrapper
{
    private DashboardTiersFragment mFragment;

    @Before
    public void setUp() throws Exception
    {
        mFragment = new DashboardTiersFragment();
        Bundle args = new Bundle();
        args.putSerializable(BundleKeys.PROVIDER_EVALUATION, TestDataManager.createProviderEvaluation());
        mFragment.setArguments(args);
        SupportFragmentTestUtil.startFragment(mFragment, MainActivity.class);
    }

    @Test
    public void shouldShowTitleAndReviews() throws Exception
    {
        assertEquals(mFragment.getString(R.string.tier),
                ((AppCompatActivity) mFragment.getActivity()).getSupportActionBar().getTitle());
    }

}
