package com.handy.portal.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.handy.portal.R;

import butterknife.Bind;

public class TooltipView extends InjectedRelativeLayout
{
    @Bind(R.id.tooltip_text)
    TextView tooltipText;
    @Bind(R.id.tooltip_subtext)
    TextView tooltipSubtext;

    public TooltipView(Context context)
    {
        super(context);
    }

    public TooltipView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public TooltipView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public void setContent(CharSequence text, CharSequence subtext)
    {
        tooltipText.setText(text);
        tooltipSubtext.setText(subtext);
    }
}
