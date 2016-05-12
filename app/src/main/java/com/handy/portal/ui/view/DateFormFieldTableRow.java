package com.handy.portal.ui.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.util.UIUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DateFormFieldTableRow extends TableRow implements Errorable
{
    @Bind(R.id.label_text_date)
    TextView mLabelText;
    @Bind(R.id.month_value_text)
    TextView mMonthValueText;
    @Bind(R.id.year_value_text)
    TextView mYearValueText;
    @Bind(R.id.error_indicator_date)
    View mErrorIndicatorDate;

    public DateFormFieldTableRow(Context context)
    {
        super(context);
        init();
    }

    public DateFormFieldTableRow(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    private void init()
    {
        inflate(getContext(), R.layout.element_date_form_field, this);
        ButterKnife.bind(this);
        mMonthValueText.addTextChangedListener(new UIUtils.FormFieldErrorStateRemover(this));
        mYearValueText.addTextChangedListener(new UIUtils.FormFieldErrorStateRemover(this));
    }

    public TextView getLabel()
    {
        return mLabelText;
    }

    public TextView getMonthValue()
    {
        return mMonthValueText;
    }

    public TextView getYearValue()
    {
        return mYearValueText;
    }

    public View getErrorIndicator()
    {
        return mErrorIndicatorDate;
    }

    @Override
    public void setErrorState(boolean error)
    {
        int errorColor = ContextCompat.getColor(getContext(), R.color.plumber_red);
        int normalLabelColor = ContextCompat.getColor(getContext(), R.color.form_label);
        int normalValueColor = ContextCompat.getColor(getContext(), R.color.black);

        getErrorIndicator().setVisibility(error ? VISIBLE : INVISIBLE);
        getLabel().setTextColor(error ? errorColor : normalLabelColor);
        getMonthValue().setTextColor(error ? errorColor : normalValueColor);
        getYearValue().setTextColor(error ? errorColor : normalValueColor);
    }
}
