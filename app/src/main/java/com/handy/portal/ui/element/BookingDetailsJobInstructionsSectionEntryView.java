package com.handy.portal.ui.element;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handy.portal.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by cdavis on 5/8/15.
 */
public class BookingDetailsJobInstructionsSectionEntryView extends RelativeLayout
{
    @InjectView(R.id.booking_details_job_instructions_entry_text)
    protected TextView entryText;

    @InjectView(R.id.booking_details_job_instructions_entry_icon)
    protected ImageView bulletPoint;

    public BookingDetailsJobInstructionsSectionEntryView(final Context context)
    {
        super(context);
    }

    public BookingDetailsJobInstructionsSectionEntryView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    public BookingDetailsJobInstructionsSectionEntryView(final Context context, final AttributeSet attrs, final int defStyle)
    {
        super(context, attrs, defStyle);
    }
    public void init(String message, boolean bulleted)
    {
        ButterKnife.inject(this);
        bulletPoint.setVisibility(bulleted ? VISIBLE : GONE);
        entryText.setText(message);
    }
}
