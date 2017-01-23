package com.handy.portal.library.ui.fragment.dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;

import com.handy.portal.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * dialog fragment that slides up from the bottom.
 * has a dismiss button and a confirm button
 */
public abstract class ConfirmActionSlideUpDialogFragment extends SlideUpDialogFragment
{
    @BindView(R.id.confirm_action_button)
    protected Button mConfirmActionButton;
    @BindView(R.id.confirm_action_dismiss_button)
    View mDismissButton;

    /**
     *
     * @param inflater
     * @param container
     * @return the view that will be stuffed inside confirm_booking_action_content of this fragment's view
     */
    protected abstract View inflateConfirmActionContentView(LayoutInflater inflater, ViewGroup container);

    protected final View inflateContentView(LayoutInflater inflater, ViewGroup container)
    {
        return inflater.inflate(R.layout.layout_confirm_action_dialog, container, false);
    }

    protected abstract void onConfirmActionButtonClicked();
    protected abstract int getConfirmButtonBackgroundResourceId();
    protected abstract String getConfirmButtonText();

    public boolean cancelDialogOnTouchOutside()
    {
        return true;
    }

    /**
     * creates the view with the subclass's specific content layout
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        ScrollView confirmBookingActionContentLayout = (ScrollView) view.findViewById(R.id.confirm_action_content);
        confirmBookingActionContentLayout.addView(inflateConfirmActionContentView(inflater, container));
        return view;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        mConfirmActionButton.setBackgroundResource(getConfirmButtonBackgroundResourceId());
        mConfirmActionButton.setText(getConfirmButtonText());
    }

    @OnClick(R.id.confirm_action_button)
    public void onConfirmButtonClicked()
    {
        onConfirmActionButtonClicked();
    }

    @OnClick(R.id.confirm_action_dismiss_button)
    public void onDismissButtonClicked()
    {
        dismiss();
    }

    protected void hideDismissButton()
    {
        mDismissButton.setVisibility(View.GONE);
    }
}
