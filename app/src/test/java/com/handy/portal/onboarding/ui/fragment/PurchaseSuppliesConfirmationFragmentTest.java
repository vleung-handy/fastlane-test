package com.handy.portal.onboarding.ui.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.RobolectricGradleTestWrapper;
import com.handy.portal.TestUtils;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.ProfileEvent;
import com.handy.portal.event.StripeEvent;
import com.handy.portal.model.Designation;
import com.handy.portal.model.ProviderPersonalInfo;
import com.handy.portal.model.ProviderProfile;
import com.handy.portal.onboarding.model.OnboardingDetails;
import com.handy.portal.onboarding.model.subflow.SubflowData;
import com.handy.portal.onboarding.model.subflow.SubflowType;
import com.handy.portal.onboarding.model.supplies.SuppliesInfo;
import com.handy.portal.onboarding.model.supplies.SuppliesOrderInfo;
import com.handy.portal.onboarding.ui.activity.OnboardingSubflowActivity;
import com.handy.portal.payments.PaymentEvent;
import com.stripe.android.model.Token;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.robolectric.Robolectric;
import org.robolectric.util.ActivityController;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PurchaseSuppliesConfirmationFragmentTest extends RobolectricGradleTestWrapper
{

    private PurchaseSuppliesConfirmationFragment mFragment;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private OnboardingDetails mOnboardingDetails;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Intent mIntent;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private SubflowData mSubflowData;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private SuppliesInfo mSuppliesInfo;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ProviderProfile mProviderProfile;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ProviderPersonalInfo mProviderPersonalInfo;
    @Captor
    private ArgumentCaptor<Intent> mIntentCaptor;
    private ActivityController<OnboardingSubflowActivity> mActivityController;

    @Before
    public void setUp() throws Exception
    {
        initMocks(this);
        when(mOnboardingDetails.getSubflowDataByType(SubflowType.SUPPLIES))
                .thenReturn(mSubflowData);
        when(mIntent.getSerializableExtra(BundleKeys.ONBOARDING_DETAILS))
                .thenReturn(mOnboardingDetails);
        when(mIntent.getSerializableExtra(BundleKeys.SUBFLOW_TYPE))
                .thenReturn(SubflowType.SUPPLIES);
        when(mSubflowData.getSuppliesInfo()).thenReturn(mSuppliesInfo);
        when(mProviderPersonalInfo.getAddress().getShippingAddress()).thenReturn("123 Handy St");
        when(mProviderPersonalInfo.getFullName()).thenReturn("John Doe");
        when(mProviderProfile.getProviderPersonalInfo()).thenReturn(mProviderPersonalInfo);
    }

    private void startFragment()
    {
        mActivityController = Robolectric.buildActivity(OnboardingSubflowActivity.class, mIntent);
        mActivityController.create().resume().visible();
        ((PurchaseSuppliesFragment) mActivityController.get().getSupportFragmentManager()
                .getFragments().get(0)).onPrimaryButtonClicked();
        mFragment = spy((PurchaseSuppliesConfirmationFragment) mActivityController.get()
                .getSupportFragmentManager().getFragments().get(1));
    }

    @Test
    public void shouldNotShowOrderSummaryIfCardIsNotRequired() throws Exception
    {
        when(mSuppliesInfo.isCardRequired()).thenReturn(false);

        startFragment();

        assertThat(mFragment.mOrderSummary.getVisibility(), equalTo(View.GONE));
    }

    @Test
    public void shouldShowOrderSummaryWithOrderTotalIfCardIsRequired() throws Exception
    {
        when(mSuppliesInfo.isCardRequired()).thenReturn(true);
        when(mSuppliesInfo.getCost()).thenReturn("$67");

        startFragment();

        assertThat(mFragment.mOrderSummary.getVisibility(), equalTo(View.VISIBLE));
        assertThat(((TextView) mFragment.mOrderSummary.findViewById(R.id.description))
                .getText().toString(), equalTo("Order Total: $67"));
    }

    @Test
    public void shouldPopulateShippingSummaryIfAddressInfoIsAvailable() throws Exception
    {
        startFragment();
        final ProfileEvent.ReceiveProviderProfileSuccess event =
                new ProfileEvent.ReceiveProviderProfileSuccess(mProviderProfile);
        mFragment.onReceiveProviderInfoSuccess(event);

        assertThat(mFragment.mEditAddressForm.getVisibility(), equalTo(View.GONE));
        assertThat(mFragment.mShippingSummary.getVisibility(), equalTo(View.VISIBLE));
        assertThat(((TextView) mFragment.mShippingSummary.findViewById(R.id.description))
                .getText().toString(), equalTo("John Doe\n123 Handy St"));
    }

    @Test
    public void shouldShowEditAddressIfAddressInfoIsNotAvailable() throws Exception
    {
        startFragment();
        final ProfileEvent.ReceiveProviderProfileError event =
                new ProfileEvent.ReceiveProviderProfileError(null);
        mFragment.onReceiveProviderInfoError(event);

        assertThat(mFragment.mShippingSummary.getVisibility(), equalTo(View.GONE));
        assertThat(mFragment.mEditAddressForm.getVisibility(), equalTo(View.VISIBLE));
        assertThat(mFragment.mCancelEditAddress.getVisibility(), equalTo(View.GONE));
    }

    @Test
    public void shouldPopulatePaymentSummaryIfCardInfoIsAvailable() throws Exception
    {
        when(mSuppliesInfo.isCardRequired()).thenReturn(true);
        when(mProviderPersonalInfo.getCardLast4()).thenReturn("7788");
        startFragment();
        final ProfileEvent.ReceiveProviderProfileSuccess event =
                new ProfileEvent.ReceiveProviderProfileSuccess(mProviderProfile);
        mFragment.onReceiveProviderInfoSuccess(event);

        assertThat(mFragment.mEditPaymentForm.getVisibility(), equalTo(View.GONE));
        assertThat(mFragment.mPaymentSummary.getVisibility(), equalTo(View.VISIBLE));
        assertThat(((TextView) mFragment.mPaymentSummary.findViewById(R.id.description))
                .getText().toString(), equalTo("Card ending in 7788"));
    }

    @Test
    public void shouldShowEditPaymentIfCardInfoIsNotAvailable() throws Exception
    {
        when(mSuppliesInfo.isCardRequired()).thenReturn(true);
        when(mProviderPersonalInfo.getCardLast4()).thenReturn(null);
        startFragment();
        final ProfileEvent.ReceiveProviderProfileSuccess event =
                new ProfileEvent.ReceiveProviderProfileSuccess(mProviderProfile);
        mFragment.onReceiveProviderInfoSuccess(event);

        assertThat(mFragment.mPaymentSummary.getVisibility(), equalTo(View.GONE));
        assertThat(mFragment.mEditPaymentForm.getVisibility(), equalTo(View.VISIBLE));
        assertThat(mFragment.mCancelEditPayment.getVisibility(), equalTo(View.GONE));
    }

    @Test
    public void shouldDisplayNoticeThatSuppliesFeeWillBeCharged() throws Exception
    {
        when(mSuppliesInfo.isCardRequired()).thenReturn(false);
        when(mSuppliesInfo.getCost()).thenReturn("$67");
        when(mProviderPersonalInfo.getCardLast4()).thenReturn(null);
        startFragment();
        final ProfileEvent.ReceiveProviderProfileSuccess event =
                new ProfileEvent.ReceiveProviderProfileSuccess(mProviderProfile);
        mFragment.onReceiveProviderInfoSuccess(event);

        assertThat(mFragment.mEditPaymentForm.getVisibility(), equalTo(View.GONE));
        assertThat(((TextView) mFragment.mPaymentSummary.findViewById(R.id.description))
                .getText().toString(), equalTo("You\'ll see a $67 supplies fee on your first "
                + "payment to take care of the supply kit."));
    }

    @Test
    public void testCardSubmission() throws Exception
    {
        when(mSuppliesInfo.getCost()).thenReturn("$67");
        when(mProviderPersonalInfo.getAddress().getShippingAddress()).thenReturn("123 Handy St");
        when(mProviderPersonalInfo.getFullName()).thenReturn("John Doe");
        shouldShowEditPaymentIfCardInfoIsNotAvailable();

        mFragment.mCreditCardNumberField.setValue("1234123412341234");
        mFragment.mExpirationDateField.getMonthValue().setText("01");
        mFragment.mExpirationDateField.getYearValue().setText("2020");
        mFragment.mSecurityCodeField.setValue("123");
        mFragment.onPrimaryButtonClicked();

        final StripeEvent.RequestStripeChargeToken tokenEvent =
                TestUtils.getFirstMatchingBusEvent(mFragment.getBus(),
                        StripeEvent.RequestStripeChargeToken.class);
        assertNotNull(tokenEvent);
        assertThat(tokenEvent.getCard().getNumber(), equalTo("1234123412341234"));
        assertThat(tokenEvent.getCard().getExpMonth(), equalTo(1));
        assertThat(tokenEvent.getCard().getExpYear(), equalTo(2020));
        assertThat(tokenEvent.getCard().getCVC(), equalTo("123"));

        final Token token = mock(Token.class);
        when(token.getId()).thenReturn("token");
        mFragment.onReceiveStripeChargeTokenSuccess(
                new StripeEvent.ReceiveStripeChargeTokenSuccess(token));

        final PaymentEvent.RequestUpdateCreditCard updateEvent =
                TestUtils.getFirstMatchingBusEvent(mFragment.getBus(),
                        PaymentEvent.RequestUpdateCreditCard.class);
        assertNotNull(updateEvent);
        assertThat(updateEvent.getToken().getId(), equalTo("token"));
        mFragment.onReceiveUpdateCreditCardSuccess(
                new PaymentEvent.ReceiveUpdateCreditCardSuccess());

        final HandyEvent.RequestOnboardingSupplies optInEvent =
                TestUtils.getFirstMatchingBusEvent(mFragment.getBus(),
                        HandyEvent.RequestOnboardingSupplies.class);
        assertNotNull(optInEvent);
        assertTrue(optInEvent.getOptIn());

        mFragment.onReceiveOnboardingSuppliesSuccess(
                new HandyEvent.ReceiveOnboardingSuppliesSuccess());
        verify(mFragment).terminate(mIntentCaptor.capture());
        final SuppliesOrderInfo suppliesOrderInfo = (SuppliesOrderInfo) mIntentCaptor.getValue()
                .getSerializableExtra(BundleKeys.SUPPLIES_ORDER_INFO);
        assertNotNull(suppliesOrderInfo);
        assertThat(suppliesOrderInfo.getDesignation(), equalTo(Designation.YES));
        assertThat(suppliesOrderInfo.getOrderTotalText(), equalTo("$67"));
        assertTrue(suppliesOrderInfo.getPaymentText().matches(".*ending in 1234"));
        assertThat(suppliesOrderInfo.getShippingText(), equalTo("John Doe\n123 Handy St"));
    }

    @Test
    public void testAddressSubmission() throws Exception
    {
        when(mSuppliesInfo.isCardRequired()).thenReturn(false);
        shouldShowEditAddressIfAddressInfoIsNotAvailable();

        mFragment.mAddress1Field.setValue("123 Handy St");
        mFragment.mCityField.setValue("New York");
        mFragment.mStateField.setValue("NY");
        mFragment.mZipField.setValue("10001");

        mFragment.onPrimaryButtonClicked();

        final ProfileEvent.RequestProfileUpdate updateEvent =
                TestUtils.getFirstMatchingBusEvent(mFragment.getBus(),
                        ProfileEvent.RequestProfileUpdate.class);
        assertNotNull(updateEvent);
        assertThat(updateEvent.address1, equalTo("123 Handy St"));
        assertThat(updateEvent.address2, equalTo(""));
        assertThat(updateEvent.city, equalTo("New York"));
        assertThat(updateEvent.state, equalTo("NY"));
        assertThat(updateEvent.zipCode, equalTo("10001"));

        mFragment.onReceiveProfileUpdateSuccess(new ProfileEvent.ReceiveProfileUpdateSuccess(
                mock(ProviderPersonalInfo.class, Answers.RETURNS_DEEP_STUBS.get())));
        final HandyEvent.RequestOnboardingSupplies optInEvent =
                TestUtils.getFirstMatchingBusEvent(mFragment.getBus(),
                        HandyEvent.RequestOnboardingSupplies.class);
        assertNotNull(optInEvent);
        assertTrue(optInEvent.getOptIn());

        mFragment.onReceiveOnboardingSuppliesSuccess(
                new HandyEvent.ReceiveOnboardingSuppliesSuccess());
        verify(mFragment).terminate(mIntentCaptor.capture());
        final SuppliesOrderInfo suppliesOrderInfo = (SuppliesOrderInfo) mIntentCaptor.getValue()
                .getSerializableExtra(BundleKeys.SUPPLIES_ORDER_INFO);
        assertNotNull(suppliesOrderInfo);
    }
}
