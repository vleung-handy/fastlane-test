package com.handy.portal.ui.element;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.model.booking.Booking;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BookingDetailsJobInstructionsSectionView extends RelativeLayout
{
    @Bind(R.id.booking_details_job_instructions_section_title_text)
    TextView mSectionTitleText;
    @Bind(R.id.booking_details_job_instructions_section_title_icon)
    ImageView mSectionIcon;
    @Bind(R.id.booking_details_job_instructions_section_entries_layout)
    LinearLayout mEntriesLayout;

    public BookingDetailsJobInstructionsSectionView(final Context context)
    {
        super(context);
    }

    public BookingDetailsJobInstructionsSectionView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    public BookingDetailsJobInstructionsSectionView(final Context context, final AttributeSet attrs, final int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public void init(String sectionTitle, @Nullable Integer sectionIconId, List<?> entries)
    {
        ButterKnife.bind(this);

        mSectionTitleText.setText(sectionTitle);
        if (sectionIconId != null)
        {
            mSectionIcon.setImageResource(sectionIconId);
        }

        for (Object entry : entries)
        {
            BookingDetailsJobInstructionsSectionEntryView entryView =
                    (BookingDetailsJobInstructionsSectionEntryView) LayoutInflater.from(getContext())
                            .inflate(R.layout.element_booking_details_job_instructions_entry, mEntriesLayout, false);
            mEntriesLayout.addView(entryView);
            if (entry instanceof Booking.BookingInstruction)
            {
                Booking.BookingInstruction instruction = (Booking.BookingInstruction) entry;
                entryView.init(instruction.getDescription());
            }
            else if (entry instanceof String)
            {
                entryView.init(entry.toString());
            }
        }
    }
}
