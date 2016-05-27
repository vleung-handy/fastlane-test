package com.handy.portal.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.library.ui.fragment.InjectedFragment;
import com.handy.portal.manager.TermsManager;
import com.handy.portal.model.TermsDetails;
import com.handy.portal.ui.activity.SplashActivity;
import com.handy.portal.library.ui.view.HandyWebView;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class TermsFragment extends InjectedFragment
{
    @Bind(R.id.loading_overlay)
    protected View loadingOverlay;

    @Bind(R.id.terms_webview)
    protected HandyWebView termsWebView;

    @Bind(R.id.accept_button)
    protected Button acceptButton;

    @Bind(R.id.accept_checkbox)
    protected CheckBox acceptCheckbox;

    @Bind(R.id.instructions)
    protected TextView instructionsText;

    @Bind(R.id.terms_layout)
    protected ViewGroup termsLayout;

    @Bind(R.id.fetch_error_view)
    protected ViewGroup errorLayout;

    @Bind(R.id.fetch_error_text)
    protected TextView errorText;

    @Inject
    TermsManager termsManager;

    private int activeTermsIndex = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_terms, container);

        ButterKnife.bind(this, view);

        updateView(getActiveTermsDetails());

        return view;
    }

    private TermsDetails getActiveTermsDetails()
    {
        if (termsManager.getNewestTermsDetailsGroup() != null && activeTermsIndex < termsManager.getNewestTermsDetailsGroup().getTermsDetails().length)
        {
            return termsManager.getNewestTermsDetailsGroup().getTermsDetails()[activeTermsIndex];
        }
        return null;
    }

    private TermsDetails nextTerm()
    {
        activeTermsIndex++;
        return getActiveTermsDetails();
    }

    @OnClick(R.id.accept_button)
    protected void acceptTerms()
    {
        if (acceptCheckbox.isChecked())
        {
            loadingOverlay.setVisibility(View.VISIBLE);
            bus.post(new HandyEvent.AcceptTerms(getActiveTermsDetails()));
        }
        else
        {
            acceptCheckbox.setTextColor(ContextCompat.getColor(getContext(), R.color.plumber_red));
        }
    }

    @OnClick(R.id.try_again_button)
    protected void doCheckForTermsAgain()
    {
        startActivity(new Intent(this.getActivity(), SplashActivity.class));//TODO: we should not have to relaunch SplashActivity and go through its flow to check for terms again
    }

    @OnCheckedChanged(R.id.accept_checkbox)
    protected void onCheckboxChecked(boolean checked)
    {
        if (checked)
        {
            acceptCheckbox.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        }
    }

    @Subscribe
    public void onAcceptTermsSuccess(HandyEvent.AcceptTermsSuccess event)
    {
        TermsDetails activeTerms = getActiveTermsDetails();
        if (activeTerms != null && activeTerms.getCode().equals(event.getTermsCode())) //just in case event is fired twice for the same term
        {
            TermsDetails nextTerms = nextTerm();
            if (nextTerms == null) //no more terms to accept
            {
                Intent intent = new Intent(this.getActivity(), SplashActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            else
            {
                updateView(nextTerms);
            }
        }
        else
        {
            Crashlytics.logException(new Exception("User tried to accept the same terms twice")); //this shouldn't happen since there's an overlay immediately after user presses accept
            loadingOverlay.setVisibility(View.GONE);
        }
    }

    @Subscribe
    public void onAcceptTermsError(HandyEvent.AcceptTermsError event)
    {
        loadingOverlay.setVisibility(View.GONE);
        showToast(R.string.error_accepting_terms);
    }

    private void updateView(TermsDetails termsDetails)
    {
        if (termsDetails != null)
        {
            acceptButton.setText(termsDetails.getAction());
            instructionsText.setText(termsDetails.getInstructions());
            termsWebView.loadHtml(termsDetails.getContent());
            acceptCheckbox.setChecked(false);
            acceptCheckbox.setTextColor(ContextCompat.getColor(getContext(), R.color.black));

            bus.post(new HandyEvent.TermsDisplayed(termsDetails.getCode()));
        }
        else
        {
            termsLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorText.setText(R.string.error_loading);
        }
        loadingOverlay.setVisibility(View.GONE);
    }

}
