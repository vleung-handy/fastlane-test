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
import com.handy.portal.model.Booking;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BookingDetailsJobInstructionsSectionView extends RelativeLayout
{
    @Bind(R.id.booking_details_job_instructions_section_title_text)
    protected TextView sectionTitleText;

    @Bind(R.id.booking_details_job_instructions_section_title_icon)
    protected ImageView sectionIcon;

    @Bind(R.id.booking_details_job_instructions_section_entries_layout)
    protected LinearLayout entriesLayout;

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

        sectionTitleText.setText(sectionTitle);
        if (sectionIconId != null)
        {
            sectionIcon.setImageResource(sectionIconId);
        }

        for (int i = 0; i < entries.size(); i++)
        {
            LayoutInflater.from(getContext()).inflate(R.layout.element_booking_details_job_instructions_entry, entriesLayout);
            BookingDetailsJobInstructionsSectionEntryView entryView = ((BookingDetailsJobInstructionsSectionEntryView) (entriesLayout.getChildAt(i)));
            if (entries.get(i) instanceof Booking.BookingInstruction)
            {
                Booking.BookingInstruction instruction = (Booking.BookingInstruction) entries.get(i);
                entryView.init(instruction.getDescription());
            }
            else
            {
                entryView.init(entries.get(i).toString());
            }
        }
    }
}
