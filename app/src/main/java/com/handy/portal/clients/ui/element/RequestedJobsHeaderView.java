package com.handy.portal.clients.ui.element;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handy.portal.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class RequestedJobsHeaderView extends FrameLayout {
    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.help_icon)
    View mHelpIcon;

    private ViewModel mViewModel;

    public RequestedJobsHeaderView(@NonNull final Context context) {
        super(context);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.view_requested_jobs_header, this);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.help_icon)
    void onHelpIconClicked() {
        if (mViewModel != null && !TextUtils.isEmpty(mViewModel.getHelpContent())) {
            new AlertDialog.Builder(getContext())
                    .setMessage(mViewModel.getHelpContent())
                    .setPositiveButton(R.string.ok, null)
                    .create()
                    .show();
        }
    }

    public void bind(final ViewModel viewModel) {
        mViewModel = viewModel;
        mTitle.setText(mViewModel.getTitle());
        mHelpIcon.setVisibility(TextUtils.isEmpty(mViewModel.getHelpContent()) ? GONE : VISIBLE);
    }

    public static class ViewModel {
        private final String mTitle;
        private final String mHelpContent;

        public ViewModel(final String title, final String helpContent) {
            mTitle = title;
            mHelpContent = helpContent;
        }

        public String getTitle() {
            return mTitle;
        }

        public String getHelpContent() {
            return mHelpContent;
        }
    }
}
