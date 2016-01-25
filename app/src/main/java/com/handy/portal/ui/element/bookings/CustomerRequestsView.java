package com.handy.portal.ui.element.bookings;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handy.portal.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CustomerRequestsView extends FrameLayout
{
    @Bind(R.id.booking_details_job_instructions_checklist_title_text)
    TextView mSectionTitleText;
    @Bind(R.id.booking_details_job_instructions_checklist_icon)
    ImageView mSectionIcon;
    @Bind(R.id.booking_details_job_instructions_checklist_entries_layout)
    LinearLayout mEntriesLayout;

    private List<CheckBox> mCheckBoxEntries = new ArrayList<>();

    public CustomerRequestsView(final Context context, final String sectionTitle,
                                @Nullable final Integer sectionIconId, final List<String> entries)
    {
        super(context);
        init(sectionTitle, sectionIconId, entries);
    }

    public CustomerRequestsView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    public CustomerRequestsView(final Context context, final AttributeSet attrs, final int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public void init(String sectionTitle, @Nullable Integer sectionIconId, @NonNull List<String> entries)
    {
        inflate(getContext(), R.layout.element_booking_details_job_instructions_checklist, this);
        ButterKnife.bind(this);

        mSectionTitleText.setText(sectionTitle);
        if (sectionIconId != null)
        {
            mSectionIcon.setImageResource(sectionIconId);
        }

        for (String entry : entries)
        {
            CheckBox blueCheckBox = (CheckBox) LayoutInflater.from(getContext())
                    .inflate(R.layout.checkbox_blue_circle, mEntriesLayout, false);
            blueCheckBox.setText(entry);
            mEntriesLayout.addView(blueCheckBox);
            mCheckBoxEntries.add(blueCheckBox);
        }
    }

    public boolean isChecked()
    {
        for (CheckBox checkBox : mCheckBoxEntries)
        {
            if (checkBox.isChecked()) { return true; }
        }
        return false;
    }
}
