package com.handy.portal.ui.element.dashboard;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.PointF;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.event.ProviderDashboardEvent;
import com.handy.portal.library.util.Utils;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FiveStarRatingPercentageView extends FrameLayout
{
    @Inject
    EventBus mBus;

    @Bind(R.id.five_star_percentage_number)
    TextView mFiveStarPercentageNumber;
    @Bind(R.id.five_star_percentage_sign)
    TextView mFiveStarPercentageSign;
    @Bind(R.id.dynamicArcView)
    DecoView mDynamicArcView;
    @Bind(R.id.five_star_percentage_info_wrapper)
    RelativeLayout mFiveStarPercentageInfoWrapper;

    private int mColor;
    private int mPercentage;
    private int mBackIndex;
    private int mSeries1Index;
    private boolean mAnimatedView = false;

    private static int CIRCULAR_GRAPH_ANIMATION_DURATION_MILLIS = 1000;
    private static int PERCENTAGE_TEXT_DELAY_DURATION_MILLIS = 500;
    private static float MAX_GRAPH_VALUE = 100f;
    private static float CIRCULAR_TRACK_WIDTH = 8f;

    public FiveStarRatingPercentageView(final Context context)
    {
        super(context);
        init();
    }

    public FiveStarRatingPercentageView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public FiveStarRatingPercentageView(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FiveStarRatingPercentageView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void setContentColor(int colorResourceId)
    {
        mColor = ContextCompat.getColor(getContext(), colorResourceId);
        mFiveStarPercentageNumber.setTextColor(mColor);
        mFiveStarPercentageSign.setTextColor(mColor);
    }

    public void setupDecoView(int percentage)
    {
        mPercentage = percentage;
        createTracks();
    }

    public void startAnimation()
    {
        animateTrack();
        fadeInPercentageText();
    }

    public void setOnResumeState()
    {
        mAnimatedView = true;

        mFiveStarPercentageInfoWrapper.setVisibility(View.VISIBLE);

        mDynamicArcView.addEvent(new DecoEvent.Builder(MAX_GRAPH_VALUE)
                .setIndex(mBackIndex)
                .setDuration(0)
                .build());

        mDynamicArcView.addEvent(new DecoEvent.Builder(mPercentage)
                .setIndex(mSeries1Index)
                .setDuration(0)
                .build());
    }

    private void animateTrack()
    {
        mDynamicArcView.addEvent(new DecoEvent.Builder(MAX_GRAPH_VALUE)
                .setIndex(mBackIndex)
                .setDuration(CIRCULAR_GRAPH_ANIMATION_DURATION_MILLIS)
                .build());
    }

    private void fadeInPercentageText()
    {
        Animation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(CIRCULAR_GRAPH_ANIMATION_DURATION_MILLIS - PERCENTAGE_TEXT_DELAY_DURATION_MILLIS);
        fadeIn.setStartOffset(PERCENTAGE_TEXT_DELAY_DURATION_MILLIS);
        fadeIn.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(final Animation animation)
            {

            }

            @Override
            public void onAnimationEnd(final Animation animation)
            {
                mFiveStarPercentageInfoWrapper.setVisibility(View.VISIBLE);
                mBus.post(new ProviderDashboardEvent.AnimateFiveStarPercentageGraph());
            }

            @Override
            public void onAnimationRepeat(final Animation animation)
            {

            }
        });

        mFiveStarPercentageInfoWrapper.startAnimation(fadeIn);
    }

    public void animateProgressBar()
    {
        if (!mAnimatedView)
        {
            mAnimatedView = true;

            mDynamicArcView.addEvent(new DecoEvent.Builder(mPercentage)
                    .setIndex(mSeries1Index)
                    .setDuration(CIRCULAR_GRAPH_ANIMATION_DURATION_MILLIS)
                    .build());
        }
    }

    private void init()
    {
        Utils.inject(getContext(), this);
        inflate(getContext(), R.layout.element_five_star_rating_percentage, this);
        ButterKnife.bind(this);
    }

    /*
     Create the tracks for the circular graphs to animate along
    */
    private void createTracks()
    {
        mDynamicArcView.configureAngles(360, 0);

        SeriesItem arcBackTrack = new SeriesItem.Builder(ContextCompat.getColor(getContext(), R.color.border_gray))
                .setRange(0, MAX_GRAPH_VALUE, 0)
                .setInitialVisibility(false)
                .setLineWidth(getDimension(CIRCULAR_TRACK_WIDTH))
                .setChartStyle(SeriesItem.ChartStyle.STYLE_DONUT)
                .build();

        mBackIndex = mDynamicArcView.addSeries(arcBackTrack);

        float inset = 0;
        SeriesItem seriesItem1 = new SeriesItem.Builder(mColor)
                .setRange(0, MAX_GRAPH_VALUE, 0)
                .setInitialVisibility(false)
                .setCapRounded(true)
                .setLineWidth(getDimension(CIRCULAR_TRACK_WIDTH))
                .setInset(new PointF(inset, inset))
                .build();

        seriesItem1.addArcSeriesItemListener(new SeriesItem.SeriesItemListener()
        {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition)
            {
                mFiveStarPercentageNumber.setText(String.valueOf((int) currentPosition));
            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete)
            {

            }
        });

        mSeries1Index = mDynamicArcView.addSeries(seriesItem1);
    }

    /**
     * Convert base dip into pixels based on the display metrics of the current device
     *
     * @param base dip value
     * @return pixels from base dip
     */
    private float getDimension(float base)
    {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, base, getResources().getDisplayMetrics());
    }
}
