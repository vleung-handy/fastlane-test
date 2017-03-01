package com.handy.portal.library.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.handy.portal.R;

public final class BasicInputTextView extends InputTextField {
    private int minLength;

    public BasicInputTextView(final Context context, final int minLength) {
        super(context);
        init(minLength);
    }

    public BasicInputTextView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        readStyleAttributes(context, attrs);
    }

    public BasicInputTextView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        readStyleAttributes(context, attrs);
    }

    private void readStyleAttributes(final Context context, final AttributeSet attrs) {
        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.BasicInputTextView, 0, 0);
        try {
            init(ta.getInteger(R.styleable.BasicInputTextView_minLength, 0));
        }
        finally {
            ta.recycle();
        }
    }

    private void init(final int minLength) {
        setMinLength(minLength);
    }

    public final void setMinLength(final int minLength) {
        this.minLength = minLength;
    }

    public final boolean validate() {
        if (getInput().length() < minLength) {
            highlight();
            return false;
        }
        else {
            unHighlight();
            return true;
        }
    }

    public final String getInput() {
        return this.getText().toString().trim();
    }
}
