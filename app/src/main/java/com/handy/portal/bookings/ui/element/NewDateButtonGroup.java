package com.handy.portal.bookings.ui.element;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.handy.portal.bookings.ui.adapter.DatesPagerAdapter;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NewDateButtonGroup extends LinearLayout
        implements NewDateButton.SelectionChangedListener
{
    private final DatesPagerAdapter.DateSelectedListener mDateSelectedListener;
    private Map<Date, NewDateButton> mDateButtons;

    public NewDateButtonGroup(
            final Context context,
            final List<Date> dates,
            final DatesPagerAdapter.DateSelectedListener dateSelectedListener
    )
    {
        super(context);
        mDateSelectedListener = dateSelectedListener;
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
            final NewDateButton dateButton = new NewDateButton(getContext(), date);
            dateButton.setSelectionChangedListener(this);
            mDateButtons.put(date, dateButton);
            addView(dateButton);
        }
    }

    public NewDateButton getDateButtonForDate(final Date date)
    {
        return mDateButtons.get(date);
    }

    public void clearSelection()
    {
        for (NewDateButton button : mDateButtons.values())
        {
            button.setSelected(false);
        }
    }

    @Override
    public void onSelectionChanged(final NewDateButton targetButton)
    {
        if (!targetButton.isSelected())
        {
            return;
        }
        for (final NewDateButton button : mDateButtons.values())
        {
            if (button.isSelected() && !targetButton.equals(button))
            {
                button.setSelected(false);
            }
        }
        if (mDateSelectedListener != null)
        {
            mDateSelectedListener.onDateSelected(targetButton.getDate());
        }
    }
}
