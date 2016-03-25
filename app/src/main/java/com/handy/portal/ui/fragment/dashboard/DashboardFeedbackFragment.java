package com.handy.portal.ui.fragment.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.event.NavigationEvent;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.model.dashboard.ProviderEvaluation;
import com.handy.portal.model.dashboard.ProviderFeedback;
import com.handy.portal.model.logs.FeedbackLog;
import com.handy.portal.ui.element.dashboard.DashboardFeedbackView;
import com.handy.portal.ui.fragment.ActionBarFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DashboardFeedbackFragment extends ActionBarFragment
{
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

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mEvaluation = (ProviderEvaluation) getArguments().getSerializable(BundleKeys.PROVIDER_EVALUATION);
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

        setOptionsMenuEnabled(true);
        setBackButtonEnabled(true);
        setActionBarTitle(R.string.feedback);
        setActionBarVisible(true);

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
        bus.post(new LogEvent.AddLogEvent(new FeedbackLog.VideoLibrarySelected()));
        bus.post(new NavigationEvent.NavigateToTab(MainViewTab.DASHBOARD_VIDEO_LIBRARY));
    }

    public void swapToVideo(String youtubeId)
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable(BundleKeys.YOUTUBE_VIDEO_ID, youtubeId);
        bus.post(new NavigationEvent.NavigateToTab(MainViewTab.YOUTUBE_PLAYER, bundle));
    }
}