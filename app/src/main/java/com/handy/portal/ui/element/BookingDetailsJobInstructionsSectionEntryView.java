package com.handy.portal.ui.element;

import android.content.Context;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.util.TextUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class BookingDetailsJobInstructionsSectionEntryView extends RelativeLayout
{
    @InjectView(R.id.booking_details_job_instructions_entry_text)
    protected TextView entryText;

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

    public void init(String message)
    {
        ButterKnife.inject(this);
        String formattedMessage = TextUtils.formatHtmlLinks(message);
        formattedMessage = TextUtils.formatHtmlLineBreaks(formattedMessage);

        entryText.setText(Html.fromHtml(formattedMessage));
        entryText.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
