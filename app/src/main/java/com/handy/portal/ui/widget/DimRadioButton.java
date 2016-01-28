package com.handy.portal.ui.widget;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.RadioButton;

public class DimRadioButton extends RadioButton
{

    public DimRadioButton(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
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