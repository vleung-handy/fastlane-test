package com.handy.portal.ui.fragment.payments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.handy.portal.R;
import com.handy.portal.constant.FormDefinitionKey;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.PaymentEvents;
import com.handy.portal.event.RegionDefinitionEvent;
import com.handy.portal.event.StripeEvents;
import com.handy.portal.manager.ProviderManager;
import com.handy.portal.model.definitions.FieldDefinition;
import com.handy.portal.model.definitions.FormDefinitionWrapper;
import com.handy.portal.model.payments.DebitCardInfo;
import com.handy.portal.ui.fragment.ActionBarFragment;
import com.handy.portal.ui.view.DateFormFieldTableRow;
import com.handy.portal.ui.view.FormFieldTableRow;
import com.handy.portal.util.UIUtils;
import com.squareup.otto.Subscribe;

import java.util.Map;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class PaymentsUpdateDebitCardFragment extends ActionBarFragment
{
    //TODO: need to consolidate this logic with the other update payment fragment!

    @InjectView(R.id.debit_card_number_field)
    FormFieldTableRow debitCardNumberField;

    @InjectView(R.id.expiration_date_field)
    DateFormFieldTableRow expirationDateField;

    @InjectView(R.id.security_code_field)
    FormFieldTableRow securityCodeField;

    @InjectView(R.id.tax_id_field)
    FormFieldTableRow taxIdField;

    @Inject
    ProviderManager providerManager;

    FormDefinitionWrapper formDefinitionWrapper;

    private static final String FORM_KEY = FormDefinitionKey.UPDATE_DEBIT_CARD_INFO;

    private static final int DEBIT_CARD_RECIPIENT_REQUEST_ID = 1;
    private static final int DEBIT_CARD_FOR_CHARGE_REQUEST_ID = 2;

    //TODO: create a state manager object
    private boolean receivedDebitCardRecipientSuccess;
    private boolean receivedDebitCardForChargeSuccess;

    @Override
    public void onCreate(Bundle savedInstance)
    {
        super.onCreate(savedInstance);
        setOptionsMenuEnabled(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_payments_update_debit_card, container, false);
        ButterKnife.inject(this, view);

        setFormFieldErrorStateRemovers();

        return view;
    }

    @Override
    protected MainViewTab getTab()
    {
        return MainViewTab.PAYMENTS;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setBackButtonEnabled(true);
        setActionBarTitle(R.string.add_debit_card);
        resetStates();
        bus.post(new RegionDefinitionEvent.RequestFormDefinitions(providerManager.getCachedActiveProvider().getCountry(), this.getContext()));
    }

    public boolean validate()
    {
        boolean allFieldsValid = true;
        Map<String, FieldDefinition> fieldDefinitionMap = formDefinitionWrapper.getFieldDefinitionsForForm(FORM_KEY);
        if (fieldDefinitionMap != null)
        {
            allFieldsValid = UIUtils.validateField(debitCardNumberField, fieldDefinitionMap.get(FormDefinitionKey.FieldDefinitionKey.DEBIT_CARD_NUMBER));
            allFieldsValid &= UIUtils.validateField(expirationDateField,
                    fieldDefinitionMap.get(FormDefinitionKey.FieldDefinitionKey.EXPIRATION_MONTH),
                    fieldDefinitionMap.get(FormDefinitionKey.FieldDefinitionKey.EXPIRATION_YEAR)
            );
            allFieldsValid &= UIUtils.validateField(securityCodeField, fieldDefinitionMap.get(FormDefinitionKey.FieldDefinitionKey.SECURITY_CODE_NUMBER));
            allFieldsValid &= UIUtils.validateField(taxIdField, fieldDefinitionMap.get(FormDefinitionKey.FieldDefinitionKey.TAX_ID_NUMBER));
        }

        return allFieldsValid;
    }

    private void setFormFieldErrorStateRemovers()
    {
        debitCardNumberField.getValue().addTextChangedListener(new UIUtils.FormFieldErrorStateRemover(debitCardNumberField));
        expirationDateField.getMonthValue().addTextChangedListener(new UIUtils.FormFieldErrorStateRemover(expirationDateField));
        expirationDateField.getYearValue().addTextChangedListener(new UIUtils.FormFieldErrorStateRemover(expirationDateField));
        securityCodeField.getValue().addTextChangedListener(new UIUtils.FormFieldErrorStateRemover(securityCodeField));
        taxIdField.getValue().addTextChangedListener(new UIUtils.FormFieldErrorStateRemover(taxIdField));
    }

    @OnClick(R.id.payments_update_info_debit_card_submit_button)
    public void onSubmitForm()
    {
        if (validate())
        {
            DebitCardInfo debitCardInfo = new DebitCardInfo();
            debitCardInfo.setCardNumber(debitCardNumberField.getValue().getText().toString());
            debitCardInfo.setCvc(securityCodeField.getValue().getText().toString());
            debitCardInfo.setExpMonth(expirationDateField.getMonthValue().getText().toString());
            debitCardInfo.setExpYear(expirationDateField.getYearValue().getText().toString());
            bus.post(new StripeEvents.RequestStripeTokenFromDebitCard(debitCardInfo, DEBIT_CARD_FOR_CHARGE_REQUEST_ID));
            bus.post(new StripeEvents.RequestStripeTokenFromDebitCard(debitCardInfo, DEBIT_CARD_RECIPIENT_REQUEST_ID));
            bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        }
        else
        {
            onFailure(R.string.form_not_filled_out_correctly);
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
            UIUtils.setFieldsFromDefinition(debitCardNumberField, fieldDefinitionMap.get(FormDefinitionKey.FieldDefinitionKey.DEBIT_CARD_NUMBER));
            UIUtils.setFieldsFromDefinition(securityCodeField, fieldDefinitionMap.get(FormDefinitionKey.FieldDefinitionKey.SECURITY_CODE_NUMBER));
            UIUtils.setFieldsFromDefinition(taxIdField, fieldDefinitionMap.get(FormDefinitionKey.FieldDefinitionKey.TAX_ID_NUMBER));

            UIUtils.setFieldsFromDefinition(expirationDateField,
                    fieldDefinitionMap.get(FormDefinitionKey.FieldDefinitionKey.EXPIRATION_DATE),
                    fieldDefinitionMap.get(FormDefinitionKey.FieldDefinitionKey.EXPIRATION_MONTH),
                    fieldDefinitionMap.get(FormDefinitionKey.FieldDefinitionKey.EXPIRATION_YEAR)
            );
        }

        debitCardNumberField.getValue().requestFocus();
    }

    @Subscribe
    public void onReceiveStripeTokenFromDebitCardSuccess(StripeEvents.ReceiveStripeTokenFromDebitCardSuccess event)
    {
        String token = event.stripeTokenResponse.getStripeToken();

        if (event.requestIdentifier == DEBIT_CARD_RECIPIENT_REQUEST_ID)
        {
            String taxIdString = taxIdField.getValue().getText().toString();
            String expMonthString = expirationDateField.getMonthValue().getText().toString();
            String expYearString = expirationDateField.getYearValue().getText().toString();
            String cardNumberString = debitCardNumberField.getValue().getText().toString();
            String cardNumberLast4Digits = cardNumberString.substring(cardNumberString.length() - 4);
            bus.post(new PaymentEvents.RequestCreateDebitCardRecipient(token, taxIdString, cardNumberLast4Digits, expMonthString, expYearString));
        }
        else if (event.requestIdentifier == DEBIT_CARD_FOR_CHARGE_REQUEST_ID)
        {
            bus.post(new PaymentEvents.RequestCreateDebitCardForCharge(token));
        }
    }

    @Subscribe
    public void onReceiveStripeTokenFromDebitCardError(StripeEvents.ReceiveStripeTokenFromDebitCardError event)
    {
        onFailure(R.string.update_debit_card_failed);
    }

    private void onFailure(int errorStringId)
    {
        resetStates();
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        showToast(errorStringId, Toast.LENGTH_LONG);
    }

    private void checkSuccess()
    {
        if (receivedDebitCardForChargeSuccess && receivedDebitCardRecipientSuccess)
        {
            onSuccess();
        }
    }

    private void onSuccess()
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        showToast(R.string.update_debit_card_success, Toast.LENGTH_LONG);
        UIUtils.dismissOnBackPressed(getActivity());
    }

    private void resetStates()
    {
        receivedDebitCardRecipientSuccess = false;
        receivedDebitCardForChargeSuccess = false;
    }

    @Subscribe
    public void onReceiveCreateDebitCardRecipientSuccess(PaymentEvents.ReceiveCreateDebitCardRecipientSuccess event)
    {
        receivedDebitCardRecipientSuccess = true;
        checkSuccess();
    }

    @Subscribe
    public void onReceiveCreateDebitCardRecipientError(PaymentEvents.ReceiveCreateDebitCardRecipientError event)
    {
        onFailure(R.string.update_debit_card_failed);
    }

    @Subscribe
    public void onReceiveCreateDebitCardForChargeSuccess(PaymentEvents.ReceiveCreateDebitCardForChargeSuccess event)
    {
        receivedDebitCardForChargeSuccess = true;
        checkSuccess();
    }

    @Subscribe
    public void onReceiveCreateDebitCardForChargeError(PaymentEvents.ReceiveCreateDebitCardForChargeError event)
    {
        onFailure(R.string.update_debit_card_failed);
    }
}
