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
import com.handy.portal.ui.activity.MainActivity;
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

    @InjectView(R.id.terms_webview)
    protected WebView termsWebview;

    @InjectView(R.id.accept_button)
    protected Button acceptButton;

    @InjectView(R.id.accept_checkbox)
    protected CheckBox acceptCheckbox;

    @InjectView(R.id.instructions)
    protected TextView instructionsText;

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
            bus.post(new Event.AcceptTermsEvent(termsManager.getNewestTermsDetails()));
        }
        else
        {
            acceptCheckbox.setTextColor(getResources().getColor(R.color.error_red));
        }
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
        startActivity(new Intent(this.getActivity(), MainActivity.class));
    }

    @Subscribe
    public void onAcceptTermsError(Event.AcceptTermsErrorEvent event)
    {
        // TODO: error state for accept terms
    }

    private void initView()
    {
        TermsDetails newestTermsDetails = termsManager.getNewestTermsDetails();

        acceptButton.setText(newestTermsDetails.getAction());

        instructionsText.setText(newestTermsDetails.getInstructions());

        String htmlContent = wrapContent(newestTermsDetails.getContent());
        termsWebview.loadDataWithBaseURL(ASSETS_BASE_URL, htmlContent, "text/html", "utf-8", null);
    }

    private String wrapContent(String content)
    {
        String template = "%s"; // fall back to just displaying without html wrapping
        try
        {
            InputStream stream = getActivity().getAssets().open("terms_template.html");
            template = CharStreams.toString(new InputStreamReader(stream, "UTF-8"));
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return String.format(template, content);
    }
}
