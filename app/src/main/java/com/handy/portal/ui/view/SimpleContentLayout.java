package com.handy.portal.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.handy.portal.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SimpleContentLayout extends FrameLayout
{
    @Bind(R.id.image_holder)
    View mImageHolder;
    @Bind(R.id.image)
    ImageView mImage;
    @Bind(R.id.title)
    TextView mTitle;
    @Bind(R.id.description)
    TextView mDescription;
    @Bind(R.id.action_button)
    TextView mAction;

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

    public SimpleContentLayout setAction(final String actionText,
                                         final OnClickListener onClickListener)
    {
        mAction.setText(actionText);
        mAction.setOnClickListener(onClickListener);
        mAction.setVisibility(VISIBLE);
        return this;
    }
}
