package com.handy.portal.preactivation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.Country;
import com.handy.portal.constant.FormDefinitionKey;
import com.handy.portal.event.ProfileEvent;
import com.handy.portal.event.RegionDefinitionEvent;
import com.handy.portal.event.StripeEvent;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.OnboardingSuppliesLog;
import com.handy.portal.model.ProviderPersonalInfo;
import com.handy.portal.model.definitions.FieldDefinition;
import com.handy.portal.model.onboarding.OnboardingSuppliesInfo;
import com.handy.portal.payments.PaymentEvent;
import com.handy.portal.library.ui.view.DateFormFieldTableRow;
import com.handy.portal.library.ui.view.FormFieldTableRow;
import com.handy.portal.library.ui.view.SimpleContentLayout;
import com.handy.portal.library.util.UIUtils;
import com.squareup.otto.Subscribe;
import com.stripe.android.model.Card;

import java.util.Map;

import butterknife.Bind;

public class PurchaseSuppliesPaymentFragment extends PreActivationFlowFragment
{
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

    private Map<String, FieldDefinition> mFieldDefinitions;
    private OnboardingSuppliesInfo mOnboardingSuppliesInfo;
    private Card mCard;
    private String mCardLast4;

    public static PurchaseSuppliesPaymentFragment newInstance(
            final OnboardingSuppliesInfo onboardingSuppliesInfo)
    {
        final PurchaseSuppliesPaymentFragment fragment = new PurchaseSuppliesPaymentFragment();
        final Bundle arguments = new Bundle();
        arguments.putSerializable(BundleKeys.ONBOARDING_SUPPLIES, onboardingSuppliesInfo);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mOnboardingSuppliesInfo = (OnboardingSuppliesInfo) getArguments()
                .getSerializable(BundleKeys.ONBOARDING_SUPPLIES);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        showLoadingOverlay();
        bus.post(new RegionDefinitionEvent.RequestFormDefinitions(Country.US, getActivity()));
        bus.post(new ProfileEvent.RequestProviderProfile(false));
    }

    @Subscribe
    public void onReceiveProviderInfoSuccess(final ProfileEvent.ReceiveProviderProfileSuccess event)
    {
        final ProviderPersonalInfo providerPersonalInfo =
                event.providerProfile.getProviderPersonalInfo();
        if (providerPersonalInfo != null
                && (mCardLast4 = providerPersonalInfo.getCardLast4()) != null)
        {
            mPaymentSummary
                    .setContent(getString(R.string.payment_method),
                            getString(R.string.card_info_formatted,
                                    getString(R.string.card), mCardLast4))
                    .setAction(getString(R.string.edit), new View.OnClickListener()
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
            unfreezeEditPaymentForm();
        }
        hideLoadingOverlay();
    }

    @Subscribe
    public void onReceiveProviderInfoError(final ProfileEvent.ReceiveProviderProfileError event)
    {
        unfreezeEditPaymentForm();
        hideLoadingOverlay();
    }

    private void freezeEditPaymentForm()
    {
        mPaymentSummary.setVisibility(View.VISIBLE);
        mEditPaymentForm.setVisibility(View.GONE);
    }

    private void unfreezeEditPaymentForm()
    {
        mPaymentSummary.setVisibility(View.GONE);
        mEditPaymentForm.setVisibility(View.VISIBLE);
        mCreditCardNumberField.requestFocus();
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        final String orderTotalFormatted = getString(R.string.order_total_formatted,
                mOnboardingSuppliesInfo.getSuppliesCost());
        mOrderSummary.setContent(getString(R.string.supply_starter_kit), orderTotalFormatted)
                .setImage(ContextCompat.getDrawable(getContext(), R.drawable.img_supplies));

        bus.post(new LogEvent.AddLogEvent(new OnboardingSuppliesLog(
                OnboardingSuppliesLog.Types.PAYMENT_SCREEN_SHOWN)));
    }

    @Subscribe
    public void onReceiveFormDefinitionsSuccess(
            final RegionDefinitionEvent.ReceiveFormDefinitionsSuccess event)
    {
        mFieldDefinitions = event.formDefinitionWrapper
                .getFieldDefinitionsForForm(FormDefinitionKey.UPDATE_CREDIT_CARD_INFO);
        UIUtils.setFieldsFromDefinition(mCreditCardNumberField,
                mFieldDefinitions.get(FormDefinitionKey.FieldDefinitionKey.CREDIT_CARD_NUMBER));
        UIUtils.setFieldsFromDefinition(mExpirationDateField,
                mFieldDefinitions.get(FormDefinitionKey.FieldDefinitionKey.EXPIRATION_DATE),
                mFieldDefinitions.get(FormDefinitionKey.FieldDefinitionKey.EXPIRATION_MONTH),
                mFieldDefinitions.get(FormDefinitionKey.FieldDefinitionKey.EXPIRATION_YEAR));
        UIUtils.setFieldsFromDefinition(mSecurityCodeField,
                mFieldDefinitions.get(FormDefinitionKey.FieldDefinitionKey.SECURITY_CODE_NUMBER));
    }

    @Override
    protected int getLayoutResId()
    {
        return R.layout.view_purchase_supplies_payment;
    }

    @Override
    protected String getTitle()
    {
        return getString(R.string.payment);
    }

    @Nullable
    @Override
    protected String getHeaderText()
    {
        return getString(R.string.enter_payment_information);
    }

    @Nullable
    @Override
    protected String getSubHeaderText()
    {
        return mOnboardingSuppliesInfo.getChargeNotice();
    }

    @Override
    protected String getPrimaryButtonText()
    {
        return getString(R.string.continue_to_confirmation);
    }

    @Override
    protected void onPrimaryButtonClicked()
    {
        bus.post(new LogEvent.AddLogEvent(new OnboardingSuppliesLog(
                OnboardingSuppliesLog.Types.CONTINUE_TO_CONFIRMATION_SELECTED)));

        UIUtils.dismissKeyboard(getActivity());

        if (mEditPaymentForm.getVisibility() == View.VISIBLE)
        {
            if (!validate())
            {
                return;
            }

            mCard = new Card(
                    mCreditCardNumberField.getValue().getText().toString(),
                    Integer.parseInt(mExpirationDateField.getMonthValue().getText().toString()),
                    Integer.parseInt(mExpirationDateField.getYearValue().getText().toString()),
                    mSecurityCodeField.getValue().getText().toString()
            );
            showLoadingOverlay();
            bus.post(new StripeEvent.RequestStripeChargeToken(mCard, Country.US));
            bus.post(new LogEvent.AddLogEvent(new OnboardingSuppliesLog(
                    OnboardingSuppliesLog.ServerTypes.GET_STRIPE_TOKEN.submitted())));
        }
        else if (mCardLast4 != null)
        {
            next(PurchaseSuppliesConfirmationFragment
                    .newInstance(mOnboardingSuppliesInfo, getString(R.string.card), mCardLast4));
        }
        else
        {
            Crashlytics.logException(
                    new RuntimeException("Attempted to continue without credit card information."));
        }
    }

    private boolean validate()
    {
        boolean allFieldsValid = UIUtils.validateField(mCreditCardNumberField,
                mFieldDefinitions.get(FormDefinitionKey.FieldDefinitionKey.CREDIT_CARD_NUMBER));
        allFieldsValid &= UIUtils.validateField(mExpirationDateField,
                mFieldDefinitions.get(FormDefinitionKey.FieldDefinitionKey.EXPIRATION_MONTH),
                mFieldDefinitions.get(FormDefinitionKey.FieldDefinitionKey.EXPIRATION_YEAR));
        allFieldsValid &= UIUtils.validateField(mSecurityCodeField,
                mFieldDefinitions.get(FormDefinitionKey.FieldDefinitionKey.SECURITY_CODE_NUMBER));
        return allFieldsValid;
    }

    @Subscribe
    public void onReceiveStripeChargeTokenSuccess(
            final StripeEvent.ReceiveStripeChargeTokenSuccess event)
    {
        bus.post(new LogEvent.AddLogEvent(new OnboardingSuppliesLog(
                OnboardingSuppliesLog.ServerTypes.GET_STRIPE_TOKEN.success())));
        bus.post(new PaymentEvent.RequestUpdateCreditCard(event.getToken()));
        bus.post(new LogEvent.AddLogEvent(new OnboardingSuppliesLog(
                OnboardingSuppliesLog.ServerTypes.UPDATE_CREDIT_CARD.submitted())));
    }

    @Subscribe
    public void onReceiveUpdateCreditCardSuccess(
            final PaymentEvent.ReceiveUpdateCreditCardSuccess event)
    {
        bus.post(new LogEvent.AddLogEvent(new OnboardingSuppliesLog(
                OnboardingSuppliesLog.ServerTypes.UPDATE_CREDIT_CARD.success())));
        hideLoadingOverlay();
        mCreditCardNumberField.getValue().setText(null);
        mExpirationDateField.getMonthValue().setText(null);
        mExpirationDateField.getYearValue().setText(null);
        mSecurityCodeField.getValue().setText(null);
        next(PurchaseSuppliesConfirmationFragment
                .newInstance(mOnboardingSuppliesInfo, mCard.getType(), mCard.getLast4()));
    }

    @Subscribe
    public void onReceiveStripeChargeTokenError(
            final StripeEvent.ReceiveStripeChargeTokenError event)
    {
        bus.post(new LogEvent.AddLogEvent(new OnboardingSuppliesLog(
                OnboardingSuppliesLog.ServerTypes.GET_STRIPE_TOKEN.error())));
        hideLoadingOverlay();
        showError(event.getError().getMessage());
    }

    @Subscribe
    public void onReceiveUpdateCreditCardError(
            final PaymentEvent.ReceiveUpdateCreditCardError event)
    {
        bus.post(new LogEvent.AddLogEvent(new OnboardingSuppliesLog(
                OnboardingSuppliesLog.ServerTypes.UPDATE_CREDIT_CARD.error())));
        hideLoadingOverlay();
        showError(event.error.getMessage());
    }
}
