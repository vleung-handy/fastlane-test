package com.handy.portal.ui.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.handy.portal.ui.widget.InputTextField;

public final class EmailInputTextView extends InputTextField
{

    public EmailInputTextView(final Context context) {
        super(context);
    }

    public EmailInputTextView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public EmailInputTextView(final Context context, final AttributeSet attrs, final  int defStyle) {
        super(context, attrs, defStyle);
    }

    public final boolean validate() {
        final String email = this.getText().toString().trim();
        if (email == null || !email.matches("[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}")) {
            highlight();
            return false;
        }
        else {
            unHighlight();
            return true;
        }
    }
    
    public final String getEmail() {
        return this.getText().toString().trim();
    }
}
