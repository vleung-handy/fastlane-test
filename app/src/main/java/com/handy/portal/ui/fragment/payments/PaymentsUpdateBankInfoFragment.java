package com.handy.portal.ui.fragment.payments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.handy.portal.R;
import com.handy.portal.constant.FormDefinitionKey;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.PaymentEvents;
import com.handy.portal.event.RegionDefinitionEvent;
import com.handy.portal.event.StripeEvents;
import com.handy.portal.manager.ProviderManager;
import com.handy.portal.model.Provider;
import com.handy.portal.model.definitions.FieldDefinition;
import com.handy.portal.model.definitions.FormDefinitionWrapper;
import com.handy.portal.model.payments.BankAccountInfo;
import com.handy.portal.ui.fragment.InjectedFragment;
import com.handy.portal.util.UIUtils;
import com.squareup.otto.Subscribe;

import java.util.Map;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PaymentsUpdateBankInfoFragment extends InjectedFragment //TODO: make a form class
{
    //TODO: need to consolidate this logic with the other update payment fragment!

    @InjectView(R.id.payments_update_info_routing_number_label)
    TextView routingNumberLabel;

    @InjectView(R.id.payments_update_info_account_number_label)
    TextView accountNumberLabel;

    @InjectView(R.id.payments_update_info_tax_id_label)
    TextView taxIdLabel;

    @InjectView(R.id.payments_update_info_routing_number_text)
    TextView routingNumberText;

    @InjectView(R.id.payments_update_info_account_number_text)
    TextView accountNumberText;

    @InjectView(R.id.payments_update_info_tax_id_text)
    TextView taxIdText;

    @InjectView(R.id.payments_update_info_submit_button)
    Button submitButton;

    @Inject
    ProviderManager providerManager;

    FormDefinitionWrapper formDefinitionWrapper;

    private static final String FORM_KEY = FormDefinitionKey.UPDATE_BANK_INFO;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_payments_update_bank_account, container, false);
        ButterKnife.inject(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        submitButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onSubmitForm();
            }
        });

//        accountNumberText.setText("000123456789");
//        taxIdText.setText("000000000");
//        routingNumberText.setText("110000000");
    }

    @Override
    public void onResume()
    {
        super.onResume();
        bus.post(new RegionDefinitionEvent.RequestFormDefinitions(providerManager.getCachedActiveProvider().getCountry(), this.getContext()));
    }

    private boolean validate()
    {
        boolean allFieldsValid = true;
        Map<String, FieldDefinition> fieldDefinitionMap = formDefinitionWrapper.getFieldDefinitionsForForm(FORM_KEY);
        if (fieldDefinitionMap != null)
        {
            allFieldsValid = UIUtils.validateField(routingNumberText, fieldDefinitionMap.get(FormDefinitionKey.FieldDefinitionKey.ROUTING_NUMBER))
                    && UIUtils.validateField(accountNumberText, fieldDefinitionMap.get(FormDefinitionKey.FieldDefinitionKey.ACCOUNT_NUMBER))
                    && UIUtils.validateField(taxIdText, fieldDefinitionMap.get(FormDefinitionKey.FieldDefinitionKey.TAX_ID_NUMBER));
        }

        if (!allFieldsValid)
        {
            //show banner
        }
        return allFieldsValid;
    }

    private void onSubmitForm()
    {
        if (validate())
        {
            String routingNumber = routingNumberText.getText().toString();
            String accountNumber = accountNumberText.getText().toString();
            BankAccountInfo bankAccountInfo = new BankAccountInfo();
            bankAccountInfo.setAccountNumber(accountNumber);
            bankAccountInfo.setRoutingNumber(routingNumber);

            Provider provider = providerManager.getCachedActiveProvider();
            bankAccountInfo.setCurrency(provider.getPaymentCurrencyCode());
            bankAccountInfo.setCountry(provider.getCountry());
            bus.post(new StripeEvents.RequestStripeTokenFromBankAccount(bankAccountInfo));
            bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        }
    }

    @Subscribe
    public void onReceiveFormDefinitionsSuccess(RegionDefinitionEvent.ReceiveFormDefinitionsSuccess event)
    {
        this.formDefinitionWrapper = event.formDefinitionWrapper;
        updateFormWithDefinitions(formDefinitionWrapper);
    }

    private void updateFormWithDefinitions(FormDefinitionWrapper formDefinitionWrapper)
    {
        Map<String, FieldDefinition> fieldDefinitionMap = formDefinitionWrapper.getFieldDefinitionsForForm(FORM_KEY);
        if (fieldDefinitionMap != null)
        {
            UIUtils.setFieldsFromDefinition(routingNumberLabel, routingNumberText, fieldDefinitionMap.get(FormDefinitionKey.FieldDefinitionKey.ROUTING_NUMBER));
            UIUtils.setFieldsFromDefinition(accountNumberLabel, accountNumberText, fieldDefinitionMap.get(FormDefinitionKey.FieldDefinitionKey.ACCOUNT_NUMBER));
            UIUtils.setFieldsFromDefinition(taxIdLabel, taxIdText, fieldDefinitionMap.get(FormDefinitionKey.FieldDefinitionKey.TAX_ID_NUMBER));
        }
    }

    @Subscribe
    public void onReceiveStripeTokenFromBankAccountSuccess(StripeEvents.ReceiveStripeTokenFromBankAccountSuccess event)
    {
        String token = event.stripeTokenResponse.getStripeToken();

        String taxIdString = taxIdText.getText().toString();
        String accountNumberString = accountNumberText.getText().toString();
        String accountNumberLast4Digits = accountNumberString.substring(accountNumberString.length() - 4);
        bus.post(new PaymentEvents.RequestCreateBankAccount(token, taxIdString, accountNumberLast4Digits));
    }

    private void onFailure()
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        showToast(R.string.update_bank_account_failed, Toast.LENGTH_LONG);
    }

    @Subscribe
    public void onReceiveStripeTokenFromBankAccountError(StripeEvents.ReceiveStripeTokenFromBankAccountError event)
    {
        onFailure();
    }

    @Subscribe
    public void onReceiveCreateBankAccountSuccess(PaymentEvents.ReceiveCreateBankAccountSuccess event)
    {
        if (event.successfullyCreated)
        {
            bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
            showToast(R.string.update_bank_account_success, Toast.LENGTH_LONG);
            UIUtils.dismissOnBackPressed(getActivity());
        }
        else
        {
            onFailure();
        }
    }

    @Subscribe
    public void onReceiveCreateBankAccountError(PaymentEvents.ReceiveCreateBankAccountError event)
    {
        onFailure();
    }
}
