package com.handy.portal.dashboard.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.handy.portal.R;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;
import static android.widget.LinearLayout.HORIZONTAL;
import static android.widget.LinearLayout.VERTICAL;


public class CirclePageIndicatorView extends View implements DashboardViewPagerListener {
    private static final int STROKE_WIDTH_DP = 1;
    private static final float RADIUS_DP = 2.5f;

    private float mRadius;
    private final Paint mPaintPageFill = new Paint(ANTI_ALIAS_FLAG);
    private final Paint mPaintStroke = new Paint(ANTI_ALIAS_FLAG);
    private final Paint mPaintFill = new Paint(ANTI_ALIAS_FLAG);
    private ViewPager mViewPager;
    private int mCurrentPage;
    private boolean mCentered;
    private int mOrientation;

    public CirclePageIndicatorView(final Context context) {
        super(context);
        init();
    }

    public CirclePageIndicatorView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CirclePageIndicatorView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CirclePageIndicatorView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        final int defaultPageColor = ContextCompat.getColor(getContext(), R.color.white);
        final int defaultFillColor = ContextCompat.getColor(getContext(), R.color.tertiary_gray);
        final int defaultStrokeColor = ContextCompat.getColor(getContext(), R.color.tertiary_gray);

        int stroke = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, STROKE_WIDTH_DP, getResources().getDisplayMetrics());
        int radius = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, RADIUS_DP, getResources().getDisplayMetrics());

        mPaintPageFill.setStyle(Paint.Style.FILL);
        mPaintPageFill.setColor(defaultPageColor);
        mPaintStroke.setStyle(Paint.Style.STROKE);
        mPaintStroke.setColor(defaultStrokeColor);
        mPaintStroke.setStrokeWidth(stroke);
        mPaintFill.setStyle(Paint.Style.FILL);
        mPaintFill.setColor(defaultFillColor);
        mRadius = radius;

        mCentered = true;
        mOrientation = HORIZONTAL;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mViewPager == null) {
            return;
        }
        final int count = mViewPager.getAdapter().getCount();
        if (count == 0) {
            return;
        }

        if (mCurrentPage >= count) {
            setCurrentItem(count - 1);
            return;
        }

        int longSize;
        int longPaddingBefore;
        int longPaddingAfter;
        int shortPaddingBefore;
        if (mOrientation == HORIZONTAL) {
            longSize = getWidth();
            longPaddingBefore = getPaddingLeft();
            longPaddingAfter = getPaddingRight();
            shortPaddingBefore = getPaddingTop();
        }
        else {
            longSize = getHeight();
            longPaddingBefore = getPaddingTop();
            longPaddingAfter = getPaddingBottom();
            shortPaddingBefore = getPaddingLeft();
        }

        final float nextCircleDrawOffset = mRadius * 4;
        final float shortOffset = shortPaddingBefore + mRadius;
        float longOffset = longPaddingBefore + mRadius;
        if (mCentered) {
            longOffset += ((longSize - longPaddingBefore - longPaddingAfter) / 2.0f) - ((count * nextCircleDrawOffset) / 2.0f);
        }

        float dX;
        float dY;

        float pageFillRadius = mRadius;
        if (mPaintStroke.getStrokeWidth() > 0) {
            pageFillRadius -= mPaintStroke.getStrokeWidth() / 2.0f;
        }

        //Draw stroked circles
        for (int iLoop = 0; iLoop < count; iLoop++) {
            float drawLong = longOffset + (iLoop * nextCircleDrawOffset);
            if (mOrientation == HORIZONTAL) {
                dX = drawLong;
                dY = shortOffset;
            }
            else {
                dX = shortOffset;
                dY = drawLong;
            }
            // Only paint fill if not completely transparent
            if (mPaintPageFill.getAlpha() > 0) {
                canvas.drawCircle(dX, dY, pageFillRadius, mPaintPageFill);
            }

            // Only paint stroke if a stroke width was non-zero
            if (pageFillRadius != mRadius) {
                canvas.drawCircle(dX, dY, mRadius, mPaintStroke);
            }
        }

        //Draw the filled circle
        float cx = mCurrentPage * nextCircleDrawOffset;

        if (mOrientation == HORIZONTAL) {
            dX = longOffset + cx;
            dY = shortOffset;
        }
        else {
            dX = shortOffset;
            dY = longOffset + cx;
        }
        canvas.drawCircle(dX, dY, mRadius, mPaintFill);
    }

    @Override
    public void setViewPager(ViewPager view) {
        if (mViewPager == view) {
            return;
        }
        if (mViewPager != null) {
            mViewPager.addOnPageChangeListener(null);
        }
        if (view.getAdapter() == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }
        mViewPager = view;
        mViewPager.addOnPageChangeListener(this);
        invalidate();
    }

    @Override
    public void setCurrentItem(int item) {
        if (mViewPager == null) {
            throw new IllegalStateException("ViewPager has not been bound.");
        }
        mViewPager.setCurrentItem(item);
        mCurrentPage = item;
        invalidate();
    }

    @Override
    public void onPageScrollStateChanged(int state) { }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

    @Override
    public void onPageSelected(int position) {
        mCurrentPage = position;
        invalidate();
    }

    public void setOrientation(int orientation) {
        switch (orientation) {
            case HORIZONTAL:
            case VERTICAL:
                mOrientation = orientation;
                requestLayout();
                break;
            default:
                throw new IllegalArgumentException("Orientation must be either HORIZONTAL or VERTICAL.");
        }
    }

    public int getOrientation() {
        return mOrientation;
    }
}
