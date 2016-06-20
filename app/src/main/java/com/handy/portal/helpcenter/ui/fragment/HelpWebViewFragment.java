package com.handy.portal.helpcenter.ui.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.AppPage;
import com.handy.portal.manager.ConfigManager;
import com.handy.portal.model.ConfigurationResponse;
import com.handy.portal.webview.PortalWebViewFragment;

import javax.inject.Inject;

public class HelpWebViewFragment extends PortalWebViewFragment
{
    @Inject
    ConfigManager mConfigManager;

    @Override
    protected AppPage getTab()
    {
        return AppPage.HELP_WEBVIEW;
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        setActionBarTitle(R.string.help);

        final ConfigurationResponse configuration = mConfigManager.getConfigurationResponse();
        if (configuration != null)
        {
            String helpCenterUrl = configuration.getHelpCenterUrl();

            final String redirectPath = getArguments().getString(BundleKeys.HELP_REDIRECT_PATH);
            if (redirectPath != null)
            {
                helpCenterUrl = Uri.parse(helpCenterUrl).buildUpon()
                        .appendQueryParameter("redirect_to", redirectPath)
                        .build().toString();
                setOptionsMenuEnabled(true);
                setBackButtonEnabled(true);
            }
            getWebView().loadUrl(helpCenterUrl);
        }
        // TODO: Handle null configuration
    }
}
