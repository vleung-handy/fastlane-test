package com.handy.portal.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.handy.portal.R;

public class NotificationIconImageView extends ImageView
{
    private static final int[] STATE_READ = {R.attr.state_read};

    public NotificationIconImageView(final Context context)
    {
        super(context);
    }

    public NotificationIconImageView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    public NotificationIconImageView(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    private boolean mIsRead = false;

    public void setRead(boolean isRead)
    {
        mIsRead = isRead;
    }

    @Override
    public int[] onCreateDrawableState(int extraSpace)
    {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (mIsRead)
        {
            mergeDrawableStates(drawableState, STATE_READ);
        }

        return drawableState;
    }
}
