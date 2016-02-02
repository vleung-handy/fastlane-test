package com.handy.portal.helpcenter.helpcontact.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.helpcenter.model.HelpNode;
import com.handy.portal.model.Provider;
import com.handy.portal.ui.view.InjectedRelativeLayout;
import com.handy.portal.ui.widget.BasicInputTextView;
import com.handy.portal.ui.widget.EmailInputTextView;
import com.handy.portal.ui.widget.FirstNameInputTextView;

import butterknife.Bind;

public final class HelpContactView extends InjectedRelativeLayout
{
    @Bind(R.id.send_message_button)
    public Button sendMessageButton;

    @Bind(R.id.subject_text)
    public TextView subjectText;
    @Bind(R.id.help_contact_user_name_text)
    public FirstNameInputTextView nameText;
    @Bind(R.id.help_contact_email_text)
    public EmailInputTextView emailText;
    @Bind(R.id.help_contact_comment_text)
    public BasicInputTextView commentText;

    @Bind(R.id.name_layout)
    ViewGroup nameLayout;
    @Bind(R.id.email_layout)
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

    public void updateDisplay(HelpNode node)
    {
        populateSubjectData(node);
    }

    private void populateSubjectData(HelpNode node)
    {
        subjectText.setText(node.getLabel());
    }

    public void prepopulateProviderData(Provider provider)
    {
        if (provider != null)
        {
            this.nameText.setText(provider.getFullName());
            this.emailText.setText(provider.getEmail());
        }
        else
        {
            this.nameText.setText("");
            this.emailText.setText("");
        }

        this.nameText.setEnabled(!this.nameText.validate());
        this.emailText.setEnabled(!this.emailText.validate());
    }
}
