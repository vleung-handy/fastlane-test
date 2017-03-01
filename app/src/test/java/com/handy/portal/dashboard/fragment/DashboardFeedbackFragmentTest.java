package com.handy.portal.dashboard.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.handy.portal.R;
import com.handy.portal.RobolectricGradleTestWrapper;
import com.handy.portal.TestUtils;
import com.handy.portal.core.constant.BundleKeys;
import com.handy.portal.core.ui.activity.MainActivity;
import com.handy.portal.dashboard.model.ProviderEvaluation;
import com.handy.portal.dashboard.model.ProviderFeedback;
import com.handy.portal.data.TestDataManager;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class DashboardFeedbackFragmentTest extends RobolectricGradleTestWrapper {
    private DashboardFeedbackFragment mFragment;

    @Before
    public void setUp() throws Exception {
        mFragment = new DashboardFeedbackFragment();
    }

    @Test
    public void shouldShowTitleAndFeedback() throws Exception {
        Bundle args = new Bundle();
        args.putSerializable(BundleKeys.PROVIDER_EVALUATION, TestDataManager.createProviderEvaluation());
        mFragment.setArguments(args);
        SupportFragmentTestUtil.startFragment(mFragment, MainActivity.class);

        assertEquals(mFragment.getString(R.string.feedback),
                ((AppCompatActivity) mFragment.getActivity()).getSupportActionBar().getTitle());
        assertThat(mFragment.mFeedbackLayout.getChildCount(), greaterThan(0));
        assertEquals(mFragment.mNoResultView.getVisibility(), View.GONE);
    }

    @Test
    public void shouldLaunchVideoLibrary() throws Exception {
        Bundle args = new Bundle();
        args.putSerializable(BundleKeys.PROVIDER_EVALUATION, TestDataManager.createProviderEvaluation());
        mFragment.setArguments(args);
        SupportFragmentTestUtil.startFragment(mFragment, MainActivity.class);

        mFragment.switchToVideoLibrary();

        Fragment currentFragment = TestUtils.getScreenFragment(mFragment.getFragmentManager());
        assertThat(currentFragment, instanceOf(DashboardVideoLibraryFragment.class));
    }

    @Test
    public void shouldShowNoResultViewIfNoFeedback() throws Exception {
        Bundle args = new Bundle();
        ProviderEvaluation evaluation = TestDataManager.createProviderEvaluation();
        when(evaluation.getProviderFeedback()).thenReturn(new ArrayList<ProviderFeedback>());
        args.putSerializable(BundleKeys.PROVIDER_EVALUATION, evaluation);
        mFragment.setArguments(args);
        SupportFragmentTestUtil.startFragment(mFragment, MainActivity.class);

        assertEquals(mFragment.mFeedbackLayout.getChildCount(), 0);
        assertEquals(mFragment.mNoResultView.getVisibility(), View.VISIBLE);
    }
}
