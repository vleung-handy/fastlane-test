package com.handy.portal.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.library.ui.fragment.InjectedFragment;
import com.handy.portal.library.ui.view.HandyWebView;
import com.handy.portal.model.TermsDetails;
import com.handy.portal.ui.activity.TermsActivity;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class TermsFragment extends InjectedFragment
{
    @Bind(R.id.loading_overlay)
    protected View mLoadingOverlay;
    @Bind(R.id.terms_web_view)
    protected HandyWebView mTermsWebView;
    @Bind(R.id.accept_button)
    protected Button mAcceptButton;
    @Bind(R.id.accept_checkbox)
    protected CheckBox mAcceptCheckbox;
    @Bind(R.id.instructions)
    protected TextView mInstructionsText;

    private TermsDetails mTerms;

    public static TermsFragment newInstance(final TermsDetails termsDetails)
    {
        final TermsFragment fragment = new TermsFragment();
        final Bundle arguments = new Bundle();
        arguments.putSerializable(BundleKeys.TERMS, termsDetails);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mTerms = (TermsDetails) getArguments().getSerializable(BundleKeys.TERMS);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater,
                             final ViewGroup container,
                             final Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.fragment_terms, container, false);
        ButterKnife.bind(this, view);
        updateView(mTerms);
        return view;
    }

    @OnClick(R.id.accept_button)
    protected void acceptTerms()
    {
        if (mAcceptCheckbox.isChecked())
        {
            mLoadingOverlay.setVisibility(View.VISIBLE);
            bus.post(new HandyEvent.AcceptTerms(mTerms));
        }
        else
        {
            mAcceptCheckbox.setTextColor(ContextCompat.getColor(getContext(), R.color.plumber_red));
        }
    }

    @OnCheckedChanged(R.id.accept_checkbox)
    protected void onCheckboxChecked(boolean checked)
    {
        if (checked)
        {
            mAcceptCheckbox.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        }
    }

    @Subscribe
    public void onAcceptTermsSuccess(HandyEvent.AcceptTermsSuccess event)
    {
        ((TermsActivity) getActivity()).proceed();
    }

    @Subscribe
    public void onAcceptTermsError(HandyEvent.AcceptTermsError event)
    {
        mLoadingOverlay.setVisibility(View.GONE);
        showToast(R.string.error_accepting_terms);
    }

    private void updateView(@NonNull TermsDetails termsDetails)
    {
        mAcceptButton.setText(termsDetails.getAction());
        mInstructionsText.setText(termsDetails.getInstructions());
        mTermsWebView.loadHtml(termsDetails.getContent());
        mAcceptCheckbox.setChecked(false);
        mAcceptCheckbox.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
    }

}
