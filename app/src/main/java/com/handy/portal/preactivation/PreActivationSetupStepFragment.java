package com.handy.portal.preactivation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handy.portal.R;
import com.handy.portal.ui.fragment.ActionBarFragment;

import butterknife.ButterKnife;
import butterknife.OnClick;

public abstract class PreActivationSetupStepFragment extends ActionBarFragment
{
    @OnClick(R.id.primary_button)
    public void triggerPrimaryButton()
    {
        onPrimaryButtonClicked();
    }

    @OnClick(R.id.secondary_button)
    public void triggerSecondaryButton()
    {
        onSecondaryButtonClicked();
    }

    abstract protected int getLayoutResId();

    abstract protected int getTitleResId();

    abstract protected void onPrimaryButtonClicked();

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
        View view = inflater.inflate(getLayoutResId(), container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        setActionBarTitle(getTitleResId());
    }
}
