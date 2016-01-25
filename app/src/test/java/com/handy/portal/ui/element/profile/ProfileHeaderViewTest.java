package com.handy.portal.ui.element.profile;

import android.app.Application;
import android.widget.TextView;

import com.handy.portal.BuildConfig;
import com.handy.portal.R;
import com.handy.portal.model.PerformanceInfo;
import com.handy.portal.model.ProviderPersonalInfo;
import com.handy.portal.model.ProviderProfile;
import com.handy.portal.util.DateTimeUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class,
        packageName = "com.handy.portal",
        sdk = 19)
public class ProfileHeaderViewTest
{
    private static final Application APP = RuntimeEnvironment.application;
    private ProfileHeaderView phView;
    private ProviderProfile providerProfile;
    private String firstName;
    private String lastName;
    private Float jobsRating;
    private Date activationDate;

    @Before
    public void setUp() throws Exception
    {
        firstName = "My";
        lastName = "Pro";
        jobsRating = 3.5f;
        activationDate = new Date();
        phView = new ProfileHeaderView(APP, buildProviderProfile());
    }

    @Test
    public void shouldHaveFirstName()
    {
        TextView titleText = (TextView) phView.findViewById(R.id.provider_first_name_text);
        assertEquals(providerProfile.getProviderPersonalInfo().getFirstName(), titleText.getText());
    }

    @Test
    public void shouldHaveLastName()
    {
        TextView titleText = (TextView) phView.findViewById(R.id.provider_last_name_text);
        assertEquals(providerProfile.getProviderPersonalInfo().getLastName(), titleText.getText());
    }

    @Test
    public void shouldHaveJobsRating()
    {
        TextView titleText = (TextView) phView.findViewById(R.id.jobs_rating_text);
        assertTrue(titleText.getText().toString().contains(jobsRating.toString()));
    }

    @Test
    public void shouldHaveActivationDate()
    {
        TextView titleText = (TextView) phView.findViewById(R.id.joined_handy_text);
        assertTrue(titleText.getText().toString().contains(DateTimeUtils.formatMonthDateYear(activationDate)));
    }

    private ProviderProfile buildProviderProfile()
    {
        ProviderPersonalInfo personalInfo = mock(ProviderPersonalInfo.class);
        when(personalInfo.getFirstName()).thenReturn(firstName);
        when(personalInfo.getLastName()).thenReturn(lastName);
        when(personalInfo.getActivationDate()).thenReturn(activationDate);

        PerformanceInfo performanceInfo = mock(PerformanceInfo.class);
        when(performanceInfo.getTotalRating()).thenReturn(jobsRating);

        providerProfile = mock(ProviderProfile.class);
        when(providerProfile.getProviderPersonalInfo()).thenReturn(personalInfo);
        when(providerProfile.getPerformanceInfo()).thenReturn(performanceInfo);

        return providerProfile;
    }
}