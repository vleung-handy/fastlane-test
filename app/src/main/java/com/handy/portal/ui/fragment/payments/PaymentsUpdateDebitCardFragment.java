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
import com.handy.portal.model.definitions.FieldDefinition;
import com.handy.portal.model.definitions.FormDefinitionWrapper;
import com.handy.portal.model.payments.DebitCardInfo;
import com.handy.portal.ui.fragment.InjectedFragment;
import com.squareup.otto.Subscribe;

import java.util.Map;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PaymentsUpdateDebitCardFragment extends InjectedFragment
{
    //TODO: need to consolidate this logic with the other update payment fragment!

    @InjectView(R.id.payments_update_info_debit_card_number_label)
    TextView debitCardNumberLabel;

    @InjectView(R.id.payments_update_info_debit_card_expiration_date_label)
    TextView debitCardExpirationDateLabel;

    @InjectView(R.id.payments_update_info_debit_card_tax_id_label)
    TextView debitCardTaxIdLabel;

    @InjectView(R.id.payments_update_info_debit_card_security_code_label)
    TextView debitCardSecurityCodeLabel;

    @InjectView(R.id.payments_update_info_debit_card_number_text)
    TextView debitCardNumberText;

    @InjectView(R.id.payments_update_info_debit_card_expiration_month_text)
    TextView debitCardExpirationMonthText;

    @InjectView(R.id.payments_update_info_debit_card_expiration_year_text)
    TextView debitCardExpirationYearText;

    @InjectView(R.id.payments_update_info_debit_card_security_code_text)
    TextView debitCardSecurityCodeText;

    @InjectView(R.id.payments_update_info_debit_tax_id_text)
    TextView taxIdText;

    @Inject
    ProviderManager providerManager;

    @InjectView(R.id.payments_update_info_debit_card_submit_button)
    Button submitButton;

    FormDefinitionWrapper formDefinitionWrapper;

    private final String FORM_KEY = FormDefinitionKey.UPDATE_DEBIT_CARD_INFO;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_payments_update_debit_card, container, false);
        ButterKnife.inject(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        debitCardNumberText.setText("4000056655665556"); //TODO: remove. test only
        debitCardExpirationMonthText.setText("01");
        debitCardExpirationYearText.setText("2017");
        debitCardSecurityCodeText.setText("424");
        taxIdText.setText("000000000");
        submitButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onSubmitForm();
            }
        });
    }

    @Override
    public void onResume()
    {
        super.onResume();
        bus.post(new RegionDefinitionEvent.RequestFormDefinitions(providerManager.getCachedActiveProvider().getCountry(), this.getContext()));
    }

    public boolean validate()
    {
        //TODO: implement

        boolean allFieldsValid = true;

        Map<String, FieldDefinition> fieldDefinitionMap = formDefinitionWrapper.getFieldDefinitionsForForm(FORM_KEY);

        FieldDefinition fieldDefinition = fieldDefinitionMap.get(FormDefinitionKey.FieldDefinitionKey.TAX_ID_NUMBER);
        if (!fieldDefinition.getCompiledPattern().matcher(taxIdText.getText()).matches())
        {
            taxIdText.setError(fieldDefinition.getErrorMessage());
            allFieldsValid = false;
        }

        fieldDefinition = fieldDefinitionMap.get(FormDefinitionKey.FieldDefinitionKey.SECURITY_CODE_NUMBER);
        if (!fieldDefinition.getCompiledPattern().matcher(debitCardSecurityCodeText.getText()).matches())
        {
            debitCardSecurityCodeText.setError(fieldDefinition.getErrorMessage());
            allFieldsValid = false;
        }
        fieldDefinition = fieldDefinitionMap.get(FormDefinitionKey.FieldDefinitionKey.DEBIT_CARD_NUMBER);
        if (!fieldDefinition.getCompiledPattern().matcher(debitCardNumberText.getText()).matches())
        {
            debitCardNumberText.setError(fieldDefinition.getErrorMessage());
            allFieldsValid = false;
        }

        if (!allFieldsValid)
        {
            //show message
        }
        return allFieldsValid;
    }

    public void onSubmitForm()
    {
        if (validate())
        {
            DebitCardInfo debitCardInfo = new DebitCardInfo();
            debitCardInfo.setCardNumber(debitCardNumberText.getText().toString());
            debitCardInfo.setCvc(debitCardSecurityCodeText.getText().toString());
            debitCardInfo.setExpMonth(debitCardExpirationMonthText.getText().toString());
            debitCardInfo.setExpYear(debitCardExpirationYearText.getText().toString());
            bus.post(new StripeEvents.RequestStripeTokenFromDebitCard(debitCardInfo));
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
            FieldDefinition fieldDefinition = fieldDefinitionMap.get(FormDefinitionKey.FieldDefinitionKey.DEBIT_CARD_NUMBER);
            debitCardNumberLabel.setText(fieldDefinition.getDisplayName());
            debitCardNumberText.setHint(fieldDefinition.getHintText());

            fieldDefinition = fieldDefinitionMap.get(FormDefinitionKey.FieldDefinitionKey.SECURITY_CODE_NUMBER);
            debitCardSecurityCodeLabel.setText(fieldDefinition.getDisplayName());
            debitCardSecurityCodeText.setHint(fieldDefinition.getHintText());

            fieldDefinition = fieldDefinitionMap.get(FormDefinitionKey.FieldDefinitionKey.TAX_ID_NUMBER);
            debitCardTaxIdLabel.setText(fieldDefinition.getDisplayName());
            taxIdText.setHint(fieldDefinition.getHintText());

            fieldDefinition = fieldDefinitionMap.get(FormDefinitionKey.FieldDefinitionKey.EXPIRATION_DATE);
            debitCardExpirationDateLabel.setText(fieldDefinition.getDisplayName());
        }


    }

    @Subscribe
    public void onReceiveStripeTokenFromDebitCardSuccess(StripeEvents.ReceiveStripeTokenFromDebitCardSuccess event)
    {

        String token = event.stripeTokenResponse.getStripeToken();
        System.out.println("Received Stripe token: " + token);

        //TODO: need to do validation first
        String taxIdString = taxIdText.getText().toString();
        String expMonthString = debitCardExpirationMonthText.getText().toString();
        String expYearString = debitCardExpirationYearText.getText().toString();
        String cardNumberString = debitCardNumberText.getText().toString();
        String cardNumberLast4Digits = cardNumberString.substring(cardNumberString.length() - 4);
        bus.post(new PaymentEvents.RequestCreateDebitCardRecipient(token, taxIdString, cardNumberLast4Digits, expMonthString, expYearString));

    }

    @Subscribe
    public void onReceiveStripeTokenFromDebitCardError(StripeEvents.ReceiveStripeTokenFromDebitCardError event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        //TODO: implement. below is test message only
        Toast.makeText(this.getContext(), "Failed to get stripe token", Toast.LENGTH_LONG).show();
    }

    //TODO: clean this up
    @Subscribe
    public void onReceiveCreateDebitCardRecipientSuccess(PaymentEvents.ReceiveCreateDebitCardRecipientSuccess event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        //TODO: implement. below is test message only
        Toast.makeText(this.getContext(), event.successfullyCreated ? "Successfully created debit card" : "Failed to create debit card", Toast.LENGTH_LONG).show();
    }

    @Subscribe
    public void onReceiveCreateDebitCardRecipientError(PaymentEvents.ReceiveCreateDebitCardRecipientError event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        //TODO: implement. below is test message only
        Toast.makeText(this.getContext(), "Failed to create debit card", Toast.LENGTH_LONG).show();
    }
}
