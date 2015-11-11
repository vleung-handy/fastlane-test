package com.handy.portal.ui.fragment.profile;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.handy.portal.R;
import com.handy.portal.constant.FormDefinitionKey;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.RegionDefinitionEvent;
import com.handy.portal.manager.ProviderManager;
import com.handy.portal.model.ProviderPersonalInfo;
import com.handy.portal.model.definitions.FieldDefinition;
import com.handy.portal.model.definitions.FormDefinitionWrapper;
import com.handy.portal.ui.fragment.ActionBarFragment;
import com.handy.portal.util.TextUtils;
import com.handy.portal.util.UIUtils;
import com.squareup.otto.Subscribe;

import java.util.Map;
import java.util.regex.Pattern;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class ProfileUpdateFragment extends ActionBarFragment
{
    @InjectView(R.id.provider_name_edit_text)
    EditText mNameText;
    @InjectView(R.id.provider_email_edit_text)
    EditText mEmailText;
    @InjectView(R.id.provider_email_error_indicator)
    ImageView mEmailError;
    @InjectView(R.id.provider_address_edit_text)
    EditText mAddressText;
    @InjectView(R.id.provider_address2_edit_text)
    EditText mAddress2Text;
    @InjectView(R.id.provider_address_error_indicator)
    ImageView mAddressError;
    @InjectView(R.id.provider_city_edit_text)
    EditText mCityText;
    @InjectView(R.id.provider_state_edit_text)
    EditText mStateText;
    @InjectView(R.id.provider_zip_code_edit_text)
    EditText mZipCodeText;
    @InjectView(R.id.provider_area_error_indicator)
    ImageView mAreaError;
    @InjectView(R.id.provider_phone_edit_text)
    EditText mPhoneText;
    @InjectView(R.id.provider_phone_error_indicator)
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
        setActionBar(R.string.edit_your_profile, false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_profile_update_provider_info, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        setFormFieldErrorStateRemovers();
        initialize();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.menu_x_back, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        bus.post(new RegionDefinitionEvent.RequestFormDefinitions(mProviderManager.getCachedActiveProvider().getCountry(), this.getContext()));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_exit:
                onBackButtonPressed();
                return true;
            default:
                return false;
        }
    }

    @OnClick(R.id.profile_update_provider_button)
    public void onSubmitForm()
    {
        if (validate())
        {
            showToast("Input is good");
        }
        else
        {
            onFailure(R.string.form_not_filled_out_correctly);
        }
    }

    @Subscribe
    public void onReceiveFormDefinitionsSuccess(RegionDefinitionEvent.ReceiveFormDefinitionsSuccess event)
    {
        mFormDefinitionWrapper = event.formDefinitionWrapper;
        updateFormWithDefinitions();
    }

    private void initialize()
    {
        ProviderPersonalInfo info = mProviderManager.getCachedProviderProfile().getProviderPersonalInfo();
        if (info == null) { return; }
        mNameText.setText(info.getFirstName() + " " + info.getLastName());
        mPhoneText.setText(info.getPhone());
        mAddressText.setText(info.getAddress().getAddress1());
        mAddress2Text.setText(info.getAddress().getAddress2());
        mCityText.setText(info.getAddress().getCity());
        mStateText.setText(info.getAddress().getState());
        mZipCodeText.setText(info.getAddress().getZip());
        mEmailText.setText(info.getEmail());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            mPhoneText.addTextChangedListener(new PhoneNumberFormattingTextWatcher(mProviderManager.getCachedActiveProvider().getCountry()));
        }
    }

    private boolean validate()
    {
        Map<String, FieldDefinition> fieldDefinitionMap = mFormDefinitionWrapper.getFieldDefinitionsForForm(FormDefinitionKey.UPDATE_PROVIDER_INFO);
        if (fieldDefinitionMap == null) { return true; }

        boolean allFieldsValid = validateField(mEmailText, mEmailError, fieldDefinitionMap.get(FormDefinitionKey.FieldDefinitionKey.EMAIL).getCompiledPattern());
        allFieldsValid &= validateField(mAddressText, mAddressError, fieldDefinitionMap.get(FormDefinitionKey.FieldDefinitionKey.ADDRESS).getCompiledPattern());
        allFieldsValid &= validateField(mPhoneText, mPhoneError, fieldDefinitionMap.get(FormDefinitionKey.FieldDefinitionKey.PHONE).getCompiledPattern());
        allFieldsValid &= validateField(mCityText, mAreaError, fieldDefinitionMap.get(FormDefinitionKey.FieldDefinitionKey.CITY).getCompiledPattern());
        allFieldsValid &= validateField(mStateText, mAreaError, fieldDefinitionMap.get(FormDefinitionKey.FieldDefinitionKey.STATE).getCompiledPattern());
        allFieldsValid &= validateField(mZipCodeText, mAreaError, fieldDefinitionMap.get(FormDefinitionKey.FieldDefinitionKey.ZIP_CODE).getCompiledPattern());

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

    private boolean validateField(EditText text, View errorIndicator, Pattern pattern)
    {
        if (!TextUtils.validateText(text.getText(), pattern))
        {
            text.setTextColor(ContextCompat.getColor(getContext(), R.color.error_red));
            errorIndicator.setVisibility(View.VISIBLE);
            return false;
        }
        return true;
    }

    private void onFailure(int errorStringId)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        showToast(errorStringId, Toast.LENGTH_LONG);
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
