package com.handy.portal.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.handy.portal.R;
import com.handy.portal.model.Booking;
import com.handy.portal.ui.widget.BasicInputTextView;
import com.handy.portal.ui.widget.EmailInputTextView;
import com.handy.portal.ui.widget.FirstNameInputTextView;

import butterknife.InjectView;

public final class HelpContactView extends InjectedRelativeLayout
{
    @InjectView(R.id.send_message_button)
    public Button sendMessageButton;
    @InjectView(R.id.help_contact_user_name_text)
    public FirstNameInputTextView nameText;
    @InjectView(R.id.help_contact_email_text)
    public EmailInputTextView emailText;
    @InjectView(R.id.help_contact_comment_text)
    public BasicInputTextView commentText;

    @InjectView(R.id.name_layout)
    ViewGroup nameLayout;
    @InjectView(R.id.email_layout)
    ViewGroup emailLayout;

    public HelpContactView(final Context context)
    {
        super(context);
    }

    public HelpContactView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    public HelpContactView(final Context context, final AttributeSet attrs, final int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public void updateDisplay(Booking.User user)
    {
        prepopulateUserData(user);
    }

    private void prepopulateUserData(Booking.User user)
    {
        if (user != null)
        {
            //TODO: Get the real user from loginmanager when we start receiving full user data
            //this.nameText.setText(user.getFullName());
            //this.emailText.setText(user.getEmail());

            this.nameText.setText("");
            this.emailText.setText("");

            //Hide the name and email fields if they are prepopulated so the user can not alter them
            //Validating them off prepopulated data, not hiding if the prepop data would prevent validation
            if (nameText.validate())
            {
                this.nameLayout.setVisibility(View.GONE);
            }
            if (emailText.validate())
            {
                this.emailLayout.setVisibility(View.GONE);
            }
        }
    }
}
