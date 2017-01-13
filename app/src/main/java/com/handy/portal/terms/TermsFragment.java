package com.handy.portal.terms;

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
import com.handy.portal.core.constant.BundleKeys;
import com.handy.portal.library.ui.fragment.InjectedFragment;
import com.handy.portal.library.ui.view.HandyWebView;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class TermsFragment extends InjectedFragment
{
    @BindView(R.id.loading_overlay)
    protected View mLoadingOverlay;
    @BindView(R.id.terms_web_view)
    protected HandyWebView mTermsWebView;
    @BindView(R.id.accept_button)
    protected Button mAcceptButton;
    @BindView(R.id.accept_checkbox)
    protected CheckBox mAcceptCheckbox;
    @BindView(R.id.instructions)
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

    @Override
    public void onResume()
    {
        super.onResume();
        bus.register(this);
    }

    @Override
    public void onPause()
    {
        bus.unregister(this);
        super.onPause();
    }

    @OnClick(R.id.accept_button)
    protected void acceptTerms()
    {
        if (mAcceptCheckbox.isChecked())
        {
            mLoadingOverlay.setVisibility(View.VISIBLE);
            bus.post(new TermsEvent.AcceptTerms(mTerms));
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
    public void onAcceptTermsSuccess(TermsEvent.AcceptTermsSuccess event)
    {
        ((TermsActivity) getActivity()).proceed();
    }

    @Subscribe
    public void onAcceptTermsError(TermsEvent.AcceptTermsError event)
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
