package com.handy.portal.webview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.handy.portal.R;
import com.handy.portal.core.constant.BundleKeys;
import com.handy.portal.core.constant.RequestCode;
import com.handy.portal.core.manager.ProviderManager;
import com.handy.portal.core.model.ProviderProfile;
import com.handy.portal.core.ui.view.ProShareFooterView;
import com.handy.portal.library.util.FragmentUtils;
import com.handy.portal.library.util.ShareUtils;
import com.handy.portal.library.util.Utils;

import javax.inject.Inject;

import butterknife.BindView;

public class ShareProviderWebViewFragment extends PortalWebViewFragment {

    public static final String HELP_URL = "https://prohelp.handy.com/hc/en-us/articles/115003012287-Pro-Profile-FAQ";
    @Inject
    ProviderManager mProviderManager;

    @BindView(R.id.portal_web_view_footer)
    FrameLayout mFooterContainer;

    public static ShareProviderWebViewFragment newInstance() {
        ShareProviderWebViewFragment fragment = new ShareProviderWebViewFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ProviderProfile profile = mProviderManager.getCachedProviderProfile();

        Bundle args = getArguments();
        args.putString(BundleKeys.TARGET_URL, profile.getReferralInfo().getProfileUrl() + "?hide_header=1");
        args.putString(BundleKeys.TITLE, getString(R.string.your_public_profile));
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ProShareFooterView footerView = new ProShareFooterView(getContext());
        footerView.setShareClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final Intent dummyIntent = new Intent();
                dummyIntent.setAction(Intent.ACTION_SEND);
                dummyIntent.setType("text/plain");

                final Intent activityPickerIntent = new Intent();
                activityPickerIntent.setAction(Intent.ACTION_PICK_ACTIVITY);
                activityPickerIntent.putExtra(Intent.EXTRA_TITLE, getString(R.string.share_with));
                activityPickerIntent.putExtra(Intent.EXTRA_INTENT, dummyIntent);
                startActivityForResult(activityPickerIntent, RequestCode.PICK_ACTIVITY);
            }
        });
        footerView.setHelpClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                PortalWebViewFragment fragment =
                        PortalWebViewFragment.newInstance(HELP_URL, getString(R.string.help));
                FragmentUtils.switchToFragment(ShareProviderWebViewFragment.this, fragment, true);
            }
        });
        mFooterContainer.addView(footerView);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
        if (requestCode == RequestCode.PICK_ACTIVITY
                && resultCode == Activity.RESULT_OK
                && intent != null) {
            final ProviderProfile profile = mProviderManager.getCachedProviderProfile();

            final String channel = ShareUtils.getChannelFromIntent(getContext(), intent);
            if (channel.equalsIgnoreCase(ShareUtils.CHANNEL_TWITTER)) {
                intent.putExtra(Intent.EXTRA_TEXT, getString(
                        R.string.profile_share_twitter_text_formatted,
                        profile.getReferralInfo().getProfileUrl())
                );
            }
            else {
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.book_my_service));
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.profile_share_text_formatted,
                        profile.getProviderPersonalInfo().getFirstName(),
                        profile.getReferralInfo().getProfileUrl()));
            }
            Utils.safeLaunchIntent(intent, getContext());
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }
}
