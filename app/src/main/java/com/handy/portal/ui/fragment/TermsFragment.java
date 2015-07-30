package com.handy.portal.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.manager.TermsManager;
import com.handy.portal.model.TermsDetails;
import com.handy.portal.ui.activity.SplashActivity;
import com.handy.portal.ui.element.HandyWebView;
import com.handy.portal.ui.element.LoadingOverlayView;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class TermsFragment extends InjectedFragment
{
    @InjectView(R.id.loading_overlay)
    protected LoadingOverlayView loadingOverlay;

    @InjectView(R.id.terms_webview)
    protected HandyWebView termsWebView;

    @InjectView(R.id.accept_button)
    protected Button acceptButton;

    @InjectView(R.id.accept_checkbox)
    protected CheckBox acceptCheckbox;

    @InjectView(R.id.instructions)
    protected TextView instructionsText;

    @InjectView(R.id.terms_layout)
    protected ViewGroup termsLayout;

    @InjectView(R.id.fetch_error_view)
    protected ViewGroup errorLayout;

    @InjectView(R.id.fetch_error_text)
    protected TextView errorText;

    @Inject
    TermsManager termsManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_terms, container);

        ButterKnife.inject(this, view);

        initView();

        return view;
    }

    @OnClick(R.id.accept_button)
    protected void acceptTerms()
    {
        if (acceptCheckbox.isChecked())
        {
            loadingOverlay.setOverlayVisibility(true);
            bus.post(new HandyEvent.AcceptTerms(termsManager.getNewestTermsDetails()));
        }
        else
        {
            acceptCheckbox.setTextColor(getResources().getColor(R.color.error_red));
        }
    }

    @OnClick(R.id.try_again_button)
    protected void doCheckForTermsAgain()
    {
        startActivity(new Intent(this.getActivity(), SplashActivity.class));
    }

    @OnCheckedChanged(R.id.accept_checkbox)
    protected void onCheckboxChecked(boolean checked)
    {
        if (checked)
        {
            acceptCheckbox.setTextColor(getResources().getColor(R.color.black));
        }
    }

    @Subscribe
    public void onAcceptTermsSuccess(HandyEvent.AcceptTermsSuccess event)
    {
        Intent intent = new Intent(this.getActivity(), SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Subscribe
    public void onAcceptTermsError(HandyEvent.AcceptTermsError event)
    {
        loadingOverlay.setOverlayVisibility(false);
        showToast(R.string.error_accepting_terms);
    }

    private void initView()
    {
        TermsDetails newestTermsDetails = termsManager.getNewestTermsDetails();

        if (newestTermsDetails != null)
        {
            acceptButton.setText(newestTermsDetails.getAction());

            instructionsText.setText(newestTermsDetails.getInstructions());

            termsWebView.loadHtml(newestTermsDetails.getContent());

            bus.post(new HandyEvent.TermsDisplayed(newestTermsDetails.getCode()));
        }
        else
        {
            termsLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorText.setText(R.string.error_loading);
        }
    }

}
