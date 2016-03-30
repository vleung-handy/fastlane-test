package com.handy.portal.ui.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;

import com.handy.portal.R;

public class DateFormFieldTableRow extends TableRow implements Errorable
{
    public DateFormFieldTableRow(Context context)
    {
        super(context);
    }

    public DateFormFieldTableRow(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public TextView getLabel()
    {
        return (TextView) findViewById(R.id.label_text_date);
    }

    public TextView getMonthValue()
    {
        return (TextView) findViewById(R.id.month_value_text);
    }

    public TextView getYearValue()
    {
        return (TextView) findViewById(R.id.year_value_text);
    }

    public View getErrorIndicator()
    {
        return findViewById(R.id.error_indicator_date);
    }

    @Override
    public void setErrorState(boolean error)
    {
        int errorColor = ContextCompat.getColor(getContext(), R.color.error_red);
        int normalColor = ContextCompat.getColor(getContext(), R.color.black);

        getErrorIndicator().setVisibility(error ? VISIBLE : INVISIBLE);
        getLabel().setTextColor(error ? errorColor : normalColor);
        getMonthValue().setTextColor(error ? errorColor : normalColor);
        getYearValue().setTextColor(error ? errorColor : normalColor);
    }
}
