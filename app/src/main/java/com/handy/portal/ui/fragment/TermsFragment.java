package com.handy.portal.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.common.io.CharStreams;
import com.handy.portal.R;
import com.handy.portal.core.TermsDetails;
import com.handy.portal.core.TermsManager;
import com.handy.portal.event.Event;
import com.handy.portal.ui.activity.SplashActivity;
import com.handy.portal.ui.element.LoadingOverlayView;
import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class TermsFragment extends InjectedFragment
{
    private static final String ASSETS_BASE_URL = "file:///android_asset/";
    private static final String TERMS_TEMPLATE_HTML = "terms_template.html";
    private static final String UTF_8 = "UTF-8";

    @InjectView(R.id.loading_overlay)
    protected LoadingOverlayView loadingOverlay;

    @InjectView(R.id.terms_webview)
    protected WebView termsWebview;

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
            bus.post(new Event.AcceptTermsEvent(termsManager.getNewestTermsDetails()));
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
    public void onAcceptTermsSuccess(Event.AcceptTermsSuccessEvent event)
    {
        startActivity(new Intent(this.getActivity(), SplashActivity.class));
    }

    @Subscribe
    public void onAcceptTermsError(Event.AcceptTermsErrorEvent event)
    {
        loadingOverlay.setOverlayVisibility(false);
        showErrorToast(R.string.error_accepting_terms);
    }

    private void initView()
    {
        TermsDetails newestTermsDetails = termsManager.getNewestTermsDetails();

        if (newestTermsDetails != null)
        {
            acceptButton.setText(newestTermsDetails.getAction());

            instructionsText.setText(newestTermsDetails.getInstructions());

            String htmlContent = wrapContent(newestTermsDetails.getContent());
            termsWebview.loadDataWithBaseURL(ASSETS_BASE_URL, htmlContent, "text/html", UTF_8, null);

            bus.post(new Event.TermsDisplayedEvent(newestTermsDetails.getCode()));
        }
        else
        {
            termsLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorText.setText(R.string.error_loading);
        }
    }

    private String wrapContent(String content)
    {
        String template = "%s"; // fall back to just displaying without html wrapping
        try
        {
            InputStream stream = getActivity().getAssets().open(TERMS_TEMPLATE_HTML);
            template = CharStreams.toString(new InputStreamReader(stream, UTF_8));
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return String.format(template, content);
    }
}
