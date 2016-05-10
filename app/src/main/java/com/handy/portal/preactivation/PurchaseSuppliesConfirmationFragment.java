package com.handy.portal.preactivation;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;

import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.Country;
import com.handy.portal.constant.FormDefinitionKey;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.ProfileEvent;
import com.handy.portal.event.RegionDefinitionEvent;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.OnboardingSuppliesLog;
import com.handy.portal.model.Address;
import com.handy.portal.model.ProviderPersonalInfo;
import com.handy.portal.model.definitions.FieldDefinition;
import com.handy.portal.model.onboarding.OnboardingSuppliesInfo;
import com.handy.portal.payments.model.PaymentInfo;
import com.handy.portal.ui.fragment.dialog.TransientOverlayDialogFragment;
import com.handy.portal.ui.view.FormFieldTableRow;
import com.handy.portal.ui.view.SimpleContentLayout;
import com.handy.portal.util.CurrencyUtils;
import com.handy.portal.util.FragmentUtils;
import com.handy.portal.util.UIUtils;
import com.squareup.otto.Subscribe;
import com.stripe.android.model.Card;

import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

public class PurchaseSuppliesConfirmationFragment extends PreActivationFlowFragment
{
    private static final int SUCCESS_OVERLAY_DELAY_MILLIS = 1000;

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
    @Bind(R.id.cancel_edit)
    View mCancelEdit;

    private Map<String, FieldDefinition> mFieldDefinitions;
    private ProviderPersonalInfo mProviderPersonalInfo;
    private OnboardingSuppliesInfo mOnboardingSuppliesInfo;
    private String mCardLast4;
    private String mCardType;

    public static PurchaseSuppliesConfirmationFragment newInstance(
            final OnboardingSuppliesInfo onboardingSuppliesInfo,
            final Card card)
    {
        final PurchaseSuppliesConfirmationFragment fragment =
                new PurchaseSuppliesConfirmationFragment();
        final Bundle arguments = new Bundle();
        arguments.putSerializable(BundleKeys.ONBOARDING_SUPPLIES, onboardingSuppliesInfo);
        arguments.putString(BundleKeys.CARD_TYPE, card.getType());
        arguments.putString(BundleKeys.CARD_LAST4, card.getLast4());
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mOnboardingSuppliesInfo = (OnboardingSuppliesInfo) getArguments()
                .getSerializable(BundleKeys.ONBOARDING_SUPPLIES);
        mCardLast4 = getArguments().getString(BundleKeys.CARD_LAST4);
        mCardType = getArguments().getString(BundleKeys.CARD_TYPE);
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
        mShippingSummary.setContent(getString(R.string.shipping_address),
                getString(R.string.loading));
        mPaymentSummary.setContent(getString(R.string.payment_method),
                getString(R.string.card_info_formatted, mCardType, mCardLast4));
        final PaymentInfo cost = mOnboardingSuppliesInfo.getCost();
        final String orderTotalFormatted = getString(R.string.order_total_formatted,
                CurrencyUtils.formatPriceWithoutCents(cost.getAmount(), cost.getCurrencySymbol()));
        mOrderSummary.setContent(getString(R.string.supply_starter_kit), orderTotalFormatted)
                .setImage(getResources().getDrawable(R.drawable.img_supplies));

        bus.post(new LogEvent.AddLogEvent(new OnboardingSuppliesLog(
                OnboardingSuppliesLog.Types.CONFIRMATION_SCREEN_SHOWN)));
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (mFieldDefinitions == null)
        {
            bus.post(new RegionDefinitionEvent.RequestFormDefinitions(Country.US, getActivity()));
        }

        if (mProviderPersonalInfo != null)
        {
            populateShippingSummary();
        }
        else
        {
            showLoadingOverlay();
            bus.post(new ProfileEvent.RequestProviderProfile());
        }
    }

    @Subscribe
    public void onReceiveProviderInfoSuccess(final ProfileEvent.ReceiveProviderProfileSuccess event)
    {
        mProviderPersonalInfo = event.providerProfile.getProviderPersonalInfo();
        populateShippingSummary();
        hideLoadingOverlay();
    }

    @Subscribe
    public void onReceiveProviderInfoError(final ProfileEvent.ReceiveProviderProfileError event)
    {
        showEditAddressForm();
        mCancelEdit.setVisibility(View.GONE);
        hideLoadingOverlay();
    }

    private void populateShippingSummary()
    {
        final Address address = mProviderPersonalInfo.getAddress();
        mShippingSummary.setContent(getString(R.string.shipping_address),
                mProviderPersonalInfo.getFirstName() + " " +
                        mProviderPersonalInfo.getLastName() + "\n" + address.getStreetAddress() +
                        "\n" + address.getCityStateZip())
                .setAction(getString(R.string.edit), new View.OnClickListener()
                {
                    @Override
                    public void onClick(final View v)
                    {
                        showEditAddressForm();
                    }
                });
    }

    private void showEditAddressForm()
    {
        if (mProviderPersonalInfo != null)
        {
            final Address address = mProviderPersonalInfo.getAddress();
            mAddress1Field.getValue().setText(address.getAddress1());
            mAddress2Field.getValue().setText(address.getAddress2());
            mCityField.getValue().setText(address.getCity());
            mStateField.getValue().setText(address.getState());
            mZipField.getValue().setText(address.getZip());
        }
        mEditAddressForm.setVisibility(View.VISIBLE);
        mShippingSummary.setVisibility(View.GONE);
        mAddress1Field.requestFocus();

        bus.post(new LogEvent.AddLogEvent(new OnboardingSuppliesLog(
                OnboardingSuppliesLog.Types.EDIT_ADDRESS_SHOWN)));
    }

    @Subscribe
    public void onReceiveFormDefinitions(
            final RegionDefinitionEvent.ReceiveFormDefinitionsSuccess event)
    {
        mFieldDefinitions = event.formDefinitionWrapper
                .getFieldDefinitionsForForm(FormDefinitionKey.UPDATE_ADDRESS);
        UIUtils.setFieldsFromDefinition(mAddress1Field,
                mFieldDefinitions.get(FormDefinitionKey.FieldDefinitionKey.ADDRESS));
        UIUtils.setFieldsFromDefinition(mAddress2Field,
                mFieldDefinitions.get(FormDefinitionKey.FieldDefinitionKey.ADDRESS2));
        UIUtils.setFieldsFromDefinition(mCityField,
                mFieldDefinitions.get(FormDefinitionKey.FieldDefinitionKey.CITY));
        UIUtils.setFieldsFromDefinition(mStateField,
                mFieldDefinitions.get(FormDefinitionKey.FieldDefinitionKey.STATE));
        UIUtils.setFieldsFromDefinition(mZipField,
                mFieldDefinitions.get(FormDefinitionKey.FieldDefinitionKey.ZIP_CODE));
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
        return mOnboardingSuppliesInfo.getChargeNotice();
    }

    @Override
    protected String getPrimaryButtonText()
    {
        return getString(R.string.confirm_purchase);
    }

    @Override
    protected void onPrimaryButtonClicked()
    {
        bus.post(new LogEvent.AddLogEvent(new OnboardingSuppliesLog(
                OnboardingSuppliesLog.Types.CONFIRM_PURCHASE_SELECTED)));

        UIUtils.dismissKeyboard(getActivity());

        if (mEditAddressForm.getVisibility() == View.VISIBLE)
        {
            if (validate())
            {
                bus.post(new ProfileEvent.RequestProfileUpdate(
                        "",
                        "",
                        mAddress1Field.getValue().getText(),
                        mAddress2Field.getValue().getText(),
                        mCityField.getValue().getText(),
                        mStateField.getValue().getText(),
                        mZipField.getValue().getText()
                ));
                showLoadingOverlay();
                bus.post(new LogEvent.AddLogEvent(new OnboardingSuppliesLog(
                        OnboardingSuppliesLog.ServerTypes.UPDATE_ADDRESS.submitted())));
            }
        }
        else
        {
            bus.post(new HandyEvent.RequestOnboardingSupplies(true));
            showLoadingOverlay();
            bus.post(new LogEvent.AddLogEvent(new OnboardingSuppliesLog(
                    OnboardingSuppliesLog.ServerTypes.REQUEST_SUPPLIES.submitted())));
        }
    }

    @Subscribe
    public void onReceiveProfileUpdateSuccess(final ProfileEvent.ReceiveProfileUpdateSuccess event)
    {
        bus.post(new LogEvent.AddLogEvent(new OnboardingSuppliesLog(
                OnboardingSuppliesLog.ServerTypes.UPDATE_ADDRESS.success())));
        mProviderPersonalInfo = event.providerPersonalInfo;
        populateShippingSummary();
        bus.post(new HandyEvent.RequestOnboardingSupplies(true));
        bus.post(new LogEvent.AddLogEvent(new OnboardingSuppliesLog(
                OnboardingSuppliesLog.ServerTypes.REQUEST_SUPPLIES.submitted())));
    }

    @Subscribe
    public void onReceiveOnboardingSuppliesSuccess(final HandyEvent.ReceiveOnboardingSuppliesSuccess event)
    {
        bus.post(new LogEvent.AddLogEvent(new OnboardingSuppliesLog(
                OnboardingSuppliesLog.ServerTypes.REQUEST_SUPPLIES.success())));
        hideLoadingOverlay();
        final TransientOverlayDialogFragment fragment = TransientOverlayDialogFragment.newInstance(
                R.anim.overlay_fade_in_then_out,
                R.drawable.ic_success_circle,
                R.string.supplies_ordered
        );
        FragmentUtils.safeLaunchDialogFragment(fragment, getActivity(), null);
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                terminate();
            }
        }, SUCCESS_OVERLAY_DELAY_MILLIS);
    }

    @Subscribe
    public void onReceiveProfileUpdateError(final ProfileEvent.ReceiveProfileUpdateError event)
    {
        bus.post(new LogEvent.AddLogEvent(new OnboardingSuppliesLog(
                OnboardingSuppliesLog.ServerTypes.UPDATE_ADDRESS.error())));
        hideLoadingOverlay();
        showError(event.error.getMessage());
    }

    @Subscribe
    public void onReceiveOnboardingSuppliesError(final HandyEvent.ReceiveOnboardingSuppliesError event)
    {
        bus.post(new LogEvent.AddLogEvent(new OnboardingSuppliesLog(
                OnboardingSuppliesLog.ServerTypes.REQUEST_SUPPLIES.error())));
        hideLoadingOverlay();
        showError(event.error.getMessage());
    }

    private boolean validate()
    {
        boolean allFieldsValid = UIUtils.validateField(mAddress1Field,
                mFieldDefinitions.get(FormDefinitionKey.FieldDefinitionKey.ADDRESS));
        allFieldsValid &= UIUtils.validateField(mAddress2Field,
                mFieldDefinitions.get(FormDefinitionKey.FieldDefinitionKey.ADDRESS2));
        allFieldsValid &= UIUtils.validateField(mCityField,
                mFieldDefinitions.get(FormDefinitionKey.FieldDefinitionKey.CITY));
        allFieldsValid &= UIUtils.validateField(mStateField,
                mFieldDefinitions.get(FormDefinitionKey.FieldDefinitionKey.STATE));
        allFieldsValid &= UIUtils.validateField(mZipField,
                mFieldDefinitions.get(FormDefinitionKey.FieldDefinitionKey.ZIP_CODE));
        return allFieldsValid;
    }
}
