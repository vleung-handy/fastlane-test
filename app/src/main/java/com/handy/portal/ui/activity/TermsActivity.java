package com.handy.portal.ui.activity;

import android.os.Bundle;
import android.webkit.WebView;

import com.google.common.io.CharStreams;
import com.handy.portal.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class TermsActivity extends BaseActivity
{
    private static final String ASSETS_BASE_URL = "file:///android_asset/";

    @InjectView(R.id.terms_webview)
    protected WebView termsWebview;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);
        ButterKnife.inject(this);

        String htmlContent = wrapContent(termsManager.getNewestTermsDetails().getContent());
        termsWebview.loadDataWithBaseURL(ASSETS_BASE_URL, htmlContent, "text/html", "utf-8", null);
    }

    private String wrapContent(String content)
    {
        String template = "%s"; // fall back to just displaying without html wrapping
        try
        {
            InputStream stream = getAssets().open("terms_template.html");
            template = CharStreams.toString(new InputStreamReader(stream, "UTF-8"));
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return String.format(template, content);
    }
}
