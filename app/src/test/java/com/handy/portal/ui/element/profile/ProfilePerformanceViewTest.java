package com.handy.portal.ui.element.profile;


import android.app.Application;
import android.widget.TextView;

import com.handy.portal.BuildConfig;
import com.handy.portal.R;
import com.handy.portal.model.PerformanceInfo;
import com.handy.portal.ui.constructor.ProfilePerformanceView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class,
        packageName = "com.handy.portal",
        sdk = 19)
public class ProfilePerformanceViewTest
{
    private static final Application APP = RuntimeEnvironment.application;
    private ProfilePerformanceView mProfilePerformanceView;
    private PerformanceInfo mPerformanceInfo;
    private float mTrailing28DayRating;
    private int mTrailing28DayJobsCount;
    private String mRate;

    @Before
    public void setUp() throws Exception
    {
        mTrailing28DayRating = 4.50f;
        mTrailing28DayJobsCount = 20;
        mRate = "45";
        mProfilePerformanceView = new ProfilePerformanceView(APP, buildPerformanceInfo());
    }

    @Test
    public void shouldHaveTrailingRating()
    {
        TextView trailingRatingText = (TextView) mProfilePerformanceView.findViewById(R.id.trailing_rating_text);
        assertEquals(mPerformanceInfo.getTrailing28DayRating(), Float.parseFloat(trailingRatingText.getText().toString())
                , 0.01);
    }

    @Test
    public void shouldHaveTrailingJobs()
    {
        TextView trailingJobsText = (TextView) mProfilePerformanceView.findViewById(R.id.trailing_jobs_text);
        assertEquals(mPerformanceInfo.getTrailing28DayJobsCount(), Integer.parseInt(trailingJobsText.getText().toString()));
    }

    @Test
    public void shouldHaveTrailingRate()
    {
        TextView trailingRateText = (TextView) mProfilePerformanceView.findViewById(R.id.trailing_rate_text);
        assertEquals(mPerformanceInfo.getRate(), trailingRateText.getText());
    }

    private PerformanceInfo buildPerformanceInfo()
    {
        mPerformanceInfo = mock(PerformanceInfo.class);
        when(mPerformanceInfo.getTrailing28DayRating()).thenReturn(mTrailing28DayRating);
        when(mPerformanceInfo.getTrailing28DayJobsCount()).thenReturn(mTrailing28DayJobsCount);
        when(mPerformanceInfo.getRate()).thenReturn(mRate);

        return mPerformanceInfo;
    }
}