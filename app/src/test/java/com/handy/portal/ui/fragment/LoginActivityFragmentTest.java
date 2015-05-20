package com.handy.portal.ui.fragment;

import android.view.View;

import com.handy.portal.R;
import com.handy.portal.RobolectricGradleTestWrapper;
import com.handy.portal.core.LoginDetails;
import com.handy.portal.core.PinRequestDetails;
import com.handy.portal.event.Event;
import com.handy.portal.ui.activity.LoginActivity;
import com.handy.portal.ui.widget.InputTextField;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.robolectric.Robolectric;
import org.robolectric.shadows.ShadowToast;
import org.robolectric.util.ActivityController;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class LoginActivityFragmentTest extends RobolectricGradleTestWrapper {

    private static final String VALID_PHONE_NUMBER = "1231231234";

    private View fragmentView;
    private LoginActivityFragment fragment;

    @Before
    public void setUp() throws Exception {
        ActivityController<LoginActivity> controller = Robolectric.buildActivity(LoginActivity.class);
        LoginActivity activity = controller.create().get();
        fragment = (LoginActivityFragment) activity.getSupportFragmentManager().getFragments().get(0);
        fragmentView = fragment.getView();
        reset(fragment.bus);
    }

    @Test
    public void givenValidPhoneNumber_whenRequestPinButtonClicked_thenRequestPinCode() throws Exception {
        makePinRequest("1111111111");

        ArgumentCaptor<Event.RequestPinCodeEvent> argument = ArgumentCaptor.forClass(Event.RequestPinCodeEvent.class);
        verify(fragment.bus).post(argument.capture());
        assertThat(argument.getValue().phoneNumber, equalTo("1111111111"));
    }

    @Test
    public void givenValidPinCode_whenLogInButtonClicked_thenRequestLogin() throws Exception {
        makeLoginRequest("5353");

        ArgumentCaptor<Event.RequestLoginEvent> argument = ArgumentCaptor.forClass(Event.RequestLoginEvent.class);
        verify(fragment.bus).post(argument.capture());
        assertThat(argument.getValue().phoneNumber, equalTo(VALID_PHONE_NUMBER));
        assertThat(argument.getValue().pinCode, equalTo("5353"));
    }

    @Test
    public void givenInvalidPhoneNumber_whenRequestPinButtonClicked_thenNoRequestPinCode() throws Exception {
        makePinRequest("1234");

        verifyZeroInteractions(fragment.bus);
    }

    @Test
    public void givenInvalidPinCode_whenLogInButtonClicked_thenNoRequestLogin() throws Exception {
        makeLoginRequest("123");

        verifyZeroInteractions(fragment.bus);
    }

    @Test
    public void givenUnrecognizedPhoneNumber_whenPinCodeRequestDetailsReceived_thenDisplayErrorToast() throws Exception {
        makePinRequest("7777777777"); // triggers stage change
        Event.PinCodeRequestReceivedEvent event = mock(Event.PinCodeRequestReceivedEvent.class);
        event.success = true;
        event.pinRequestDetails = mock(PinRequestDetails.class);
        when(event.pinRequestDetails.getSuccess()).thenReturn(false);

        fragment.onPinCodeRequestReceived(event);

        assertThat(ShadowToast.getTextOfLatestToast(), equalTo(fragment.getString(R.string.login_error_bad_phone)));
    }

    @Test
    public void givenWrongPinCode_whenLoginRequestDetailsReceived_thenDisplayErrorToast() throws Exception {
        makeLoginRequest("7777");
        Event.LoginRequestReceivedEvent event = mock(Event.LoginRequestReceivedEvent.class);
        event.success = true;
        event.loginDetails = mock(LoginDetails.class);
        when(event.loginDetails.getSuccess()).thenReturn(false);
        fragment.onLoginRequestReceived(event);

        assertThat(ShadowToast.getTextOfLatestToast(), equalTo(fragment.getString(R.string.login_error_bad_login)));
    }

    private void makeLoginRequest(String pinCode) {
        makePinRequest(VALID_PHONE_NUMBER); // assumes valid pin request
        reset(fragment.bus);

        Event.PinCodeRequestReceivedEvent event = mock(Event.PinCodeRequestReceivedEvent.class);
        event.success = true;
        event.pinRequestDetails = mock(PinRequestDetails.class);
        when(event.pinRequestDetails.getSuccess()).thenReturn(true);
        fragment.onPinCodeRequestReceived(event);

        ((InputTextField) fragmentView.findViewById(R.id.pin_code_edit_text)).setText(pinCode);
        fragmentView.findViewById(R.id.login_button).performClick();
    }

    private void makePinRequest(String phoneNumber) {
        ((InputTextField) fragmentView.findViewById(R.id.phone_number_edit_text)).setText(phoneNumber);
        fragmentView.findViewById(R.id.login_button).performClick();
    }

}
