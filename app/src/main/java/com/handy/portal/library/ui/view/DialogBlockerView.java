package com.handy.portal.library.ui.view;

import android.content.Context;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handy.portal.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * the view used for the blocking dialog fragments
 */
public class DialogBlockerView extends LinearLayout
{
    @BindView(R.id.dialog_blocker_title)
    TextView mTitle;
    @BindView(R.id.dialog_blocker_message)
    TextView mMessage;
    @BindView(R.id.dialog_blocker_action_button)
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

    public DialogBlockerView setActionButton(@StringRes int buttonTextResourceId,
                                             OnClickListener onClickListener)
    {
        mActionButton.setText(buttonTextResourceId);
        mActionButton.setOnClickListener(onClickListener);
        return this;
    }
}
