package com.handy.portal.library.ui.view;

import android.animation.LayoutTransition;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handy.portal.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TabWithCountView extends LinearLayout {
    @BindView(R.id.tab_title)
    TextView mTitle;
    @BindView(R.id.tab_count)
    TextView mCount;

    public TabWithCountView(final Context context) {
        super(context);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.view_tab_with_count, this);
        setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);
        setLayoutTransition(new LayoutTransition());
        ButterKnife.bind(this);
    }

    public void setTitle(@StringRes final int titleResId) {
        mTitle.setText(titleResId);
    }

    public void setCount(@Nullable final Long unreadCount) {
        if (unreadCount != null) {
            mCount.setVisibility(unreadCount > 0 ? VISIBLE : GONE);
            String unreadCountText = String.valueOf(unreadCount);
            if (unreadCount > 99L) {
                unreadCountText = "99+";
            }
            mCount.setText(unreadCountText);
        }
        else {
            mCount.setVisibility(GONE);
        }
    }
}
