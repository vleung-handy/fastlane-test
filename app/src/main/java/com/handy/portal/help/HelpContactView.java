package com.handy.portal.help;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.handy.portal.R;
import com.handy.portal.manager.LoginManager;
import com.handy.portal.model.Booking;
import com.handy.portal.ui.fragment.HelpContactFragment;
import com.handy.portal.ui.widget.BasicInputTextView;
import com.handy.portal.ui.widget.EmailInputTextView;
import com.handy.portal.ui.widget.FirstNameInputTextView;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class HelpContactView
{
    @InjectView(R.id.send_message_button)
    Button sendMessageButton;
    @InjectView(R.id.help_contact_user_name_text)
    FirstNameInputTextView nameText;
    @InjectView(R.id.help_contact_email_text)
    EmailInputTextView emailText;
    @InjectView(R.id.help_contact_comment_text)
    BasicInputTextView commentText;

    @InjectView(R.id.name_layout)
    ViewGroup nameLayout;
    @InjectView(R.id.email_layout)
    ViewGroup emailLayout;

    @Inject
    LoginManager loginManager;

    protected ViewGroup parentViewGroup;
    protected Activity activity;
    protected HelpContactFragment helpFragment;

    protected int getLayoutResourceId()
    {
        return R.layout.element_help_contact;
    }

    public void constructView(HelpNode helpNode, ViewGroup parentViewGroup, Activity activity, HelpContactFragment helpFragment)
    {
        this.parentViewGroup = parentViewGroup;
        this.activity = activity;
        this.helpFragment = helpFragment;

        LayoutInflater.from(activity).inflate(getLayoutResourceId(), parentViewGroup);

        ButterKnife.inject(this, parentViewGroup);

        constructHelpNodeContactView();
    }

    public void constructHelpNodeContactView()
    {
        prepopulateUserData();
    }

    private void prepopulateUserData()
    {
        Booking.User user = new Booking.User();

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
