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

    private final String FORM_KEY = FormDefinitionKey.UPDATE_BANK_INFO;

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
        setInputFilters();
        accountNumberText.setText("000123456789");
        taxIdText.setText("000000000");
        routingNumberText.setText("110000000");
    }

    @Override
    public void onResume()
    {
        super.onResume();
        bus.post(new RegionDefinitionEvent.RequestFormDefinitions(providerManager.getCachedActiveProvider().getCountry(), this.getContext()));

    }

    public boolean validate()
    {
        boolean allFieldsValid = true;

        Map<String, FieldDefinition> fieldDefinitionMap = formDefinitionWrapper.getFieldDefinitionsForForm(FORM_KEY);

        if (fieldDefinitionMap != null)
        {
            FieldDefinition fieldDefinition = fieldDefinitionMap.get(FormDefinitionKey.FieldDefinitionKey.ROUTING_NUMBER);
            if (!fieldDefinition.getCompiledPattern().matcher(routingNumberText.getText()).matches())
            {
                routingNumberText.setError(fieldDefinition.getErrorMessage());
                allFieldsValid = false;
            }

            fieldDefinition = fieldDefinitionMap.get(FormDefinitionKey.FieldDefinitionKey.ACCOUNT_NUMBER);
            if (!fieldDefinition.getCompiledPattern().matcher(accountNumberText.getText()).matches())
            {
                accountNumberText.setError(fieldDefinition.getErrorMessage());
                allFieldsValid = false;
            }

            fieldDefinition = fieldDefinitionMap.get(FormDefinitionKey.FieldDefinitionKey.TAX_ID_NUMBER);
            if (!fieldDefinition.getCompiledPattern().matcher(taxIdText.getText()).matches())
            {
                taxIdText.setError(fieldDefinition.getErrorMessage());
                allFieldsValid = false;
            }
        }


        if (!allFieldsValid)
        {
            //show message
        }
        return allFieldsValid;
    }

    private void setInputFilters()
    {
        //TODO: implement. below is for test only
//        List<InputFilter> filters = new LinkedList<>();
//        InputFilter lengthFilter = new InputFilter.LengthFilter(9);
//        filters.add(lengthFilter);
//        taxIdText.setInputType(InputType.TYPE_CLASS_NUMBER);
//        taxIdText.setFilters(filters.toArray(new InputFilter[]{}));
//
//        routingNumberText.setInputType(InputType.TYPE_CLASS_NUMBER);
//        routingNumberText.setFilters(filters.toArray(new InputFilter[]{}));
//
//        accountNumberText.setInputType(InputType.TYPE_CLASS_NUMBER);
//        accountNumberText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
    }

    public void onSubmitForm()
    {
        if (validate())
        {
            String routingNumber = routingNumberText.getText().toString();
            String accountNumber = accountNumberText.getText().toString();
            String taxId = taxIdText.getText().toString();
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
        //TODO
        Map<String, FieldDefinition> fieldDefinitionMap = formDefinitionWrapper.getFieldDefinitionsForForm(FORM_KEY);
        if (fieldDefinitionMap != null)
        {
            FieldDefinition fieldDefinition = fieldDefinitionMap.get(FormDefinitionKey.FieldDefinitionKey.ROUTING_NUMBER);
            routingNumberLabel.setText(fieldDefinition.getDisplayName());
            routingNumberText.setHint(fieldDefinition.getHintText());

            fieldDefinition = fieldDefinitionMap.get(FormDefinitionKey.FieldDefinitionKey.ACCOUNT_NUMBER);
            accountNumberLabel.setText(fieldDefinition.getDisplayName());
            accountNumberText.setHint(fieldDefinition.getHintText());

            fieldDefinition = fieldDefinitionMap.get(FormDefinitionKey.FieldDefinitionKey.TAX_ID_NUMBER);
            taxIdLabel.setText(fieldDefinition.getDisplayName());
            taxIdText.setHint(fieldDefinition.getHintText());
        }
    }

    @Subscribe
    public void onReceiveStripeTokenFromBankAccountSuccess(StripeEvents.ReceiveStripeTokenFromBankAccountSuccess event)
    {
        String token = event.stripeTokenResponse.getStripeToken();

        //TODO: need to do validation first
        String taxIdString = taxIdText.getText().toString();
        String accountNumberString = accountNumberText.getText().toString();
        String accountNumberLast4Digits = accountNumberString.substring(accountNumberString.length() - 4);
        bus.post(new PaymentEvents.RequestCreateBankAccount(token, taxIdString, accountNumberLast4Digits));
    }

    @Subscribe
    public void onReceiveStripeTokenFromBankAccountError(StripeEvents.ReceiveStripeTokenFromBankAccountError event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        //TODO: implement. below is test message only
        Toast.makeText(this.getContext(), "Failed to get stripe token", Toast.LENGTH_LONG).show();

    }

    @Subscribe
    public void onReceiveCreateBankAccountSuccess(PaymentEvents.ReceiveCreateBankAccountSuccess event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        //TODO: implement. below is test message only
        Toast.makeText(this.getContext(), event.successfullyCreated ? "Successfully created bank account" : "Failed to create bank account", Toast.LENGTH_LONG).show();

    }

    @Subscribe
    public void onReceiveCreateBankAccountError(PaymentEvents.ReceiveCreateBankAccountError event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        //TODO: implement. below is test message only
        Toast.makeText(this.getContext(), "Failed to create bank account", Toast.LENGTH_LONG).show();

    }
}
