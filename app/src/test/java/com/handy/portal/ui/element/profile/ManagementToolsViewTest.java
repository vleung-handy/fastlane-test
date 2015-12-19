package com.handy.portal.ui.element.profile;

import android.app.Application;
import android.widget.TextView;

import com.handy.portal.BuildConfig;
import com.handy.portal.R;
import com.handy.portal.TestUtils;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.model.ProviderProfile;
import com.squareup.otto.Bus;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class,
        packageName = "com.handy.portal",
        sdk = 19)
public class ManagementToolsViewTest
{
    @Mock
    Bus mBus;

    @InjectMocks
    ManagementToolsView mView;

    private static final Application APP = RuntimeEnvironment.application;

    @Before
    public void setUp() throws Exception
    {
        mView = new ManagementToolsView(APP, new ProviderProfile());
        initMocks(this);
    }

    @Test
    public void shouldHaveCorrectTitle()
    {
        TextView titleText = (TextView) mView.findViewById(R.id.profile_section_header_title_text);
        assertEquals(APP.getString(R.string.management_tools), titleText.getText());
    }

    @Test
    public void shouldShowEmailIncomeVerification()
    {
        TextView incomeText = (TextView) mView.findViewById(R.id.provider_email_income_verification_text);
        assertEquals(APP.getString(R.string.email_income_verification), incomeText.getText());
    }

    @Test
    public void shouldShowOrderResupplyKit()
    {
        TextView resupplyText = (TextView) mView.findViewById(R.id.provider_get_resupply_kit_text);
        assertEquals(APP.getString(R.string.get_resupply_kit), resupplyText.getText());
    }

    @Test
    public void shouldPostEmailVerificationEventAfterClick()
    {
        TextView incomeText = (TextView) mView.findViewById(R.id.provider_email_income_verification_text);
        incomeText.performClick();

        ArgumentCaptor<HandyEvent> captor = ArgumentCaptor.forClass(HandyEvent.class);
        verify(mBus, atLeastOnce()).post(captor.capture());
        HandyEvent.RequestSendIncomeVerification event =
                TestUtils.getBusCaptorValue(captor, HandyEvent.RequestSendIncomeVerification.class);
        assertNotNull("RequestSendIncomeVerification event was not post to bus", event);
    }

    @Test
    public void shouldNavigateToRequestSupplyAfterClick()
    {
        TextView resupplyText = (TextView) mView.findViewById(R.id.provider_get_resupply_kit_text);
        resupplyText.performClick();

        ArgumentCaptor<HandyEvent> captor = ArgumentCaptor.forClass(HandyEvent.class);
        verify(mBus, atLeastOnce()).post(captor.capture());
        HandyEvent.NavigateToTab event =
                TestUtils.getBusCaptorValue(captor, HandyEvent.NavigateToTab.class);
        assertNotNull("NavigateToTab event was not post to bus", event);
    }


    @Test
    public void shouldShowOverlayWhenEmailVerificationSuccess()
    {
        // research how to handle subscribed bus event
    }

    @Test
    public void shouldShowToastWhenEmailVerificationFailed()
    {
        // research how to handle subscribed bus event
    }

    @Test
    public void shouldDisableResupplyTextWhenUnavailable()
    {
        // research how to use mockito to fake the ProviderProfile mock
    }

    @Test
    public void shouldEnableResupplyTextWhenAvailable()
    {
        // research how to use mockito to fake the ProviderProfile mock
    }
}
