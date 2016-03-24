package com.handy.portal.ui.drawable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.handy.portal.R;
import com.handy.portal.util.Utils;

public class BadgeDrawable extends Drawable
{

    private Paint mBadgePaint;
    private Paint mTextPaint;
    private Rect mTxtRect = new Rect();

    private String mCount = "";
    private boolean mWillDraw = false;

    public BadgeDrawable(Context context)
    {
        float textSize = context.getResources().getDimension(R.dimen.badge_text_size);
        mBadgePaint = new Paint();
        mBadgePaint.setColor(ContextCompat.getColor(context, R.color.error_red));
        mBadgePaint.setAntiAlias(true);
        mBadgePaint.setStyle(Paint.Style.FILL);
        mBadgePaint.setAlpha(Utils.RGBA_ALPHA_100_PERCENT);

        mTextPaint = new Paint();
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTypeface(Typeface.DEFAULT);
        mTextPaint.setTextSize(textSize);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    public void draw(Canvas canvas)
    {
        if (!mWillDraw)
        {
            return;
        }
        Rect bounds = getBounds();
        float width = bounds.right - bounds.left;
        float height = bounds.bottom - bounds.top;
        // Position the badge in the top-right quadrant of the icon.

  /*Using Math.max rather than Math.min */
        float radius = ((Math.max(width, height) / 2)) / 2;
        float centerX = (width - radius - 1) + 10;
        float centerY = radius - 5;
        if (mCount.length() <= 2)
        {
            // Draw badge circle.
            canvas.drawCircle(centerX, centerY, radius + 7, mBadgePaint);
        }
        else
        {
            canvas.drawCircle(centerX, centerY, radius + 8, mBadgePaint);
        }
        // Draw badge count text inside the circle.
        mTextPaint.getTextBounds(mCount, 0, mCount.length(), mTxtRect);
        float textHeight = mTxtRect.bottom - mTxtRect.top;
        float textY = centerY + (textHeight / 2f);
        if (mCount.length() > 2)
        { canvas.drawText("99+", centerX, textY, mTextPaint); }
        else
        { canvas.drawText(mCount, centerX, textY, mTextPaint); }
    }

    /*
     Sets the count (i.e notifications) to display.
      */
    public void setCount(String count)
    {
        mCount = count;
        // Only draw a badge if there are notifications.
        mWillDraw = !count.equalsIgnoreCase("0");
        invalidateSelf();
    }

    @Override
    public void setAlpha(int alpha)
    {
        setAlpha(Utils.RGBA_ALPHA_100_PERCENT);
    }

    @Override
    public void setColorFilter(ColorFilter cf)
    {
        // do nothing
    }

    @Override
    public int getOpacity()
    {
        return PixelFormat.UNKNOWN;
    }
}
