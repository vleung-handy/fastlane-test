package com.handy.portal.ui.element.dashboard;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.logger.handylogger.EventLogFactory;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.model.dashboard.ProviderFeedback;
import com.handy.portal.ui.view.YoutubeImagePlaceholderView;
import com.handy.portal.ui.widget.BulletTextView;
import com.handy.portal.util.TextUtils;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DashboardFeedbackView extends FrameLayout implements View.OnClickListener
{
    @Inject
    Bus mBus;
    @Inject
    EventLogFactory mEventLogFactory;

    @Bind(R.id.dashboard_feedback_title)
    TextView mTitle;
    @Bind(R.id.dashboard_feedback_description)
    TextView mDescription;
    @Bind(R.id.dashboard_feedback_tips)
    LinearLayout mTips;

    public DashboardFeedbackView(final Context context, @NonNull final ProviderFeedback providerFeedback)
    {
        super(context);
        init();
        setDisplay(providerFeedback);
    }

    public DashboardFeedbackView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public DashboardFeedbackView(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DashboardFeedbackView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init()
    {
        inflate(getContext(), R.layout.element_dashboard_feedback, this);
        ButterKnife.bind(this);
    }

    public void setDisplay(@NonNull final ProviderFeedback feedback)
    {
        mTitle.setText(feedback.getTitle());
        mDescription.setText(feedback.getSubtitle());

        if (feedback.getFeedbackTips() == null) { return; }

        for (final ProviderFeedback.FeedbackTip tip : feedback.getFeedbackTips())
        {
            if (ProviderFeedback.FeedbackTip.DATA_TYPE_TEXT.equalsIgnoreCase(tip.getDataType()))
            {
                mTips.addView(new BulletTextView(getContext(), tip.getData()));
            }
            else if (ProviderFeedback.FeedbackTip.DATA_TYPE_VIDEO_ID.equalsIgnoreCase(tip.getDataType()))
            {
                if (!TextUtils.isNullOrEmpty(tip.getData()))
                {
                    YoutubeImagePlaceholderView youtubeImagePlaceholderView =
                            new YoutubeImagePlaceholderView(getContext());
                    youtubeImagePlaceholderView.setVideoTitle(feedback.getTitle());
                    youtubeImagePlaceholderView.setID(tip.getData());

                    youtubeImagePlaceholderView.setOnClickListener(this);

                    mTips.addView(youtubeImagePlaceholderView);
                }
            }
        }
    }

    @Override
    public void onClick(final View v)
    {
        YoutubeImagePlaceholderView youtubeImagePlaceholderView = (YoutubeImagePlaceholderView) v;
        mBus.post(new LogEvent.AddLogEvent(mEventLogFactory.createVideoClickedLog(youtubeImagePlaceholderView.getVideoTitle())));

        final Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(youtubeImagePlaceholderView.getURL()));
        getContext().startActivity(intent);
    }
}
