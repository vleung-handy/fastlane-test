package com.handy.portal.ui.element;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.constant.SupportActionType;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.model.Booking;
import com.handy.portal.util.SupportActionUtils;
import com.handy.portal.util.Utils;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SupportActionView extends FrameLayout
{
    @Inject
    Bus mBus;

    @Bind(R.id.support_action_icon)
    ImageView mIcon;
    @Bind(R.id.support_action_text)
    TextView mText;

    private Booking.Action mAction;

    public SupportActionView(final Context context)
    {
        super(context);
    }

    public SupportActionView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    public SupportActionView(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SupportActionView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public SupportActionView(final Context context, Booking.Action action)
    {
        super(context);

        Utils.inject(context, this);

        inflate(getContext(), R.layout.list_item_support_action, this);
        ButterKnife.bind(this);

        mAction = action;
        SupportActionType supportActionType = SupportActionUtils.getSupportActionType(action);
        mIcon.setImageResource(supportActionType.getIconId());
        mText.setText(supportActionType.getTextId());
    }

    @OnClick(R.id.support_action)
    public void triggerSupportAction()
    {
        mBus.post(new HandyEvent.SupportActionTriggered(mAction));
    }

}
