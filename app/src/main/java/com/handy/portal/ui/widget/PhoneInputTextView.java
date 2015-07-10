package com.handy.portal.ui.widget;

import android.content.Context;
import android.util.AttributeSet;

public final class PhoneInputTextView extends InputTextField
{
    public PhoneInputTextView(final Context context)
    {
        super(context);
    }

    public PhoneInputTextView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    public PhoneInputTextView(final Context context, final AttributeSet attrs, final int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean validate()
    {
        return true;
    }

    public final String getPhoneNumber()
    {
        return this.getText().toString().replaceAll("[^0-9]", "");
    }
}
