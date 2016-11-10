package com.handy.portal.library.ui.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handy.portal.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TabWithCountView extends FrameLayout
{
    @BindView(R.id.tab_title)
    TextView mTitle;
    @BindView(R.id.tab_count)
    TextView mCount;

    public TabWithCountView(final Context context)
    {
        super(context);
        init();
    }

    public TabWithCountView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public TabWithCountView(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TabWithCountView(final Context context, final AttributeSet attrs, final int defStyleAttr,
                            final int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init()
    {
        inflate(getContext(), R.layout.view_tab_with_count, this);
        ButterKnife.bind(this);
    }

    public void setTitle(@StringRes final int titleResId)
    {
        mTitle.setText(titleResId);
    }

    public void setCount(@Nullable final Long unreadCount)
    {
        if (unreadCount != null)
        {
            mCount.setVisibility(unreadCount > 0 ? VISIBLE : GONE);
            String unreadCountText = String.valueOf(unreadCount);
            if (unreadCount > 99L)
            {
                unreadCountText = "99+";
            }
            mCount.setText(unreadCountText);
        }
        else
        {
            mCount.setVisibility(GONE);
        }
    }
}
