package com.handy.portal.preactivation;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.library.util.TextUtils;
import com.handy.portal.ui.fragment.ActionBarFragment;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public abstract class PreActivationFlowFragment extends ActionBarFragment
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

    protected void next(@NonNull final PreActivationFlowFragment fragment)
    {
        ((PreActivationFlowActivity) getActivity()).next(fragment, true);
    }

    protected void terminate()
    {
        ((PreActivationFlowActivity) getActivity()).terminate();
    }

    protected void showLoadingOverlay()
    {
        mLoadingOverlay.setVisibility(View.VISIBLE);
    }

    protected void hideLoadingOverlay()
    {
        mLoadingOverlay.setVisibility(View.GONE);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater,
                             @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_pre_activation_flow, container, false);
        inflateMainContent(inflater, container, view);

        ButterKnife.bind(this, view);

        initOrHideText(mHeader, getHeaderText());
        initOrHideText(mSubHeader, getSubHeaderText());
        initOrHideImageHeader();
        initActionButtons();

        return view;
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
        final ViewGroup mainContentContainer = (ViewGroup) view.findViewById(R.id.main_content);
        if (getLayoutResId() != 0
                && mainContentContainer != null
                && mainContentContainer.getChildCount() == 0)
        {
            final View mainContent =
                    inflater.inflate(getLayoutResId(), container, false);
            mainContentContainer.addView(mainContent);
        }
    }

    private void initOrHideText(final TextView textView, @Nullable String text)
    {
        if (text != null)
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

    public void showError(@Nullable final String message)
    {
        String errorMessage = message;
        if (TextUtils.isNullOrEmpty(errorMessage))
        {
            errorMessage = getString(R.string.an_error_has_occurred);
        }
        final Snackbar snackbar = Snackbar.make(mScrollView, errorMessage,
                Snackbar.LENGTH_INDEFINITE);
        snackbar.setActionTextColor(getResources().getColor(R.color.handy_blue))
                .setAction(R.string.ok, new View.OnClickListener()
                {
                    @Override
                    public void onClick(final View v)
                    {
                        snackbar.dismiss();
                    }
                })
                .show();
    }

    public List<Booking> getPendingBookings()
    {
        return ((PreActivationFlowActivity) getActivity()).getPendingBookings();
    }

    public void setPendingBookings(final List<Booking> bookings)
    {
        ((PreActivationFlowActivity) getActivity()).setPendingBookings(bookings);
    }
}
