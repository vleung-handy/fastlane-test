package com.handy.portal.ui.constructor;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.constant.SupportActionType;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.model.Booking.Action;
import com.handy.portal.util.SupportActionUtils;
import com.handy.portal.util.Utils;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

public class SupportActionViewConstructor extends ViewConstructor<Action>
{
    @InjectView(R.id.support_action_icon)
    ImageView icon;
    @InjectView(R.id.support_action_text)
    TextView text;

    @Inject
    Bus bus;

    private Action action;

    public SupportActionViewConstructor(Context context)
    {
        super(context);
        Utils.inject(context, this);
    }

    @Override
    int getLayoutResourceId()
    {
        return R.layout.element_support_action;
    }

    @OnClick(R.id.support_action)
    public void triggerSupportAction()
    {
        bus.post(new HandyEvent.SupportActionTrigerred(action));
    }

    @Override
    protected boolean constructView(ViewGroup container, Action action)
    {
        this.action = action;
        SupportActionType supportActionType = SupportActionUtils.getSupportActionType(action);
        icon.setImageResource(supportActionType.getIconId());
        text.setText(supportActionType.getTextId());
        return true;
    }
}
