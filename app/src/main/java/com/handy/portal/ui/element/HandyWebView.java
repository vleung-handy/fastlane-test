package com.handy.portal.ui.element;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.crashlytics.android.Crashlytics;
import com.google.common.io.CharStreams;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HandyWebView extends WebView //TODO: refactor class name
{

    private static final String ASSETS_BASE_URL = "file:///android_asset/";//TODO: can consolidate these fields into a parameter file
    private static final String TEMPLATE_HTML = "webview_template.html";
    private static final String UTF_8 = "UTF-8";
    private static final String TEMPLATE_PLACEHOLDER = "{{content}}";

    public HandyWebView(Context context)
    {
        super(context);
        overrideWebViewUrlLoading();
    }

    public HandyWebView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        overrideWebViewUrlLoading();
    }

    public HandyWebView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        overrideWebViewUrlLoading();
    }

    public void clearHtml()
    {
        loadUrl("about:blank"); //what is the overhead time of this? if slow, need to reconsider where called
    }

    public void loadHtml(String htmlBody)
    {
        String htmlContent = wrapContent(htmlBody);
        loadDataWithBaseURL(ASSETS_BASE_URL, htmlContent, "text/html", UTF_8, null);
    }

    private String wrapContent(String content)
    {
        try
        {
            InputStream stream = this.getContext().getAssets().open(TEMPLATE_HTML);
            String template = CharStreams.toString(new InputStreamReader(stream, UTF_8));
            return template.replace(TEMPLATE_PLACEHOLDER, content);

        } catch (IOException e)
        {
            Crashlytics.logException(e);
            return content;
        }
    }

    private void overrideWebViewUrlLoading()
    {
        setWebViewClient(new WebViewClient()
        {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                if (url != null)
                {
                    try{
                        view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    }catch(Exception e){
                        Crashlytics.log("Attempted to open " + url);
                        Crashlytics.logException(e);
                    }
                    return true;
                } else
                {
                    return false;
                }
            }

            @Override
            public void onPageFinished(WebView webView, String url){
                super.onPageFinished(webView, url);
                //TODO: run callback to make webview visible
            }
        });
    }
}
