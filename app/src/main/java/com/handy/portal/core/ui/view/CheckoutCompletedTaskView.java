package com.handy.portal.core.ui.view;


import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handy.portal.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CheckoutCompletedTaskView extends FrameLayout
{
    @BindView(R.id.task_text)
    TextView mTaskText;

    public CheckoutCompletedTaskView(final Context context)
    {
        super(context);
        initView();
    }

    public CheckoutCompletedTaskView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        initView();
    }

    public CheckoutCompletedTaskView(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        initView();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CheckoutCompletedTaskView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView()
    {
        LayoutInflater.from(getContext()).inflate(R.layout.element_checkout_completed_task, this);
        ButterKnife.bind(this);
    }

    public String getTaskText()
    {
        return mTaskText.getText().toString();
    }

    public void setTaskText(String taskText)
    {
        mTaskText.setText(taskText);
    }
}
