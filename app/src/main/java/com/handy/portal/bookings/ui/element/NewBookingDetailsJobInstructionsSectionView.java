package com.handy.portal.bookings.ui.element;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.util.FontUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NewBookingDetailsJobInstructionsSectionView extends FrameLayout
{
    @Bind(R.id.booking_details_job_instructions_section_title_text)
    TextView mSectionTitleText;
    @Bind(R.id.booking_details_job_instructions_section_entries_layout)
    LinearLayout mEntriesLayout;

    public NewBookingDetailsJobInstructionsSectionView(final Context context)
    {
        super(context);
        init();
    }

    public NewBookingDetailsJobInstructionsSectionView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public NewBookingDetailsJobInstructionsSectionView(final Context context, final AttributeSet attrs, final int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public NewBookingDetailsJobInstructionsSectionView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init()
    {
        inflate(getContext(), R.layout.element_booking_details_job_instructions_section_new, this);
        ButterKnife.bind(this);
    }

    public void setDisplay(String sectionTitle, List<?> entries)
    {
        if (entries != null && !entries.isEmpty())
        {
            mSectionTitleText.setText(sectionTitle);

            for (Object entry : entries)
            {
                if (entry instanceof Booking.BookingInstruction)
                {
                    Booking.BookingInstruction instruction = (Booking.BookingInstruction) entry;
                    setDisplay(sectionTitle, instruction.getDescription());
                }
                else if (entry instanceof String)
                {
                    setDisplay(sectionTitle, entry.toString());

                }
            }
        }
    }

    public void setDisplay(String sectionTitle, String entry)
    {
        mSectionTitleText.setText(sectionTitle);

        int textSize = (int) getResources().getDimension(R.dimen.default_text_size);
        TextView entryView = new TextView(getContext());
        entryView.setText(entry);
        entryView.setTypeface(FontUtils.getFont(getContext(), FontUtils.CIRCULAR_BOOK));
        entryView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        entryView.setLineSpacing(0, 1.2f);
        entryView.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        mEntriesLayout.addView(entryView);
    }
}
