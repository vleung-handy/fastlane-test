package com.handy.portal.bookings.ui.element;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.common.collect.Lists;
import com.handy.portal.bookings.ui.adapter.DatesPagerAdapter;
import com.handy.portal.library.util.DateTimeUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class NewDateButtonGroup extends LinearLayout
        implements NewDateButton.SelectionChangedListener {
    private final DatesPagerAdapter.DateSelectedListener mDateSelectedListener;
    private Map<Date, NewDateButton> mDateButtons;

    public NewDateButtonGroup(
            final Context context,
            final List<Date> dates,
            final DatesPagerAdapter.DateSelectedListener dateSelectedListener
    ) {
        super(context);
        mDateSelectedListener = dateSelectedListener;
        mDateButtons = new LinkedHashMap<>();
        init(dates);
    }

    private void init(final List<Date> dates) {
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        setOrientation(LinearLayout.HORIZONTAL);
        for (final Date date : dates) {
            final NewDateButton dateButton = new NewDateButton(getContext(), date);
            dateButton.setSelectionChangedListener(this);
            mDateButtons.put(date, dateButton);
            addView(dateButton);
        }
    }

    public NewDateButton getDateButtonForDate(final Date date) {
        //We only care about the date, the time info will be ignored
        return mDateButtons.get(DateTimeUtils.getDateWithoutTime(date));
    }

    public void clearSelection() {
        for (final NewDateButton button : mDateButtons.values()) {
            button.setSelected(false);
        }
    }

    public NewDateButton getFirstEnabledDateButton() {
        for (final NewDateButton button : mDateButtons.values()) {
            if (button.isEnabled()) {
                return button;
            }
        }
        return null;
    }

    public NewDateButton getLastEnabledDateButton() {
        final ArrayList<NewDateButton> buttons = Lists.newArrayList(mDateButtons.values());
        Collections.reverse(buttons);
        for (final NewDateButton button : buttons) {
            if (button.isEnabled()) {
                return button;
            }
        }
        return null;
    }

    @Override
    public void onSelectionChanged(final NewDateButton targetButton) {
        if (!targetButton.isSelected()) {
            return;
        }
        for (final NewDateButton button : mDateButtons.values()) {
            if (button.isSelected() && !targetButton.equals(button)) {
                button.setSelected(false);
            }
        }
        if (mDateSelectedListener != null) {
            mDateSelectedListener.onDateSelected(targetButton.getDate());
        }
    }
}
