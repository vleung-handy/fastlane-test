package com.handy.portal.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.model.HelpNode;
import com.handy.portal.model.Provider;
import com.handy.portal.ui.widget.BasicInputTextView;
import com.handy.portal.ui.widget.EmailInputTextView;
import com.handy.portal.ui.widget.FirstNameInputTextView;

import butterknife.InjectView;

public final class HelpContactView extends InjectedRelativeLayout
{
    @InjectView(R.id.send_message_button)
    public Button sendMessageButton;

    @InjectView(R.id.subject_text)
    public TextView subjectText;
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

    public void updateDisplay(HelpNode node)
    {
        populateSubjectData(node);
    }

    private void populateSubjectData(HelpNode node)
    {
        subjectText.setText(node.getLabel());
    }

    private void setUserFieldsEditable(boolean editable){
        this.nameText.setEnabled(editable);
        this.emailText.setEnabled(editable);
    }

    public void prepopulateProviderData(Provider provider)//TODO: set data by making it listen to event that indicates provider data was successfully retrieved
    {
        if (provider == null)
        {
            this.nameText.setText("");
            this.emailText.setText("");
            setUserFieldsEditable(true);
        }
        else
        {
            this.nameText.setText(provider.getFullName());
            this.emailText.setText(provider.getEmail());
            setUserFieldsEditable(false);
        }
    }
}
