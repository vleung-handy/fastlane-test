package com.handy.portal.library.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.handy.portal.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * a simple list item with an icon on the left and text on the right
 */
public class BulletListItem extends FrameLayout
{
    @Bind(R.id.list_item_bullet_image_view)
    ImageView mBulletImageView;

    @Bind(R.id.list_item_text)
    TextView mText;

    public BulletListItem(final Context context)
    {
        super(context);
        init(null);
    }

    public BulletListItem(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init(attrs);
    }

    public BulletListItem(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BulletListItem(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs)
    {
        inflate(getContext(), R.layout.list_item_bullet, this);
        ButterKnife.bind(this);

        TypedArray a = getContext().getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.BulletListItem,
                0, 0);

        try
        {
            Drawable iconDrawable = a.getDrawable(R.styleable.BulletListItem_iconDrawable);
            String text = a.getString(R.styleable.BulletListItem_text);

            setBulletDrawable(iconDrawable);
            setText(text);
        }
        finally
        {
            a.recycle();
        }
    }

    public BulletListItem setBulletDrawable(Drawable drawable)
    {
        mBulletImageView.setBackground(drawable);
        return this;
    }

    public BulletListItem setBulletDrawable(int drawableResourceId)
    {
        mBulletImageView.setBackgroundResource(drawableResourceId);
        return this;
    }

    public BulletListItem setText(String text)
    {
        mText.setText(text);
        return this;
    }

    public BulletListItem setText(int textResourceId)
    {
        mText.setText(textResourceId);
        return this;
    }

    public BulletListItem setBulletColorTint(int colorResourceId)
    {
        mBulletImageView.setColorFilter(
                ContextCompat.getColor(getContext(), colorResourceId),
                PorterDuff.Mode.SRC_ATOP);
        return this;
    }
}
