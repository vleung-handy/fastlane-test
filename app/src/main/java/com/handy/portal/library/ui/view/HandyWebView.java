package com.handy.portal.library.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.crashlytics.android.Crashlytics;
import com.google.common.io.CharStreams;
import com.handy.portal.library.util.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HandyWebView extends WebView //TODO: refactor class name
{

    private static final String ASSETS_BASE_URL = "file:///android_asset/";//TODO: can consolidate these fields into a parameter file
    private static final String TEMPLATE_HTML = "webview_template.html";
    private static final String UTF_8 = "UTF-8";
    private static final String TEMPLATE_PLACEHOLDER = "{{content}}";
    @Nullable
    private InvalidateCallback invalidateCallback;

    public HandyWebView(Context context) {
        super(context);
        init();
    }

    public HandyWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HandyWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void clearHtml() {
        loadUrl("about:blank"); //what is the overhead time of this? if slow, need to reconsider where called
    }

    public void loadHtml(String htmlBody) {
        loadHtml(htmlBody, null);
    }

    public void loadHtml(String htmlBody, @Nullable InvalidateCallback invalidateCallback) {
        this.invalidateCallback = invalidateCallback;
        String htmlContent = wrapContent(htmlBody);
        loadDataWithBaseURL(ASSETS_BASE_URL, htmlContent, "text/html", UTF_8, null);
    }

    private String wrapContent(String content) {
        try {
            InputStream stream = this.getContext().getAssets().open(TEMPLATE_HTML);
            String template = CharStreams.toString(new InputStreamReader(stream, UTF_8));
            return content == null ? "" : template.replace(TEMPLATE_PLACEHOLDER, content);

        }
        catch (IOException e) {
            Crashlytics.logException(e);
            return content;
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressWarnings("deprecation")
    private void init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(final WebView view, final WebResourceRequest request) {
                    String url = request.getUrl().toString();
                    if (url != null) {
                        try {
                            Utils.safeLaunchIntent(new Intent(Intent.ACTION_VIEW, Uri.parse(url)), view.getContext());
                        }
                        catch (Exception e) {
                            Crashlytics.log("Attempted to open " + url);
                            Crashlytics.logException(e);
                        }
                        return true;
                    }
                    else {
                        return false;
                    }
                }
            });
        }
        else {
            setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if (url != null) {
                        try {
                            Utils.safeLaunchIntent(new Intent(Intent.ACTION_VIEW, Uri.parse(url)), view.getContext());
                        }
                        catch (Exception e) {
                            Crashlytics.log("Attempted to open " + url);
                            Crashlytics.logException(e);
                        }
                        return true;
                    }
                    else {
                        return false;
                    }
                }
            });
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if (getContentHeight() > 0 && invalidateCallback != null) {
            invalidateCallback.invalidate();
        }
    }

    public interface InvalidateCallback {
        void invalidate();
    }
}
