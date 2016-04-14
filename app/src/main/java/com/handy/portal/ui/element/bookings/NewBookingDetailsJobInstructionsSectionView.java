package com.handy.portal.ui.element.bookings;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.model.Booking;
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
                int textSize = (int) getResources().getDimension(R.dimen.default_text_size);
                TextView entryView = new TextView(getContext());

                LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                llp.setMargins(0, 0, 0, (int) getResources()
                        .getDimension(R.dimen.extra_margin_xxsmall));
                entryView.setLayoutParams(llp);
                entryView.setTypeface(FontUtils.getFont(getContext(), FontUtils.CIRCULAR_BOOK));
                entryView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                entryView.setLineSpacing(0, 1.2f);
                entryView.setTextColor(ContextCompat.getColor(getContext(), R.color.black));

                if (entry instanceof Booking.BookingInstruction)
                {
                    Booking.BookingInstruction instruction = (Booking.BookingInstruction) entry;
                    entryView.setText(instruction.getDescription());
                }
                else if (entry instanceof String)
                {
                    entryView.setText(entry.toString());
                }

                mEntriesLayout.addView(entryView);
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
