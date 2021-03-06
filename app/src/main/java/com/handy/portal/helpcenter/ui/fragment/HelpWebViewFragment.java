package com.handy.portal.helpcenter.ui.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.handy.portal.R;
import com.handy.portal.core.constant.BundleKeys;
import com.handy.portal.core.constant.MainViewPage;
import com.handy.portal.core.manager.ConfigManager;
import com.handy.portal.core.model.ConfigurationResponse;
import com.handy.portal.webview.PortalWebViewFragment;

import javax.inject.Inject;

public class HelpWebViewFragment extends PortalWebViewFragment {
    @Inject
    ConfigManager mConfigManager;

    public static HelpWebViewFragment newInstance() {
        HelpWebViewFragment fragment = new HelpWebViewFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    @Override
    protected MainViewPage getAppPage() {
        return MainViewPage.HELP_WEBVIEW;
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        setActionBarTitle(R.string.help);

        final ConfigurationResponse configuration = mConfigManager.getConfigurationResponse();
        if (configuration != null) {
            String helpCenterUrl = configuration.getHelpCenterUrl();
            if (helpCenterUrl == null) {
                showToast(R.string.failed_to_open_help);
            }
            else {
                final String redirectPath = getArguments().getString(BundleKeys.HELP_REDIRECT_PATH);
                if (redirectPath != null) {
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
