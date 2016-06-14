package com.handy.portal.onboarding.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.Country;
import com.handy.portal.constant.FormDefinitionKey;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.ProfileEvent;
import com.handy.portal.event.RegionDefinitionEvent;
import com.handy.portal.event.StripeEvent;
import com.handy.portal.library.ui.view.DateFormFieldTableRow;
import com.handy.portal.library.ui.view.FormFieldTableRow;
import com.handy.portal.library.ui.view.SimpleContentLayout;
import com.handy.portal.library.util.TextUtils;
import com.handy.portal.library.util.UIUtils;
import com.handy.portal.library.util.Utils;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.NativeOnboardingLog;
import com.handy.portal.model.Address;
import com.handy.portal.model.Designation;
import com.handy.portal.model.ProviderPersonalInfo;
import com.handy.portal.model.definitions.FieldDefinition;
import com.handy.portal.onboarding.model.supplies.SuppliesInfo;
import com.handy.portal.onboarding.model.supplies.SuppliesOrderInfo;
import com.handy.portal.payments.PaymentEvent;
import com.squareup.otto.Subscribe;
import com.stripe.android.model.Card;

import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

public class PurchaseSuppliesConfirmationFragment extends OnboardingSubflowFragment
{
    @Bind(R.id.shipping_summary)
    SimpleContentLayout mShippingSummary;
    @Bind(R.id.edit_address_form)
    View mEditAddressForm;
    @Bind(R.id.address_1_field)
    FormFieldTableRow mAddress1Field;
    @Bind(R.id.address_2_field)
    FormFieldTableRow mAddress2Field;
    @Bind(R.id.city_field)
    FormFieldTableRow mCityField;
    @Bind(R.id.state_field)
    FormFieldTableRow mStateField;
    @Bind(R.id.zip_field)
    FormFieldTableRow mZipField;
    @Bind(R.id.cancel_edit_address)
    View mCancelEditAddress;
    @Bind(R.id.credit_card_number_field)
    FormFieldTableRow mCreditCardNumberField;
    @Bind(R.id.expiration_date_field)
    DateFormFieldTableRow mExpirationDateField;
    @Bind(R.id.security_code_field)
    FormFieldTableRow mSecurityCodeField;
    @Bind(R.id.order_summary)
    SimpleContentLayout mOrderSummary;
    @Bind(R.id.payment_summary)
    SimpleContentLayout mPaymentSummary;
    @Bind(R.id.edit_payment_form)
    View mEditPaymentForm;
    @Bind(R.id.cancel_edit_payment)
    View mCancelEditPayment;

    private Map<String, FieldDefinition> mPaymentFieldDefinitions;
    private Map<String, FieldDefinition> mAddressFieldDefinitions;
    private ProviderPersonalInfo mProviderPersonalInfo;
    private Card mCard;
    private SuppliesInfo mSuppliesInfo;
    private SuppliesOrderInfo mSuppliesOrderInfo;

    /**
     * These signifies whether the address/payments ready for the confirm purchase step.
     */
    private boolean mAddressReady = false;
    private boolean mPaymentReady = false;

    /**
     * These signifies whether address or payment needs the "loading overlay"
     */
    private boolean mAddressLoading = false;
    private boolean mPaymentLoading = false;

    public static PurchaseSuppliesConfirmationFragment newInstance()
    {
        return new PurchaseSuppliesConfirmationFragment();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mSuppliesInfo = mSubflowData.getSuppliesInfo();
        mSuppliesOrderInfo = new SuppliesOrderInfo();
    }

    @OnClick(R.id.cancel_edit_address)
    void onCancelAddressEditClicked()
    {
        mShippingSummary.setVisibility(View.VISIBLE);
        mEditAddressForm.setVisibility(View.GONE);
    }

    @OnClick(R.id.cancel_edit_payment)
    void onCancelPaymentEditClicked()
    {
        mPaymentSummary.setVisibility(View.VISIBLE);
        mEditPaymentForm.setVisibility(View.GONE);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        mShippingSummary.setContent(getString(R.string.shipping_address),
                getString(R.string.loading));

        final String orderTotalFormatted = getString(R.string.order_total_formatted,
                mSuppliesInfo.getCost());
        mSuppliesOrderInfo.setOrderTotalText(mSuppliesInfo.getCost());
        if (mSuppliesInfo.isCardRequired())
        {
            mOrderSummary.setContent(getString(R.string.starter_supply_kit), orderTotalFormatted)
                    .setImage(ContextCompat.getDrawable(getContext(), R.drawable.img_supplies));
        }
        else
        {
            mOrderSummary.setVisibility(View.GONE);
        }

        bus.post(new LogEvent.AddLogEvent(new NativeOnboardingLog(
                NativeOnboardingLog.Types.SUPPLIES_CONFIRMATION_SHOWN)));
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (mAddressFieldDefinitions == null)
        {
            bus.post(new RegionDefinitionEvent.RequestFormDefinitions(Country.US, getActivity()));
        }

        if (mProviderPersonalInfo != null)
        {
            onProviderLoaded();
        }
        else
        {
            showLoadingOverlay();
            bus.post(new ProfileEvent.RequestProviderProfile(true));
        }
    }

    @Subscribe
    public void onReceiveProviderInfoSuccess(final ProfileEvent.ReceiveProviderProfileSuccess event)
    {
        //as a side effect of our implementation of "RequestProfileUpdate", an update call will
        //cause this method to trigger. We don't need to update this if we already have one.
        if (mProviderPersonalInfo == null)
        {
            mProviderPersonalInfo = event.providerProfile.getProviderPersonalInfo();
            onProviderLoaded();

            hideLoadingOverlay();
        }
    }

    private void onProviderLoaded()
    {
        populateShippingSummary();
        populatePaymentSummary();
    }

    private void populatePaymentSummary()
    {
        if (mSuppliesInfo.isCardRequired())
        {
            String cardLast4 = mProviderPersonalInfo.getCardLast4();
            if (!TextUtils.isNullOrEmpty(cardLast4))
            {
                final String cardInfoFormatted = getString(R.string.card_info_formatted,
                        getString(R.string.card), cardLast4);
                mSuppliesOrderInfo.setPaymentText(cardInfoFormatted);
                mPaymentSummary.setContent(getString(R.string.payment_method), cardInfoFormatted)
                        .setAction(getString(R.string.edit),
                                new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(final View v)
                                    {
                                        unfreezeEditPaymentForm();
                                    }
                                });

                freezeEditPaymentForm();
            }
            else
            {
                //Since the user doesn't have any payments, don't let them cancel out.
                mCancelEditPayment.setVisibility(View.GONE);
                unfreezeEditPaymentForm();
            }
        }
        else
        {
            final String feeNoticeFormatted =
                    getString(R.string.supplies_fee_notice_formatted, mSuppliesInfo.getCost());
            mPaymentSummary.setContent(getString(R.string.supplies_fee), feeNoticeFormatted)
                    .setAction(getResources().getDrawable(R.drawable.ic_question_gray),
                            new View.OnClickListener()
                            {
                                @Override
                                public void onClick(final View v)
                                {
                                    final String feesHelpLink = mSuppliesInfo.getFeesHelpLink();
                                    if (!TextUtils.isNullOrEmpty(feesHelpLink))
                                    {
                                        final Intent intent = new Intent(Intent.ACTION_VIEW,
                                                Uri.parse(feesHelpLink));
                                        Utils.safeLaunchIntent(intent, getActivity());
                                    }
                                }
                            });
            freezeEditPaymentForm();
        }
    }

    @Subscribe
    public void onReceiveProviderInfoError(final ProfileEvent.ReceiveProviderProfileError event)
    {
        showEditAddressForm();
        if (mSuppliesInfo.isCardRequired())
        {
            unfreezeEditPaymentForm();
        }
        mCancelEditAddress.setVisibility(View.GONE);
        mCancelEditPayment.setVisibility(View.GONE);
        hideLoadingOverlay();
    }

    private void freezeEditPaymentForm()
    {
        mPaymentSummary.setVisibility(View.VISIBLE);
        mEditPaymentForm.setVisibility(View.GONE);
    }

    private void unfreezeEditPaymentForm()
    {
        mEditPaymentForm.setVisibility(View.VISIBLE);
        mPaymentSummary.setVisibility(View.GONE);
        mCreditCardNumberField.requestFocus();
    }


    private void populateShippingSummary()
    {
        final Address address = mProviderPersonalInfo.getAddress();
        final String shippingSummary =
                mProviderPersonalInfo.getFullName() + "\n" + address.getShippingAddress();
        mSuppliesOrderInfo.setShippingText(shippingSummary);
        mShippingSummary.setContent(getString(R.string.shipping_address), shippingSummary)
                .setAction(getString(R.string.edit), new View.OnClickListener()
                {
                    @Override
                    public void onClick(final View v)
                    {
                        showEditAddressForm();
                    }
                });
    }

    private void showEditAddressForm()
    {
        if (mProviderPersonalInfo != null)
        {
            final Address address = mProviderPersonalInfo.getAddress();
            mAddress1Field.getValue().setText(address.getAddress1());
            mAddress2Field.getValue().setText(address.getAddress2());
            mCityField.getValue().setText(address.getCity());
            mStateField.getValue().setText(address.getState());
            mZipField.getValue().setText(address.getZip());
        }
        mEditAddressForm.setVisibility(View.VISIBLE);
        mShippingSummary.setVisibility(View.GONE);
        mAddress1Field.requestFocus();

        bus.post(new LogEvent.AddLogEvent(new NativeOnboardingLog(
                NativeOnboardingLog.Types.EDIT_ADDRESS_SHOWN)));
    }

    @Subscribe
    public void onReceiveFormDefinitions(
            final RegionDefinitionEvent.ReceiveFormDefinitionsSuccess event)
    {
        mAddressFieldDefinitions = event.formDefinitionWrapper
                .getFieldDefinitionsForForm(FormDefinitionKey.UPDATE_ADDRESS);
        UIUtils.setFieldsFromDefinition(mAddress1Field,
                mAddressFieldDefinitions.get(FormDefinitionKey.FieldDefinitionKey.ADDRESS));
        UIUtils.setFieldsFromDefinition(mAddress2Field,
                mAddressFieldDefinitions.get(FormDefinitionKey.FieldDefinitionKey.ADDRESS2));
        UIUtils.setFieldsFromDefinition(mCityField,
                mAddressFieldDefinitions.get(FormDefinitionKey.FieldDefinitionKey.CITY));
        UIUtils.setFieldsFromDefinition(mStateField,
                mAddressFieldDefinitions.get(FormDefinitionKey.FieldDefinitionKey.STATE));
        UIUtils.setFieldsFromDefinition(mZipField,
                mAddressFieldDefinitions.get(FormDefinitionKey.FieldDefinitionKey.ZIP_CODE));

        mPaymentFieldDefinitions = event.formDefinitionWrapper
                .getFieldDefinitionsForForm(FormDefinitionKey.UPDATE_CREDIT_CARD_INFO);
        UIUtils.setFieldsFromDefinition(mCreditCardNumberField,
                mPaymentFieldDefinitions.get(FormDefinitionKey.FieldDefinitionKey.CREDIT_CARD_NUMBER));
        UIUtils.setFieldsFromDefinition(mExpirationDateField,
                mPaymentFieldDefinitions.get(FormDefinitionKey.FieldDefinitionKey.EXPIRATION_DATE),
                mPaymentFieldDefinitions.get(FormDefinitionKey.FieldDefinitionKey.EXPIRATION_MONTH),
                mPaymentFieldDefinitions.get(FormDefinitionKey.FieldDefinitionKey.EXPIRATION_YEAR));
        UIUtils.setFieldsFromDefinition(mSecurityCodeField,
                mPaymentFieldDefinitions.get(FormDefinitionKey.FieldDefinitionKey.SECURITY_CODE_NUMBER));
    }

    @Override
    protected int getButtonType()
    {
        return ButtonTypes.SINGLE_FIXED;
    }

    @Override
    protected int getLayoutResId()
    {
        return R.layout.view_purchase_supplies_confirmation;
    }

    @Override
    protected String getTitle()
    {
        return getString(R.string.purchase_supplies);
    }

    @Nullable
    @Override
    protected String getHeaderText()
    {
        return getString(mSuppliesInfo.isCardRequired() ?
                R.string.enter_payment_information : R.string.confirm_shipping_details);
    }

    @Nullable
    @Override
    protected String getSubHeaderText()
    {
        return mSuppliesInfo.getChargeNotice();
    }

    @Override
    protected void onPrimaryButtonClicked()
    {
        bus.post(new LogEvent.AddLogEvent(new NativeOnboardingLog(
                NativeOnboardingLog.Types.SUPPLIES_CONFIRM_PURCHASE_SELECTED)));

        UIUtils.dismissKeyboard(getActivity());

        if (!validateForms())
        {
            //there is something invalid about the form, do nothing until it's valid.
            return;
        }

        if (mEditAddressForm.getVisibility() == View.VISIBLE)
        {
            mAddressReady = false;

            showLoadingOverlay();
            mAddressLoading = true;
            bus.post(new ProfileEvent.RequestProfileUpdate(
                    "",
                    "",
                    mAddress1Field.getValue().getText(),
                    mAddress2Field.getValue().getText(),
                    mCityField.getValue().getText(),
                    mStateField.getValue().getText(),
                    mZipField.getValue().getText()
            ));
            bus.post(new LogEvent.AddLogEvent(new NativeOnboardingLog(
                    NativeOnboardingLog.ServerTypes.UPDATE_ADDRESS.submitted())));
        }
        else
        {
            mAddressReady = true;
        }

        if (mEditPaymentForm.getVisibility() == View.VISIBLE && mSuppliesInfo.isCardRequired())
        {
            mPaymentReady = false;

            mCard = new Card(
                    mCreditCardNumberField.getValue().getText().toString(),
                    Integer.parseInt(mExpirationDateField.getMonthValue().getText().toString()),
                    Integer.parseInt(mExpirationDateField.getYearValue().getText().toString()),
                    mSecurityCodeField.getValue().getText().toString()
            );
            showLoadingOverlay();
            mPaymentLoading = true;
            bus.post(new StripeEvent.RequestStripeChargeToken(mCard, Country.US));
            bus.post(new LogEvent.AddLogEvent(new NativeOnboardingLog(
                    NativeOnboardingLog.ServerTypes.GET_STRIPE_TOKEN.submitted())));
        }
        else
        {
            mPaymentReady = true;
        }

        //this is when both address and payment already exists
        if (mEditAddressForm.getVisibility() != View.VISIBLE
                && mEditPaymentForm.getVisibility() != View.VISIBLE)
        {
            mAddressReady = true;
            mPaymentReady = true;
            confirmPurchase();
        }
    }

    /**
     * Performs validation on both address and payment as necessary.
     *
     * @return true if form is valid.
     */
    private boolean validateForms()
    {
        boolean validAddress = true;
        boolean validPayment = true;

        if (mEditAddressForm.getVisibility() == View.VISIBLE)
        {
            validAddress = validateAddress();
        }

        if (mEditPaymentForm.getVisibility() == View.VISIBLE)
        {
            validPayment = validatePayment();
        }

        return validAddress && validPayment;
    }

    /**
     * This signifies the shipping address was successfully updated.
     *
     * @param event
     */
    @Subscribe
    public void onReceiveProfileUpdateSuccess(final ProfileEvent.ReceiveProfileUpdateSuccess event)
    {
        mAddressReady = true;
        mAddressLoading = false;
        bus.post(new LogEvent.AddLogEvent(new NativeOnboardingLog(
                NativeOnboardingLog.ServerTypes.UPDATE_ADDRESS.success())));
        mProviderPersonalInfo = event.providerPersonalInfo;
        populateShippingSummary();
        confirmPurchase();
    }

    @Subscribe
    public void onReceiveProfileUpdateError(final ProfileEvent.ReceiveProfileUpdateError event)
    {
        mAddressLoading = false;
        bus.post(new LogEvent.AddLogEvent(new NativeOnboardingLog(
                NativeOnboardingLog.ServerTypes.UPDATE_ADDRESS.error())));
        smartHideLoadingOverlay();
        showError(event.error.getMessage(), true);
    }

    /**
     * This signifies step 1 of 2 of saving the credit card is successful.
     *
     * @param event
     */
    @Subscribe
    public void onReceiveStripeChargeTokenSuccess(
            final StripeEvent.ReceiveStripeChargeTokenSuccess event)
    {
        bus.post(new LogEvent.AddLogEvent(new NativeOnboardingLog(
                NativeOnboardingLog.ServerTypes.GET_STRIPE_TOKEN.success())));
        bus.post(new PaymentEvent.RequestUpdateCreditCard(event.getToken()));
        bus.post(new LogEvent.AddLogEvent(new NativeOnboardingLog(
                NativeOnboardingLog.ServerTypes.UPDATE_CREDIT_CARD.submitted())));
    }

    /**
     * This signifies step 2 of 2 of saving the credit card is successful.
     *
     * @param event
     */
    @Subscribe
    public void onReceiveUpdateCreditCardSuccess(
            final PaymentEvent.ReceiveUpdateCreditCardSuccess event)
    {
        mPaymentReady = true;
        mPaymentLoading = false;
        bus.post(new LogEvent.AddLogEvent(new NativeOnboardingLog(
                NativeOnboardingLog.ServerTypes.UPDATE_CREDIT_CARD.success())));
        final String cardInfoFormatted = getString(R.string.card_info_formatted,
                mCard.getType(), mCard.getLast4());
        mSuppliesOrderInfo.setPaymentText(cardInfoFormatted);
        confirmPurchase();
    }

    @Subscribe
    public void onReceiveStripeChargeTokenError(
            final StripeEvent.ReceiveStripeChargeTokenError event)
    {
        mPaymentLoading = false;
        bus.post(new LogEvent.AddLogEvent(new NativeOnboardingLog(
                NativeOnboardingLog.ServerTypes.GET_STRIPE_TOKEN.error())));
        smartHideLoadingOverlay();
        showError(event.getError().getMessage(), true);
    }

    @Subscribe
    public void onReceiveUpdateCreditCardError(
            final PaymentEvent.ReceiveUpdateCreditCardError event)
    {
        mPaymentLoading = false;
        bus.post(new LogEvent.AddLogEvent(new NativeOnboardingLog(
                NativeOnboardingLog.ServerTypes.UPDATE_CREDIT_CARD.error())));
        smartHideLoadingOverlay();
        showError(event.error.getMessage(), true);
    }

    private void confirmPurchase()
    {
        if (mAddressReady && mPaymentReady)
        {
            showLoadingOverlay();
            bus.post(new HandyEvent.RequestOnboardingSupplies(true));
            bus.post(new LogEvent.AddLogEvent(
                    new NativeOnboardingLog.RequestSupplies.Submitted(true)));
        }
        else
        {
            smartHideLoadingOverlay();
        }
    }

    /**
     * Only hide the overlay if both the address and payment have finished loading
     */
    private void smartHideLoadingOverlay()
    {
        if (!mAddressLoading && !mPaymentLoading)
        {
            hideLoadingOverlay();
        }
    }

    @Subscribe
    public void onReceiveOnboardingSuppliesSuccess(final HandyEvent.ReceiveOnboardingSuppliesSuccess event)
    {
        bus.post(new LogEvent.AddLogEvent(
                new NativeOnboardingLog.RequestSupplies.Success(true)));
        hideLoadingOverlay();
        final Intent data = new Intent();
        mSuppliesOrderInfo.setDesignation(Designation.YES);
        data.putExtra(BundleKeys.SUPPLIES_ORDER_INFO, mSuppliesOrderInfo);
        terminate(data);
    }

    @Subscribe
    public void onReceiveOnboardingSuppliesError(final HandyEvent.ReceiveOnboardingSuppliesError event)
    {
        bus.post(new LogEvent.AddLogEvent(
                new NativeOnboardingLog.RequestSupplies.Error(true)));
        hideLoadingOverlay();
        showError(event.error.getMessage(), true);
    }

    private boolean validateAddress()
    {
        boolean allFieldsValid = UIUtils.validateField(mAddress1Field,
                mAddressFieldDefinitions.get(FormDefinitionKey.FieldDefinitionKey.ADDRESS));
        allFieldsValid &= UIUtils.validateField(mAddress2Field,
                mAddressFieldDefinitions.get(FormDefinitionKey.FieldDefinitionKey.ADDRESS2));
        allFieldsValid &= UIUtils.validateField(mCityField,
                mAddressFieldDefinitions.get(FormDefinitionKey.FieldDefinitionKey.CITY));
        allFieldsValid &= UIUtils.validateField(mStateField,
                mAddressFieldDefinitions.get(FormDefinitionKey.FieldDefinitionKey.STATE));
        allFieldsValid &= UIUtils.validateField(mZipField,
                mAddressFieldDefinitions.get(FormDefinitionKey.FieldDefinitionKey.ZIP_CODE));
        return allFieldsValid;
    }

    private boolean validatePayment()
    {
        boolean allFieldsValid = UIUtils.validateField(mCreditCardNumberField,
                mPaymentFieldDefinitions.get(FormDefinitionKey.FieldDefinitionKey.CREDIT_CARD_NUMBER));
        allFieldsValid &= UIUtils.validateField(mExpirationDateField,
                mPaymentFieldDefinitions.get(FormDefinitionKey.FieldDefinitionKey.EXPIRATION_MONTH),
                mPaymentFieldDefinitions.get(FormDefinitionKey.FieldDefinitionKey.EXPIRATION_YEAR));
        allFieldsValid &= UIUtils.validateField(mSecurityCodeField,
                mPaymentFieldDefinitions.get(FormDefinitionKey.FieldDefinitionKey.SECURITY_CODE_NUMBER));
        return allFieldsValid;
    }

}
