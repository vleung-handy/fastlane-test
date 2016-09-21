package com.handy.portal.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handy.portal.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HandyCheckBox extends FrameLayout
{
    @BindView(R.id.check_box)
    CheckBox mCheckBox;
    @BindView(R.id.label)
    TextView mLabel;

    @OnClick(R.id.check_box_wrapper)
    public void onCheckBoxWrapperClicked()
    {
        mCheckBox.toggle();
    }

    public HandyCheckBox(final Context context)
    {
        super(context);
        init();
    }

    public HandyCheckBox(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public HandyCheckBox(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HandyCheckBox(final Context context, final AttributeSet attrs, final int defStyleAttr,
                         final int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init()
    {
        inflate(getContext(), R.layout.view_check_box_item, this);
        ButterKnife.bind(this);
    }

    public void setLabel(final String text)
    {
        mLabel.setText(text);
    }

    public void setChecked(final boolean checked)
    {
        mCheckBox.setChecked(checked);
    }

    public void setOnCheckedChangeListener(
            final CompoundButton.OnCheckedChangeListener onCheckedChangeListener)
    {
        mCheckBox.setOnCheckedChangeListener(onCheckedChangeListener);
    }
}
