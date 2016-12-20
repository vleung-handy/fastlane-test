package com.handy.portal.ui.fragment;

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
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.Country;
import com.handy.portal.constant.FormDefinitionKey;
import com.handy.portal.constant.MainViewPage;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.NavigationEvent;
import com.handy.portal.event.ProfileEvent;
import com.handy.portal.event.RegionDefinitionEvent;
import com.handy.portal.library.util.TextUtils;
import com.handy.portal.library.util.UIUtils;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.ProfileLog;
import com.handy.portal.manager.AppseeManager;
import com.handy.portal.manager.ConfigManager;
import com.handy.portal.manager.PrefsManager;
import com.handy.portal.manager.ProviderManager;
import com.handy.portal.model.ConfigurationResponse;
import com.handy.portal.model.ProviderPersonalInfo;
import com.handy.portal.model.ProviderProfile;
import com.handy.portal.model.definitions.FieldDefinition;
import com.handy.portal.model.definitions.FormDefinitionWrapper;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.Subscribe;

import java.util.Map;
import java.util.regex.Pattern;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.handy.portal.model.ProviderPersonalInfo.ProfileImage.Type.THUMBNAIL;

public class ProfileUpdateFragment extends ActionBarFragment
{
    @BindView(R.id.provider_name_edit_text)
    EditText mNameText;
    @BindView(R.id.provider_email_edit_text)
    EditText mEmailText;
    @BindView(R.id.provider_email_error_indicator)
    ImageView mEmailError;
    @BindView(R.id.provider_address_edit_text)
    EditText mAddressText;
    @BindView(R.id.provider_address2_edit_text)
    EditText mAddress2Text;
    @BindView(R.id.provider_address_error_indicator)
    ImageView mAddressError;
    @BindView(R.id.provider_city_edit_text)
    EditText mCityText;
    @BindView(R.id.provider_state_edit_text)
    EditText mStateText;
    @BindView(R.id.provider_zip_code_edit_text)
    EditText mZipCodeText;
    @BindView(R.id.provider_area_error_indicator)
    ImageView mAreaError;
    @BindView(R.id.provider_phone_edit_text)
    EditText mPhoneText;
    @BindView(R.id.provider_phone_error_indicator)
    ImageView mPhoneError;
    @BindView(R.id.provider_image)
    ImageView mImage;
    @BindView(R.id.provider_image_holder)
    ViewGroup mImageHolder;
    @BindView(R.id.provider_image_edit_button)
    TextView mEditImageButton;

    @Inject
    ProviderManager mProviderManager;
    @Inject
    ConfigManager mConfigManager;
    @Inject
    PrefsManager mPrefsManager;

    private FormDefinitionWrapper mFormDefinitionWrapper;
    private boolean mEditingProImage;

    @Override
    protected MainViewPage getAppPage()
    {
        return MainViewPage.PROFILE_UPDATE;
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

        AppseeManager.markViewsAsSensitive(mImage);
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
        bus.register(this);
        setBackButtonEnabled(true);

        final ProviderProfile providerProfile = mProviderManager.getCachedProviderProfile();
        if (providerProfile != null && providerProfile.getProviderPersonalInfo() != null)
        {
            String country = providerProfile.getProviderPersonalInfo().getAddress().getCountry();
            bus.post(new RegionDefinitionEvent.RequestFormDefinitions(country, this.getContext()));
        }

        if (mEditingProImage)
        {
            initProImage();
            mEditingProImage = false;
        }
    }

    @Override
    public void onPause()
    {
        bus.unregister(this);
        super.onPause();
    }

    @OnClick(R.id.profile_update_provider_button)
    public void onSubmitForm()
    {
        if (validate())
        {
            bus.post(new LogEvent.AddLogEvent(new ProfileLog.EditProfileSubmitted()));
            bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
            bus.post(new ProfileEvent.RequestProfileUpdate(mEmailText.getText(), mPhoneText.getText(), mAddressText.getText(),
                    mAddress2Text.getText(), mCityText.getText(), mStateText.getText(), mZipCodeText.getText()));
            bus.post(new ProfileEvent.SubmittedProfileUpdate());
        }
        else
        {
            final String errorMessage = getString(R.string.form_not_filled_out_correctly);
            bus.post(new LogEvent.AddLogEvent(new ProfileLog.EditProfileValidationFailure(errorMessage)));
            showToast(errorMessage, Toast.LENGTH_LONG);
        }
    }

    @OnClick({R.id.provider_image, R.id.provider_image_edit_button})
    public void onEditImageClicked()
    {
        final ConfigurationResponse configuration = mConfigManager.getConfigurationResponse();
        if (configuration != null && configuration.isProfilePictureEnabled())
        {
            mEditingProImage = true;
            final Bundle bundle = new Bundle();
            bundle.putSerializable(BundleKeys.NAVIGATION_SOURCE, EditPhotoFragment.Source.PROFILE);
            bus.post(new NavigationEvent.NavigateToPage(MainViewPage.PROFILE_PICTURE, bundle, true));
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
        bus.post(new LogEvent.AddLogEvent(new ProfileLog.EditProfileConfirmed()));
        showToast(R.string.update_profile_success, Toast.LENGTH_LONG);
        UIUtils.dismissOnBackPressed(getActivity());
    }

    @Subscribe
    public void onReceiveUpdateProfileError(ProfileEvent.ReceiveProfileUpdateError event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        String errorMessage = event.error.getMessage();
        if (errorMessage == null)
        {
            errorMessage = getString(R.string.update_profile_failed);
        }
        bus.post(new LogEvent.AddLogEvent(new ProfileLog.EditProfileError(errorMessage)));
        showToast(errorMessage, Toast.LENGTH_LONG);
    }

    public void initialize()
    {
        ProviderProfile profile = mProviderManager.getCachedProviderProfile();
        if (profile == null || profile.getProviderPersonalInfo() == null)
        {
            Crashlytics.logException(new NullPointerException("ProviderProfile is null."));
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
        if (info.isUK())
        {
            mStateText.setVisibility(View.GONE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            String phone = info.getLocalPhone() != null ? info.getLocalPhone() : "";
            String country = info.getAddress().getCountry();
            if (TextUtils.isNullOrEmpty(country))
            {
                country = Country.US.toUpperCase();
            }
            mPhoneText.setText(PhoneNumberUtils.formatNumber(phone, country));
            mPhoneText.addTextChangedListener(new PhoneNumberFormattingTextWatcher(country));
        }
        else
        {
            mPhoneText.setText(info.getLocalPhone());
        }

        initProImage();
    }

    private void initProImage()
    {
        final ConfigurationResponse configuration = mConfigManager.getConfigurationResponse();
        if (configuration != null && configuration.isProfilePictureEnabled())
        {
            mImageHolder.setVisibility(View.VISIBLE);
            final String imageUrl = mProviderManager.getCachedProfileImageUrl(THUMBNAIL);
            if (imageUrl != null)
            {
                Picasso.with(getActivity())
                        .load(imageUrl)
                        .placeholder(R.drawable.img_pro_placeholder)
                        .noFade()
                        .into(mImage);
            }
            else
            {
                mImage.setImageResource(R.drawable.img_pro_placeholder);
            }

            if (!configuration.isProfilePictureUploadEnabled())
            {
                mEditImageButton.setVisibility(View.GONE);
            }
        }
        else
        {
            mImageHolder.setVisibility(View.GONE);
        }
    }

    private boolean validate()
    {
        if (mFormDefinitionWrapper == null)
        {
            // Server issue, assigned as part of success callback
            return false;
        }
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
        if (mFormDefinitionWrapper == null)
        { return; }
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
            textView.setTextColor(ContextCompat.getColor(getContext(), R.color.plumber_red));
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
            mText.setTextColor(ContextCompat.getColor(mText.getContext(), R.color.dark_gray));
            mErrorIndicator.setVisibility(View.INVISIBLE);
        }
    }
}
