package com.handy.portal.onboarding.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.library.util.TextUtils;
import com.handy.portal.onboarding.model.OnboardingDetails;
import com.handy.portal.onboarding.model.subflow.SubflowData;
import com.handy.portal.onboarding.model.subflow.SubflowType;
import com.handy.portal.onboarding.ui.activity.OnboardingSubflowActivity;
import com.handy.portal.ui.fragment.ActionBarFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public abstract class OnboardingSubflowFragment extends ActionBarFragment
{
    @Bind(R.id.loading_overlay)
    protected View mLoadingOverlay;
    @Bind(R.id.action_button_group)
    protected ViewGroup mActionButtonGroup;
    @Bind(R.id.single_action_button)
    protected Button mSingleActionButton;
    @Bind(R.id.group_primary_button)
    protected Button mGroupPrimaryButton;
    @Bind(R.id.group_secondary_button)
    protected Button mGroupSecondaryButton;
    @Bind(R.id.header_image)
    protected ImageView mHeaderImage;
    @Bind(R.id.header)
    protected TextView mHeader;
    @Bind(R.id.sub_header)
    protected TextView mSubHeader;
    @Bind(R.id.scroll_view)
    protected ScrollView mScrollView;
    protected ViewGroup mMainContentContainer;
    protected SubflowData mSubflowData;


    public static final class ButtonTypes
    {
        public static final int NONE = 0;
        public static final int SINGLE = 1;
        public static final int DOUBLE = 2;
        public static final int SINGLE_FIXED = 3;
    }


    public static final class HeaderImageTypes
    {
        public static final int NONE = 0;
        public static final int WELCOME = 1;
        public static final int COMPLETE = 2;
        public static final int ERROR = 3;
    }

    protected abstract int getButtonType();

    protected int getHeaderImageType()
    {
        return HeaderImageTypes.NONE;
    }

    @OnClick({R.id.group_primary_button, R.id.single_action_button})
    void triggerPrimaryButton()
    {
        onPrimaryButtonClicked();
    }

    @Nullable
    @OnClick(R.id.group_secondary_button)
    void triggerSecondaryButton()
    {
        onSecondaryButtonClicked();
    }

    abstract protected int getLayoutResId();

    abstract protected String getTitle();

    @Nullable
    abstract protected String getHeaderText();

    @Nullable
    abstract protected String getSubHeaderText();

    protected String getPrimaryButtonText()
    {
        return getString(R.string.continue_to_next_step);
    }

    abstract protected void onPrimaryButtonClicked();

    protected String getSecondaryButtonText()
    {
        return getString(R.string.skip_this_step);
    }

    protected void onSecondaryButtonClicked()
    {
        // do nothing
    }

    protected void next(@NonNull final OnboardingSubflowFragment fragment)
    {
        ((OnboardingSubflowActivity) getActivity()).next(fragment, true);
    }

    protected void terminate(@NonNull final Intent data)
    {
        ((OnboardingSubflowActivity) getActivity()).terminate(data);
    }

    protected void redo(final SubflowType subflowType, final int requestCode)
    {
        final Intent intent = new Intent(getActivity(), OnboardingSubflowActivity.class);
        intent.putExtra(BundleKeys.ONBOARDING_DETAILS, getOnboardingDetails());
        intent.putExtra(BundleKeys.SUBFLOW_TYPE, subflowType);
        startActivityForResult(intent, requestCode);
    }

    protected OnboardingDetails getOnboardingDetails()
    {
        return ((OnboardingSubflowActivity) getActivity()).getOnboardingDetails();
    }

    protected void showLoadingOverlay()
    {
        mLoadingOverlay.setVisibility(View.VISIBLE);
    }

    protected void hideLoadingOverlay()
    {
        mLoadingOverlay.setVisibility(View.GONE);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mSubflowData = (SubflowData) getArguments().getSerializable(BundleKeys.SUBFLOW_DATA);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater,
                             @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_onboarding_subflow, container, false);
        inflateMainContent(inflater, container, view);

        ButterKnife.bind(this, view);

        initOrHideText(mHeader, getHeaderText());
        initOrHideText(mSubHeader, getSubHeaderText());
        initOrHideImageHeader();
        initActionButtons();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_x_back, menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_exit:
                cancel(new Intent());
                return true;
            default:
                return false;
        }
    }

    protected void cancel(@NonNull final Intent data)
    {
        ((OnboardingSubflowActivity) getActivity()).cancel(data);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        boolean allowBackNavigation = false;
        if (getArguments() != null)
        {
            allowBackNavigation =
                    getArguments().getBoolean(BundleKeys.ALLOW_BACK_NAVIGATION, false);
        }
        setActionBar(getTitle(), allowBackNavigation);
    }

    private void inflateMainContent(final LayoutInflater inflater,
                                    final @Nullable ViewGroup container,
                                    final View view)
    {
        mMainContentContainer = (ViewGroup) view.findViewById(R.id.main_content);
        if (getLayoutResId() != 0
                && mMainContentContainer != null
                && mMainContentContainer.getChildCount() == 0)
        {
            final View mainContent =
                    inflater.inflate(getLayoutResId(), container, false);
            mMainContentContainer.addView(mainContent);
        }
    }

    private void initOrHideText(final TextView textView, @Nullable String text)
    {
        if (!TextUtils.isNullOrEmpty(text))
        {
            textView.setText(text);
            textView.setVisibility(View.VISIBLE);
        }
        else
        {
            textView.setVisibility(View.GONE);
        }
    }

    private void initOrHideImageHeader()
    {
        switch (getHeaderImageType())
        {
            case HeaderImageTypes.WELCOME:
                mHeaderImage.setImageResource(R.drawable.img_avatar_welcome);
                break;
            case HeaderImageTypes.COMPLETE:
                mHeaderImage.setImageResource(R.drawable.img_avatar_complete);
                break;
            case HeaderImageTypes.ERROR:
                mHeaderImage.setImageResource(R.drawable.img_avatar_error);
                break;
            case HeaderImageTypes.NONE:
            default:
                mHeaderImage.setVisibility(View.GONE);
                break;
        }
    }

    private void initActionButtons()
    {
        switch (getButtonType())
        {
            case ButtonTypes.DOUBLE:
                mGroupSecondaryButton.setText(getSecondaryButtonText());
                mGroupSecondaryButton.setVisibility(View.VISIBLE);
            case ButtonTypes.SINGLE:
                mSingleActionButton.setVisibility(View.GONE);
                mActionButtonGroup.setVisibility(View.VISIBLE);
                mGroupPrimaryButton.setVisibility(View.VISIBLE);
                mGroupPrimaryButton.setText(getPrimaryButtonText());
                break;
            case ButtonTypes.SINGLE_FIXED:
                mSingleActionButton.setVisibility(View.VISIBLE);
                mActionButtonGroup.setVisibility(View.GONE);
                mSingleActionButton.setText(getPrimaryButtonText());
                break;
            case ButtonTypes.NONE:
            default:
                break;
        }
    }

    public void disableButtons()
    {
        setButtonsEnabled(false);
    }

    public void enableButtons()
    {
        setButtonsEnabled(true);
    }

    private void setButtonsEnabled(final boolean enabled)
    {
        switch (getButtonType())
        {
            case ButtonTypes.DOUBLE:
                setButtonEnabled(mGroupSecondaryButton, enabled);
            case ButtonTypes.SINGLE:
                setButtonEnabled(mGroupPrimaryButton, enabled);
                break;
            case ButtonTypes.SINGLE_FIXED:
                setButtonEnabled(mSingleActionButton, enabled);
                break;
            case ButtonTypes.NONE:
            default:
                break;
        }
    }

    private void setButtonEnabled(final Button button, final boolean enabled)
    {
        final float alpha = enabled ? 1.0f : 0.5f;
        button.setAlpha(alpha);
        button.setEnabled(enabled);
    }

    public void showError(final String message)
    {
        showError(message, null, null);
    }

    public void showError(@Nullable final String message,
                          @Nullable final String actionText,
                          @Nullable final ErrorActionOnClickListener actionListener)
    {
        String errorMessage = message;
        if (TextUtils.isNullOrEmpty(errorMessage))
        {
            errorMessage = getString(R.string.an_error_has_occurred);
        }
        final Snackbar snackbar = Snackbar.make(mScrollView, errorMessage,
                Snackbar.LENGTH_INDEFINITE);
        setErrorAction(snackbar, actionText, actionListener);
        snackbar.setActionTextColor(getResources().getColor(R.color.handy_blue))
                .show();
    }

    private void setErrorAction(final Snackbar snackbar,
                                @Nullable final String actionText,
                                @Nullable final ErrorActionOnClickListener actionListener)
    {
        final String resolvedActionText =
                TextUtils.isNullOrEmpty(actionText) ? getString(R.string.ok) : actionText;
        snackbar.setAction(resolvedActionText, new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                if (actionListener != null)
                {
                    actionListener.onClick(snackbar);
                }
                snackbar.dismiss();
            }
        });
    }

    public interface ErrorActionOnClickListener
    {
        void onClick(final Snackbar snackbar);
    }
}
