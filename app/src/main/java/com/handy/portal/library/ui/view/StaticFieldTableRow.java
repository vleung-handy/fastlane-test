package com.handy.portal.library.ui.view;

import android.content.Context;
import android.util.AttributeSet;

import com.handy.portal.R;

public class StaticFieldTableRow extends FieldTableRow {
    public StaticFieldTableRow(final Context context) {
        super(context);
    }

    public StaticFieldTableRow(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.element_static_field;
    }
}
