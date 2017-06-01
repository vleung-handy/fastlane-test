package com.handy.portal.availability.fragment;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.core.ui.fragment.ActionBarFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class EditWeeklyAvailableHoursFragment extends ActionBarFragment {

    @BindView(R.id.fetch_error_view)
    View mFetchErrorView;
    @BindView(R.id.fetch_error_text)
    TextView mFetchErrorText;
    @BindView(R.id.available_hours_info_banner_body)
    TextView mInfoBannerBody;

    @LayoutRes
    protected abstract int getContentResId();

    @Nullable
    @Override
    public View onCreateView(
            final LayoutInflater inflater,
            @Nullable final ViewGroup container,
            @Nullable final Bundle savedInstanceState
    ) {
        final View view = inflater.inflate(R.layout.fragment_edit_weekly_available_hours, container, false);
        inflater.inflate(getContentResId(), (ViewGroup) view.findViewById(R.id.available_hours_content), true);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFetchErrorText.setText(R.string.error_fetching_available_hours);
        com.handy.portal.library.util.TextUtils.stripUnderlines(mInfoBannerBody);
    }
}
