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
import com.handy.portal.util.Utils;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FiveStarRatingPercentageView extends FrameLayout
{
    @Inject
    Bus mBus;

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
    private boolean mInitialAnimationDone = false;

    private static int CIRCULAR_GRAPH_ANIMATION_DURATION = 1000;
    private static int PERCENTAGE_TEXT_DELAY_DURATION = 500;
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

    public void setContentColor(int colorId)
    {
        mColor = ContextCompat.getColor(getContext(), colorId);
        mFiveStarPercentageNumber.setTextColor(mColor);
        mFiveStarPercentageSign.setTextColor(mColor);
    }

    public void setupDecoView(int percentage)
    {
        mPercentage = percentage;
        createTracks();

        if (!mInitialAnimationDone)
        {
            animateTrack();
            fadeInPercentageText();
        }
    }

    private void animateTrack()
    {
        mDynamicArcView.addEvent(new DecoEvent.Builder(100)
                .setIndex(mBackIndex)
                .setDuration(CIRCULAR_GRAPH_ANIMATION_DURATION)
                .build());
    }

    private void fadeInPercentageText()
    {
        Animation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(CIRCULAR_GRAPH_ANIMATION_DURATION - PERCENTAGE_TEXT_DELAY_DURATION);
        fadeIn.setStartOffset(PERCENTAGE_TEXT_DELAY_DURATION);
        fadeIn.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(final Animation animation)
            {

            }

            @Override
            public void onAnimationEnd(final Animation animation)
            {
                mInitialAnimationDone = true;
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
        if (mInitialAnimationDone && !mAnimatedView)
        {
            mAnimatedView = true;

            mDynamicArcView.addEvent(new DecoEvent.Builder(0)
                    .setIndex(mSeries1Index)
                    .setDelay(0)
                    .setDuration(1)
                    .build());

            mDynamicArcView.addEvent(new DecoEvent.Builder(mPercentage)
                    .setIndex(mSeries1Index)
                    .setDelay(1)
                    .setDuration(CIRCULAR_GRAPH_ANIMATION_DURATION)
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
        final float seriesMax = 100f;
        mDynamicArcView.configureAngles(360, 0);

        SeriesItem arcBackTrack = new SeriesItem.Builder(ContextCompat.getColor(getContext(), R.color.border_grey))
                .setRange(0, seriesMax, 0)
                .setInitialVisibility(false)
                .setLineWidth(getDimension(CIRCULAR_TRACK_WIDTH))
                .setChartStyle(SeriesItem.ChartStyle.STYLE_DONUT)
                .build();

        mBackIndex = mDynamicArcView.addSeries(arcBackTrack);

        float inset = 0;
        SeriesItem seriesItem1 = new SeriesItem.Builder(mColor)
                .setRange(0, seriesMax, 0)
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

    private float getDimension(float base)
    {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, base, getResources().getDisplayMetrics());
    }
}
