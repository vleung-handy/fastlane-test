package com.handy.portal.library.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.handy.portal.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SimpleContentLayout extends FrameLayout
{
    @BindView(R.id.image_holder)
    View mImageHolder;
    @BindView(R.id.image)
    ImageView mImage;
    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.description)
    TextView mDescription;
    @BindView(R.id.action_button)
    TextView mActionButton;
    @BindView(R.id.action_icon)
    ImageView mActionIcon;
    @BindView(R.id.expand_button)
    TextView mExpandButton;
    @BindView(R.id.content_holder)
    View mContentHolder;

    @Nullable
    private Runnable mExpandCallback;

    @OnClick(R.id.expand_button)
    void onExpandButtonClicked()
    {
        mContentHolder.setVisibility(VISIBLE);
        mExpandButton.setVisibility(GONE);
        if (mExpandCallback != null)
        {
            mExpandCallback.run();
        }
    }

    public SimpleContentLayout(final Context context)
    {
        super(context);
        init();
    }

    public SimpleContentLayout(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public SimpleContentLayout(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SimpleContentLayout(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init()
    {
        inflate(getContext(), R.layout.view_simple_content, this);
        ButterKnife.bind(this);
    }

    public SimpleContentLayout setContent(final String titleText, final String descriptionText)
    {
        mTitle.setText(titleText);
        mDescription.setText(descriptionText);
        return this;
    }

    public SimpleContentLayout setImage(final Drawable drawable)
    {
        mImage.setImageDrawable(drawable);
        mImageHolder.setVisibility(VISIBLE);
        return this;
    }

    public SimpleContentLayout setAction(final String actionButtonText,
                                         final OnClickListener onClickListener)
    {
        mActionButton.setText(actionButtonText);
        mActionButton.setOnClickListener(onClickListener);
        mActionButton.setVisibility(VISIBLE);
        return this;
    }

    public SimpleContentLayout setAction(final Drawable icon,
                                         final OnClickListener onClickListener)
    {
        mActionIcon.setImageDrawable(icon);
        mActionIcon.setOnClickListener(onClickListener);
        mActionIcon.setVisibility(VISIBLE);
        return this;
    }

    public SimpleContentLayout collapse(final String expandButtonText,
                                        @Nullable final Runnable expandCallback)
    {
        mExpandCallback = expandCallback;
        mExpandButton.setText(expandButtonText);
        mExpandButton.setVisibility(VISIBLE);
        mContentHolder.setVisibility(GONE);
        return this;
    }
}
