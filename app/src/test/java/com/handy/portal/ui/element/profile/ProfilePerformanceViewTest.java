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
    private ProfilePerformanceView profilePerformanceView;
    private PerformanceInfo performanceInfo;
    private float trailing28DayRating;
    private int trailing28DayJobsCount;
    private String rate;

    @Before
    public void setUp() throws Exception
    {
        trailing28DayRating = 4.50f;
        trailing28DayJobsCount = 20;
        rate = "45";
        profilePerformanceView = new ProfilePerformanceView(APP, buildPerformanceInfo());
    }

    @Test
    public void shouldHaveTrailingRating()
    {
        TextView trailingRatingText = (TextView) profilePerformanceView.findViewById(R.id.trailing_rating_text);
        assertEquals(performanceInfo.getTrailing28DayRating(), Float.parseFloat(trailingRatingText.getText().toString())
                , 0.01);
    }

    @Test
    public void shouldHaveTrailingJobs()
    {
        TextView trailingJobsText = (TextView) profilePerformanceView.findViewById(R.id.trailing_jobs_text);
        assertEquals(performanceInfo.getTrailing28DayJobsCount(), Integer.parseInt(trailingJobsText.getText().toString()));
    }

    @Test
    public void shouldHaveTrailingRate()
    {
        TextView trailingRateText = (TextView) profilePerformanceView.findViewById(R.id.trailing_rate_text);
        assertEquals(performanceInfo.getRate(), trailingRateText.getText());
    }

    private PerformanceInfo buildPerformanceInfo()
    {
        performanceInfo = mock(PerformanceInfo.class);
        when(performanceInfo.getTrailing28DayRating()).thenReturn(trailing28DayRating);
        when(performanceInfo.getTrailing28DayJobsCount()).thenReturn(trailing28DayJobsCount);
        when(performanceInfo.getRate()).thenReturn(rate);

        return performanceInfo;
    }
}