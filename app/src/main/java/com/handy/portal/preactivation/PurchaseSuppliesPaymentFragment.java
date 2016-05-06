package com.handy.portal.preactivation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.handy.portal.R;
import com.handy.portal.constant.FormDefinitionKey;
import com.handy.portal.event.RegionDefinitionEvent;
import com.handy.portal.model.definitions.FieldDefinition;
import com.handy.portal.ui.view.DateFormFieldTableRow;
import com.handy.portal.ui.view.FormFieldTableRow;
import com.handy.portal.ui.view.SimpleContentLayout;
import com.handy.portal.util.UIUtils;
import com.squareup.otto.Subscribe;

import java.util.Map;

import butterknife.Bind;

public class PurchaseSuppliesPaymentFragment extends PreActivationSetupStepFragment
{
    @Bind(R.id.credit_card_number_field)
    FormFieldTableRow mCreditCardNumberField;
    @Bind(R.id.expiration_date_field)
    DateFormFieldTableRow mExpirationDateField;
    @Bind(R.id.security_code_field)
    FormFieldTableRow mSecurityCodeField;
    @Bind(R.id.order_summary)
    SimpleContentLayout mOrderSummary;

    @Override
    public void onResume()
    {
        super.onResume();
        bus.post(new RegionDefinitionEvent.RequestFormDefinitions("US", getActivity()));
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        // FIXME: Pull form server
        final String orderTotalFormatted = getString(R.string.order_total_formatted, "$75");
        mOrderSummary.setContent(getString(R.string.supply_starter_kit), orderTotalFormatted)
                .setImage(getResources().getDrawable(R.drawable.img_supplies));
    }

    @Subscribe
    public void onReceiveFormDefinitions(
            final RegionDefinitionEvent.ReceiveFormDefinitionsSuccess event)
    {
        final Map<String, FieldDefinition> fieldDefinitions = event.formDefinitionWrapper
                .getFieldDefinitionsForForm(FormDefinitionKey.UPDATE_CREDIT_CARD_INFO);
        UIUtils.setFieldsFromDefinition(mCreditCardNumberField,
                fieldDefinitions.get(FormDefinitionKey.FieldDefinitionKey.CREDIT_CARD_NUMBER));
        UIUtils.setFieldsFromDefinition(mExpirationDateField,
                fieldDefinitions.get(FormDefinitionKey.FieldDefinitionKey.EXPIRATION_DATE),
                fieldDefinitions.get(FormDefinitionKey.FieldDefinitionKey.EXPIRATION_MONTH),
                fieldDefinitions.get(FormDefinitionKey.FieldDefinitionKey.EXPIRATION_YEAR));
        UIUtils.setFieldsFromDefinition(mSecurityCodeField,
                fieldDefinitions.get(FormDefinitionKey.FieldDefinitionKey.SECURITY_CODE_NUMBER));
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
        return getString(R.string.wont_charge_until_two_weeks);
    }

    @Override
    protected String getPrimaryButtonText()
    {
        return getString(R.string.continue_to_confirmation);
    }

    @Override
    protected void onPrimaryButtonClicked()
    {
        UIUtils.dismissKeyboard(getActivity());
        goToStep(PreActivationSetupStep.PURCHASE_SUPPLIES_CONFIRMATION);
    }
}
