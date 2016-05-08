package com.handy.portal.preactivation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.handy.portal.R;
import com.handy.portal.constant.FormDefinitionKey;
import com.handy.portal.event.RegionDefinitionEvent;
import com.handy.portal.model.definitions.FieldDefinition;
import com.handy.portal.ui.view.FormFieldTableRow;
import com.handy.portal.ui.view.SimpleContentLayout;
import com.handy.portal.util.UIUtils;
import com.squareup.otto.Subscribe;

import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

public class PurchaseSuppliesConfirmationFragment extends PreActivationSetupStepFragment
{
    @Bind(R.id.shipping_summary)
    SimpleContentLayout mShippingSummary;
    @Bind(R.id.edit_address_form)
    View mEditAddressForm;
    @Bind(R.id.payment_summary)
    SimpleContentLayout mPaymentSummary;
    @Bind(R.id.order_summary)
    SimpleContentLayout mOrderSummary;
    @Bind(R.id.address_1_field)
    FormFieldTableRow mAddress1Field;
    @Bind(R.id.address_2_field)
    FormFieldTableRow mAddress2Field;
    @Bind(R.id.city_field)
    FormFieldTableRow mCityField;
    @Bind(R.id.state_field)
    FormFieldTableRow mStateField;
    @Bind(R.id.zip_field)
    FormFieldTableRow mZipField;

    public static PurchaseSuppliesConfirmationFragment newInstance()
    {
        return new PurchaseSuppliesConfirmationFragment();
    }

    @OnClick(R.id.cancel_edit)
    void onCancelEditClicked()
    {
        mShippingSummary.setVisibility(View.VISIBLE);
        mEditAddressForm.setVisibility(View.GONE);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        // FIXME: Pull from server
        mShippingSummary.setContent(getString(R.string.shipping_address),
                "John Doe\n123 Penny Lane\nBrooklyn, NY 12345")
                .setAction(getString(R.string.edit), new View.OnClickListener()
                {
                    @Override
                    public void onClick(final View v)
                    {
                        mEditAddressForm.setVisibility(View.VISIBLE);
                        mShippingSummary.setVisibility(View.GONE);
                    }
                });
        // FIXME: Pull from arguments
        mPaymentSummary.setContent(getString(R.string.payment_method),
                "Visa ending in 1234");
        // FIXME: Pull form server
        final String orderTotalFormatted = getString(R.string.order_total_formatted, "$75");
        mOrderSummary.setContent(getString(R.string.supply_starter_kit), orderTotalFormatted)
                .setImage(getResources().getDrawable(R.drawable.img_supplies));
    }

    @Override
    public void onResume()
    {
        super.onResume();
        bus.post(new RegionDefinitionEvent.RequestFormDefinitions("US", getActivity()));
    }

    @Subscribe
    public void onReceiveFormDefinitions(
            final RegionDefinitionEvent.ReceiveFormDefinitionsSuccess event)
    {
        final Map<String, FieldDefinition> fieldDefinitions = event.formDefinitionWrapper
                .getFieldDefinitionsForForm(FormDefinitionKey.UPDATE_ADDRESS);
        UIUtils.setFieldsFromDefinition(mAddress1Field,
                fieldDefinitions.get(FormDefinitionKey.FieldDefinitionKey.ADDRESS));
        UIUtils.setFieldsFromDefinition(mAddress2Field,
                fieldDefinitions.get(FormDefinitionKey.FieldDefinitionKey.ADDRESS2));
        UIUtils.setFieldsFromDefinition(mCityField,
                fieldDefinitions.get(FormDefinitionKey.FieldDefinitionKey.CITY));
        UIUtils.setFieldsFromDefinition(mStateField,
                fieldDefinitions.get(FormDefinitionKey.FieldDefinitionKey.STATE));
        UIUtils.setFieldsFromDefinition(mZipField,
                fieldDefinitions.get(FormDefinitionKey.FieldDefinitionKey.ZIP_CODE));
    }

    @Override
    protected int getLayoutResId()
    {
        return R.layout.view_purchase_supplies_confirmation;
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
        return getString(R.string.confirm_your_purchase);
    }

    @Nullable
    @Override
    protected String getSubHeaderText()
    {
        return null;
    }

    @Override
    protected String getPrimaryButtonText()
    {
        return getString(R.string.confirm_purchase);
    }

    @Override
    protected void onPrimaryButtonClicked()
    {
        UIUtils.dismissKeyboard(getActivity());
        next(null);
    }
}
