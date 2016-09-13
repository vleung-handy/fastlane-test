package com.handy.portal.library.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.handy.portal.R;

import butterknife.BindView;
import butterknife.ButterKnife;


public class PhoneNumberDialogBlockerView extends RelativeLayout
{
    @BindView(R.id.dialog_blocker_action_button)
    Button mDialogBlockerActionButton;

    public PhoneNumberDialogBlockerView(final Context context)
    {
        super(context);
        initView(context);
    }

    public PhoneNumberDialogBlockerView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        initView(context);
    }

    public PhoneNumberDialogBlockerView(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PhoneNumberDialogBlockerView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    public PhoneNumberDialogBlockerView setActionButtonListener(OnClickListener onClickListener)
    {
        mDialogBlockerActionButton.setOnClickListener(onClickListener);
        return this;
    }

    private void initView(Context context)
    {
        LayoutInflater.from(context).inflate(R.layout.fragment_phone_number_dialog_blocker, this);
        ButterKnife.bind(this);
    }
}
