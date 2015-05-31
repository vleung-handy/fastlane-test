package com.handy.portal.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.handy.portal.R;
import com.handy.portal.RobolectricGradleTestWrapper;
import com.handy.portal.core.LoginDetails;
import com.handy.portal.core.PinRequestDetails;
import com.handy.portal.data.DataManager;
import com.handy.portal.data.EnvironmentManager;
import com.handy.portal.data.BuildConfigWrapper;
import com.handy.portal.event.Event;
import com.handy.portal.ui.activity.MainActivity;
import com.handy.portal.ui.widget.InputTextField;
import com.squareup.otto.Bus;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowCookieManager;
import org.robolectric.shadows.ShadowToast;
import org.robolectric.util.SupportFragmentTestUtil;

import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.robolectric.Shadows.shadowOf;

public class LoginActivityFragmentTest extends RobolectricGradleTestWrapper
{

    private static final String VALID_PHONE_NUMBER = "1231231234";

    @Mock
    private Bus bus;
    @Mock
    private BuildConfigWrapper buildConfigWrapper;
    @Mock
    private EnvironmentManager environmentManager;
    @Mock
    private DataManager dataManager;

    @InjectMocks
    private LoginActivityFragment fragment;

    private FragmentActivity activity;
    private View fragmentView;

    @Before
    public void setUp() throws Exception
    {
        fragment = new LoginActivityFragment();
        SupportFragmentTestUtil.startFragment(fragment);
        fragmentView = fragment.getView();
        activity = fragment.getActivity();

        initMocks(this);
    }

    @Test
    public void givenValidPhoneNumber_whenRequestPinButtonClicked_thenRequestPinCode() throws Exception
    {
        makePinRequest("1111111111");

        ArgumentCaptor<Event.RequestPinCodeEvent> argument = ArgumentCaptor.forClass(Event.RequestPinCodeEvent.class);
        verify(bus).post(argument.capture());
        assertThat(argument.getValue().phoneNumber, equalTo("1111111111"));
    }

    @Test
    public void givenValidPinCode_whenLogInButtonClicked_thenRequestLogin() throws Exception
    {
        makeLoginRequest("5353");

        ArgumentCaptor<Event.RequestLoginEvent> argument = ArgumentCaptor.forClass(Event.RequestLoginEvent.class);
        verify(bus).post(argument.capture());
        assertThat(argument.getValue().phoneNumber, equalTo(VALID_PHONE_NUMBER));
        assertThat(argument.getValue().pinCode, equalTo("5353"));
    }

    @Test
    public void givenValidPinCode_whenLoginRequestDetailsReceived_thenSetCredentialsCookie() throws Exception
    {
        when(dataManager.getBaseUrl()).thenReturn("http://cats.url");
        makeLoginRequest("5353");
        receiveLoginRequest(true, "something", "credentials=something");

        assertTrue(ShadowCookieManager.getInstance().getCookie("http://cats.url").contains("credentials=something"));
    }

    @Test
    public void givenValidPinCode_whenLoginRequestDetailsReceived_thenGoToMainActivity() throws Exception
    {
        makeLoginRequest("5353");
        receiveLoginRequest(true, null, null);

        Intent expectedIntent = new Intent(activity, MainActivity.class);
        assertThat(shadowOf(activity).getNextStartedActivity(), equalTo(expectedIntent));
    }

    @Test
    public void givenInvalidPinCode_whenLogInButtonClicked_thenNoRequestLogin() throws Exception
    {
        makeLoginRequest("123");

        verifyZeroInteractions(bus);
    }

    @Test
    public void givenUnrecognizedPhoneNumber_whenPinCodeRequestDetailsReceived_thenDisplayErrorToast() throws Exception
    {
        makePinRequest("7777777777"); // triggers stage change
        receivePinCodeRequest(false);

        assertThat(ShadowToast.getTextOfLatestToast(), equalTo(fragment.getString(R.string.login_error_bad_phone)));
    }

    @Test
    public void givenWrongPinCode_whenLoginRequestDetailsReceived_thenDisplayErrorToast() throws Exception
    {
        makeLoginRequest("7777");
        receiveLoginRequest(false, null, null);

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

    @Test
    public void whenLoginHelpClicked_thenSendIntentForMail() throws Exception {
        fragmentView.findViewById(R.id.login_help).performClick();

        Intent actualIntent = shadowOf(activity).getNextStartedActivity();
        Intent expectedIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(LoginActivityFragment.HELP_CENTER_URL));
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

    @Test
    public void givenDialogForSwitchingEnvironmentsShown_whenItemClicked_thenSwitchEnvironment() throws Exception
    {
        when(buildConfigWrapper.isDebug()).thenReturn(true);
        fragmentView.findViewById(R.id.logo).performClick();
        reset(environmentManager);

        ShadowAlertDialog alertDialog = shadowOf(ShadowAlertDialog.getLatestAlertDialog());
        alertDialog.clickOnItem(2);

        String secondItem = (String) alertDialog.getItems()[2];
        verify(environmentManager).setEnvironment(EnvironmentManager.Environment.valueOf(secondItem));
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
        Event.PinCodeRequestReceivedEvent event = mock(Event.PinCodeRequestReceivedEvent.class);
        event.success = true;
        event.pinRequestDetails = mock(PinRequestDetails.class);
        when(event.pinRequestDetails.getSuccess()).thenReturn(isValid);
        fragment.onPinCodeRequestReceived(event);
    }

    private void receiveLoginRequest(boolean isValid, String credentials, String credentialsCookie)
    {
        Event.LoginRequestReceivedEvent event = mock(Event.LoginRequestReceivedEvent.class);
        event.success = true;
        event.loginDetails = mock(LoginDetails.class);
        when(event.loginDetails.getSuccess()).thenReturn(isValid);
        when(event.loginDetails.getUserCredentials()).thenReturn(credentials);
        when(event.loginDetails.getUserCredentialsCookie()).thenReturn(credentialsCookie);
        fragment.onLoginRequestReceived(event);
    }

}
