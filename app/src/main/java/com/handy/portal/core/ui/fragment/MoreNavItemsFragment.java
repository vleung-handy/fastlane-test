package com.handy.portal.core.ui.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handy.portal.R;
import com.handy.portal.bookings.manager.BookingManager;
import com.handy.portal.core.constant.MainViewPage;
import com.handy.portal.core.manager.ConfigManager;
import com.handy.portal.core.manager.PageNavigationManager;
import com.handy.portal.core.manager.PrefsManager;
import com.handy.portal.core.manager.ProviderManager;
import com.handy.portal.core.ui.activity.ProShareActivity;
import com.handy.portal.core.ui.view.ProHeaderView;
import com.handybook.shared.layer.LayerHelper;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.handy.portal.core.constant.MainViewPage.ACCOUNT_SETTINGS;
import static com.handy.portal.core.constant.MainViewPage.DASHBOARD;
import static com.handy.portal.core.constant.MainViewPage.DASHBOARD_VIDEO_LIBRARY;
import static com.handy.portal.core.constant.MainViewPage.HELP_WEBVIEW;
import static com.handy.portal.core.constant.MainViewPage.PAYMENTS;
import static com.handy.portal.core.constant.MainViewPage.PROFILE_UPDATE;
import static com.handy.portal.core.constant.MainViewPage.REFER_A_FRIEND;
import static com.handy.portal.core.model.ProviderPersonalInfo.ProfileImage.Type.THUMBNAIL;

public class MoreNavItemsFragment extends ActionBarFragment {

    @Inject
    EventBus mBus;
    @Inject
    ProviderManager mProviderManager;
    @Inject
    ConfigManager mConfigManager;
    @Inject
    PrefsManager mPrefsManager;
    @Inject
    BookingManager mBookingManager;
    @Inject
    LayerHelper mLayerHelper;
    @Inject
    PageNavigationManager mNavigationManager;

    @BindView(R.id.more_pro_header)
    ProHeaderView mProHeaderView;

    @Override
    protected MainViewPage getAppPage() {
        return MainViewPage.MORE_ITEMS;
    }

    @Nullable
    @Override
    public View onCreateView(
            final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_more_nav_items, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mProHeaderView.setDisplay(
                mProviderManager.getCachedProviderProfile(),
                mProviderManager.getCachedProfileImageUrl(THUMBNAIL));
    }

    @Override
    public void onResume() {
        super.onResume();
        setActionBar(getString(R.string.profile), false);
    }

    @OnClick(R.id.more_pro_header)
    public void launchProfile() {
        mNavigationManager.navigateToPage(getActivity().getSupportFragmentManager(),
                PROFILE_UPDATE, null, null, true);
    }

    @OnClick(R.id.more_view_and_share)
    public void launchViewAndShare() {
        startActivity(new Intent(getContext(), ProShareActivity.class));
    }

    @OnClick(R.id.more_ratings_and_feedback)
    public void launchRatingAndFeedback() {
        mNavigationManager.navigateToPage(getActivity().getSupportFragmentManager(),
                DASHBOARD, null, null, true);
    }

    @OnClick(R.id.more_payments)
    public void launchPayments() {
        mNavigationManager.navigateToPage(getActivity().getSupportFragmentManager(),
                PAYMENTS, null, null, true);
    }

    @OnClick(R.id.more_refer_a_friend)
    public void launchReferral() {
        mNavigationManager.navigateToPage(getActivity().getSupportFragmentManager(),
                REFER_A_FRIEND, null, null, true);
    }

    @OnClick(R.id.more_account_settings)
    public void launchAccountSettings() {
        mNavigationManager.navigateToPage(getActivity().getSupportFragmentManager(),
                ACCOUNT_SETTINGS, null, null, true);
    }

    @OnClick(R.id.more_video_library)
    public void launchVideoLibrary() {
        mNavigationManager.navigateToPage(getActivity().getSupportFragmentManager(),
                DASHBOARD_VIDEO_LIBRARY, null, null, true);
    }

    @OnClick(R.id.more_help)
    public void launchHelp() {
        mNavigationManager.navigateToPage(getActivity().getSupportFragmentManager(),
                HELP_WEBVIEW, null, null, true);
    }
}
