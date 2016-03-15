package com.handy.portal.ui.fragment.profile;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;
import com.handy.portal.constant.FormDefinitionKey;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.ProfileEvent;
import com.handy.portal.event.RegionDefinitionEvent;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.manager.ProviderManager;
import com.handy.portal.model.Provider;
import com.handy.portal.model.ProviderPersonalInfo;
import com.handy.portal.model.ProviderProfile;
import com.handy.portal.model.definitions.FieldDefinition;
import com.handy.portal.model.definitions.FormDefinitionWrapper;
import com.handy.portal.ui.fragment.ActionBarFragment;
import com.handy.portal.util.TextUtils;
import com.handy.portal.util.UIUtils;
import com.squareup.otto.Subscribe;

import java.util.Map;
import java.util.regex.Pattern;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileUpdateFragment extends ActionBarFragment
{
    @Bind(R.id.provider_name_edit_text)
    EditText mNameText;
    @Bind(R.id.provider_email_edit_text)
    EditText mEmailText;
    @Bind(R.id.provider_email_error_indicator)
    ImageView mEmailError;
    @Bind(R.id.provider_address_edit_text)
    EditText mAddressText;
    @Bind(R.id.provider_address2_edit_text)
    EditText mAddress2Text;
    @Bind(R.id.provider_address_error_indicator)
    ImageView mAddressError;
    @Bind(R.id.provider_city_edit_text)
    EditText mCityText;
    @Bind(R.id.provider_state_edit_text)
    EditText mStateText;
    @Bind(R.id.provider_zip_code_edit_text)
    EditText mZipCodeText;
    @Bind(R.id.provider_area_error_indicator)
    ImageView mAreaError;
    @Bind(R.id.provider_phone_edit_text)
    EditText mPhoneText;
    @Bind(R.id.provider_phone_error_indicator)
    ImageView mPhoneError;

    @Inject
    ProviderManager mProviderManager;

    private FormDefinitionWrapper mFormDefinitionWrapper;

    @Override
    protected MainViewTab getTab()
    {
        return MainViewTab.PROFILE_UPDATE;
    }

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

        View view = inflater.inflate(R.layout.fragment_profile_update_provider_info, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        setFormFieldErrorStateRemovers();
        setActionBar(R.string.edit_your_profile, false);
        initialize();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setBackButtonEnabled(true);

        if (mProviderManager.getCachedActiveProvider() != null)
        {
            bus.post(new RegionDefinitionEvent.RequestFormDefinitions(
                    mProviderManager.getCachedActiveProvider().getCountry(), this.getContext()));
        }
    }

    @OnClick(R.id.profile_update_provider_button)
    public void onSubmitForm()
    {
        if (validate())
        {
            bus.post(new LogEvent.AddLogEvent(mEventLogFactory.createEditProfileConfirmedLog()));
            bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
            bus.post(new ProfileEvent.RequestProfileUpdate(mEmailText.getText(), mPhoneText.getText(), mAddressText.getText(),
                    mAddress2Text.getText(), mCityText.getText(), mStateText.getText(), mZipCodeText.getText()));
            bus.post(new ProfileEvent.SubmittedProfileUpdate());
        }
        else
        {
            showToast(R.string.form_not_filled_out_correctly, Toast.LENGTH_LONG);
        }
    }

    @Subscribe
    public void onReceiveFormDefinitionsSuccess(RegionDefinitionEvent.ReceiveFormDefinitionsSuccess event)
    {
        mFormDefinitionWrapper = event.formDefinitionWrapper;
        updateFormWithDefinitions();
    }

    @Subscribe
    public void onReceiveUpdateProfileSuccess(ProfileEvent.ReceiveProfileUpdateSuccess event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        showToast(R.string.update_profile_success, Toast.LENGTH_LONG);
        UIUtils.dismissOnBackPressed(getActivity());
    }

    @Subscribe
    public void onReceiveUpdateProfileError(ProfileEvent.ReceiveProfileUpdateError event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        if (event.error.getMessage() != null)
        {
            showToast(event.error.getMessage(), Toast.LENGTH_LONG);
        }
        else
        {
            showToast(R.string.update_profile_failed, Toast.LENGTH_LONG);
        }
    }

    private void initialize()
    {
        Provider provider = mProviderManager.getCachedActiveProvider();
        ProviderProfile profile = mProviderManager.getCachedProviderProfile();
        if (provider == null || profile == null || profile.getProviderPersonalInfo() == null)
        {
            Crashlytics.logException(new NullPointerException("Provider or ProviderProfile is null."));
            return;
        }

        ProviderPersonalInfo info = profile.getProviderPersonalInfo();
        mNameText.setText(info.getFirstName() + " " + info.getLastName());
        mAddressText.setText(info.getAddress().getAddress1());
        mAddress2Text.setText(info.getAddress().getAddress2());
        mCityText.setText(info.getAddress().getCity());
        mStateText.setText(info.getAddress().getState());
        mZipCodeText.setText(info.getAddress().getZip());
        mEmailText.setText(info.getEmail());
        if (provider.isUK())
        {
            mStateText.setVisibility(View.GONE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            String phone = info.getLocalPhone() != null ? info.getLocalPhone() : "";
            String country = provider.getCountry() != null ? provider.getCountry() : "US";
            mPhoneText.setText(PhoneNumberUtils.formatNumber(phone, country));
            mPhoneText.addTextChangedListener(new PhoneNumberFormattingTextWatcher(provider.getCountry()));
        }
        else
        {
            mPhoneText.setText(info.getLocalPhone());
        }
    }

    private boolean validate()
    {
        Map<String, FieldDefinition> fieldDefinitionMap = mFormDefinitionWrapper.getFieldDefinitionsForForm(FormDefinitionKey.UPDATE_PROVIDER_INFO);
        if (fieldDefinitionMap == null) { return true; }

        boolean allFieldsValid = validateField(mEmailText.getText(), mEmailText, mEmailError, fieldDefinitionMap.get(FormDefinitionKey.FieldDefinitionKey.EMAIL).getCompiledPattern());
        allFieldsValid &= validateField(mAddressText.getText(), mAddressText, mAddressError, fieldDefinitionMap.get(FormDefinitionKey.FieldDefinitionKey.ADDRESS).getCompiledPattern());
        allFieldsValid &= validateField(mCityText.getText(), mCityText, mAreaError, fieldDefinitionMap.get(FormDefinitionKey.FieldDefinitionKey.CITY).getCompiledPattern());
        allFieldsValid &= validateField(mStateText.getText(), mStateText, mAreaError, fieldDefinitionMap.get(FormDefinitionKey.FieldDefinitionKey.STATE).getCompiledPattern());
        allFieldsValid &= validateField(mZipCodeText.getText(), mZipCodeText, mAreaError, fieldDefinitionMap.get(FormDefinitionKey.FieldDefinitionKey.ZIP_CODE).getCompiledPattern());
        String phone = mPhoneText.getText().toString().replaceAll("[\\D]", ""); // Remove special characters from phone number
        allFieldsValid &= validateField(phone, mPhoneText, mPhoneError, fieldDefinitionMap.get(FormDefinitionKey.FieldDefinitionKey.PHONE).getCompiledPattern());

        return allFieldsValid;
    }

    private void setFormFieldErrorStateRemovers()
    {
        mEmailText.addTextChangedListener(new FormFieldErrorStateRemover(mEmailText, mEmailError));
        mAddressText.addTextChangedListener(new FormFieldErrorStateRemover(mAddressText, mAddressError));
        mPhoneText.addTextChangedListener(new FormFieldErrorStateRemover(mPhoneText, mPhoneError));
        mCityText.addTextChangedListener(new FormFieldErrorStateRemover(mCityText, mAreaError));
        mStateText.addTextChangedListener(new FormFieldErrorStateRemover(mStateText, mAreaError));
        mZipCodeText.addTextChangedListener(new FormFieldErrorStateRemover(mZipCodeText, mAreaError));
    }

    private void updateFormWithDefinitions()
    {
        Map<String, FieldDefinition> fieldDefinitionMap = mFormDefinitionWrapper.getFieldDefinitionsForForm(FormDefinitionKey.UPDATE_PROVIDER_INFO);
        if (fieldDefinitionMap != null)
        {
            mAddress2Text.setHint(fieldDefinitionMap.get(FormDefinitionKey.FieldDefinitionKey.ADDRESS2).getHintText());
            mCityText.setHint(fieldDefinitionMap.get(FormDefinitionKey.FieldDefinitionKey.CITY).getHintText());
            mStateText.setHint(fieldDefinitionMap.get(FormDefinitionKey.FieldDefinitionKey.STATE).getHintText());
            mZipCodeText.setHint(fieldDefinitionMap.get(FormDefinitionKey.FieldDefinitionKey.ZIP_CODE).getHintText());
            UIUtils.setInputFilterForInputType(mZipCodeText, fieldDefinitionMap.get(FormDefinitionKey.FieldDefinitionKey.ZIP_CODE).getInputType());
        }
    }

    private boolean validateField(CharSequence text, EditText textView, View errorIndicator, Pattern pattern)
    {
        if (!TextUtils.validateText(text, pattern))
        {
            textView.setTextColor(ContextCompat.getColor(getContext(), R.color.error_red));
            errorIndicator.setVisibility(View.VISIBLE);
            return false;
        }
        return true;
    }

    private static class FormFieldErrorStateRemover implements TextWatcher
    {
        private EditText mText;
        private ImageView mErrorIndicator;

        public FormFieldErrorStateRemover(EditText text, ImageView errorIndicator)
        {
            mText = text;
            mErrorIndicator = errorIndicator;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }

        @Override
        public void afterTextChanged(Editable s)
        {
            mText.setTextColor(ContextCompat.getColor(mText.getContext(), R.color.dark_grey));
            mErrorIndicator.setVisibility(View.INVISIBLE);
        }
    }
}
