package com.handy.portal.ui.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;

import com.handy.portal.R;

public class FormFieldTableRow extends TableRow implements Errorable
{
    public FormFieldTableRow(Context context)
    {
        super(context);
    }

    public FormFieldTableRow(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public TextView getLabel()
    {
        return (TextView) findViewById(R.id.label_text);
    }

    public TextView getValue()
    {
        return (TextView) findViewById(R.id.value_text);
    }

    public View getErrorIndicator()
    {
        return findViewById(R.id.error_indicator);
    }

    @Override
    public void setErrorState(boolean error)
    {
        int errorColor = ContextCompat.getColor(getContext(), R.color.error_red);
        int normalColor = ContextCompat.getColor(getContext(), R.color.black);

        getErrorIndicator().setVisibility(error ? VISIBLE : INVISIBLE);
        getLabel().setTextColor(error ? errorColor : normalColor);
        getValue().setTextColor(error ? errorColor : normalColor);
    }
}
