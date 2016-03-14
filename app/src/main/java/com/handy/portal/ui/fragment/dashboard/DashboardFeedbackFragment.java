package com.handy.portal.ui.fragment.dashboard;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.model.dashboard.ProviderEvaluation;
import com.handy.portal.model.dashboard.ProviderFeedback;
import com.handy.portal.ui.element.dashboard.DashboardFeedbackView;
import com.handy.portal.ui.fragment.ActionBarFragment;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DashboardFeedbackFragment extends ActionBarFragment
{
    @Inject
    Bus mBus;

    @Bind(R.id.layout_dashboard_feedback)
    LinearLayout mFeedbackLayout;
    @Bind(R.id.no_result_view)
    ViewGroup mNoResultView;
    @Bind(R.id.no_result_text)
    TextView mNoResultText;

    private ProviderEvaluation mEvaluation;

    @Override
    protected MainViewTab getTab()
    {
        return MainViewTab.DASHBOARD_REVIEWS;
    }

    public static DashboardFeedbackFragment newInstance(ProviderEvaluation providerEvaluation)
    {
        DashboardFeedbackFragment fragment = new DashboardFeedbackFragment();
        Bundle args = new Bundle();
        args.putSerializable(BundleKeys.EVALUATION, providerEvaluation);
        fragment.setArguments(args);
        return fragment;
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
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_dashboard_feedback, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        setHasOptionsMenu(true);
        setMenuVisibility(true);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null)
        {
            // Need to show action bar because we are hiding it in youtube player fragment
            actionBar.show();

            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(R.string.feedback);
        }

        if (mEvaluation == null || mEvaluation.getProviderFeedback() == null)
        {
            Crashlytics.log("feedback not found in: " + getClass().getSimpleName());
            return;
        }

        if (mEvaluation.getProviderFeedback().size() > 0)
        {
            mNoResultView.setVisibility(View.GONE);
            for (ProviderFeedback feedback : mEvaluation.getProviderFeedback())
            {
                mFeedbackLayout.addView(new DashboardFeedbackView(getContext(), feedback));
            }
        }
        else
        {
            mNoResultView.setVisibility(View.VISIBLE);
            mNoResultText.setText(R.string.no_feedback);
        }
    }

    @OnClick(R.id.video_library)
    public void switchToVideoLibrary()
    {
        mBus.post(new HandyEvent.NavigateToTab(MainViewTab.DASHBOARD_VIDEO_LIBRARY, new Bundle()));
    }

    public void swapToVideo(String youtubeId)
    {
        Bundle arguments = new Bundle();
        arguments.putSerializable(BundleKeys.YOUTUBE_ID, youtubeId);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        YoutubePlayerFragment fragment = YoutubePlayerFragment.newInstance(youtubeId);
        transaction.replace(R.id.main_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
