package com.handy.portal.payments.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.handy.portal.R;
import com.handy.portal.core.constant.FormDefinitionKey;
import com.handy.portal.core.constant.MainViewPage;
import com.handy.portal.core.event.RegionDefinitionEvent;
import com.handy.portal.core.event.StripeEvent;
import com.handy.portal.core.manager.ProviderManager;
import com.handy.portal.core.model.ProviderProfile;
import com.handy.portal.core.model.definitions.FieldDefinition;
import com.handy.portal.core.model.definitions.FormDefinitionWrapper;
import com.handy.portal.core.ui.fragment.ActionBarFragment;
import com.handy.portal.library.ui.view.DateFormFieldTableRow;
import com.handy.portal.library.ui.view.FormFieldTableRow;
import com.handy.portal.library.util.UIUtils;
import com.handy.portal.payments.PaymentEvent;
import com.handy.portal.payments.model.DebitCardInfo;

import org.greenrobot.eventbus.Subscribe;

import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PaymentsUpdateDebitCardFragment extends ActionBarFragment {
    @BindView(R.id.debit_card_number_field)
    FormFieldTableRow debitCardNumberField;

    @BindView(R.id.expiration_date_field)
    DateFormFieldTableRow expirationDateField;

    @BindView(R.id.security_code_field)
    FormFieldTableRow securityCodeField;

    @BindView(R.id.tax_id_field)
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

    public static PaymentsUpdateDebitCardFragment newInstance() {
        return new PaymentsUpdateDebitCardFragment();
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setOptionsMenuEnabled(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
        view.addView(inflater.inflate(R.layout.fragment_payments_update_debit_card, container, false));

        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setActionBarTitle(R.string.add_debit_card);
    }

    @Override
    protected MainViewPage getAppPage() {
        return MainViewPage.PAYMENTS;
    }

    @Override
    public void onResume() {
        super.onResume();
        setBackButtonEnabled(true);
        resetStates();

        bus.register(this);

        final ProviderProfile providerProfile = providerManager.getCachedProviderProfile();
        if (providerProfile != null && providerProfile.getProviderPersonalInfo() != null) {
            bus.post(new RegionDefinitionEvent.RequestFormDefinitions(
                    providerProfile.getProviderPersonalInfo().getAddress().getCountry(),
                    this.getContext()));
        }
    }

    @Override
    public void onPause() {
        bus.unregister(this);
        super.onPause();
    }

    public boolean validate() {
        boolean allFieldsValid = true;
        Map<String, FieldDefinition> fieldDefinitionMap = formDefinitionWrapper.getFieldDefinitionsForForm(FORM_KEY);
        if (fieldDefinitionMap != null) {
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

    @OnClick(R.id.payments_update_info_debit_card_submit_button)
    public void onSubmitForm() {
        if (validate()) {
            DebitCardInfo debitCardInfo = new DebitCardInfo();
            debitCardInfo.setCardNumber(debitCardNumberField.getValue().getText().toString());
            debitCardInfo.setCvc(securityCodeField.getValue().getText().toString());
            debitCardInfo.setExpMonth(expirationDateField.getMonthValue().getText().toString());
            debitCardInfo.setExpYear(expirationDateField.getYearValue().getText().toString());

            ProviderProfile providerProfile = providerManager.getCachedProviderProfile();
            if (providerProfile != null && providerProfile.getProviderPersonalInfo() != null) {
                debitCardInfo.setCurrencyCode(providerProfile.getProviderPersonalInfo()
                        .getCurrencyCode().toLowerCase());
            }
            bus.post(new StripeEvent.RequestStripeTokenFromDebitCard(debitCardInfo, DEBIT_CARD_FOR_CHARGE_REQUEST_ID));
            bus.post(new StripeEvent.RequestStripeTokenFromDebitCard(debitCardInfo, DEBIT_CARD_RECIPIENT_REQUEST_ID));
            showProgressSpinner(true);
        }
        else {
            onFailure(R.string.form_not_filled_out_correctly);
        }
    }

    @Subscribe
    public void onReceiveFormDefinitionsSuccess(RegionDefinitionEvent.ReceiveFormDefinitionsSuccess event) {
        this.formDefinitionWrapper = event.formDefinitionWrapper;
        updateFormWithDefinitions(formDefinitionWrapper);
    }

    private void updateFormWithDefinitions(FormDefinitionWrapper formDefinitionWrapper) {
        Map<String, FieldDefinition> fieldDefinitionMap = formDefinitionWrapper.getFieldDefinitionsForForm(FORM_KEY);

        if (fieldDefinitionMap != null) {
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
    public void onReceiveStripeTokenFromDebitCardSuccess(StripeEvent.ReceiveStripeTokenFromDebitCardSuccess event) {
        String token = event.stripeTokenResponse.getStripeToken();

        if (event.requestIdentifier == DEBIT_CARD_RECIPIENT_REQUEST_ID) {
            String taxIdString = taxIdField.getValue().getText().toString();
            String expMonthString = expirationDateField.getMonthValue().getText().toString();
            String expYearString = expirationDateField.getYearValue().getText().toString();
            String cardNumberString = debitCardNumberField.getValue().getText().toString();
            String cardNumberLast4Digits = cardNumberString.substring(cardNumberString.length() - 4);
            bus.post(new PaymentEvent.RequestCreateDebitCardRecipient(token, taxIdString, cardNumberLast4Digits, expMonthString, expYearString));
        }
        else if (event.requestIdentifier == DEBIT_CARD_FOR_CHARGE_REQUEST_ID) {
            bus.post(new PaymentEvent.RequestCreateDebitCardForCharge(token));
        }
    }

    @Subscribe
    public void onReceiveStripeTokenFromDebitCardError(StripeEvent.ReceiveStripeTokenFromDebitCardError event) {
        onFailure(R.string.update_debit_card_failed);
    }

    private void onFailure(int errorStringId) {
        resetStates();
        hideProgressSpinner();
        showToast(errorStringId, Toast.LENGTH_LONG);
    }

    private void checkSuccess() {
        if (receivedDebitCardForChargeSuccess && receivedDebitCardRecipientSuccess) {
            onSuccess();
        }
    }

    private void onSuccess() {
        hideProgressSpinner();
        showToast(R.string.update_debit_card_success, Toast.LENGTH_LONG);
        UIUtils.dismissOnBackPressed(getActivity());
    }

    private void resetStates() {
        receivedDebitCardRecipientSuccess = false;
        receivedDebitCardForChargeSuccess = false;
    }

    @Subscribe
    public void onReceiveCreateDebitCardRecipientSuccess(PaymentEvent.ReceiveCreateDebitCardRecipientSuccess event) {
        receivedDebitCardRecipientSuccess = true;
        checkSuccess();
    }

    @Subscribe
    public void onReceiveCreateDebitCardRecipientError(PaymentEvent.ReceiveCreateDebitCardRecipientError event) {
        onFailure(R.string.update_debit_card_failed);
    }

    @Subscribe
    public void onReceiveCreateDebitCardForChargeSuccess(PaymentEvent.ReceiveCreateDebitCardForChargeSuccess event) {
        receivedDebitCardForChargeSuccess = true;
        checkSuccess();
    }

    @Subscribe
    public void onReceiveCreateDebitCardForChargeError(PaymentEvent.ReceiveCreateDebitCardForChargeError event) {
        onFailure(R.string.update_debit_card_failed);
    }
}
