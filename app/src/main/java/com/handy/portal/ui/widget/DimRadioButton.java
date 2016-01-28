package com.handy.portal.ui.widget;


import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.RadioButton;

public class DimRadioButton extends RadioButton
{

    public DimRadioButton(final Context context)
    {
        super(context);
    }

    public DimRadioButton(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    public DimRadioButton(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public DimRadioButton(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void setChecked(final boolean checked)
    {
        super.setChecked(checked);
        if (isChecked())
        {
            setAlpha(1.0f);
        }
        else
        {
            setAlpha(0.5f);
        }
    }
}
