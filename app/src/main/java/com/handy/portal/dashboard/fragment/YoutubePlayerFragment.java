package com.handy.portal.dashboard.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.handy.portal.R;
import com.handy.portal.core.constant.BundleKeys;

public class YoutubePlayerFragment extends YouTubePlayerSupportFragment implements YouTubePlayer.OnInitializedListener {
    private String mYoutubeVideoId;

    public static YoutubePlayerFragment newInstance(String youtubeId) {
        YoutubePlayerFragment fragment = new YoutubePlayerFragment();
        Bundle args = new Bundle();
        args.putSerializable(BundleKeys.YOUTUBE_VIDEO_ID, youtubeId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        mYoutubeVideoId = getArguments().getString(BundleKeys.YOUTUBE_VIDEO_ID);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) { getActionBar().hide(); }

        initialize(getString(R.string.google_app_key), this);
    }

    @Override
    public void onInitializationSuccess(final YouTubePlayer.Provider provider, final YouTubePlayer youTubePlayer, final boolean b) {
        youTubePlayer.loadVideo(mYoutubeVideoId);
        youTubePlayer.setShowFullscreenButton(false);
    }

    @Override
    public void onInitializationFailure(final YouTubePlayer.Provider provider, final YouTubeInitializationResult youTubeInitializationResult) {
    }

    @Nullable
    private ActionBar getActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }
}
