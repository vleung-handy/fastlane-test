package com.handy.portal.ui.fragment.dashboard;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.model.dashboard.ProviderEvaluation;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DashboardFeedbackFragment extends YouTubePlayerSupportFragment
{
//    @Inject
//    Bus mBus;

    @Bind(R.id.layout_dashboard_feedback)
    LinearLayout mFeedbackLayout;
    @Bind(R.id.no_result_view)
    ViewGroup mNoResultView;
    @Bind(R.id.no_result_text)
    TextView mNoResultText;

    private ProviderEvaluation mEvaluation;

    private static final String LOG_TAG = DashboardFeedbackFragment.class.getSimpleName();

//    @Override
//    protected MainViewTab getTab()
//    {
//        return MainViewTab.DASHBOARD_REVIEWS;
//    }

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
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(R.string.feedback);
        }

//        if (mEvaluation == null || mEvaluation.getProviderFeedback() == null)
//        {
//            Crashlytics.log("feedback not found in: " + getClass().getSimpleName());
//            return;
//        }
//
//        if (mEvaluation.getProviderFeedback().size() > 0)
//        {
//            mNoResultView.setVisibility(View.GONE);
//            for (ProviderFeedback feedback : mEvaluation.getProviderFeedback())
//            {
//                mFeedbackLayout.addView(new DashboardFeedbackView(getContext(), feedback));
//            }
//        }
//        else
//        {
//            mNoResultView.setVisibility(View.VISIBLE);
//            mNoResultText.setText(R.string.no_feedback);
//        }

//        YouTubePlayerView youTubePlayerView = new YouTubePlayerView(getContext());
        initialize("AIzaSyCKCHTy6QgYdpLXtEpoDs22MI0JnqP8_mc", new YouTubePlayer.OnInitializedListener()
        {
            @Override
            public void onInitializationSuccess(final YouTubePlayer.Provider provider, final YouTubePlayer youTubePlayer, final boolean b)
            {
//                activePlayer = youTubePlayer;
//                activePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                youTubePlayer.loadVideo("tOWJKV4L3pE");
                youTubePlayer.setFullscreen(true);

//                Log.d(LOG_TAG, "SUCCESS");
//                youTubePlayer.loadVideo("https://www.youtube.com/watch?v=tOWJKV4L3pE");
            }

            @Override
            public void onInitializationFailure(final YouTubePlayer.Provider provider, final YouTubeInitializationResult youTubeInitializationResult)
            {
                Log.d(LOG_TAG, "FAILURE");
            }
        });
//        mFeedbackLayout.addView(youTubePlayerView);
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @OnClick(R.id.video_library)
    public void switchToVideoLibrary()
    {
//        mBus.post(new HandyEvent.NavigateToTab(MainViewTab.DASHBOARD_VIDEO_LIBRARY, new Bundle()));
    }


}
