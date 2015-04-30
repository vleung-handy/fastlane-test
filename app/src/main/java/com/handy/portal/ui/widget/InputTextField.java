package com.handy.portal.ui.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

import com.handy.portal.R;

public abstract class InputTextField extends EditText {
    private final ColorStateList defaultHintColor = this.getHintTextColors();
    private final ColorStateList defaultTextColor = this.getTextColors();
    private boolean isHighlighted;

    public InputTextField(final Context context) {
        super(context);
        init();
    }

    public InputTextField(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public InputTextField (final Context context, final AttributeSet attrs, final  int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    void init() {
        this.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence charSequence, final int start,
                                          final int count, final int after) {

            }

            @Override
            public void onTextChanged(final CharSequence charSequence, final int start,
                                      final int before, final int count) {
                if (start != 0 || before != 0) unHighlight();
            }

            @Override
            public void afterTextChanged(final Editable editable) {
            }
        });
    }

    public final void highlight() {
        isHighlighted = true;
        this.setHintTextColor(getResources().getColor(R.color.error_red));
        this.setTextColor(getResources().getColor(R.color.error_red));
    }

    public final void unHighlight() {
        isHighlighted = false;
        this.setHintTextColor(defaultHintColor);
        this.setTextColor(defaultTextColor);
    }

    public final boolean isHighlighted() {
        return isHighlighted;
    }

    abstract boolean validate();
}
