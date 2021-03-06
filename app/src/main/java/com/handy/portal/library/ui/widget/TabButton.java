package com.handy.portal.library.ui.widget;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handy.portal.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TabButton extends FrameLayout {
    @BindView(R.id.tab_icon)
    ImageView mTabIcon;
    @BindView(R.id.tab_title)
    TextView mTabTitle;
    @BindView(R.id.tab_unread_count)
    TextView mTabUnreadCount;
    private TabButtonGroup mGroup;

    public TabButton(final Context context) {
        super(context);
    }

    public TabButton(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public TabButton(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TabButton(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public TabButton init(@StringRes final int titleResId, @DrawableRes final int iconResId) {
        removeAllViews();
        final View view = inflate(getContext(), R.layout.element_tab, this);
        ButterKnife.bind(this, view);
        mTabTitle.setText(titleResId);
        mTabIcon.setImageResource(iconResId);

        setClickable(true);
        setForeground(getSelectableItemBackgroundDrawable());

        final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        params.weight = 1.0f;
        setLayoutParams(params);

        return this;
    }

    public void setUnreadCount(final int unreadCount) {
        mTabUnreadCount.setVisibility(unreadCount > 0 ? VISIBLE : GONE);
        String unreadCountText = String.valueOf(unreadCount);
        if (unreadCount > 99) {
            unreadCountText = "99+";
        }
        mTabUnreadCount.setText(unreadCountText);
    }

    private Drawable getSelectableItemBackgroundDrawable() {
        int[] attrs = new int[]{R.attr.selectableItemBackground};
        final TypedArray typedArray = getContext().obtainStyledAttributes(attrs);
        final Drawable drawable = typedArray.getDrawable(0);
        typedArray.recycle();
        return drawable;
    }

    public void setGroup(final TabButtonGroup group) {
        mGroup = group;
    }

    public void toggle() {
        mGroup.toggle(this);
    }

    public void dim(final float alpha) {
        mTabIcon.setAlpha(alpha);
        mTabTitle.setAlpha(alpha);
    }

    public String getTitle() {
        return mTabTitle.getText().toString();
    }
}
