package com.handy.portal.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.handy.portal.R;
import com.handy.portal.RobolectricGradleTestWrapper;
import com.handy.portal.TestUtils;
import com.handy.portal.core.BuildConfigWrapper;
import com.handy.portal.core.EnvironmentModifier;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.helpcenter.constants.HelpCenterUrl;
import com.handy.portal.library.ui.widget.InputTextField;
import com.handy.portal.model.LoginDetails;
import com.handy.portal.model.PinRequestDetails;
import com.handy.portal.ui.activity.LoginActivity;
import com.handy.portal.ui.activity.SplashActivity;

import org.greenrobot.eventbus.EventBus;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.robolectric.Robolectric;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowToast;
import org.robolectric.util.ActivityController;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.robolectric.Shadows.shadowOf;

public class LoginActivityFragmentTest extends RobolectricGradleTestWrapper
{

    private static final String VALID_PHONE_NUMBER = "1231231234";

    @Mock
    private EventBus bus;
    @Mock
    private BuildConfigWrapper buildConfigWrapper;
    @Mock
    private EnvironmentModifier environmentModifier;
    @Mock
    private DataManager dataManager;
    @InjectMocks
    private LoginActivityFragment fragment;

    private FragmentActivity activity;
    private View fragmentView;

    @Before
    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        ActivityController<LoginActivity> activityController = Robolectric.buildActivity(LoginActivity.class).create();
        activityController.start().resume().visible();

        fragment = (LoginActivityFragment) activityController.get().getSupportFragmentManager().getFragments().get(0);
        fragmentView = fragment.getView();
        activity = activityController.get();

        initMocks(this);

        when(environmentModifier.getEnvironmentPrefix()).thenReturn("ms");
    }

    @Test
    public void givenValidPhoneNumber_whenRequestPinButtonClicked_thenRequestPinCode() throws Exception
    {
        makePinRequest("1111111111");

        final HandyEvent.RequestPinCode event = TestUtils.getFirstMatchingBusEvent(bus, HandyEvent.RequestPinCode.class);
        assertNotNull(event);
        assertThat(event.phoneNumber, equalTo("1111111111"));
    }

    @Test
    public void givenValidPinCode_whenLogInButtonClicked_thenRequestLogin() throws Exception
    {
        makeLoginRequest("535353");

        final HandyEvent.RequestLogin event = TestUtils.getFirstMatchingBusEvent(bus, HandyEvent.RequestLogin.class);
        assertNotNull(event);
        assertThat(event.phoneNumber, equalTo(VALID_PHONE_NUMBER));
        assertThat(event.pinCode, equalTo("535353"));
    }

    @Test
    public void givenValidPinCode_whenLoginRequestDetailsReceived_thenGoToSplashActivity() throws Exception
    {
        makeLoginRequest("535353");
        receiveLoginRequest(true, null);

        Intent expectedIntent = new Intent(activity, SplashActivity.class);
        Intent actualIntent = shadowOf(activity).getNextStartedActivity();
        assertEquals(actualIntent.getComponent(), expectedIntent.getComponent());
    }

    @Test
    public void givenInvalidPinCode_whenLogInButtonClicked_thenNoRequestLogin() throws Exception
    {
        makeLoginRequest("123");

        final HandyEvent.RequestLogin event = TestUtils.getFirstMatchingBusEvent(bus, HandyEvent.RequestLogin.class);
        assertNull(event);
    }

    @Test
    public void givenUnrecognizedPhoneNumber_whenPinCodeRequestDetailsReceived_thenDisplayErrorToast() throws Exception
    {
        makePinRequest("7777777777"); // triggers state change
        receivePinCodeRequest(false);

        assertThat(ShadowToast.getTextOfLatestToast(), equalTo(fragment.getString(R.string.login_error_bad_phone)));
    }

    @Test
    public void givenWrongPinCode_whenLoginRequestDetailsReceived_thenDisplayErrorToast() throws Exception
    {
        makeLoginRequest("777777");
        receiveLoginRequest(false, null);

        assertThat(ShadowToast.getTextOfLatestToast(), equalTo(fragment.getString(R.string.login_error_bad_login)));
    }

    @Test
    public void givenInputtingPinCode_whenBackButtonClicked_thenGoBackToInputtingPhoneNumber() throws Exception
    {
        makePinRequest(VALID_PHONE_NUMBER);
        receivePinCodeRequest(true);

        fragmentView.findViewById(R.id.back_button).performClick();

        assertThat(fragmentView.findViewById(R.id.phone_input_layout).getVisibility(), equalTo(View.VISIBLE));
    }

    @Ignore //TODO: re-enable when we can mock intent.resolveActivity(context.getPackageManager())
    public void whenLoginHelpClicked_thenSendViewIntentToHelpCenterUrl() throws Exception
    {
        fragmentView.findViewById(R.id.login_help_button).performClick();
        fragmentView.findViewById(R.id.login_help).performClick();

        Intent actualIntent = shadowOf(activity).getNextStartedActivity();
        Intent expectedIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(HelpCenterUrl.LOGIN_HELP_ABSOLUTE_URL));
        assertThat(actualIntent, equalTo(expectedIntent));
    }

    @Test
    public void givenDebugBuildType_whenLogoClicked_thenShowDialogForSwitchingEnvironments() throws Exception
    {
        when(buildConfigWrapper.isDebug()).thenReturn(true);

        fragmentView.findViewById(R.id.logo).performClick();

        assertNotNull(ShadowAlertDialog.getLatestAlertDialog());
    }

    @Test
    public void givenNotDebugBuildType_whenLogoClicked_thenDoNotShowDialogForSwitchingEnvironments() throws Exception
    {
        when(buildConfigWrapper.isDebug()).thenReturn(false);

        fragmentView.findViewById(R.id.logo).performClick();

        assertNull(ShadowAlertDialog.getLatestAlertDialog());
    }

    private void makeLoginRequest(String pinCode)
    {
        makePinRequest(VALID_PHONE_NUMBER); // assumes valid pin request
        reset(bus);

        receivePinCodeRequest(true);

        ((InputTextField) fragmentView.findViewById(R.id.pin_code_edit_text)).setText(pinCode);
        fragmentView.findViewById(R.id.login_button).performClick();
    }

    private void makePinRequest(String phoneNumber)
    {
        ((InputTextField) fragmentView.findViewById(R.id.phone_number_edit_text)).setText(phoneNumber);
        fragmentView.findViewById(R.id.login_button).performClick();
    }

    private void receivePinCodeRequest(boolean isValid)
    {
        HandyEvent.ReceivePinCodeSuccess event = mock(HandyEvent.ReceivePinCodeSuccess.class);
        event.pinRequestDetails = mock(PinRequestDetails.class);
        when(event.pinRequestDetails.getSuccess()).thenReturn(isValid);
        fragment.onPinCodeRequestReceived(event);
    }

    private void receiveLoginRequest(boolean isValid, String credentials)
    {
        HandyEvent.ReceiveLoginSuccess event = mock(HandyEvent.ReceiveLoginSuccess.class);
        event.loginDetails = mock(LoginDetails.class);
        when(event.loginDetails.getSuccess()).thenReturn(isValid);
        when(event.loginDetails.getAuthToken()).thenReturn(credentials);
        fragment.onLoginRequestSuccess(event);
    }

}
