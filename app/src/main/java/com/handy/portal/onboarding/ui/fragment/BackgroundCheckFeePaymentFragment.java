package com.handy.portal.onboarding.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;
import com.handy.portal.constant.Country;
import com.handy.portal.constant.FormDefinitionKey;
import com.handy.portal.event.RegionDefinitionEvent;
import com.handy.portal.event.StripeEvent;
import com.handy.portal.library.ui.view.CreditCardInputView;
import com.handy.portal.library.ui.view.SimpleContentLayout;
import com.handy.portal.library.util.UIUtils;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.NativeOnboardingLog;
import com.handy.portal.model.definitions.FieldDefinition;
import com.handy.portal.onboarding.model.BackgroundCheckFeeInfo;
import com.handy.portal.payments.PaymentEvent;
import com.stripe.android.model.Card;

import org.greenrobot.eventbus.Subscribe;

import java.util.Map;

import butterknife.BindView;

/*
TODO consolidate with supplies fragment
 */
public class BackgroundCheckFeePaymentFragment extends OnboardingSubflowUIFragment
{
    @BindView(R.id.fragment_background_check_credit_card_input)
    CreditCardInputView mCreditCardInputView;
    @BindView(R.id.order_summary)
    SimpleContentLayout mOrderSummary;
    @BindView(R.id.edit_payment_form)
    View mEditPaymentForm;

    private BackgroundCheckFeeInfo mBackgroundCheckFeeInfo;
    public static BackgroundCheckFeePaymentFragment newInstance()
    {
        return new BackgroundCheckFeePaymentFragment();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mBackgroundCheckFeeInfo = mSubflowData.getBackgroundCheckFeeInfo();
        if(mBackgroundCheckFeeInfo == null)
        {
            Crashlytics.logException(new Exception("background check fee info is null"));
        }
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        mOrderSummary.setContent(mBackgroundCheckFeeInfo.getHeaderText(), mBackgroundCheckFeeInfo.getFeeFormatted())
                .setImage(ContextCompat.getDrawable(getContext(), R.drawable.ic_background_check));//todo replace image

        mCreditCardInputView.getCreditCardNumberField().requestFocus();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        bus.register(this);
        if (mCreditCardInputView.getPaymentFieldDefinitions() == null)
        {
            //todo show ui blockers n stuff
            //todo need the actual region
            showLoadingOverlay();
            bus.post(new RegionDefinitionEvent.RequestFormDefinitions(Country.US, getActivity()));
        }
    }


    @Subscribe
    public void onReceiveFormDefinitions(
            final RegionDefinitionEvent.ReceiveFormDefinitionsSuccess event)
    {
        hideLoadingOverlay();
        Map<String, FieldDefinition> paymentFieldDefinitions = event.formDefinitionWrapper
                .getFieldDefinitionsForForm(FormDefinitionKey.UPDATE_CREDIT_CARD_INFO);
        if(paymentFieldDefinitions == null)
        {
            //fatal error
            Crashlytics.logException(new Exception("unable to get field definitions for update credit card info"));
            showToast(R.string.error_missing_server_data);
            return;
        }
        mCreditCardInputView.updateWithFormFieldDefinitions(paymentFieldDefinitions);
        //TODO unblock stuff
    }

    @Override
    protected int getButtonType()
    {
        return ButtonTypes.SINGLE_FIXED;
    }

    @Override
    protected int getLayoutResId()
    {
        return R.layout.view_background_check_fee_confirmation;
    }

    @Override
    protected String getTitle()
    {
        return getResources().getString(R.string.payment);
    }

    @Nullable
    @Override
    protected String getHeaderText()
    {
        return mBackgroundCheckFeeInfo.getHeaderText();
    }

    @Nullable
    @Override
    protected String getSubHeaderText()
    {
        return mBackgroundCheckFeeInfo.getSubHeaderText();
    }

    private boolean validateForms()
    {
        return mCreditCardInputView.validateFields();
    }

    @Override
    protected void onPrimaryButtonClicked()
    {
        UIUtils.dismissKeyboard(getActivity());

        if (!validateForms())
        {
            showToast(R.string.form_not_filled_out_correctly);
            //there is something invalid about the form, do nothing until it's valid.
            return;
        }

        Card card = mCreditCardInputView.getCardFromFields();
        showLoadingOverlay();
        bus.post(new StripeEvent.RequestStripeChargeToken(card, Country.US));
        bus.post(new LogEvent.AddLogEvent(new NativeOnboardingLog(
                NativeOnboardingLog.ServerTypes.GET_STRIPE_TOKEN.submitted())));
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
        bus.post(new LogEvent.AddLogEvent(new NativeOnboardingLog(
                NativeOnboardingLog.ServerTypes.UPDATE_CREDIT_CARD.success())));
        //todo
    }
}
