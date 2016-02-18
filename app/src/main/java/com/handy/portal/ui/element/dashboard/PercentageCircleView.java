package com.handy.portal.ui.element.dashboard;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

public class PercentageCircleView extends View
{
    private static final int STROKE_WIDTH_DP = 5;
    private static final float ONE_PERCENT = .01f;

    private Paint mPaintCircle = new Paint();
    private Paint mPaintNumber = new Paint();
    private Paint mPaintBackground = new Paint();
    private Paint mPaintSubText = new Paint();
    private Paint mPaintSign = new Paint();
    private RectF mOuterBox = new RectF();
    private RectF mInnerBox = new RectF();
    private String mSubText = "";
    private String mSign = "";
    private boolean mInitialized = false;
    private float mCurrentPercentage = 0;
    private float mPercentage = 1.0f;
    private int mSize = 0;
    private int mGoodColor;
    private int mAverageColor;
    private int mBadColor;

    public PercentageCircleView(Context context)
    {
        super(context);
        init();
    }

    public PercentageCircleView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public PercentageCircleView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PercentageCircleView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init()
    {
        mPaintCircle.setAntiAlias(true);
        mPaintNumber.setAntiAlias(true);
        mPaintSign.setAntiAlias(true);
        mPaintBackground.setAntiAlias(true);
        mPaintSubText.setAntiAlias(true);

        mPaintCircle.setColor(Color.GREEN);
        mPaintNumber.setColor(Color.GREEN);
        mPaintSign.setColor(Color.GREEN);
        mPaintBackground.setColor(Color.WHITE);
        mPaintSubText.setColor(Color.GRAY);

        mGoodColor = Color.GREEN;
        mAverageColor = Color.YELLOW;
        mBadColor = Color.RED;
    }

    @Override
    protected void onDraw(final Canvas canvas)
    {
        super.onDraw(canvas);
        if (!mInitialized)
        {
            initialize();
            mInitialized = true;
        }

        int digits = getDigits((int) (mCurrentPercentage * 100));

        canvas.drawArc(mOuterBox, -90, mCurrentPercentage * 360, true, mPaintCircle);
        canvas.drawArc(mInnerBox, -90, 360, true, mPaintBackground);

        int xPos = (mSize / 2);
        int yPos = (int) ((mSize / 2) - ((mPaintNumber.descent() + mPaintNumber.ascent()) / 2));
        //((mPaintNumber.descent() + mPaintNumber.ascent()) / 2) is the distance from the baseline to the center.

        canvas.drawText(Integer.toString((int) (mCurrentPercentage * 100)), xPos, yPos, mPaintNumber);
        canvas.drawText(mSign, xPos + mPaintSign.getTextSize() * digits, yPos - mPaintSign.getTextSize(), mPaintSign);
        canvas.drawText(mSubText, xPos, yPos + mPaintSubText.getTextSize() * 1.4f, mPaintSubText);

        if (mCurrentPercentage < mPercentage)
        {
            mCurrentPercentage += ONE_PERCENT;
            invalidate();
        }
    }

    public void setPercentage(float percentage)
    {
        mPercentage = percentage;
        resetColor();
    }

    public void setColor(int backgroundColor, int subtitleColor, int goodColor, int averageColor, int badColor)
    {
        mPaintBackground.setColor(backgroundColor);
        mPaintSubText.setColor(subtitleColor);

        mGoodColor = goodColor;
        mAverageColor = averageColor;
        mBadColor = badColor;

        resetColor();
    }

    public void setSubText(String subText)
    {
        mSubText = subText;
    }

    public void setSign(String sign)
    {
        mSign = sign;
    }

    private void initialize()
    {
        mSize = Math.min(getWidth(), getHeight());
        int stroke = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, STROKE_WIDTH_DP, getResources().getDisplayMetrics());

        mPaintNumber.setTextSize(mSize / 3);
        mPaintNumber.setTextAlign(Paint.Align.CENTER);

        mPaintSign.setTextSize(mSize / 9);
        mPaintSign.setTextAlign(Paint.Align.CENTER);

        mPaintSubText.setTextSize(mSize / 9);
        mPaintSubText.setTextAlign(Paint.Align.CENTER);

        mOuterBox = new RectF(0, 0, mSize, mSize);
        mInnerBox = new RectF(stroke, stroke, mSize - stroke, mSize - stroke);
    }

    private void resetColor()
    {
        if (mPercentage >= .8f)
        {
            mPaintCircle.setColor(mGoodColor);
            mPaintNumber.setColor(mGoodColor);
            mPaintSign.setColor(mGoodColor);
        }
        else if (mPercentage >= .6f)
        {
            mPaintCircle.setColor(mAverageColor);
            mPaintNumber.setColor(mAverageColor);
            mPaintSign.setColor(mAverageColor);
        }
        else
        {
            mPaintCircle.setColor(mBadColor);
            mPaintNumber.setColor(mBadColor);
            mPaintSign.setColor(mBadColor);
        }
    }

    private int getDigits(int num)
    {
        if (num == 0)
        { return 1; }
        else
        { return (int) (Math.log10(num) + 1); }
    }
}
