package com.handy.portal.bookings.ui.element;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NewDateButtonGroup extends LinearLayout
        implements NewDateButtonView.SelectionChangedListener
{
    private Map<Date, NewDateButtonView> mDateButtons;

    public NewDateButtonGroup(final Context context, final List<Date> dates)
    {
        super(context);
        mDateButtons = new HashMap<>();
        init(dates);
    }

    private void init(final List<Date> dates)
    {
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        setOrientation(LinearLayout.HORIZONTAL);
        for (final Date date : dates)
        {
            final NewDateButtonView dateButtonView = new NewDateButtonView(getContext(), date);
            dateButtonView.setSelectionChangedListener(this);
            mDateButtons.put(date, dateButtonView);
            addView(dateButtonView);
        }
    }

    public boolean selectDate(final Date date)
    {
        final NewDateButtonView view = getDateButtonForDate(date);
        if (view != null)
        {
            view.select();
            return true;
        }
        else
        {
            return false;
        }
    }

    public NewDateButtonView getDateButtonForDate(final Date date)
    {
        return mDateButtons.get(date);
    }

    @Override
    public void onSelectionChanged(final NewDateButtonView targetView)
    {
        if (!targetView.isSelected())
        {
            return;
        }
        for (final NewDateButtonView view : mDateButtons.values())
        {
            if (view.isSelected() && !targetView.equals(view))
            {
                view.setSelected(false);
            }
        }
    }
}
