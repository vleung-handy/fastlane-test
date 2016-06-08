package com.handy.portal.library.ui.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.library.util.UIUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FormFieldTableRow extends TableRow implements Errorable
{
    @Bind(R.id.label_text)
    TextView mLabelText;
    @Bind(R.id.value_text)
    TextView mValueText;
    @Bind(R.id.error_indicator)
    View mErrorIndicator;

    public FormFieldTableRow(Context context)
    {
        super(context);
        init();
    }

    public FormFieldTableRow(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    private void init()
    {
        inflate(getContext(), R.layout.element_form_field, this);
        ButterKnife.bind(this);
        mValueText.addTextChangedListener(new UIUtils.FormFieldErrorStateRemover(this));
    }

    public TextView getLabel()
    {
        return mLabelText;
    }

    public TextView getValue()
    {
        return mValueText;
    }

    public View getErrorIndicator()
    {
        return mErrorIndicator;
    }

    @Override
    public void setErrorState(boolean error)
    {
        int errorColor = ContextCompat.getColor(getContext(), R.color.plumber_red);
        int normalLabelColor = ContextCompat.getColor(getContext(), R.color.form_label);
        int normalValueColor = ContextCompat.getColor(getContext(), R.color.black);

        getErrorIndicator().setVisibility(error ? VISIBLE : INVISIBLE);
        getLabel().setTextColor(error ? errorColor : normalLabelColor);
        getValue().setTextColor(error ? errorColor : normalValueColor);
    }
}