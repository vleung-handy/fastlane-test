package com.handy.portal.payments.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;
import com.handy.portal.constant.FormDefinitionKey;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.RegionDefinitionEvent;
import com.handy.portal.event.StripeEvent;
import com.handy.portal.manager.ProviderManager;
import com.handy.portal.model.Provider;
import com.handy.portal.model.definitions.FieldDefinition;
import com.handy.portal.model.definitions.FormDefinitionWrapper;
import com.handy.portal.payments.PaymentEvent;
import com.handy.portal.payments.model.BankAccountInfo;
import com.handy.portal.ui.fragment.ActionBarFragment;
import com.handy.portal.ui.view.FormFieldTableRow;
import com.handy.portal.util.UIUtils;
import com.squareup.otto.Subscribe;

import java.util.Map;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PaymentsUpdateBankAccountFragment extends ActionBarFragment
{
    @Bind(R.id.routing_number_field)
    FormFieldTableRow routingNumberField;

    @Bind(R.id.account_number_field)
    FormFieldTableRow accountNumberField;

    @Bind(R.id.tax_id_field)
    FormFieldTableRow taxIdField;

    @Bind(R.id.bank_account_setup_helper)
    ViewGroup bankAccountSetupHelper;

    @Inject
    ProviderManager providerManager;

    FormDefinitionWrapper formDefinitionWrapper;

    private static final String FORM_KEY = FormDefinitionKey.UPDATE_BANK_INFO;

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

        View view = inflater.inflate(R.layout.fragment_payments_update_bank_account, container, false);
        ButterKnife.bind(this, view);

        setFormFieldErrorStateRemovers();

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        setActionBarTitle(R.string.add_bank_account);
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
        Provider provider = providerManager.getCachedActiveProvider();

        if(provider != null)
        {
            bus.post(new RegionDefinitionEvent.RequestFormDefinitions(provider.getCountry(), this.getContext()));
            if (!provider.isUS())
            {
                bankAccountSetupHelper.setVisibility(View.GONE);
            }
        }
        else
        {
            Crashlytics.log("PaymentsUpdateBankAccountFragment null cached provider on resume");
        }
    }

    private boolean validate()
    {
        boolean allFieldsValid = true;
        Map<String, FieldDefinition> fieldDefinitionMap = formDefinitionWrapper.getFieldDefinitionsForForm(FORM_KEY);
        if (fieldDefinitionMap != null)
        {
            allFieldsValid = UIUtils.validateField(routingNumberField, fieldDefinitionMap.get(FormDefinitionKey.FieldDefinitionKey.ROUTING_NUMBER));
            allFieldsValid &= UIUtils.validateField(accountNumberField, fieldDefinitionMap.get(FormDefinitionKey.FieldDefinitionKey.ACCOUNT_NUMBER));
            allFieldsValid &= UIUtils.validateField(taxIdField, fieldDefinitionMap.get(FormDefinitionKey.FieldDefinitionKey.TAX_ID_NUMBER));
        }
        return allFieldsValid;
    }

    @OnClick(R.id.payments_update_info_bank_account_submit_button)
    public void onSubmitForm()
    {
        if (validate())
        {
            String routingNumber = routingNumberField.getValue().getText().toString();
            String accountNumber = accountNumberField.getValue().getText().toString();
            BankAccountInfo bankAccountInfo = new BankAccountInfo();
            bankAccountInfo.setAccountNumber(accountNumber);
            bankAccountInfo.setRoutingNumber(routingNumber);

            Provider provider = providerManager.getCachedActiveProvider();
            if (provider != null)
            {
                bankAccountInfo.setCurrency(provider.getPaymentCurrencyCode());
                bankAccountInfo.setCountry(provider.getCountry());
            }
            bus.post(new StripeEvent.RequestStripeTokenFromBankAccount(bankAccountInfo));
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

    private void setFormFieldErrorStateRemovers()
    {
        routingNumberField.getValue().addTextChangedListener(new UIUtils.FormFieldErrorStateRemover(routingNumberField));
        accountNumberField.getValue().addTextChangedListener(new UIUtils.FormFieldErrorStateRemover(accountNumberField));
        taxIdField.getValue().addTextChangedListener(new UIUtils.FormFieldErrorStateRemover(taxIdField));
    }

    private void updateFormWithDefinitions(FormDefinitionWrapper formDefinitionWrapper)
    {
        Map<String, FieldDefinition> fieldDefinitionMap = formDefinitionWrapper.getFieldDefinitionsForForm(FORM_KEY);
        if (fieldDefinitionMap != null)
        {
            UIUtils.setFieldsFromDefinition(routingNumberField, fieldDefinitionMap.get(FormDefinitionKey.FieldDefinitionKey.ROUTING_NUMBER));
            UIUtils.setFieldsFromDefinition(accountNumberField, fieldDefinitionMap.get(FormDefinitionKey.FieldDefinitionKey.ACCOUNT_NUMBER));
            UIUtils.setFieldsFromDefinition(taxIdField, fieldDefinitionMap.get(FormDefinitionKey.FieldDefinitionKey.TAX_ID_NUMBER));
        }

        routingNumberField.getValue().requestFocus();
    }

    @Subscribe
    public void onReceiveStripeTokenFromBankAccountSuccess(StripeEvent.ReceiveStripeTokenFromBankAccountSuccess event)
    {
        String token = event.stripeTokenResponse.getStripeToken();

        String taxIdString = taxIdField.getValue().getText().toString();
        String accountNumberString = accountNumberField.getValue().getText().toString();
        String accountNumberLast4Digits = accountNumberString.substring(accountNumberString.length() - 4);
        bus.post(new PaymentEvent.RequestCreateBankAccount(token, taxIdString, accountNumberLast4Digits));
    }

    private void onFailure(int errorStringId)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        showToast(errorStringId, Toast.LENGTH_LONG);
    }

    @Subscribe
    public void onReceiveStripeTokenFromBankAccountError(StripeEvent.ReceiveStripeTokenFromBankAccountError event)
    {
        onFailure(R.string.update_bank_account_failed);
    }

    @Subscribe
    public void onReceiveCreateBankAccountSuccess(PaymentEvent.ReceiveCreateBankAccountSuccess event)
    {
        if (event.successfullyCreated)
        {
            bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
            showToast(R.string.update_bank_account_success, Toast.LENGTH_LONG);
            UIUtils.dismissOnBackPressed(getActivity());
        }
        else
        {
            onFailure(R.string.update_bank_account_failed);
        }
    }

    @Subscribe
    public void onReceiveCreateBankAccountError(PaymentEvent.ReceiveCreateBankAccountError event)
    {
        onFailure(R.string.update_bank_account_failed);
    }
}
