package com.handy.portal.ui.element;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handy.portal.R;

import java.util.List;

/**
 * Created by cdavis on 5/8/15.
 */
public class BookingDetailsJobInstructionsSectionView extends RelativeLayout
{
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
    public void init(String sectionTitle, int sectionIconId, List<String> entries, boolean bulleted)
    {
        TextView sectionTitleText =(TextView) findViewById(R.id.booking_details_job_instructions_section_title_text);
        sectionTitleText.setText(sectionTitle);

        ImageView sectionIcon = (ImageView) findViewById(R.id.booking_details_job_instructions_section_title_icon);
        sectionIcon.setImageResource(sectionIconId);

        LinearLayout entriesLayout = (LinearLayout) findViewById(R.id.booking_details_job_instructions_section_entries_layout);

        for(int i = 0; i < entries.size(); i++)
        {
            LayoutInflater.from(getContext()).inflate(R.layout.element_booking_details_job_instructions_entry, entriesLayout);
            RelativeLayout layout = ((RelativeLayout) (entriesLayout.getChildAt(i)));
            ImageView bulletPoint = (ImageView) layout.findViewById(R.id.booking_details_job_instructions_entry_icon);
            bulletPoint.setVisibility(bulleted ? VISIBLE : GONE);
            TextView entryText = (TextView) layout.findViewById(R.id.booking_details_job_instructions_entry_text);
            entryText.setText(entries.get(i));
        }
    }

}
