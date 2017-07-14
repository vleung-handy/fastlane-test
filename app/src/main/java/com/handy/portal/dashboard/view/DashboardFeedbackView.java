package com.handy.portal.dashboard.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.core.constant.BundleKeys;
import com.handy.portal.core.constant.MainViewPage;
import com.handy.portal.core.manager.PageNavigationManager;
import com.handy.portal.dashboard.model.ProviderFeedback;
import com.handy.portal.library.ui.view.YoutubeImagePlaceholderView;
import com.handy.portal.library.ui.widget.BulletTextView;
import com.handy.portal.library.util.TextUtils;
import com.handy.portal.library.util.Utils;
import com.handy.portal.logger.handylogger.model.FeedbackLog;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DashboardFeedbackView extends FrameLayout implements View.OnClickListener {
    @Inject
    EventBus mBus;
    @Inject
    PageNavigationManager mNavigationManager;

    @BindView(R.id.dashboard_feedback_title)
    TextView mTitle;
    @BindView(R.id.dashboard_feedback_description)
    TextView mDescription;
    @BindView(R.id.dashboard_feedback_tips)
    LinearLayout mTips;

    public DashboardFeedbackView(final Context context, @NonNull final ProviderFeedback providerFeedback) {
        super(context);
        init();
        setDisplay(providerFeedback);
    }

    public DashboardFeedbackView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DashboardFeedbackView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DashboardFeedbackView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.element_dashboard_feedback, this);
        ButterKnife.bind(this);

        Utils.inject(getContext(), this);
    }

    public void setDisplay(@NonNull final ProviderFeedback feedback) {
        String sectionTitle = feedback.getTitle();

        mTitle.setText(sectionTitle);
        mDescription.setText(feedback.getSubtitle());

        if (feedback.getFeedbackTips() == null) { return; }

        for (final ProviderFeedback.FeedbackTip tip : feedback.getFeedbackTips()) {
            if (ProviderFeedback.FeedbackTip.DATA_TYPE_TEXT.equalsIgnoreCase(tip.getDataType())) {
                mTips.addView(new BulletTextView(getContext(), tip.getData()));
            }
            else if (ProviderFeedback.FeedbackTip.DATA_TYPE_VIDEO_ID.equalsIgnoreCase(tip.getDataType())) {
                if (!TextUtils.isNullOrEmpty(tip.getData())) {
                    YoutubeImagePlaceholderView youtubeImagePlaceholderView =
                            new YoutubeImagePlaceholderView(getContext());
                    youtubeImagePlaceholderView.setID(tip.getData());
                    youtubeImagePlaceholderView.setSection(sectionTitle);

                    youtubeImagePlaceholderView.setOnClickListener(this);

                    mTips.addView(youtubeImagePlaceholderView);
                }
            }
        }
    }

    @Override
    public void onClick(final View v) {
        YoutubeImagePlaceholderView view = (YoutubeImagePlaceholderView) v;
        Bundle bundle = new Bundle();
        bundle.putString(BundleKeys.YOUTUBE_VIDEO_ID, view.getID());

        mBus.post(new FeedbackLog.VideoSelected(view.getSection()));
        if (getContext() instanceof AppCompatActivity) {
            mNavigationManager.navigateToPage(((AppCompatActivity) getContext()).getSupportFragmentManager(),
                    MainViewPage.YOUTUBE_PLAYER, bundle, null, false);
        }
    }
}
