package com.handy.portal.help;

import android.content.Context;
import android.util.AttributeSet;

import com.handy.portal.ui.widget.InputTextField;

public final class FirstNameInputTextView extends InputTextField
{

    public FirstNameInputTextView(final Context context) {
        super(context);
    }

    public FirstNameInputTextView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public FirstNameInputTextView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
    }

    public final boolean validate() {
        final String name = this.getText().toString().trim();
        if(name.length() > 0) {
            unHighlight();
            return true;
        }
        else {
            highlight();
            return false;
        }
    }

}
