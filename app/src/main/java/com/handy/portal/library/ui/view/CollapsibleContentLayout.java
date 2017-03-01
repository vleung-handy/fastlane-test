package com.handy.portal.library.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handy.portal.R;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * This is a layout with a header view and a collapsible content view. Tapping the header collapses
 * or expands the content view. The header is set using the {@code setHeader()} method. The content
 * view should be supplied in the layout file and it must be nested within
 * {@link CollapsibleContentLayout}.
 */
public class CollapsibleContentLayout extends LinearLayout {
    private static final int CHEVRON_DEGREES_EXPANDED = 180;
    private static final int CHEVRON_DEGREES_COLLAPSED = 0;
    private static final float PIVOT_CENTER = 0.5f;
    private ViewGroup mContent;
    @BindView(R.id.icon)
    ImageView mIcon;
    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.subtitle)
    TextView mSubtitle;
    @BindView(R.id.chevron)
    ImageView mChevron;
    @BindInt(R.integer.chevron_rotation_duration_millis)
    int mChevronRotationDurationMillis;
    private View mHeader;

    public CollapsibleContentLayout(final Context context) {
        super(context);
        init();
    }

    public CollapsibleContentLayout(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CollapsibleContentLayout(final Context context, final AttributeSet attrs,
                                    final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CollapsibleContentLayout(final Context context, final AttributeSet attrs,
                                    final int defStyleAttr, final int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        // This is the content view that should be nested within CollapsibleContentLayout in the
        // layout file. The reason why index is 1 is because index 0 is the header view right above
        // the content view.
        mContent = (ViewGroup) getChildAt(1);
        collapse();
    }

    private void init() {
        final LayoutInflater inflater = LayoutInflater.from(getContext());
        mHeader = inflater.inflate(R.layout.view_collapsible_content_header, this, false);
        mHeader.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (mContent.getVisibility() == VISIBLE) {
                    collapse();
                }
                else {
                    expand();
                }
            }
        });
        addView(mHeader, 0);
        ButterKnife.bind(this, mHeader);
    }

    private void expand() {
        rotateChevron(CHEVRON_DEGREES_COLLAPSED, CHEVRON_DEGREES_EXPANDED);
        mContent.setVisibility(VISIBLE);
    }

    private void collapse() {
        rotateChevron(CHEVRON_DEGREES_EXPANDED, CHEVRON_DEGREES_COLLAPSED);
        mContent.setVisibility(GONE);
    }

    private void rotateChevron(final int fromDegrees, final int toDegrees) {
        final RotateAnimation animation = new RotateAnimation(
                fromDegrees, toDegrees,
                Animation.RELATIVE_TO_SELF, PIVOT_CENTER,
                Animation.RELATIVE_TO_SELF, PIVOT_CENTER);
        animation.setFillAfter(true);
        animation.setDuration(mChevronRotationDurationMillis);
        mChevron.startAnimation(animation);
    }

    public void setHeader(@DrawableRes final int iconResId, final String titleText,
                          final String subtitleText) {
        mIcon.setImageResource(iconResId);
        mTitle.setText(titleText);
        mSubtitle.setText(subtitleText);
    }

    public ViewGroup getContentViewContainer() {
        return mContent;
    }

    public void freeze() {
        mHeader.setClickable(false);
        mHeader.setAlpha(0.5f);
        mChevron.setVisibility(GONE);
    }
}
