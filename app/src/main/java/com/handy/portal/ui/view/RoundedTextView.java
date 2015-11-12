package com.handy.portal.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.util.FontUtils;
import com.handy.portal.util.UIUtils;

public class RoundedTextView extends TextView
{
    private static final int FONT_SIZE = 14;
    private static final int MARGE_SIZE_DP = 3;
    private static LinearLayout.LayoutParams sLayoutParams;

    public RoundedTextView(Context context)
    {
        super(context);
        init();
    }

    public RoundedTextView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public RoundedTextView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public RoundedTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init()
    {
        setTextSize(TypedValue.COMPLEX_UNIT_SP, FONT_SIZE);
        setTextColor(Color.WHITE);
        setBackgroundResource(R.drawable.rounded_background_gray);
        setTypeface(FontUtils.getFont(getContext(), FontUtils.CIRCULAR_BOOK));

        if (sLayoutParams == null)
        {
            sLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            int margePx = UIUtils.calculateDpToPx(getContext(), MARGE_SIZE_DP);
            sLayoutParams.setMargins(margePx, 0, margePx, 0);
        }
        setLayoutParams(sLayoutParams);
    }
}
