package com.handy.portal.ui.widget;

import android.content.Context;
import android.util.AttributeSet;

public final class PinCodeInputTextView extends InputTextField
{
    private static final int PIN_CODE_LENGTH = 4;

    public PinCodeInputTextView(final Context context)
    {
        super(context);
        init();
    }

    public PinCodeInputTextView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public PinCodeInputTextView(final Context context, final AttributeSet attrs, final int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    public final boolean validate()
    {
        final String pinCode = this.getText().toString();
        if (pinCode.length() != PIN_CODE_LENGTH)
        {
            highlight();
            return false;
        }
        else
        {
            unHighlight();
            return true;
        }
    }


}
