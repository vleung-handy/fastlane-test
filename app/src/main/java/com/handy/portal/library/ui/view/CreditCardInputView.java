package com.handy.portal.library.ui.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.TableLayout;

import com.handy.portal.R;
import com.handy.portal.constant.FormDefinitionKey;
import com.handy.portal.library.util.UIUtils;
import com.handy.portal.model.definitions.FieldDefinition;
import com.stripe.android.model.Card;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * a widget for inputting credit card info
 * with a method to update based on a form definition
 */
public class CreditCardInputView extends TableLayout
{
    @BindView(R.id.credit_card_number_field)
    FormFieldTableRow mCreditCardNumberField;
    @BindView(R.id.expiration_date_field)
    DateFormFieldTableRow mExpirationDateField;
    @BindView(R.id.security_code_field)
    FormFieldTableRow mSecurityCodeField;

    private Map<String, FieldDefinition> mPaymentFieldDefinitions;

    public CreditCardInputView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        inflate(getContext(), R.layout.view_edit_payment, this);
        ButterKnife.bind(this);
        setEnabled(false);
    }

    public FormFieldTableRow getCreditCardNumberField()
    {
        return mCreditCardNumberField;
    }

    public Map<String, FieldDefinition> getPaymentFieldDefinitions()
    {
        return mPaymentFieldDefinitions;
    }

    /**
     * this must be called before this widget can be used
     * @param paymentFieldDefinitions
     */
    public void updateWithFormFieldDefinitions(@NonNull Map<String, FieldDefinition>
                                                       paymentFieldDefinitions)
    {
        UIUtils.setFieldsFromDefinition(mCreditCardNumberField,
                paymentFieldDefinitions.get(FormDefinitionKey.FieldDefinitionKey.CREDIT_CARD_NUMBER));
        UIUtils.setFieldsFromDefinition(mExpirationDateField,
                paymentFieldDefinitions.get(FormDefinitionKey.FieldDefinitionKey.EXPIRATION_DATE),
                paymentFieldDefinitions.get(FormDefinitionKey.FieldDefinitionKey.EXPIRATION_MONTH),
                paymentFieldDefinitions.get(FormDefinitionKey.FieldDefinitionKey.EXPIRATION_YEAR));
        UIUtils.setFieldsFromDefinition(mSecurityCodeField,
                paymentFieldDefinitions.get(FormDefinitionKey.FieldDefinitionKey.SECURITY_CODE_NUMBER));
        mPaymentFieldDefinitions = paymentFieldDefinitions;
        setEnabled(true);
    }

    public boolean validateFields()
    {
        if(mPaymentFieldDefinitions == null) return true;
        boolean allFieldsValid = UIUtils.validateField(mCreditCardNumberField,
                mPaymentFieldDefinitions.get(FormDefinitionKey.FieldDefinitionKey.CREDIT_CARD_NUMBER));
        allFieldsValid &= UIUtils.validateField(mExpirationDateField,
                mPaymentFieldDefinitions.get(FormDefinitionKey.FieldDefinitionKey.EXPIRATION_MONTH),
                mPaymentFieldDefinitions.get(FormDefinitionKey.FieldDefinitionKey.EXPIRATION_YEAR));
        allFieldsValid &= UIUtils.validateField(mSecurityCodeField,
                mPaymentFieldDefinitions.get(FormDefinitionKey.FieldDefinitionKey.SECURITY_CODE_NUMBER));
        return allFieldsValid;
    }

    public Card getCardFromFields()
    {
        Card card = new Card(
                mCreditCardNumberField.getValue().getText().toString(),
                Integer.parseInt(mExpirationDateField.getMonthValue().getText().toString()),
                Integer.parseInt(mExpirationDateField.getYearValue().getText().toString()),
                mSecurityCodeField.getValue().getText().toString()
        );
        return card;
    }

}
