package com.handy.portal.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.util.TextUtils;
import com.handy.portal.util.UIUtils;

public class RoundedTextView extends TextView
{
    private static LinearLayout.LayoutParams sLayoutParams;
    private static Typeface sFont;

    public RoundedTextView(Context context)
    {
        super(context);
        init();
    }

    public RoundedTextView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public RoundedTextView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public RoundedTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init()
    {
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        setTextColor(Color.WHITE);
        setBackgroundResource(R.drawable.rounded_background_gray);

        if (sLayoutParams == null)
        {
            sLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            int margePx = UIUtils.calculateDpToPx(getContext(), 3);
            sLayoutParams.setMargins(margePx, 0, margePx, 0);
        }
        setLayoutParams(sLayoutParams);

        if (sFont == null)
        {
            sFont = Typeface.createFromAsset(getContext().getAssets(), TextUtils.Fonts.CIRCULAR_BOOK);
        }
        setTypeface(sFont);
    }
}
