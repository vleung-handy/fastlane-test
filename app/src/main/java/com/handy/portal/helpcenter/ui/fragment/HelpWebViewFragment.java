package com.handy.portal.helpcenter.ui.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewPage;
import com.handy.portal.manager.ConfigManager;
import com.handy.portal.manager.ProviderManager;
import com.handy.portal.model.ConfigurationResponse;
import com.handy.portal.webview.PortalWebViewFragment;

import javax.inject.Inject;

public class HelpWebViewFragment extends PortalWebViewFragment
{
    @Inject
    ConfigManager mConfigManager;
    @Inject
    ProviderManager mProviderManager;

    @Override
    protected MainViewPage getAppPage()
    {
        return MainViewPage.HELP_WEBVIEW;
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState)
    {
        setActionBarTitle(R.string.help);

        final ConfigurationResponse configuration = mConfigManager.getConfigurationResponse();
        if (configuration != null)
        {
            String helpCenterUrl = configuration.getHelpCenterUrl();
            if (helpCenterUrl == null)
            {
                Toast.makeText(getContext(), getString(R.string.failed_to_open_help),
                        Toast.LENGTH_LONG).show();

                if (mProviderManager.getCachedActiveProvider() != null)
                {
                    Crashlytics.logException(new NullPointerException("Help center url null for pro in country: " +
                            mProviderManager.getCachedActiveProvider().getCountry()));
                }
                else
                {
                    Crashlytics.logException(new NullPointerException("Help center url and provider is null"));
                }
            }
            else
            {
                final String redirectPath = getArguments().getString(BundleKeys.HELP_REDIRECT_PATH);
                if (redirectPath != null)
                {
                    helpCenterUrl = Uri.parse(helpCenterUrl).buildUpon()
                            .appendQueryParameter("redirect_to", redirectPath)
                            .build().toString();
                    setOptionsMenuEnabled(true);
                    setBackButtonEnabled(true);
                }
                getArguments().putString(BundleKeys.TARGET_URL, helpCenterUrl);
            }
        }
        super.onViewCreated(view, savedInstanceState);
    }
}
