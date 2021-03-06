package com.handy.portal.dashboard.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;
import com.handy.portal.core.constant.BundleKeys;
import com.handy.portal.core.constant.MainViewPage;
import com.handy.portal.core.manager.PageNavigationManager;
import com.handy.portal.core.ui.fragment.ActionBarFragment;
import com.handy.portal.dashboard.model.ProviderEvaluation;
import com.handy.portal.dashboard.model.ProviderFeedback;
import com.handy.portal.dashboard.view.DashboardFeedbackView;
import com.handy.portal.logger.handylogger.model.FeedbackLog;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DashboardFeedbackFragment extends ActionBarFragment {
    @Inject
    PageNavigationManager mNavigationManager;

    @BindView(R.id.layout_dashboard_feedback)
    LinearLayout mFeedbackLayout;
    @BindView(R.id.no_result_view)
    ViewGroup mNoResultView;
    @BindView(R.id.no_result_text)
    TextView mNoResultText;

    private ProviderEvaluation mEvaluation;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEvaluation = (ProviderEvaluation) getArguments().getSerializable(BundleKeys.PROVIDER_EVALUATION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_dashboard_feedback, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        setOptionsMenuEnabled(true);
        setBackButtonEnabled(true);
        setActionBarTitle(R.string.feedback);
        setActionBarVisible(true);

        if (mEvaluation == null || mEvaluation.getProviderFeedback() == null) {
            Crashlytics.log("feedback not found in: " + getClass().getSimpleName());
            return;
        }

        if (mEvaluation.getProviderFeedback().size() > 0) {
            mNoResultView.setVisibility(View.GONE);
            for (ProviderFeedback feedback : mEvaluation.getProviderFeedback()) {
                mFeedbackLayout.addView(new DashboardFeedbackView(getContext(), feedback));
            }
        }
        else {
            mNoResultView.setVisibility(View.VISIBLE);
            mNoResultText.setText(R.string.no_feedback);
        }
    }

    @OnClick(R.id.video_library)
    public void switchToVideoLibrary() {
        bus.post(new FeedbackLog.VideoLibrarySelected());
        mNavigationManager.navigateToPage(getActivity().getSupportFragmentManager(),
                MainViewPage.DASHBOARD_VIDEO_LIBRARY, null, null, true);
    }

    public void swapToVideo(String youtubeId) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(BundleKeys.YOUTUBE_VIDEO_ID, youtubeId);
        mNavigationManager.navigateToPage(getActivity().getSupportFragmentManager(),
                MainViewPage.YOUTUBE_PLAYER, bundle, null, false);
    }
}
