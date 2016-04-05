package com.handy.portal.ui.element.bookings;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.model.booking.Booking;

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

    public void setDisplay(String sectionTitle, List<Booking.BookingInstruction> entries)
    {
        mSectionTitleText.setText(sectionTitle);

        for (Booking.BookingInstruction entry : entries)
        {
            TextView entryView = new TextView(getContext());
            entryView.setText(entry.getDescription());
            entryView.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
            mEntriesLayout.addView(entryView);

        }
    }
}
