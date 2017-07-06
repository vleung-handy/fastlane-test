package com.handy.portal.availability.fragment;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.core.constant.BundleKeys;
import com.handy.portal.core.constant.MainViewPage;
import com.handy.portal.core.manager.PageNavigationManager;
import com.handy.portal.core.ui.fragment.ActionBarFragment;
import com.handy.portal.helpcenter.constants.HelpCenterConstants;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public abstract class EditWeeklyAvailableHoursFragment extends ActionBarFragment {
    @Inject
    PageNavigationManager mNavigationManager;

    @BindView(R.id.fetch_error_view)
    View mFetchErrorView;
    @BindView(R.id.fetch_error_text)
    TextView mFetchErrorText;
    @BindView(R.id.available_hours_info_banner_body)
    TextView mInfoBannerBody;
    @BindView(R.id.available_hours_refresh_layout)
    SwipeRefreshLayout mRefreshLayout;
    @BindView(R.id.available_hours_scroll_view)
    ScrollView mScrollView;

    @LayoutRes
    protected abstract int getContentResId();

    protected abstract void requestAvailableHours();

    @Nullable
    @Override
    public View onCreateView(
            final LayoutInflater inflater,
            @Nullable final ViewGroup container,
            @Nullable final Bundle savedInstanceState
    ) {
        final View view = inflater.inflate(R.layout.fragment_edit_weekly_available_hours, container, false);
        if (getContentResId() != 0) {
            inflater.inflate(getContentResId(), (ViewGroup) view.findViewById(R.id.available_hours_content), true);
        }
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFetchErrorText.setText(R.string.error_fetching_available_hours);
        com.handy.portal.library.util.TextUtils.stripUnderlines(mInfoBannerBody);
        mRefreshLayout.setColorSchemeResources(R.color.handy_blue);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestAvailableHours();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        setActionBar(R.string.available_hours, true);
    }

    @OnClick(R.id.available_hours_info_banner_body)
    public void onInfoBannerClicked() {
        final Bundle arguments = new Bundle();
        arguments.putString(
                BundleKeys.TARGET_URL,
                dataManager.getBaseUrl() + HelpCenterConstants.SETTING_HOURS_INFO_PATH
        );
        mNavigationManager.navigateToPage(getActivity().getSupportFragmentManager(), MainViewPage.WEB_PAGE, arguments,
                null, true);
    }

    @OnClick(R.id.try_again_button)
    public void onRetryFetchAvailableHours() {
        requestAvailableHours();
    }
}
