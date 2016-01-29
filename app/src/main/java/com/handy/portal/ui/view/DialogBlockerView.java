package com.handy.portal.ui.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handy.portal.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * the view used for the blocking dialog fragments
 */
public class DialogBlockerView extends LinearLayout
{
    @Bind(R.id.dialog_blocker_title)
    TextView mTitle;
    @Bind(R.id.dialog_blocker_message)
    TextView mMessage;
    @Bind(R.id.dialog_blocker_action_button)
    Button mActionButton;
    public DialogBlockerView(final Context context)
    {
        super(context);
        initView(context);
    }

    private void initView(Context context)
    {
        LayoutInflater.from(context).inflate(R.layout.fragment_dialog_blocker, this);
        ButterKnife.bind(this);
    }

    public DialogBlockerView setTitle(int titleResourceId)
    {
        mTitle.setText(titleResourceId);
        return this;
    }

    public DialogBlockerView setMessage(int messageResourceId)
    {
        mMessage.setText(messageResourceId);
        return this;
    }

    public DialogBlockerView setActionButton(int buttonTextResourceId, OnClickListener onClickListener)
    {
        mActionButton.setText(buttonTextResourceId);
        mActionButton.setOnClickListener(onClickListener);
        return this;
    }
}
