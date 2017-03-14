package com.handy.portal.core.ui.view;


import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.handy.portal.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProShareFooterView extends FrameLayout {

    @BindView(R.id.pro_share_footer_share_button)
    View mShareButton;
    @BindView(R.id.pro_share_footer_help_button)
    View mHelpButton;

    public ProShareFooterView(final Context context) {
        super(context);
        init();
    }

    public ProShareFooterView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ProShareFooterView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ProShareFooterView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_pro_share_footer, this);
        ButterKnife.bind(this);
    }

    public void setShareClickListener(@NonNull OnClickListener listener) {
        mShareButton.setOnClickListener(listener);
    }

    public void setHelpClickListener(@NonNull OnClickListener listener) {
        mHelpButton.setOnClickListener(listener);
    }
}
