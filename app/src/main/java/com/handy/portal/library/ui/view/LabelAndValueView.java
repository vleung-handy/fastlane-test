package com.handy.portal.library.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handy.portal.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LabelAndValueView extends FrameLayout
{
    @Bind(R.id.label)
    TextView mLabel;
    @Bind(R.id.value)
    TextView mValue;

    public LabelAndValueView(final Context context)
    {
        super(context);
        init();
    }

    public LabelAndValueView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public LabelAndValueView(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LabelAndValueView(final Context context, final AttributeSet attrs,
                             final int defStyleAttr, final int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init()
    {
        inflate(getContext(), R.layout.view_label_and_value, this);
        ButterKnife.bind(this);
    }

    public void setContent(final String labelText, final String valueText)
    {
        mLabel.setText(labelText);
        mValue.setText(valueText);
    }

    public void setLabel(final String text)
    {
        mLabel.setText(text);
    }

    public void setValue(final String text)
    {
        mValue.setText(text);
    }
}
