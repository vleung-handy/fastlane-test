package com.handy.portal.preactivation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.handy.portal.R;
import com.handy.portal.ui.fragment.ActionBarFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public abstract class PreActivationSetupStepFragment extends ActionBarFragment
{
    @Bind(R.id.action_button_group)
    ViewGroup mActionButtonGroup;
    @Bind(R.id.single_action_button)
    Button mSingleActionButton;
    @Bind(R.id.group_primary_button)
    Button mGroupPrimaryButton;
    @Bind(R.id.group_secondary_button)
    Button mGroupSecondaryButton;

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

    protected void goToStep(@Nullable final PreActivationSetupStep step)
    {
        goToStep(step, true);
    }

    protected void goToStep(@Nullable final PreActivationSetupStep step,
                            boolean allowBackNavigation)
    {
        ((PreActivationSetupActivity) getActivity()).goToStep(step, allowBackNavigation);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater,
                             @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_pre_activation_setup, container, false);

        final ViewGroup mainContentContainer = (ViewGroup) view.findViewById(R.id.main_content);
        if (mainContentContainer != null && mainContentContainer.getChildCount() == 0)
        {
            final View mainContent =
                    inflater.inflate(getLayoutResId(), container, false);
            mainContentContainer.addView(mainContent);
        }

        ButterKnife.bind(this, view);

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

        return view;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        setActionBarTitle(getTitle());
    }
}
