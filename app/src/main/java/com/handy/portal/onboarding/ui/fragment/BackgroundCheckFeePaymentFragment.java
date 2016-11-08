package com.handy.portal.onboarding.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;
import com.handy.portal.constant.Country;
import com.handy.portal.constant.FormDefinitionKey;
import com.handy.portal.data.DataManager;
import com.handy.portal.library.ui.view.CreditCardInputView;
import com.handy.portal.library.ui.view.SimpleContentLayout;
import com.handy.portal.library.util.UIUtils;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.NativeOnboardingLog;
import com.handy.portal.manager.RegionDefinitionsManager;
import com.handy.portal.manager.StripeManager;
import com.handy.portal.model.SuccessWrapper;
import com.handy.portal.model.definitions.FieldDefinition;
import com.handy.portal.model.definitions.FormDefinitionWrapper;
import com.handy.portal.onboarding.model.BackgroundCheckFeeInfo;
import com.handy.portal.payments.PaymentsManager;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import java.util.Map;

import javax.inject.Inject;

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

    @Inject
    RegionDefinitionsManager mRegionDefinitionsManager;

    @Inject
    StripeManager mStripeManager;
    @Inject
    PaymentsManager mPaymentsManager;

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
        bus.post(new LogEvent.AddLogEvent(new NativeOnboardingLog.BackgroundCheckFeePageLog.Shown()));
    }

    @Override
    public void onResume()
    {
        super.onResume();
        /*
        putting this in onResume() in case this request fails due to no network connection
         */
        if (mCreditCardInputView.getPaymentFieldDefinitions() == null)
        {
            requestPaymentFieldDefinitions();
        }
    }

    @Nullable
    @Override
    protected String getHelpCenterArticleUrl()
    {
        if(mBackgroundCheckFeeInfo != null)
        {
            return mBackgroundCheckFeeInfo.getHelpUrl();
        }
        return null;
    }

    private void requestPaymentFieldDefinitions()
    {
        //todo need the actual region
        showLoadingOverlay();

        mRegionDefinitionsManager.requestFormDefinitions(getContext(), Country.US,
                new DataManager.Callback<FormDefinitionWrapper>() {
                    @Override
                    public void onSuccess(final FormDefinitionWrapper response)
                    {
                        onReceiveFormDefinitions(response);
                    }

                    @Override
                    public void onError(final DataManager.DataManagerError error)
                    {
                        Crashlytics.logException(new Exception(
                                "unable to get form definition for credit card: " + error.getMessage()));
                        showToast(R.string.error_missing_server_data);
                    }
                });
    }

    private void onReceiveFormDefinitions(@NonNull FormDefinitionWrapper formDefinitionWrapper)
    {
        hideLoadingOverlay();
        Map<String, FieldDefinition> paymentFieldDefinitions = formDefinitionWrapper
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
        bus.post(new LogEvent.AddLogEvent(new NativeOnboardingLog.BackgroundCheckFeePageLog.ContinueButtonClicked()));

        UIUtils.dismissKeyboard(getActivity());

        if (!validateForms())
        {
            showToast(R.string.form_not_filled_out_correctly);
            //there is something invalid about the form, do nothing until it's valid.
            return;
        }

        showLoadingOverlay();

        Card card = mCreditCardInputView.getCardFromFields();
        bus.post(new LogEvent.AddLogEvent(new NativeOnboardingLog(
                NativeOnboardingLog.ServerTypes.GET_STRIPE_TOKEN.submitted())));
        requestStripeChargeToken(Country.US, card);
    }

    private void requestStripeChargeToken(@NonNull String region, @NonNull Card card)
    {
        mStripeManager.requestStripeChargeToken(region, card, new TokenCallback() {
            @Override
            public void onError(final Exception error)
            {
                //todo logging
                showToast(R.string.error);
            }

            @Override
            public void onSuccess(final Token token)
            {
                bus.post(new LogEvent.AddLogEvent(new NativeOnboardingLog(
                        NativeOnboardingLog.ServerTypes.GET_STRIPE_TOKEN.success())));
                requestUpdateCreditCard(token);
                bus.post(new LogEvent.AddLogEvent(new NativeOnboardingLog(
                        NativeOnboardingLog.ServerTypes.UPDATE_CREDIT_CARD.submitted())));
            }
        });
    }

    private void requestUpdateCreditCard(@NonNull Token token)
    {
        mPaymentsManager.requestUpdateCreditCard(token, new DataManager.Callback<SuccessWrapper>() {
            @Override
            public void onSuccess(final SuccessWrapper response)
            {
                bus.post(new LogEvent.AddLogEvent(new NativeOnboardingLog(
                        NativeOnboardingLog.ServerTypes.UPDATE_CREDIT_CARD.success())));
                //todo
            }

            @Override
            public void onError(final DataManager.DataManagerError error)
            {
                //todo logging
                showToast(R.string.error);
            }
        });
    }
}
