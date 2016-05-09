package com.handy.portal.preactivation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.ui.fragment.ActionBarFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public abstract class PreActivationFlowFragment extends ActionBarFragment
{
    @Bind(R.id.loading_overlay)
    View mLoadingOverlay;
    @Bind(R.id.action_button_group)
    ViewGroup mActionButtonGroup;
    @Bind(R.id.single_action_button)
    Button mSingleActionButton;
    @Bind(R.id.group_primary_button)
    Button mGroupPrimaryButton;
    @Bind(R.id.group_secondary_button)
    Button mGroupSecondaryButton;
    @Bind(R.id.header)
    TextView mHeader;
    @Bind(R.id.sub_header)
    TextView mSubHeader;

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

    abstract protected String getPrimaryButtonText();

    abstract protected void onPrimaryButtonClicked();

    protected String getSecondaryButtonText()
    {
        return null;
    }

    protected void onSecondaryButtonClicked()
    {
        // do nothing
    }

    protected void next(@Nullable final PreActivationFlowFragment step)
    {
        next(step, true);
    }

    protected void next(@Nullable final PreActivationFlowFragment step,
                        boolean allowBackNavigation)
    {
        ((PreActivationFlowActivity) getActivity()).next(step, allowBackNavigation);
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
        initActionButtons();

        return view;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        final boolean allowBackNavigation =
                getArguments().getBoolean(BundleKeys.ALLOW_BACK_NAVIGATION, false);
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

    private void initActionButtons()
    {
        if (getSecondaryButtonText() != null)
        {
            mActionButtonGroup.setVisibility(View.VISIBLE);
            mSingleActionButton.setVisibility(View.GONE);
            mGroupPrimaryButton.setText(getPrimaryButtonText());
            mGroupSecondaryButton.setText(getSecondaryButtonText());
        }
        else
        {
            mSingleActionButton.setVisibility(View.VISIBLE);
            mActionButtonGroup.setVisibility(View.GONE);
            mSingleActionButton.setText(getPrimaryButtonText());
        }
    }
}
