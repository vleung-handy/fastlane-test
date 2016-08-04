package com.handy.portal.library.ui.view;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.handy.portal.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * view that has an image on the left and text next to it
 *
 * supports setting the image, the text and the text color
 */
public class LeftIndicatorTextView extends FrameLayout
{
    @BindView(R.id.left_indicator_text_view_indicator_image)
    ImageView mLeftIndicatorImage;
    @BindView(R.id.left_indicator_text_view_indicator_text)
    TextView mText;

    public LeftIndicatorTextView(final Context context)
    {
        super(context);
        init();
    }

    public LeftIndicatorTextView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public LeftIndicatorTextView(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    protected void init()
    {
        inflate(getContext(), R.layout.element_left_indicator_text_view, this);
        ButterKnife.bind(this);
    }

    public LeftIndicatorTextView setBodyText(String text)
    {
        mText.setText(text);
        return this;
    }

    public LeftIndicatorTextView setTextColorResourceId(int colorResourceId)
    {
        mText.setTextColor(ContextCompat.getColor(getContext(), colorResourceId));
        return this;
    }

    public LeftIndicatorTextView setImageResourceId(int imageResourceId)
    {
        mLeftIndicatorImage.setImageDrawable(ContextCompat.getDrawable(getContext(), imageResourceId));
        return this;
    }

    public LeftIndicatorTextView setImageColorFilter(int colorId)
    {
        mLeftIndicatorImage.setColorFilter(ContextCompat.getColor(getContext(), colorId), PorterDuff.Mode.SRC_ATOP);
        return this;
    }

    public LeftIndicatorTextView setTextSize(int dimenResourceId)
    {
        mText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(dimenResourceId));
        return this;
    }

    public ImageView getImage()
    {
        return mLeftIndicatorImage;
    }
}
